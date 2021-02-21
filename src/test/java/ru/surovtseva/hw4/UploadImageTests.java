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
import ru.surovtseva.hw4.dto.negative.ErrorMessageResponse;
import ru.surovtseva.hw4.dto.negative.PostImageNegativeResponse;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.surovtseva.hw4.steps.CommonRequests.getAccountData;
import static ru.surovtseva.hw4.utils.CreateSpecUtils.*;


@Feature("Загрузка изображения")
public class UploadImageTests extends BaseTest {

    String imageDeleteHash;
    int accountID;
    RequestSpecification requestBase64AuthSpec;
    RequestSpecification requestMinImageAuthSpec;
    RequestSpecification requestMaxImageAuthSpec;
    RequestSpecification requestUrlAuthSpec;
    RequestSpecification requestUrlOverSizeAuthSpec;
    RequestSpecification requestImageUnauthSpec;
    RequestSpecification requestImageOverSizeAuthSpec;
    RequestSpecification requestBase64OverSizeAuthSpec;
    RequestSpecification requestImageWrongFormatAuthSpec;
    Faker faker = new Faker();
    String imageName;
    String imageTitle;



    @Step("Подготовк изображений перед тестом")
    @BeforeEach
    void setUp() {
        accountID = getAccountData(requestAuthCommonSpec,username).getData().getId();
        imageTitle = faker.twinPeaks().character();
        imageName = faker.twinPeaks().character();

        requestBase64AuthSpec = requestAuthCommonSpec.multiPart(createMultiPartSpec(FILE_PATH))
                .formParam("title",imageTitle)
                .formParam("name",imageName);

        requestBase64OverSizeAuthSpec = requestAuthCommonSpec.multiPart(createMultiPartSpec(BIG_FILE_PATH))
                .formParam("title",imageTitle)
                .formParam("name",imageName);


        requestMinImageAuthSpec = createUploadImageSpec(token,MIN_FILE_PATH,imageName,imageTitle);
        requestMaxImageAuthSpec = createUploadImageSpec(token,MAX_FILE_PATH,imageName,imageTitle);
        requestUrlAuthSpec = createUploadUrlSpec(token,FILE_URL,imageName,imageTitle);
        requestUrlOverSizeAuthSpec = createUploadUrlSpec(token,BIG_FILE_URL,imageName,imageTitle);
        requestImageUnauthSpec = createUploadImageSpec(clientId,FILE_PATH,imageName,imageTitle);
        requestImageOverSizeAuthSpec = createUploadImageSpec(token,BIG_FILE_PATH,imageName,imageTitle);
        requestImageWrongFormatAuthSpec = createUploadImageSpec(token,WRONG_FILE_PATH,imageName,imageTitle);


    }

