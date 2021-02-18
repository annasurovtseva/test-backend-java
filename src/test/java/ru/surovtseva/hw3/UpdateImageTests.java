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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Обновление изображения")
public class UpdateImageTests extends BaseTest {

    String imageDeleteHash;
    String imageHash;

    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        JsonPath responseBody= given()
                .headers(headersAuth)
                .multiPart("image", FILE_URL)
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
    @DisplayName("(+)Авторизированное обновление изображения")
    @Test
    void UpdateImageAuthPositiveTest() {
        given()
                .log()
                .all()
                .headers(headersAuth)
                .multiPart("title",FILE_TITLE)
                .multiPart("description","Funny octocat")
                .expect()
                .body("success", is(true))
                .body("data", is(true))
                .when()
                .post("image/{imageHash}", imageHash)
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
