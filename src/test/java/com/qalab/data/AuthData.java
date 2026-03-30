package com.qalab.data;

import java.util.HashMap;
import java.util.Map;

public final class AuthData {

    public static final String AUTH_VALID_USER_1 = "mor_2314";
    public static final String AUTH_VALID_PASS_1 = "83r5^_";
    public static final String AUTH_VALID_USER_2 = "johnd";
    public static final String AUTH_VALID_PASS_2 = "m38rmF$";
    public static final String AUTH_INVALID_USER = "ghostuser999";
    public static final String AUTH_INVALID_PASS = "wrongpassword";
    public static final String AUTH_JWT_REGEX =
            "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

    private AuthData() {
    }

    public static Map<String, String> loginBody(String username, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return body;
    }

    public static Map<String, String> loginBodyMissingPassword(String username) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        return body;
    }
}
