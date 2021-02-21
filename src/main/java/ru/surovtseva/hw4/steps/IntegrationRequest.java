package ru.surovtseva.hw4.steps;

import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.surovtseva.hw4.Endpoints;
import ru.surovtseva.hw4.dto.positive.DelUpdateImageResponse;
import ru.surovtseva.hw4.dto.positive.GetImageResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class IntegrationRequest {

    @Step("Обновление загруженного изображения")
    public IntegrationRequest updateImage(RequestSpecification reqSpec, String imageHash, ResponseSpecification respSpec) {
        DelUpdateImageResponse responseBody = given()
                .spec(reqSpec)
                .when()
                .post(Endpoints.GET_UPD_IMAGE_REQUEST, imageHash)
                .prettyPeek()
                .then()
                .spec(respSpec)
                .extract()
                .body()
                .as(DelUpdateImageResponse.class);

        assertThat(responseBody.getData(), is(true));
        return this;
    }

    @Step("Получение загруженного изображения, проверка обновлений")
    public IntegrationRequest getImage (RequestSpecification reqSpec, String imageHash, ResponseSpecification respSpec, int accID, String title, String description) {
        GetImageResponse responseBody = given()
                .spec(reqSpec)
                .when()
                .get(Endpoints.GET_UPD_IMAGE_REQUEST,imageHash)
                .prettyPeek()
                .then()
                .spec(respSpec)
                .extract()
                .body()
                .as(GetImageResponse.class);


        assertThat(responseBody.getData().getAccountId(), equalTo(accID));
        assertThat(responseBody.getData().getTitle(), equalTo(title));
        assertThat(responseBody.getData().getDescription(), equalTo(description));

        return this;
    }

    @Step("Удаление загруженного изображения")
    public IntegrationRequest deleteImage (RequestSpecification reqSpec, String username, String imageDeleteHash, ResponseSpecification respSpec) {
        DelUpdateImageResponse responseBody = given()
                .spec(reqSpec)
                .when()
                .delete(Endpoints.DELETE_IMAGE_AUTH_REQUEST, username, imageDeleteHash)
                .prettyPeek()
                .then()
                .spec(respSpec)
                .extract()
                .body()
                .as(DelUpdateImageResponse.class);

        assertThat(responseBody.getData(), is(true));

        return this;
    }

}
