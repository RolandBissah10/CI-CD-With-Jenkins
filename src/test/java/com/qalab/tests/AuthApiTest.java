package com.qalab.tests;

import com.qalab.base.BaseTest;
import com.qalab.data.AuthData;
import com.qalab.endpoints.AuthEndpoints;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Authentication")
@DisplayName("Auth API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthApiTest extends BaseTest {

    // ── VALID LOGIN ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Valid login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /auth/login - valid credentials return a JWT token")
    void login_validCredentials_returnsJwt() {
        String token = given().spec(requestSpec)
            .body(AuthData.loginBody(AuthData.AUTH_VALID_USER_1, AuthData.AUTH_VALID_PASS_1))
            .when().post(AuthEndpoints.LOGIN)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .extract().path("token");

        assertThat(token).matches(AuthData.AUTH_JWT_REGEX);
        log.info("JWT received (first 20 chars): {}...", token.substring(0, 20));
    }

    @Test
    @Order(2)
    @Story("Valid login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /auth/login - second valid user also returns a JWT token")
    void login_secondValidUser_returnsJwt() {
        String token = given().spec(requestSpec)
            .body(AuthData.loginBody(AuthData.AUTH_VALID_USER_2, AuthData.AUTH_VALID_PASS_2))
            .when().post(AuthEndpoints.LOGIN)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("token", notNullValue())
                .extract().path("token");

        // JWT must have 3 parts: header.payload.signature
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ── INVALID CREDENTIALS ───────────────────────────────────────────────────

    @Test
    @Order(3)
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /auth/login - wrong password returns 401")
    void login_wrongPassword_returns401() {
        given().spec(requestSpec)
            .body(AuthData.loginBody(AuthData.AUTH_VALID_USER_1, AuthData.AUTH_INVALID_PASS))
            .when().post(AuthEndpoints.LOGIN)
            .then().statusCode(401);
    }

    @Test
    @Order(4)
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /auth/login - non-existent user returns 401")
    void login_nonExistentUser_returns401() {
        given().spec(requestSpec)
            .body(AuthData.loginBody(AuthData.AUTH_INVALID_USER, AuthData.AUTH_INVALID_PASS))
            .when().post(AuthEndpoints.LOGIN)
            .then().statusCode(401);
    }

    // ── MISSING FIELDS ────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @Story("Invalid login")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /auth/login - missing password returns 4xx")
    void login_missingPassword_returns4xx() {
        int status = given().spec(requestSpec)
            .body(AuthData.loginBodyMissingPassword(AuthData.AUTH_VALID_USER_1))
            .when().post(AuthEndpoints.LOGIN)
            .then().extract().statusCode();

        assertThat(status).isBetween(400, 499);
    }

    // ── SCHEMA ────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Story("Schema validation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /auth/login - success response has token key with JWT format")
    void login_successResponse_hasTokenWithJwtFormat() {
        given().spec(requestSpec)
            .body(AuthData.loginBody(AuthData.AUTH_VALID_USER_1, AuthData.AUTH_VALID_PASS_1))
            .when().post(AuthEndpoints.LOGIN)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("$",     hasKey("token"))
                .body("token", instanceOf(String.class))
                .body("token", matchesPattern(AuthData.AUTH_JWT_REGEX));
    }

}
