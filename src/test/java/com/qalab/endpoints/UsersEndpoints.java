package com.qalab.endpoints;

public final class UsersEndpoints {

    public static final String USERS = "/users";

    private UsersEndpoints() {
    }

    public static String byId(int id) {
        return USERS + "/" + id;
    }
}
