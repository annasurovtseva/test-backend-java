package ru.surovtseva.hw4;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.surovtseva.hw4.common.BaseTest;
import ru.surovtseva.hw4.dto.positive.PostImageResponse;
import ru.surovtseva.hw4.steps.IntegrationRequest;

import static ru.surovtseva.hw4.steps.CommonRequests.getAccountData;
import static ru.surovtseva.hw4.steps.CommonRequests.uploadCommonImage;

@Feature("Интеграционный тест: Загрузка-Обновление-Получение-Удаление изображения")
public class IntegrationTests extends BaseTest {

    String imageDeleteHash;
    String imageHash;
    String imageTitle;
    String imageDescription;
    RequestSpecification requestUpdatespec;
    int accountID;
    Faker faker = new Faker();

    @Step("Подготовка данных")
    @BeforeEach
    void setUp() {
        accountID = getAccountData(requestAuthCommonSpec,username).getData().getId();
        PostImageResponse responseBody = uploadCommonImage(requestAuthCommonSpec, FILE_PATH);

        imageDeleteHash = responseBody.getData().getDeletehash();
        imageHash = responseBody.getData().getId();
        imageTitle = faker.gameOfThrones().character();
        imageDescription = faker.gameOfThrones().dragon();


        requestUpdatespec = requestAuthCommonSpec
                .formParam("title",imageTitle)
                .formParam("description",imageDescription);
    }

    @DisplayName("Обновление - Получение - Удаление изображения")
    @Test
    void UpdateGetDelImageTest(){
        new IntegrationRequest()
                .updateImage(requestUpdatespec,imageHash,responseCommonPositiveSpec)
                .getImage(requestAuthCommonSpec,imageHash,responseCommonPositiveSpec,
                        accountID,imageTitle,imageDescription)
                .deleteImage(requestAuthCommonSpec,username,imageDeleteHash,responseCommonPositiveSpec);

    }
}
