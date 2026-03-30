package com.qalab.endpoints;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class ProductsEndpoints {

    public static final String PRODUCTS = "/products";
    public static final String CATEGORIES = PRODUCTS + "/categories";
    public static final String CATEGORY_BY_NAME = PRODUCTS + "/category/{category}";

    private ProductsEndpoints() {
    }

    public static String byId(int id) {
        return PRODUCTS + "/" + id;
    }

    public static String byCategory(String category) {
        String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return PRODUCTS + "/category/" + encodedCategory;
    }
}
