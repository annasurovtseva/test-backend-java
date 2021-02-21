package ru.surovtseva.hw4;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw4.common.BaseTest;
import ru.surovtseva.hw4.dto.positive.DelUpdateImageResponse;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.surovtseva.hw4.steps.CommonRequests.uploadCommonImage;

@Feature("Обновление изображения")
public class UpdateImageTests extends BaseTest {

    String imageDeleteHash;
    String imageHash;
    RequestSpecification requestUpdatespec;
    Faker faker = new Faker();

    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        PostImageResponse responseBody = uploadCommonImage(requestAuthCommonSpec, FILE_PATH);

        imageDeleteHash = responseBody.getData().getDeletehash();
        imageHash = responseBody.getData().getId();

        requestUpdatespec = requestAuthCommonSpec
                .formParam("title",faker.gameOfThrones().character())
                .formParam("description",faker.gameOfThrones().dragon());
    }

    @Step("Тест: Авторизированное обновление изображения")
    @DisplayName("(+)Авторизированное обновление изображения")
    @Test
    void UpdateImageAuthPositiveTest() {
        DelUpdateImageResponse responseBody = given()
                .spec(requestUpdatespec)
                .when()
                .post(Endpoints.GET_UPD_IMAGE_REQUEST, imageHash)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(DelUpdateImageResponse.class);

        assertThat(responseBody.getData(), is(true));
    }

    @Step("Удаление изображения после теста")
    @AfterEach
    void tearDown() {
        given()
                .spec(requestAuthCommonSpec)
                .when()
                .delete(Endpoints.DELETE_IMAGE_AUTH_REQUEST, username, imageDeleteHash)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec);
    }
}
