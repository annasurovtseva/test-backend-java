package ru.surovtseva.hw3;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw3.common.BaseTest;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Загрузка изображения")
public class UploadImageTests extends BaseTest {

    String encodedImage;
    String encodeOversizeImage;
    String imageDeleteHash;

    @Step("Подготовк изображений перед тестом")
    @BeforeEach
    void setUp() {
        byte[] fileContent = getFileContentInBase64(getSendingFile(FILE_NAME));
        encodedImage = Base64.getEncoder().encodeToString(fileContent);
        byte[] bigfileContent = getFileContentInBase64(getSendingFile(BIG_FILE_NAME));
        encodeOversizeImage = Base64.getEncoder().encodeToString(bigfileContent);
    }

    @Step("Тест")
    @DisplayName("(+)Авторизированная загрузка минимально возможного файла")
    @Test
    void UploadMinImageAuthPositiveTest() {
        imageDeleteHash = given()
                .log()
                .all()
                .headers(headersAuth)
                .multiPart("image",getSendingFile(MIN_FILE_NAME))
                .multiPart("title",MIN_FILE_TITLE)
                .expect()
                .body("success", is(true))
                .body("data.id",is(notNullValue()))
                .body("data.title", equalTo(MIN_FILE_TITLE))
                .body("data.account_id", equalTo(accountID))
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
    @DisplayName("(+)Авторизированная загрузка максимально возможного файла")
    @Test
    void UploadMaxImageAuthPositiveTest() {
        imageDeleteHash = given()
                .log()
                .all()
                .headers(headersAuth)
                .multiPart("image",getSendingFile(MAX_FILE_NAME))
                .multiPart("title",MAX_FILE_TITLE)
                .expect()
                .body("success", is(true))
                .body("data.id",is(notNullValue()))
                .body("data.title", equalTo(MAX_FILE_TITLE))
                .body("data.account_id", equalTo(accountID))
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
    @DisplayName("(+)Авторизированная загрузка файла via Base64")
    @Test
    void UploadImageBase64AuthPositiveTest() {
        imageDeleteHash = given()
                .log()
                .all()
                .headers(headersAuth)
                .multiPart("image",encodedImage)
                .multiPart("title",FILE_TITLE)
                .multiPart("name",FILE_NAME)
                .expect()
                .body("success", is(true))
                .body("data.id",is(notNullValue()))
                .body("data.name", equalTo(FILE_NAME))
                .body("data.title", equalTo(FILE_TITLE))
                .body("data.account_id", equalTo(accountID))
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
    @DisplayName("(+)Авторизированная загрузка картинки via Url")
    @Test
    void UploadImageUrlAuthPositiveTest() {
        imageDeleteHash = given()
                .log()
                .method()
                .headers(headersAuth)
                .multiPart("image", FILE_URL)
                .multiPart("title",FILE_TITLE)
                .expect()
                .body("success", is(true))
                .body("data.id",is(notNullValue()))
                .body("data.title", equalTo(FILE_TITLE))
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
    @DisplayName("(+)Неавторизированная загрузка файла")
    @Test
    void UploadImageUnAuthPositiveTest() {
        imageDeleteHash = given()
                .log()
                .all()
                .headers(headersUnAuth)
                .multiPart("image",getSendingFile(FILE_NAME))
                .multiPart("title",FILE_TITLE)
                .multiPart("name",FILE_NAME)
                .expect()
                .body("success", is(true))
                .body("data.id",is(notNullValue()))
                .body("data.title", equalTo(FILE_TITLE))
                .body("data.name", equalTo(FILE_NAME))
                .body("data.account_id", equalTo(0))
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
    @DisplayName("(-)Авторизированная загрузка файла больше 10Мб via URL")
    @Test
    void UploadImageOver10UrlAuthNegativeTest() {
        imageDeleteHash = given()
                .log()
                .uri()
                .log()
                .method()
                .headers(headersAuth)
                .multiPart("image", BIG_FILE_URL)
                .expect()
                .body("success", is(false))
                .body("data.error", equalTo("File is over the size limit"))
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(400)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка файла больше 10Мб")
    @Test
    void UploadImageOver10AuthNegativeTest() {
        imageDeleteHash = given()
                .log()
                .uri()
                .log()
                .method()
                .headers(headersAuth)
                .multiPart("image",getSendingFile(BIG_FILE_NAME))
                .expect()
                .body("success", is(false))
                .body("data.error", equalTo(ERROR_SIZE))
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(400)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка Фйла больше 10Мб via Base64")
    @Test
    void UploadImageOver10Base64AuthNegativeTest() {
        imageDeleteHash = given()
                .log()
                .uri()
                .log()
                .method()
                .headers(headersAuth)
                .multiPart("image", encodeOversizeImage)
                .expect()
                .body("success", is(false))
                .body("data.error", equalTo("File is over the size limit"))
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(400)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Step("Тест")
    @DisplayName("(-)Авторизированная загрузка файла невалидного формата")
    @Test
    void UploadImageWrongFormatAuthNegativeTest() {
        imageDeleteHash = given()
                .log()
                .uri()
                .log()
                .method()
                .headers(headersAuth)
                .multiPart("image",getSendingFile(WRONG_FILE_NAME))
                .expect()
                .body("success", is(false))
                .body("data.error.message", containsString(ERROR_TYPE))
                .when()
                .post("image")
                .prettyPeek()
                .then()
                .statusCode(400)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Step("Удаление изображения после теста")
    @AfterEach
    void tearDown() {
        if (imageDeleteHash!=null){
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

    private byte[] getFileContentInBase64(File inputFile) {
        byte[] fileContent = new byte[0];
        try {
            fileContent =   FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private File getSendingFile(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }
}
