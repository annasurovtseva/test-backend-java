package ru.surovtseva.hw4.common;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.Matchers.is;


public abstract class BaseTest {
    protected static Properties properties = new Properties();
    protected static String token;
    protected static String username;
    protected static String clientId;
    protected static String FILE_URL;
    protected static String FILE_PATH;
    protected static String MIN_FILE_PATH;
    protected static String MAX_FILE_PATH;
    protected static String MAX_FILE_URL;
    protected static String BIG_FILE_URL;
    protected static String BIG_FILE_PATH;
    protected static String WRONG_FILE_PATH;
    protected static String ERROR_SIZE;
    protected static String ERROR_TYPE;
    protected static ResponseSpecification responseCommonPositiveSpec;
    protected static ResponseSpecification responseCommonNegativeSpec;
    protected static RequestSpecification requestAuthCommonSpec;
    protected static RequestSpecification requestUnauthCommonSpec;


    @BeforeAll
    static void beforeAll() {
        loadProperties();

        RestAssured.baseURI = properties.getProperty("base.url");

        createCommonSpecifications();
    }

    static void loadProperties() {
        try {
            properties.load(new FileInputStream("src/test/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientId = properties.getProperty("client.id");
        username = properties.getProperty("username");
        token = properties.getProperty("token");

        FILE_URL = properties.getProperty("file.url");
        FILE_PATH = properties.getProperty("file.path");
        MIN_FILE_PATH = properties.getProperty("min.file.path");
        MAX_FILE_PATH = properties.getProperty("max.file.path");
        MAX_FILE_URL = properties.getProperty("max.file.url");
        BIG_FILE_URL = properties.getProperty("big.file.url");
        BIG_FILE_PATH = properties.getProperty("big.file.path");
        WRONG_FILE_PATH = properties.getProperty("wrong.file.path");
        ERROR_SIZE = properties.getProperty("error.size");
        ERROR_TYPE = properties.getProperty("error.type");
    }

    static void createCommonSpecifications() {
        responseCommonPositiveSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody("success", is(true))
                .build();

        responseCommonNegativeSpec = new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectBody("success", is(false))
                .build();

        requestAuthCommonSpec = new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addHeader("Authorization",token)
                .addFilter(new AllureRestAssured())
                .build();

        requestUnauthCommonSpec = new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addHeader("Authorization",clientId)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
