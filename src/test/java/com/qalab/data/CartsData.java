package com.qalab.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CartsData {

    public static final int TOTAL_CARTS = 20;
    public static final int CART_ID_VALID = 1;
    public static final int CART_ID_TO_UPDATE = 7;
    public static final int CART_ID_TO_DELETE = 6;
    public static final int CART_USER_ID = 1;
    public static final int CART_LIMIT = 5;

    public static final int CART_NEW_USER_ID = 5;
    public static final String CART_NEW_DATE = "2024-01-01";
    public static final int CART_PRODUCT_ID_1 = 1;
    public static final int CART_PRODUCT_QTY_1 = 3;
    public static final int CART_PRODUCT_ID_2 = 2;
    public static final int CART_PRODUCT_QTY_2 = 1;
    public static final String CART_PATCH_DATE = "2024-12-31";

    public static final String CART_DATE_START = "2019-01-01";
    public static final String CART_DATE_END = "2020-12-30";

    private CartsData() {
    }

    public static Map<String, Object> newCart() {
        Map<String, Object> p1 = new HashMap<>();
        p1.put("productId", CART_PRODUCT_ID_1);
        p1.put("quantity", CART_PRODUCT_QTY_1);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("productId", CART_PRODUCT_ID_2);
        p2.put("quantity", CART_PRODUCT_QTY_2);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", CART_NEW_USER_ID);
        body.put("date", CART_NEW_DATE);
        body.put("products", Arrays.asList(p1, p2));
        return body;
    }

    public static Map<String, Object> updatedCart() {
        Map<String, Object> product = new HashMap<>();
        product.put("productId", 5);
        product.put("quantity", 2);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", 3);
        body.put("date", "2024-06-01");
        body.put("products", Arrays.asList(product));
        return body;
    }

    public static Map<String, Object> patchedCart() {
        Map<String, Object> body = new HashMap<>();
        body.put("date", CART_PATCH_DATE);
        return body;
    }
}
