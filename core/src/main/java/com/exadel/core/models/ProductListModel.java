package com.exadel.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.PostConstruct;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonSerialize(as = ProductListModel.class)
public class ProductListModel {

    private static final String PRODUCT_QUERY = "SELECT * FROM [nt:unstructured] AS product " +
            "WHERE ISDESCENDANTNODE(product, [/content/exadel/us/en/products]) " +
            "AND product.[sling:resourceType] = 'exadel/components/content/product' " +
            "ORDER BY product.[productId] DESC";

    private static final String PRODUCT_SEARCH_QUERY = "SELECT * FROM [nt:unstructured] AS product " +
            " WHERE ISDESCENDANTNODE(product, [/content/exadel/us/en/products]) " +
            " AND product.[sling:resourceType] = 'exadel/components/content/product' " +
            " AND product.[description] LIKE '%search_word%' " +
            " ORDER BY product.[productId] DESC";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @OSGiService
    private ModelFactory modelFactory;

    /**
     * Injecting Sling Servlet Request
     */
    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private int itemsPerPage = 12;

    private List<ProductModel> products;
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {
        resourceResolver = request.getResource().getResourceResolver();
        products = handleRequest();
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    /**
     * Handles different requests.
     * If parameter tag was specified method returns all products with specific tag
     * If parameter searchWord was specified method returns all products that contains this search word
     * @return List of Product Models
     */
    private List<ProductModel> handleRequest() {
        String tagName = request.getParameter("tag");
        String searchWord = request.getParameter("searchWord");

        if (tagName != null) {
            return getProductsByTag(tagName);
        } else if (searchWord != null) {
            return getProductModels(PRODUCT_SEARCH_QUERY.replace("search_word", searchWord));
        }

        return getProductModels(PRODUCT_QUERY);
    }

    /**
     * Get all products with specific tag
     * @param tagName String value represents name of tag
     * @return List of Product Models
     */
    private List<ProductModel> getProductsByTag(String tagName) {
        List<ProductModel> productModels = new ArrayList<>();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        TagManager.FindResults results = tagManager.findByTitle(tagName);
        if (results.tags != null && results.tags.length > 0) {
            Tag currentTag = results.tags[0];
            Iterator<Resource> productsResources = currentTag.find();
            while (productsResources.hasNext()) {
                Resource product = resourceResolver.getResource(productsResources.next().getPath() + "/root/container/product");
                ProductModel productModel = modelFactory.getModelFromWrappedRequest(request, product, ProductModel.class);
                productModels.add(productModel);
            }
        }
        return productModels;
    }

    /**
     * Get all products by Query Expression with pagination
     * @param queryExpression JCR_SQL2 Expression
     * @return List of Product Models
     */
    private List<ProductModel> getProductModels(String queryExpression) {
        List<ProductModel> productModels = new ArrayList<>();
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);

            int page = 0;
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }

            query.setOffset((long) itemsPerPage * page);
            query.setLimit(itemsPerPage);
            QueryResult result = query.execute();
            NodeIterator nodeIterator = result.getNodes();

            while (nodeIterator.hasNext()) {
                Resource productResource = resourceResolver.getResource(nodeIterator.nextNode().getPath());
                if (productResource == null) {
                    continue;
                }

                ProductModel productModel = modelFactory.getModelFromWrappedRequest(request, productResource, ProductModel.class);
                productModels.add(productModel);
            }
        } catch (RepositoryException exception) {
            log.error("getProductModels", exception);
        }

        return productModels;
    }
}
