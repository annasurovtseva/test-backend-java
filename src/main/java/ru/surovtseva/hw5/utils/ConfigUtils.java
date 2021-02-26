package ru.surovtseva.hw5.utils;

import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class ConfigUtils {
    Properties properties = new Properties();

    void loadProperties() {
        try {
            properties.load(new FileInputStream("src/test/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBaseUrl() {
        loadProperties();
        return properties.getProperty("base.url");
    }

}
