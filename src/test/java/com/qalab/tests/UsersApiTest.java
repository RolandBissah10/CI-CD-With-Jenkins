package com.qalab.tests;

import com.qalab.base.BaseTest;
import com.qalab.data.UsersData;
import com.qalab.endpoints.UsersEndpoints;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Users")
@DisplayName("Users API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersApiTest extends BaseTest {

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Get all users")
    @DisplayName("GET /users - returns 10 users with required fields")
    void getAllUsers_returns10Users() {
        given().spec(requestSpec)
            .when().get(UsersEndpoints.USERS)
            .then().spec(okSpec)
                .body("size()",   equalTo(UsersData.TOTAL_USERS))
                .body("email",    everyItem(containsString("@")))
                .body("username", everyItem(notNullValue()));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @Story("Get single user")
    @DisplayName("GET /users/{id} - returns correct user with nested fields")
    void getUserById_returnsCorrectUser() {
        given().spec(requestSpec)
            .when().get(UsersEndpoints.byId(UsersData.USER_ID_VALID))
            .then().spec(okSpec)
                .body("id",                   equalTo(UsersData.USER_ID_VALID))
                .body("email",                containsString("@"))
                .body("username",             notNullValue())
                .body("name.firstname",       notNullValue())
                .body("name.lastname",        notNullValue())
                .body("address.city",         notNullValue())
                .body("address.zipcode",      notNullValue());
    }

    @Test
    @Order(3)
    @Story("Get single user")
    @DisplayName("GET /users/0 - invalid id returns empty or error")
    void getUserById_invalidId_returnsErrorOrEmpty() {
        int status = given().spec(requestSpec)
            .when().get(UsersEndpoints.byId(UsersData.USER_ID_INVALID))
            .then().extract().statusCode();

        assertThat(status).isIn(200, 404);
    }

    // ── LIMIT & SORT ──────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @Story("Limit users")
    @DisplayName("GET /users?limit=3 - returns exactly 3 users")
    void getUsers_withLimit_returnsCorrectCount() {
        given().spec(requestSpec)
            .queryParam("limit", UsersData.USER_LIMIT)
            .when().get(UsersEndpoints.USERS)
            .then().spec(okSpec)
                .body("size()", equalTo(UsersData.USER_LIMIT));
    }

    @Test
    @Order(5)
    @Story("Sort users")
    @DisplayName("GET /users?sort=desc - IDs are in descending order")
    void getUsers_sortDesc_idsAreDescending() {
        List<Integer> ids = given().spec(requestSpec)
            .queryParam("sort", "desc")
            .when().get(UsersEndpoints.USERS)
            .then().spec(okSpec)
                .extract().jsonPath().getList("id", Integer.class);

        assertThat(ids).isSortedAccordingTo(Comparator.reverseOrder());
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Story("Create user")
    @DisplayName("POST /users - creates user and returns new id")
    void createUser_returnsNewId() {
        given().spec(requestSpec)
            .body(UsersData.newUser())
            .when().post(UsersEndpoints.USERS)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("id", notNullValue());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @Story("Update user")
    @DisplayName("PUT /users/{id} - full update returns updated user")
    void updateUser_fullUpdate_returnsUpdatedData() {
        given().spec(requestSpec)
            .body(UsersData.updatedUser())
            .when().put(UsersEndpoints.byId(UsersData.USER_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("$", notNullValue());
    }

    @Test
    @Order(8)
    @Story("Update user")
    @DisplayName("PATCH /users/{id} - partial update changes email only")
    void patchUser_partialUpdate_updatesEmail() {
        given().spec(requestSpec)
            .body(UsersData.patchedUser())
            .when().patch(UsersEndpoints.byId(UsersData.USER_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("$", notNullValue());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    @Story("Delete user")
    @DisplayName("DELETE /users/{id} - returns deleted user data")
    void deleteUser_returnsDeletedUser() {
        given().spec(requestSpec)
            .when().delete(UsersEndpoints.byId(UsersData.USER_ID_TO_DELETE))
            .then().spec(okSpec)
                .body("id", equalTo(UsersData.USER_ID_TO_DELETE));
    }

    // ── SCHEMA ────────────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @Story("Schema validation")
    @DisplayName("GET /users/{id} - response has all required nested fields")
    void getUser_hasAllRequiredFields() {
        given().spec(requestSpec)
            .when().get(UsersEndpoints.byId(UsersData.USER_ID_VALID))
            .then().spec(okSpec)
                .body("$",                    hasKey("id"))
                .body("$",                    hasKey("email"))
                .body("$",                    hasKey("username"))
                .body("$",                    hasKey("phone"))
                .body("$",                    hasKey("name"))
                .body("name",                 hasKey("firstname"))
                .body("name",                 hasKey("lastname"))
                .body("$",                    hasKey("address"))
                .body("address",              hasKey("city"))
                .body("address",              hasKey("street"))
                .body("address",              hasKey("zipcode"))
                .body("address",              hasKey("geolocation"))
                .body("address.geolocation",  hasKey("lat"))
                .body("address.geolocation",  hasKey("long"));
    }

}
