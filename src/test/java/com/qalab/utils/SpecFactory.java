package com.qalab.utils;

import com.qalab.data.CommonData;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Shared RestAssured request and response specifications.
 * BASE_URL can be overridden at runtime via -DBASE_URL=... (e.g. from Jenkins).
 */
public class SpecFactory {

    private SpecFactory() {}

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(CommonData.resolveBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification okSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
