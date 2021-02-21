package ru.surovtseva.hw4.utils;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class CreateSpecUtils {
    public MultiPartSpecification createMultiPartSpec (String file) {
        return new MultiPartSpecBuilder(FileEncodingUtils.getFileContent(file))
                .controlName("image")
                .build();
    }
    public RequestSpecification createUploadImageSpec (String auth, String file, String name, String title) {
        return new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .addHeader("Authorization",auth)
                .addMultiPart("image",new File(file))
                .addMultiPart("title",title)
                .addMultiPart("name",name)
                .build();

    }
    public RequestSpecification createUploadUrlSpec (String auth,String url, String name, String title) {
        return new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .addHeader("Authorization",auth)
                .addMultiPart("image",url)
                .addMultiPart("title",title)
                .addMultiPart("name",name)
                .build();
    }
}
