package ru.surovtseva.hw3;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw3.common.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Получение изображения")
public class GetImageTests extends BaseTest {

    String imageDeleteHash;
    String imageHash;

    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        JsonPath responseBody= given()
                .headers(headersAuth)
                .multiPart("image", FILE_FOR_GET_URL)
                .expect()
                .body("data.id",is(notNullValue()))
                .body("data.deletehash",is(notNullValue()))
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        imageDeleteHash = responseBody.getString("data.deletehash");
        imageHash = responseBody.getString("data.id");
     }

    @Step("Тест")
    @DisplayName("(+) Авторизированное получение изображения")
    @Test
    void GetImagePositiveTest() {
        given()
                .log()
                .method()
                .headers(headersAuth)
                .expect()
                .body("success", is(true))
                .body("data.account_id", equalTo(accountID))
                .body("data.deletehash", equalTo(imageDeleteHash))
                .when()
                .get("image/{imageHash}",imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Step("Удаление изображения после теста")
    @AfterEach
    void tearDown() {

            given()
                    .log()
                    .method()
                    .headers(headersAuth)
                    .when()
                    .delete("account/{username}/image/{deleteHash}", username, imageDeleteHash)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
    }
}
