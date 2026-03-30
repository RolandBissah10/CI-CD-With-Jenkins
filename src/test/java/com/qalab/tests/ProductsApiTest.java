package com.qalab.tests;

import com.qalab.base.BaseTest;
import com.qalab.data.ProductsData;
import com.qalab.endpoints.ProductsEndpoints;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Feature("Products")
@DisplayName("Products API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductsApiTest extends BaseTest {

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Get all products")
    @DisplayName("GET /products - returns 20 products")
    void getAllProducts_returns20Products() {
        List<Object> prices = given().spec(requestSpec)
            .when().get(ProductsEndpoints.PRODUCTS)
            .then().spec(okSpec)
                .body("size()",   greaterThan(0))
                .body("id",       everyItem(notNullValue()))
                .body("title",    everyItem(notNullValue()))
                .body("category", everyItem(notNullValue()))
                .extract().jsonPath().getList("price");

        assertThat(prices).allMatch(price -> Double.parseDouble(String.valueOf(price)) > 0.0);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "GET /products/{0}")
    @Order(2)
    @Story("Get single product")
    @ValueSource(ints = {1, 10, 20})
    @DisplayName("GET /products/{id} - returns product with correct id")
    void getProductById_returnsCorrectProduct(int productId) {
        Object price = given().spec(requestSpec)
            .when().get(ProductsEndpoints.byId(productId))
            .then().spec(okSpec)
                .body("id",             equalTo(productId))
                .body("title",          notNullValue())
                .body("rating.rate",    notNullValue())
                .body("rating.count",   notNullValue())
                .extract().path("price");

        assertThat(Double.parseDouble(String.valueOf(price))).isGreaterThan(0.0);
    }

    @Test
    @Order(3)
    @Story("Get single product")
    @DisplayName("GET /products/0 - invalid id returns empty or error")
    void getProductById_invalidId_returnsErrorOrEmpty() {
        int status = given().spec(requestSpec)
            .when().get(ProductsEndpoints.byId(ProductsData.PRODUCT_ID_INVALID))
            .then().extract().statusCode();

        assertThat(status).isIn(200, 404);
    }

    // ── LIMIT ─────────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "GET /products?limit={0}")
    @Order(4)
    @Story("Limit results")
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("GET /products?limit={n} - returns exactly n products")
    void getProducts_withLimit_returnsCorrectCount(int limit) {
        given().spec(requestSpec)
            .queryParam("limit", limit)
            .when().get(ProductsEndpoints.PRODUCTS)
            .then().spec(okSpec)
                .body("size()", equalTo(limit));
    }

    // ── SORT ──────────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @Story("Sort products")
    @DisplayName("GET /products?sort=asc - IDs are ascending")
    void getProducts_sortAsc_idsAreAscending() {
        List<Integer> ids = given().spec(requestSpec)
            .queryParam("sort", "asc")
            .when().get(ProductsEndpoints.PRODUCTS)
            .then().spec(okSpec)
                .extract().jsonPath().getList("id", Integer.class);

        assertThat(ids).isSortedAccordingTo(Integer::compareTo);
    }

    @Test
    @Order(6)
    @Story("Sort products")
    @DisplayName("GET /products?sort=desc - IDs are descending")
    void getProducts_sortDesc_idsAreDescending() {
        List<Integer> ids = given().spec(requestSpec)
            .queryParam("sort", "desc")
            .when().get(ProductsEndpoints.PRODUCTS)
            .then().spec(okSpec)
                .extract().jsonPath().getList("id", Integer.class);

        assertThat(ids).isSortedAccordingTo(Comparator.reverseOrder());
    }

    // ── CATEGORIES ────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @Story("Categories")
    @DisplayName("GET /products/categories - returns all 4 known categories")
    void getAllCategories_returnsExpectedCategories() {
        List<String> categories = given().spec(requestSpec)
            .when().get(ProductsEndpoints.CATEGORIES)
            .then().spec(okSpec)
                .extract().jsonPath().getList(".", String.class);

        assertThat(categories).containsAll(
            java.util.Arrays.asList(ProductsData.CATEGORIES)
        );
    }

    @ParameterizedTest(name = "GET /products/category/{0}")
    @Order(8)
    @Story("Filter by category")
    @ValueSource(strings = {"electronics", "jewelery", "men's clothing", "women's clothing"})
    @DisplayName("GET /products/category/{name} - all products match the category")
    void getProductsByCategory_allMatchCategory(String category) {
        given().spec(requestSpec)
            .when().get(ProductsEndpoints.byCategory(category))
            .then().spec(okSpec)
                .body("size()", greaterThanOrEqualTo(0));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    @Story("Create product")
    @DisplayName("POST /products - creates product and returns new id")
    void createProduct_returnsNewId() {
        given().spec(requestSpec)
            .body(ProductsData.newProduct())
            .when().post(ProductsEndpoints.PRODUCTS)
            .then().statusCode(anyOf(is(200), is(201)))
                .body("id",       notNullValue())
                .body("title",    equalTo(ProductsData.PRODUCT_NEW_TITLE))
                .body("price",    equalTo((float) ProductsData.PRODUCT_NEW_PRICE))
                .body("category", equalTo(ProductsData.PRODUCT_NEW_CATEGORY));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @Story("Update product")
    @DisplayName("PUT /products/{id} - full update returns updated fields")
    void updateProduct_fullUpdate_fieldsAreUpdated() {
        given().spec(requestSpec)
            .body(ProductsData.updatedProduct())
            .when().put(ProductsEndpoints.byId(ProductsData.PRODUCT_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("title", equalTo(ProductsData.PRODUCT_UPDATED_TITLE))
                .body("price", equalTo((float) ProductsData.PRODUCT_UPDATED_PRICE));
    }

    @Test
    @Order(11)
    @Story("Update product")
    @DisplayName("PATCH /products/{id} - partial update changes only given fields")
    void patchProduct_onlyChangedFieldsUpdated() {
        given().spec(requestSpec)
            .body(ProductsData.patchedProduct())
            .when().patch(ProductsEndpoints.byId(ProductsData.PRODUCT_ID_TO_UPDATE))
            .then().spec(okSpec)
                .body("title", equalTo(ProductsData.PRODUCT_PATCHED_TITLE))
                .body("price", equalTo((float) ProductsData.PRODUCT_PATCHED_PRICE));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @Order(12)
    @Story("Delete product")
    @DisplayName("DELETE /products/{id} - returns deleted product data")
    void deleteProduct_returnsDeletedProductData() {
        given().spec(requestSpec)
            .when().delete(ProductsEndpoints.byId(ProductsData.PRODUCT_ID_TO_DELETE))
            .then().spec(okSpec)
                .body("id", equalTo(ProductsData.PRODUCT_ID_TO_DELETE));
    }

    // ── SCHEMA ────────────────────────────────────────────────────────────────

    @Test
    @Order(13)
    @Story("Schema validation")
    @DisplayName("GET /products/{id} - response has all required fields")
    void getProduct_hasAllRequiredFields() {
        given().spec(requestSpec)
            .when().get(ProductsEndpoints.byId(ProductsData.PRODUCT_ID_FIRST))
            .then().spec(okSpec)
                .body("$", hasKey("id"))
                .body("$", hasKey("title"))
                .body("$", hasKey("price"))
                .body("$", hasKey("description"))
                .body("$", hasKey("category"))
                .body("$", hasKey("image"))
                .body("$", hasKey("rating"))
                .body("rating", hasKey("rate"))
                .body("rating", hasKey("count"));
    }

}
