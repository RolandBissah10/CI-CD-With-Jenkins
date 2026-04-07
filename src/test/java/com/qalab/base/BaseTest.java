package com.qalab.base;
// Package for base classes shared by all API test classes

import com.qalab.data.CommonData;
// Imports CommonData class which contains base URL and other constants
import com.qalab.utils.SpecFactory;
// Imports SpecFactory to create reusable request and response specifications
import io.qameta.allure.Epic;
// Allure annotation to group tests under an Epic in the report
import io.restassured.RestAssured;
// RestAssured class provides core API testing functionality
import io.restassured.specification.RequestSpecification;
// Interface for building HTTP request specifications
import io.restassured.specification.ResponseSpecification;
// Interface for building HTTP response expectations
import org.junit.jupiter.api.BeforeAll;
// JUnit annotation to run method once before all tests in the class
import org.junit.jupiter.api.BeforeEach;
// JUnit annotation to run method before each test method
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Logging classes for debug/info messages


@Epic("FakeStore API Tests")
// Groups all tests in this class under the "FakeStore API Tests" Epic in Allure reports
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    // Logger instance for each subclass (logs class name automatically)

    protected RequestSpecification requestSpec;
    // Standard request specification for most API calls

    protected RequestSpecification requestSpecNoEncode;
    // Request specification without URL encoding (used when parameters need exact encoding)

    protected ResponseSpecification okSpec;
    // Standard response specification for asserting successful responses (e.g., status 200)

    @BeforeAll
    static void initBaseUri() {
        // Set the base URI for all RestAssured requests before any test runs
        RestAssured.baseURI = CommonData.resolveBaseUrl();
        // resolveBaseUrl() returns either the system property "BASE_URL" or the default BASE_URL
    }

    @BeforeEach
    void setUp() {
        // Build standard request specifications before each test
        requestSpec = SpecFactory.requestSpec();
        // Creates a default request specification (headers, content type, logging, etc.)

        requestSpecNoEncode = SpecFactory.requestSpecNoEncode();
        // Creates a request specification without URL encoding for special cases

        okSpec = SpecFactory.okSpec();
        // Creates a default response specification to assert success responses
    }
}