package com.qalab.data;

import java.util.HashMap;
import java.util.Map;

public final class ProductsData {

    public static final int TOTAL_PRODUCTS = 20;
    public static final int PRODUCT_ID_FIRST = 1;
    public static final int PRODUCT_ID_TO_UPDATE = 7;
    public static final int PRODUCT_ID_TO_DELETE = 6;
    public static final int PRODUCT_ID_INVALID = 0;

    public static final String PRODUCT_NEW_TITLE = "QA Test Laptop";
    public static final double PRODUCT_NEW_PRICE = 999.99;
    public static final String PRODUCT_NEW_CATEGORY = "electronics";
    public static final String PRODUCT_NEW_DESC = "A test product for automation";
    public static final String PRODUCT_NEW_IMAGE = "https://fakestoreapi.com/img/81fAn1KnR2L._AC_SX679_.jpg";

    public static final String PRODUCT_UPDATED_TITLE = "Updated Wireless Mouse";
    public static final double PRODUCT_UPDATED_PRICE = 49.99;
    public static final String PRODUCT_UPDATED_DESC = "Full update via PUT";
    public static final String PRODUCT_UPDATED_CATEGORY = "electronics";

    public static final String PRODUCT_PATCHED_TITLE = "Patched Product Title";
    public static final double PRODUCT_PATCHED_PRICE = 19.99;

    public static final int[] PRODUCT_IDS = {1, 10, 20};
    public static final int[] PRODUCT_LIMIT_VALUES = {1, 5, 10};
    public static final String[] CATEGORIES = {
            "electronics", "jewelery", "men's clothing", "women's clothing"
    };

    private ProductsData() {
    }

    public static Map<String, Object> newProduct() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", PRODUCT_NEW_TITLE);
        body.put("price", PRODUCT_NEW_PRICE);
        body.put("description", PRODUCT_NEW_DESC);
        body.put("category", PRODUCT_NEW_CATEGORY);
        body.put("image", PRODUCT_NEW_IMAGE);
        return body;
    }

    public static Map<String, Object> updatedProduct() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", PRODUCT_UPDATED_TITLE);
        body.put("price", PRODUCT_UPDATED_PRICE);
        body.put("description", PRODUCT_UPDATED_DESC);
        body.put("category", PRODUCT_UPDATED_CATEGORY);
        body.put("image", PRODUCT_NEW_IMAGE);
        return body;
    }

    public static Map<String, Object> patchedProduct() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", PRODUCT_PATCHED_TITLE);
        body.put("price", PRODUCT_PATCHED_PRICE);
        return body;
    }
}
