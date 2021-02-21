package ru.surovtseva.hw4.steps;

import io.qameta.allure.Step;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import ru.surovtseva.hw4.Endpoints;
import ru.surovtseva.hw4.dto.positive.*;
import ru.surovtseva.hw4.utils.*;

import static io.restassured.RestAssured.given;

@UtilityClass
public class CommonRequests {

    @Step("Загрузка изображения")
    public static PostImageResponse uploadCommonImage(RequestSpecification spec, String fileName) {
        RequestSpecification multiPart = spec
                .multiPart(
                        new MultiPartSpecBuilder(FileEncodingUtils.getFileContent(fileName))
                                .controlName("image")
                                .build());
        return given()
                .spec(multiPart)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageResponse.class);
    }

    @Step("Получение account ID")
    public static GetAccountResponse getAccountData (RequestSpecification spec, String username) {
        return given()
                .spec(spec)
                .when()
                .get(Endpoints.GET_ACCOUNT_REQUEST, username)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetAccountResponse.class);
    }
}