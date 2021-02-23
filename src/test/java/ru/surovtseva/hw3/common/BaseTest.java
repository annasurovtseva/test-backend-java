package ru.surovtseva.hw3.common;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public abstract class BaseTest {
    protected static Properties properties = new Properties();
    protected static String token;
    protected static String username;
    protected static String clientId;
    protected static Map<String,String> headersAuth = new HashMap<>();
    protected static Map<String,String> headersUnAuth = new HashMap<>();
    protected static int accountID;
    protected static String FILE_URL;
    protected static String FILE_NAME;
    protected static String FILE_TITLE;
    protected static String MIN_FILE_NAME;
    protected static String MIN_FILE_TITLE;
    protected static String MAX_FILE_NAME;
    protected static String MAX_FILE_TITLE;
    protected static String BIG_FILE_URL;
    protected static String BIG_FILE_NAME;
    protected static String WRONG_FILE_NAME;
    protected static String ERROR_SIZE;
    protected static String ERROR_TYPE;
    protected static String FILE_FOR_GET_URL;

    @BeforeAll
    static void beforeAll() {
        loadProperties();

        headersAuth.put("Authorization",token);
        headersUnAuth.put("Authorization",clientId);
        RestAssured.baseURI = properties.getProperty("base.url");

        RestAssured.filters(new AllureRestAssured());

        accountID = getAccountId();
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
        FILE_NAME = properties.getProperty("file.name");
        FILE_TITLE = properties.getProperty("file.title");
        MIN_FILE_NAME = properties.getProperty("min.file.name");
        MAX_FILE_NAME = properties.getProperty("max.file.name");
        MAX_FILE_TITLE = properties.getProperty("max.file.title");
        MIN_FILE_TITLE = properties.getProperty("min.file.title");
        BIG_FILE_URL = properties.getProperty("big.file.url");
        BIG_FILE_NAME = properties.getProperty("big.file.name");
        WRONG_FILE_NAME = properties.getProperty("wrong.file.name");
        ERROR_SIZE = properties.getProperty("error.size");
        ERROR_TYPE = properties.getProperty("error.type");
        FILE_FOR_GET_URL = properties.getProperty("file.for.get.url");
    }

    static int getAccountId(){
        return given()
                .log()
                .method()
                .headers(headersAuth)
                .when()
                .get("account/{username}",username)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getInt("data.id");
     }
}
