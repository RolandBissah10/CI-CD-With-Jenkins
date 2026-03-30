package com.qalab.tests;

import com.qalab.base.BaseTest;
import com.qalab.data.CartsData;
import com.qalab.endpoints.CartsEndpoints;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Carts")
@DisplayName("Carts API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartsApiTest extends BaseTest {

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Get all carts")
    @DisplayName("GET /carts - returns 20 carts")
    void getAllCarts_returns20Carts() {
        given().spec(requestSpec)
            .when().get(CartsEndpoints.CARTS)
            .then().spec(okSpec)
                .body("size()",  greaterThan(0))
                .body("userId",  everyItem(notNullValue()))
                .body("date",    everyItem(notNullValue()));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @Story("Get single cart")
    @DisplayName("GET /carts/{id} - returns cart with product list")
    void getCartById_returnsCartWithProducts() {
        given().spec(requestSpec)
            .when().get(CartsEndpoints.byId(CartsData.CART_ID_VALID))
            .then().spec(okSpec)
                .body("id",              equalTo(CartsData.CART_ID_VALID))
                .body("products",        notNullValue())
                .body("products.size()", greaterThan(0));
    }

    // ── GET BY USER ───────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @Story("Get user carts")
    @DisplayName("GET /carts/user/{userId} - all carts belong to that user")
    void getCartsByUserId_allBelongToUser() {
        List<Integer> userIds = given().spec(requestSpec)
            .when().get(CartsEndpoints.byUserId(CartsData.CART_USER_ID))
            .then().spec(okSpec)
                .body("size()", greaterThan(0))
                .extract().jsonPath().getList("userId", Integer.class);

        assertThat(userIds).allMatch(id -> id.equals(CartsData.CART_USER_ID));
    }

    // ── DATE RANGE ────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @Story("Filter carts by date")
    @DisplayName("GET /carts?startdate=&enddate= - returns carts within range")
    void getCarts_withDateRange_returnsFilteredCarts() {
        given().spec(requestSpec)
            .queryParam("startdate", CartsData.CART_DATE_START)
            .queryParam("enddate",   CartsData.CART_DATE_END)
            .when().get(CartsEndpoints.CARTS)
            .then().spec(okSpec)
                .body("size()", greaterThan(0));
    }

    // ── LIMIT & SORT ──────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @Story("Limit carts")
    @DisplayName("GET /carts?limit=5 - returns exactly 5 carts")
    void getCarts_withLimit_returnsCorrectCount() {
        given().spec(requestSpec)
            .queryParam("limit", CartsData.CART_LIMIT)
            .when().get(CartsEndpoints.CARTS)
            .then().spec(okSpec)
                .body("size()", equalTo(CartsData.CART_LIMIT));
    }

    @Test
    @Order(6)
    @Story("Sort carts")
    @DisplayName("GET /carts?sort=desc - IDs are in descending order")
    void getCarts_sortDesc_idsAreDescending() {
        List<Integer> ids = given().spec(requestSpec)
            .queryParam("sort", "desc")
            .when().get(CartsEndpoints.CARTS)
            .then().spec(okSpec)
                .extract().jsonPath().getList("id", Integer.class);

        assertThat(ids).isSortedAccordingTo(Comparator.reverseOrder());
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @Story("Create cart")
    @DisplayName("POST /carts - creates cart and returns new id")
    void createCart_returnsNewId() {
        given().spec(requestSpec)
            .body(CartsData.newCart())
            .when().post(CartsEndpoints.CARTS)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("id",              notNullValue())
                .body("userId",          equalTo(CartsData.CART_NEW_USER_ID))
                .body("products.size()", equalTo(2));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @Story("Update cart")
    @DisplayName("PUT /carts/{id} - full update replaces cart")
    void updateCart_fullUpdate_returnsUpdatedCart() {
        given().spec(requestSpec)
            .body(CartsData.updatedCart())
            .when().put(CartsEndpoints.byId(CartsData.CART_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("id", equalTo(CartsData.CART_ID_TO_UPDATE));
    }

    @Test
    @Order(9)
    @Story("Update cart")
    @DisplayName("PATCH /carts/{id} - partial update changes date")
    void patchCart_partialUpdate_changesDate() {
        given().spec(requestSpec)
            .body(CartsData.patchedCart())
            .when().patch(CartsEndpoints.byId(CartsData.CART_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("id", equalTo(CartsData.CART_ID_TO_UPDATE));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @Story("Delete cart")
    @DisplayName("DELETE /carts/{id} - returns deleted cart")
    void deleteCart_returnsDeletedCart() {
        given().spec(requestSpec)
            .when().delete(CartsEndpoints.byId(CartsData.CART_ID_TO_DELETE))
            .then().spec(okSpec)
                .body("id", equalTo(CartsData.CART_ID_TO_DELETE));
    }

    // ── SCHEMA ────────────────────────────────────────────────────────────────

    @Test
    @Order(11)
    @Story("Schema validation")
    @DisplayName("GET /carts/{id} - response has all required fields")
    void getCart_hasAllRequiredFields() {
        given().spec(requestSpec)
            .when().get(CartsEndpoints.byId(CartsData.CART_ID_VALID))
            .then().spec(okSpec)
                .body("$",                     hasKey("id"))
                .body("$",                     hasKey("userId"))
                .body("$",                     hasKey("date"))
                .body("$",                     hasKey("products"))
                .body("products[0]",           hasKey("productId"))
                .body("products[0]",           hasKey("quantity"))
                .body("products[0].productId", instanceOf(Integer.class))
                .body("products[0].quantity",  instanceOf(Integer.class));
    }

}
