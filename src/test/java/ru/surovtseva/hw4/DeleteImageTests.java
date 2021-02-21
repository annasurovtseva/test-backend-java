package ru.surovtseva.hw4;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw4.common.BaseTest;
import ru.surovtseva.hw4.dto.positive.DelUpdateImageResponse;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.surovtseva.hw4.steps.CommonRequests.uploadCommonImage;

@Feature("Удаление изображения")
public class DeleteImageTests extends BaseTest {
    String imageDeleteHash;


    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        PostImageResponse responseBody = uploadCommonImage(requestAuthCommonSpec, FILE_PATH);
        imageDeleteHash = responseBody.getData().getDeletehash();
    }

    @Step("Тест: Авторизированное удаление изображения")
    @DisplayName("(+)Авторизированное удаление изображения")
    @Test
    void DeleteImageAuthTests(){
        DelUpdateImageResponse responseBody = given()
                .spec(requestAuthCommonSpec)
                .when()
                .delete(Endpoints.DELETE_IMAGE_AUTH_REQUEST, username, imageDeleteHash)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(DelUpdateImageResponse.class);

        assertThat(responseBody.getData(), is(true));
    }

    @Step("Тест: Неавторизированное удаление изображения")
    @DisplayName("(+)Неавторизированное удаление изображения")
    @Test
    void DeleteImageUnAuthTests(){
        DelUpdateImageResponse responseBody = given()
                .spec(requestUnauthCommonSpec)
                .when()
                .delete(Endpoints.DELETE_IMAGE_UNAUTH_REQUEST, imageDeleteHash)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(DelUpdateImageResponse.class);

        assertThat(responseBody.getData(), is(true));
    }
}
