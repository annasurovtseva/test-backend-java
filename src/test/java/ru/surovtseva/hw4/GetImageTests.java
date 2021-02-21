package ru.surovtseva.hw4;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw4.common.BaseTest;
import ru.surovtseva.hw4.dto.positive.GetImageResponse;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static ru.surovtseva.hw4.steps.CommonRequests.getAccountData;
import static ru.surovtseva.hw4.steps.CommonRequests.uploadCommonImage;

@Feature("Получение изображения")
public class GetImageTests extends BaseTest {

    String imageDeleteHash;
    String imageHash;
    int accountID;

    @Step("Загрузка изображения перед тестом")
    @BeforeEach
    void setUp() {
        PostImageResponse responseBody = uploadCommonImage(requestAuthCommonSpec, FILE_PATH);

        imageDeleteHash = responseBody.getData().getDeletehash();
        imageHash = responseBody.getData().getId();

        accountID = getAccountData(requestAuthCommonSpec,username).getData().getId();
     }

    @Step("Тест: Авторизированное получение изображения")
    @DisplayName("(+) Авторизированное получение изображения")
    @Test
    void GetImagePositiveTest() {
        GetImageResponse responseBody = given()
                .spec(requestAuthCommonSpec)
                .when()
                .get(Endpoints.GET_UPD_IMAGE_REQUEST,imageHash)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(GetImageResponse.class);


        assertThat(responseBody.getData().getAccountId(), equalTo(accountID));
        assertThat(responseBody.getData().getDeletehash(), equalTo(imageDeleteHash));
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