    @Step("Тест")
    @DisplayName("(+)Авторизированная загрузка минимально возможного файла")
    @Test
    void UploadMinImageAuthPositiveTest() {
        PostImageResponse responseBody = given()
                .spec(requestMinImageAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(PostImageResponse.class);

        assertThat(responseBody.getData().getAccountId(), equalTo(accountID));
        assertThat(responseBody.getData().getId(), is(notNullValue()));
        assertThat(responseBody.getData().getDeletehash(), is(notNullValue()));
        assertThat(responseBody.getData().getTitle(), equalTo(imageTitle));

        imageDeleteHash = responseBody.getData().getDeletehash();

    }

    @Step("Тест")
    @DisplayName("(+)Авторизированная загрузка максимально возможного файла")
    @Test
    void UploadMaxImageAuthPositiveTest() {
        PostImageResponse responseBody = given()
                .spec(requestMaxImageAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(PostImageResponse.class);

        assertThat(responseBody.getData().getAccountId(), equalTo(accountID));
        assertThat(responseBody.getData().getId(), is(notNullValue()));
        assertThat(responseBody.getData().getDeletehash(), is(notNullValue()));
        assertThat(responseBody.getData().getTitle(), equalTo(imageTitle));

        imageDeleteHash = responseBody.getData().getDeletehash();

    }

    @Step("Тест")
    @DisplayName("(+)Авторизированная загрузка файла via Base64")
    @Test
    void UploadImageBase64AuthPositiveTest() {

        PostImageResponse responseBody = given()
                .spec(requestBase64AuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(PostImageResponse.class);

        assertThat(responseBody.getData().getAccountId(), equalTo(accountID));
        assertThat(responseBody.getData().getId(), is(notNullValue()));
        assertThat(responseBody.getData().getDeletehash(), is(notNullValue()));
        assertThat(responseBody.getData().getTitle(), equalTo(imageTitle));
        assertThat(responseBody.getData().getName(), equalTo(imageName));

        imageDeleteHash = responseBody.getData().getDeletehash();
    }

    @Step("Тест")
    @DisplayName("(+)Авторизированная загрузка картинки via Url")
    @Test
    void UploadImageUrlAuthPositiveTest() {
        PostImageResponse responseBody = given()
                .spec(requestUrlAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(PostImageResponse.class);

        assertThat(responseBody.getData().getAccountId(), equalTo(accountID));
        assertThat(responseBody.getData().getId(), is(notNullValue()));
        assertThat(responseBody.getData().getDeletehash(), is(notNullValue()));
        assertThat(responseBody.getData().getTitle(), equalTo(imageTitle));
        assertThat(responseBody.getData().getName(), equalTo(imageName));

        imageDeleteHash = responseBody.getData().getDeletehash();
    }

    @Step("Тест")
    @DisplayName("(+)Неавторизированная загрузка файла")
    @Test
    void UploadImageUnAuthPositiveTest() {
        PostImageResponse responseBody = given()
                .spec(requestImageUnauthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonPositiveSpec)
                .extract()
                .body()
                .as(PostImageResponse.class);

        assertThat(responseBody.getData().getAccountId(), equalTo(0));
        assertThat(responseBody.getData().getId(), is(notNullValue()));
        assertThat(responseBody.getData().getDeletehash(), is(notNullValue()));
        assertThat(responseBody.getData().getTitle(), equalTo(imageTitle));
        assertThat(responseBody.getData().getName(), equalTo(imageName));

        imageDeleteHash = responseBody.getData().getDeletehash();
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка файла больше 10Мб via URL")
    @Test
    void UploadImageOver10UrlAuthNegativeTest() {
        PostImageNegativeResponse responseBody = given()
                .spec(requestUrlOverSizeAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonNegativeSpec)
                .extract()
                .body()
                .as(PostImageNegativeResponse.class);

        assertThat(responseBody.getData().getError(), equalTo(ERROR_SIZE));
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка файла больше 10Мб")
    @Test
    void UploadImageOver10AuthNegativeTest() {
        PostImageNegativeResponse responseBody = given()
                .spec(requestImageOverSizeAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonNegativeSpec)
                .extract()
                .body()
                .as(PostImageNegativeResponse.class);

        assertThat(responseBody.getData().getError(), equalTo(ERROR_SIZE));
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка Фйла больше 10Мб via Base64")
    @Test
    void UploadImageOver10Base64AuthNegativeTest() {
        PostImageNegativeResponse responseBody = given()
                .spec(requestBase64OverSizeAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonNegativeSpec)
                .extract()
                .body()
                .as(PostImageNegativeResponse.class);

        assertThat(responseBody.getData().getError(), equalTo(ERROR_SIZE));
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка файла невалидного формата")
    @Test
    void UploadImageWrongFormatAuthNegativeTest() {
        ErrorMessageResponse responseBody = given()
                .spec(requestImageWrongFormatAuthSpec)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(responseCommonNegativeSpec)
                .extract()
                .body()
                .as(ErrorMessageResponse.class);

        assertThat(responseBody.getData().getError().getMessage(), containsString(ERROR_TYPE));
    }

    @Step("Удаление изображения после теста")
    @AfterEach
    void tearDown() {
        if (imageDeleteHash!=null){
            given()
                    .spec(requestAuthCommonSpec)
                    .when()
                    .delete(Endpoints.DELETE_IMAGE_AUTH_REQUEST, username, imageDeleteHash)
                    .prettyPeek()
                    .then()
                    .spec(responseCommonPositiveSpec);
        }
    }
}
