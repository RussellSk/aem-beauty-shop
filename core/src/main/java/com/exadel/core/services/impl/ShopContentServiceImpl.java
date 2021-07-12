package com.exadel.core.services.impl;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
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
import java.util.Optional;

@Slf4j
@Component(service = ShopContentService.class)
public class ShopContentServiceImpl implements ShopContentService {

    private static final String PAGE_PATH = "/content/exadel/us/en/products";
    private static final String TAG_PATH = "/content/cq:tags/exadel/brands/";

    @Reference
    private ResourceResolverFactory resolverFactory;

    /**
     * Creates new product page with product component and corresponding attributes
     */
    @Override
    public void createNewProduct(Product product) {
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resolverFactory)) {
            log.info("ShopContentServiceImpl user: " + resourceResolver.getUserID());
            log.info("ShopContentServiceImpl productId: " + product.getId());

            Resource productResource = createPage(resourceResolver, product);
            Resource contentResource = createJcrContent(resourceResolver, productResource, product);
            Resource rootResource = createRootNode(resourceResolver, contentResource);
            Resource containerResource = createContainerNode(resourceResolver, rootResource);
            createProduct(resourceResolver, containerResource, product);

            resourceResolver.commit();

        } catch (LoginException | PersistenceException exception) {
            log.error("ShopContentServiceImpl", exception);
        }
    }

    /**
     * Creates Page Node
     * @param resourceResolver Resource Resolver
     * @param product Product information
     * @return Product Resource
     */
    private Resource createPage(ResourceResolver resourceResolver, Product product) {
        try {
            Resource pageResource = resourceResolver.getResource(PAGE_PATH);
            String pageUniqueName = "product" + product.getId();

            //Check if page already exist
            if (resourceResolver.getResource(PAGE_PATH + "/" + pageUniqueName) != null) {
                log.info("ShopContentServiceImpl Exist");
                return null;
            }

            //Create page
            Map<String, Object> pageProperties = new HashMap<>();
            pageProperties.put("jcr:primaryType", "cq:Page");

            return resourceResolver.create(pageResource, pageUniqueName, pageProperties);
        } catch (PersistenceException exception) {
            log.error("createPage", exception);
        }

        return null;
    }

    /**
     * Creates Jcr Content Node
     * @param resourceResolver Resource Resolver
     * @param productResource Product Resource
     * @param product Product information
     * @return Content Resource
     */
    private Resource createJcrContent(ResourceResolver resourceResolver, Resource productResource, Product product) {
        try {
            //Create page jcr:content
            Map<String, Object> pageContentProperties = new HashMap<>();
            pageContentProperties.put("jcr:primaryType", "cq:PageContent");
            pageContentProperties.put("jcr:title", product.getName());
            pageContentProperties.put("cq:template", "/conf/exadel/settings/wcm/templates/template-for-content-pages");
            pageContentProperties.put("sling:resourceType", "exadel/components/content/page");

            return resourceResolver.create(productResource, "jcr:content", pageContentProperties);
        } catch (PersistenceException exception) {
            log.error("createJcrContent", exception);
        }
        return null;
    }

    /**
     * Creates Root Node
     * @param resourceResolver Resource Resolver
     * @param contentResource Content Resource
     * @return Root Resource
     */
    private Resource createRootNode(ResourceResolver resourceResolver, Resource contentResource) {
        try {
            //Create root node
            Map<String, Object> rootProperties = new HashMap<>();
            rootProperties.put("sling:resourceType", "exadel/components/container");

            return resourceResolver.create(contentResource, "root", rootProperties);
        } catch (PersistenceException exception) {
            log.error("createRootNode", exception);
        }
        return null;
    }

    /**
     * Creates Container Node
     * @param resourceResolver Resource Resolver
     * @param rootResource Root Resource
     * @return Container Resource
     */
    private Resource createContainerNode(ResourceResolver resourceResolver, Resource rootResource) {
        try {
            //Create container node
            Map<String, Object> containerProperties = new HashMap<>();
            containerProperties.put("sling:resourceType", "exadel/components/container");
            containerProperties.put("layout", "responsiveGrid");

            return resourceResolver.create(rootResource, "container", containerProperties);
        } catch (PersistenceException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Creates Product Node
     * @param resourceResolver Resource Resolver
     * @param containerResource Container Resource
     * @param product Product Information
     */
    private void createProduct(ResourceResolver resourceResolver, Resource containerResource, Product product) {
        try {
            //Create product brand node
            Map<String, Object> productProperties = new HashMap<>();
            productProperties.put("jcr:primaryType", "nt:unstructured");
            productProperties.put("sling:resourceType", "exadel/components/content/product");
            productProperties.put("name", Optional.ofNullable(product.getName()).orElse("Unknown"));
            productProperties.put("description", Optional.ofNullable(product.getDescription()).orElse(""));
            productProperties.put("brand", Optional.ofNullable(product.getBrand()).orElse("Various"));
            productProperties.put("image", Optional.ofNullable(product.getImage_link()).orElse(""));
            productProperties.put("price", Optional.ofNullable(product.getPrice()).orElse(""));
            productProperties.put("type", Optional.ofNullable(product.getProduct_type()).orElse("Various"));
            productProperties.put("rating", Optional.of(product.getRating()).orElse(0.0));
            productProperties.put("createdAt", Optional.of(product.getCreated_at()).orElse(""));
            productProperties.put("category", Optional.ofNullable(product.getCategory()).orElse("Various"));

            resourceResolver.create(containerResource, "product", productProperties);
        } catch (PersistenceException exception) {
            log.error("createProduct",  exception);
        }
    }

    /**
     * Creates new tags in namespace brand
     */
    @Override
    public void createNewTag(String name) {
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resolverFactory)) {
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (tagManager.resolve(TAG_PATH + name) == null) {
                tagManager.createTag(TAG_PATH + name, name, "Product Brand Tag", true);
            }
        } catch (LoginException exception) {
            log.error("createNewTag", exception);
        } catch (InvalidTagFormatException exception) {
            log.error("createNewTag - tagManager", exception);
        }
    }
}
