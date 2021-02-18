package ru.surovtseva.hw3;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw3.common.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Feature("Удаление изображения")
public class DeleteImageTests extends BaseTest {
    String imageDeleteHash;

    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        imageDeleteHash= given()
                .headers(headersAuth)
                .when()
                .multiPart("image", FILE_URL)
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Step("Тест")
    @DisplayName("(+)Авторизированное удаление файла")
    @Test
    void DeleteImageAuthTests(){
        given()
                .log()
                .all()
                .headers(headersAuth)
                .expect()
                .body("success", is(true))
                .body("data", is(true))
                .when()
                .delete("account/{username}/image/{deleteHash}", username, imageDeleteHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Step("Тест")
    @DisplayName("(+)Неавторизированное удаление файла")
    @Test
    void DeleteImageUnAuthTests(){
        given()
                .log()
                .all()
                .headers(headersUnAuth)
                .expect()
                .body("success", is(true))
                .body("data", is(true))
                .when()
                .delete("image/{deleteHash}", imageDeleteHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
