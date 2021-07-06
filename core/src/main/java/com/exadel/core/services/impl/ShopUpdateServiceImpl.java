package com.exadel.core.services.impl;

import com.exadel.core.services.ShopContentService;
import com.exadel.core.services.ShopUpdateService;
import com.exadel.core.services.data.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import java.util.List;

@Slf4j
@Component(service = ShopUpdateService.class, immediate = true)
public class ShopUpdateServiceImpl implements ShopUpdateService {

    private static final String PRODUCT_API = "http://makeup-api.herokuapp.com/api/v1/products.json?brand=";
    private static final String MAKEUP_WEBSITE = "http://makeup-api.herokuapp.com/";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ShopContentService contentService;

    @Override
    public void updateProducts(String productType) {
        try {
            log.debug("Update Products Started");
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                //Initialize HttpGet request
                HttpGet getRequest = new HttpGet(PRODUCT_API + productType);
                getRequest.addHeader("accept", "application/json");
                getRequest.addHeader("content-type", "application/json");

                //Send the request
                HttpResponse response = httpClient.execute(getRequest);

                //Verify the valid code
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new Exception(String.format("Failed to connect to %s, with HTTP error code: %d", PRODUCT_API, statusCode));
                }

                //Poll back the response object
                HttpEntity httpEntity = response.getEntity();
                String apiContent = EntityUtils.toString(httpEntity);

                //Create a Java List of Product from a JSON String
                ObjectMapper objectMapper = new ObjectMapper();
                List<Product> products = objectMapper.readValue(apiContent, new TypeReference<List<Product>>() {});

                //Create new pages
                int count = 0;
                for (Product product : products) {
                    log.info("-- PRODUCT: " + product.getName());
                    contentService.createNewProduct(product);
                }
            }
        } catch (Exception exception) {
            log.error(String.format("updateProduct: %s", exception.getMessage()));
        }
    }

    @Override
    public void updateTags() {
    }

    @Override
    public void updateBrands() {
    }
}
