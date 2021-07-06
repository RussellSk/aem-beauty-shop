package com.exadel.core.services.impl;

import com.exadel.core.services.ShopContentService;
import com.exadel.core.services.data.Product;
import com.exadel.core.utils.ResourceResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component(service = ShopContentService.class)
public class ShopContentServiceImpl implements ShopContentService {

    private static final String PAGE_PATH = "/content/exadel/us/en/products";
    private static final String PRODUCT_TITLE_COMPONENT = "exadel/components/title";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void createNewProduct(Product product) {
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resolverFactory)) {
            log.info("ShopContentServiceImpl user: " + resourceResolver.getUserID());
            log.info("ShopContentServiceImpl productId: " + product.getId());

            Resource pageResource = resourceResolver.getResource(PAGE_PATH);
            String pageUniqueName = "product" + product.getId();

            //Check if page already exist
            if (resourceResolver.getResource(PAGE_PATH + "/" + pageUniqueName) != null) {
                log.info("ShopContentServiceImpl Exist");
                return;
            }

            //Create page
            Map<String, Object> pageProperties = new HashMap<>();
            pageProperties.put("jcr:primaryType", "cq:Page");
            Resource productResource = resourceResolver.create(pageResource, pageUniqueName, pageProperties);

            //Create page jcr:content
            Map<String, Object> pageContentProperties = new HashMap<>();
            pageContentProperties.put("jcr:primaryType", "cq:PageContent");
            pageContentProperties.put("jcr:title", product.getName());
            pageContentProperties.put("cq:template", "/conf/exadel/settings/wcm/templates/template-for-content-pages");
            pageContentProperties.put("sling:resourceType", "exadel/components/content/page");
            Resource contentResource = resourceResolver.create(productResource, "jcr:content", pageContentProperties);

            //Create root node
            Map<String, Object> rootProperties = new HashMap<>();
            rootProperties.put("sling:resourceType", "exadel/components/container");
            Resource rootResource = resourceResolver.create(contentResource, "root", rootProperties);

            //Create container node
            Map<String, Object> containerProperties = new HashMap<>();
            containerProperties.put("sling:resourceType", "exadel/components/container");
            containerProperties.put("layout", "responsiveGrid");
            Resource containerResource = resourceResolver.create(rootResource, "container", containerProperties);

            //Create product brand node
            Map<String, Object> productProperties = new HashMap<>();
            productProperties.put("jcr:primaryType", "nt:unstructured");
            productProperties.put("sling:resourceType", "exadel/components/content/product");
            productProperties.put("name", product.getName());
            productProperties.put("description", product.getDescription());
            productProperties.put("brand", product.getBrand());
            productProperties.put("image", product.getImage_link());
            productProperties.put("price", product.getPrice());
            productProperties.put("type", product.getProduct_type());
            productProperties.put("rating", product.getRating());
            productProperties.put("created_at", product.getCreated_at());
            productProperties.put("category", product.getCategory());
            resourceResolver.create(containerResource, "product", productProperties);

            resourceResolver.commit();

        } catch (LoginException | PersistenceException e) {
            log.error(String.format("ShopContentServiceImpl: %s", e.getMessage()));
        }
    }
}
