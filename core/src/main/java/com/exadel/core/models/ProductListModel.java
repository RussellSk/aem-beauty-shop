package com.exadel.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ProductListModel {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @OSGiService
    private ModelFactory modelFactory;

    /**
     * Injecting Sling Servlet Request
     */
    @Self
    private SlingHttpServletRequest request;

    private List<ProductModel> products;

    @PostConstruct
    protected void init() {
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        products = getProductModels(resourceResolver);
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    private List<ProductModel> getProductModels(ResourceResolver resourceResolver) {
        List<ProductModel> productModels = new ArrayList<>();
        Resource productCatalogResource = resourceResolver.getResource("/content/exadel/us/en/products");
        if (productCatalogResource == null) {
            return productModels;
        }

        Iterable<Resource> productsChildren = productCatalogResource.getChildren();
        for (Resource child : productsChildren) {
            if (!child.getName().startsWith("jcr:content")) {
                Resource productResource = resourceResolver.getResource(child.getPath() + "/jcr:content/root/container/product");
                if (productResource == null) continue;
                ProductModel productModel = modelFactory.getModelFromWrappedRequest(request, productResource, ProductModel.class);
                productModels.add(productModel);
            }
        }

        return productModels;
    }
}
