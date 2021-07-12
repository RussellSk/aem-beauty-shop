package com.exadel.core.services;

import com.exadel.core.services.data.Product;

public interface ShopContentService {
    void createNewProduct(Product product);
    void createNewTag(String name);
}
