package com.qalab.endpoints;

public final class CartsEndpoints {

    public static final String CARTS = "/carts";
    public static final String CARTS_BY_USER = CARTS + "/user/";

    private CartsEndpoints() {
    }

    public static String byId(int id) {
        return CARTS + "/" + id;
    }

    public static String byUserId(int userId) {
        return CARTS_BY_USER + userId;
    }
}
