package ru.surovtseva.hw5.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class GenerateDataUtils {

    public String generateString(int length){
        String symbols = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        StringBuilder string = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            string.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return string.toString();
    }
}
