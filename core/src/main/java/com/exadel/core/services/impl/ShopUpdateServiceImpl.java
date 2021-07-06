package com.exadel.core.services.impl;

import com.exadel.core.services.ShopContentService;
import com.exadel.core.services.ShopUpdateService;
import com.exadel.core.services.data.Product;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component(service = ShopUpdateService.class, immediate = true)
public class ShopUpdateServiceImpl implements ShopUpdateService {

    private static final String PRODUCT_API = "http://makeup-api.herokuapp.com/api/v1/products.json?brand=";
    private static final String MAKEUP_WEBSITE = "http://makeup-api.herokuapp.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_4) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ShopContentService contentService;

    /**
     * Retrieve products from API and save them
     */
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


    /**
     * Retrieve web page, parse it and then creates Brand's tags if not exist
     */
    @Override
    public Set<String> updateBrands() {
        Set<String> brands = new HashSet<>();
        try {
            Document document = Jsoup.connect(MAKEUP_WEBSITE).userAgent(USER_AGENT).timeout(15000).get();
            Element brandElement = document.getElementsByClass("tag_list").get(1);
            Elements brandsElements = brandElement.children();

            for (Element brand : brandsElements) {
                String brandName = brand.select("h4").text();
                contentService.createNewTag(brandName);
                brands.add(brandName);
            }

        } catch (IOException exception) {
            log.error(String.format("updateBrands: %s", exception.getMessage()));
        }

        return brands;
    }
}
