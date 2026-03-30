package com.qalab.data;

public final class CommonData {

    public static final String BASE_URL = "https://fakestoreapi.com";

    private CommonData() {
    }

    public static String resolveBaseUrl() {
        String override = System.getProperty("BASE_URL");
        if (override == null || override.trim().isEmpty()) {
            return BASE_URL;
        }
        return override.trim();
    }
}
