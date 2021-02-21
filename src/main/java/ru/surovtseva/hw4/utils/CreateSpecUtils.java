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
    public RequestSpecification createUploadImageBase64Spec(String file, String auth, String name, String title) {
        MultiPartSpecification spec =  new MultiPartSpecBuilder(FileEncodingUtils.getFileContent(file))
                .controlName("image")
                .build();
        return  new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .addHeader("Authorization",auth)
                .addMultiPart(spec)
                .addFormParam("title",title)
                .addFormParam("name",name)
                .build();
    }
    public RequestSpecification createUploadImageSpec (String auth, File file, String name, String title) {
        return new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .addHeader("Authorization",auth)
                .addMultiPart("image",file)
                .addMultiPart("title",title)
                .addMultiPart("name",name)
                .build();

    }
    public RequestSpecification createUploadImageSpec(String auth, String url, String name, String title) {
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
