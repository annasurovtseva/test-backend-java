package ru.surovtseva.hw4;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.surovtseva.hw4.common.BaseTest;
import ru.surovtseva.hw4.dto.negative.ErrorMessageResponse;
import ru.surovtseva.hw4.dto.negative.PostImageNegativeResponse;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;

import java.io.File;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.surovtseva.hw4.steps.CommonRequests.getAccountData;
import static ru.surovtseva.hw4.utils.CreateSpecUtils.createUploadImageBase64Spec;
import static ru.surovtseva.hw4.utils.CreateSpecUtils.createUploadImageSpec;


@Feature("Загрузка изображения")
public class UploadImageTests extends BaseTest {
    static String imageDeleteHash;
    static int accountID;
    static RequestSpecification requestUrlOverSizeAuthSpec;
    static RequestSpecification requestImageUnauthSpec;
    static RequestSpecification requestImageOverSizeAuthSpec;
    static RequestSpecification requestBase64OverSizeAuthSpec;
    static RequestSpecification requestImageWrongFormatAuthSpec;
    static Faker faker = new Faker();
    static String imageName;
    static String imageTitle;

    @Step("Подготовк данных перед тестом")
    @BeforeEach
    void setUp() {
        accountID = getAccountData(requestAuthCommonSpec,username).getData().getId();
        imageTitle = faker.twinPeaks().character();
        imageName = faker.twinPeaks().character();

        requestImageUnauthSpec = createUploadImageSpec(clientId,new File(FILE_PATH),imageName,imageTitle);
        requestBase64OverSizeAuthSpec = createUploadImageBase64Spec(BIG_FILE_PATH,token,imageName,imageTitle);
        requestUrlOverSizeAuthSpec = createUploadImageSpec(token,BIG_FILE_URL,imageName,imageTitle);
        requestImageOverSizeAuthSpec = createUploadImageSpec(token,new File(BIG_FILE_PATH),imageName,imageTitle);
        requestImageWrongFormatAuthSpec = createUploadImageSpec(token,new File(WRONG_FILE_PATH),imageName,imageTitle);
    }

    @Step("Тест: Загрузка изображений разного допустимого размера")
    @DisplayName("(+)Авторизированная загрузка изображений")
    @ParameterizedTest()
    @MethodSource("UploadImagePositiveTestValues")
    void UploadImageAuthPositiveTest(String file) {
        PostImageResponse responseBody = given()
                .spec(createUploadImageSpec(token,new File(file),imageName,imageTitle))
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

    @Step("Тест: Загрузка изображений разного допустимого размера")
    @DisplayName("(+)Авторизированная загрузка изображений via Base64")
    @ParameterizedTest()
    @MethodSource("UploadImagePositiveTestValues")
    void UploadImageBase64AuthPositiveTest(String file) {
        PostImageResponse responseBody = given()
                .spec(createUploadImageBase64Spec(file,token,imageName,imageTitle))
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

    @Step("Тест: Загрузка изображений разного допустимого размера")
    @DisplayName("(+)Авторизированная загрузка изображений via Url")
    @ParameterizedTest
    @MethodSource("UploadImageUrlPositiveTestValues")
    void UploadImageUrlAuthPositiveTest(String url) {
        PostImageResponse responseBody = given()
                .spec(createUploadImageSpec(token,url,imageName,imageTitle))
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

    @Step("Тест: Неавторизированная загрузка изображения")
    @DisplayName("(+)Неавторизированная загрузка изображения")
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

    @Step("Тест: загрузка изображения недопустимого размера")
    @DisplayName("(-)Авторизированная загрузка изображения больше 10Мб via URL")
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

    @Step("Тест: загрузка изображения недопустимого размера")
    @DisplayName("(-)Авторизированная загрузка изображения больше 10Мб")
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

    @Step("Тест: загрузка изображения недопустимого размера")
    @DisplayName("(-)Авторизированная загрузка изображения больше 10Мб via Base64")
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

    @Step("Тест: загрузка изображения недопустимого формата")
    @DisplayName("(-)Авторизированная загрузка изображения невалидного формата")
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

    private static Stream<Arguments> UploadImagePositiveTestValues() {
        return Stream.of(
                Arguments.of(FILE_PATH),
                Arguments.of(MIN_FILE_PATH),
                Arguments.of(MAX_FILE_PATH)
        );
    }

    private static Stream<Arguments> UploadImageUrlPositiveTestValues() {
        return Stream.of(
                Arguments.of(FILE_URL),
                Arguments.of(MAX_FILE_URL)
        );
    }
}
