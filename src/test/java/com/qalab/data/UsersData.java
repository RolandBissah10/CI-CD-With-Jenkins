package com.qalab.data;

import java.util.HashMap;
import java.util.Map;

public final class UsersData {

    public static final int TOTAL_USERS = 10;
    public static final int USER_ID_VALID = 1;
    public static final int USER_ID_TO_UPDATE = 1;
    public static final int USER_ID_TO_DELETE = 6;
    public static final int USER_ID_INVALID = 0;
    public static final int USER_LIMIT = 3;

    public static final String USER_NEW_EMAIL = "qaengineer@qalab.com";
    public static final String USER_NEW_USERNAME = "qa_engineer";
    public static final String USER_NEW_PASSWORD = "securePass123";
    public static final String USER_NEW_PHONE = "555-867-5309";
    public static final String USER_NEW_FIRSTNAME = "QA";
    public static final String USER_NEW_LASTNAME = "Engineer";
    public static final String USER_NEW_CITY = "Accra";
    public static final String USER_NEW_STREET = "Independence Ave";
    public static final int USER_NEW_NUMBER = 5;
    public static final String USER_NEW_ZIPCODE = "00233";
    public static final String USER_PATCH_EMAIL = "patched@qalab.com";

    private UsersData() {
    }

    public static Map<String, Object> newUser() {
        Map<String, Object> name = new HashMap<>();
        name.put("firstname", USER_NEW_FIRSTNAME);
        name.put("lastname", USER_NEW_LASTNAME);

        Map<String, Object> geo = new HashMap<>();
        geo.put("lat", "5.6037");
        geo.put("long", "-0.1870");

        Map<String, Object> address = new HashMap<>();
        address.put("city", USER_NEW_CITY);
        address.put("street", USER_NEW_STREET);
        address.put("number", USER_NEW_NUMBER);
        address.put("zipcode", USER_NEW_ZIPCODE);
        address.put("geolocation", geo);

        Map<String, Object> body = new HashMap<>();
        body.put("email", USER_NEW_EMAIL);
        body.put("username", USER_NEW_USERNAME);
        body.put("password", USER_NEW_PASSWORD);
        body.put("phone", USER_NEW_PHONE);
        body.put("name", name);
        body.put("address", address);
        return body;
    }

    public static Map<String, Object> updatedUser() {
        Map<String, Object> name = new HashMap<>();
        name.put("firstname", "Updated");
        name.put("lastname", "User");

        Map<String, Object> address = new HashMap<>();
        address.put("city", "Kumasi");
        address.put("street", "Kejetia Road");
        address.put("number", 12);
        address.put("zipcode", "00234");

        Map<String, Object> body = new HashMap<>();
        body.put("email", "updated@qalab.com");
        body.put("username", "updated_qa_user");
        body.put("password", "newPass456");
        body.put("phone", "555-000-0001");
        body.put("name", name);
        body.put("address", address);
        return body;
    }

    public static Map<String, Object> patchedUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", USER_PATCH_EMAIL);
        return body;
    }
}
