package com.exadel.core.services;

import java.util.Set;

public interface ShopUpdateService {
    void updateProducts(String productType);
    Set<String> updateBrands();
}
