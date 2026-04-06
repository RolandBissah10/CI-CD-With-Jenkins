package com.qalab.base;

import com.qalab.data.CommonData;
import com.qalab.utils.SpecFactory;
import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all test classes.
 * Sets RestAssured.baseURI once per class (BeforeAll) so it is never null,
 * then builds per-test specs in BeforeEach.
 */
@Epic("FakeStore API Tests")
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected RequestSpecification  requestSpec;
    protected RequestSpecification  requestSpecNoEncode;
    protected ResponseSpecification okSpec;

    @BeforeAll
    static void initBaseUri() {
        RestAssured.baseURI = CommonData.resolveBaseUrl();
    }

    @BeforeEach
    void setUp() {
        requestSpec         = SpecFactory.requestSpec();
        requestSpecNoEncode = SpecFactory.requestSpecNoEncode();
        okSpec              = SpecFactory.okSpec();
    }
}