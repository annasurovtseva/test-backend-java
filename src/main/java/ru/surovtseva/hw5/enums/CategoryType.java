package ru.surovtseva.hw5.enums;

import lombok.Getter;

public enum CategoryType {
    FOOD(1,"Food"),
    ELECTRONIC(2,"Electronic");

    @Getter
    private final Integer id;
    @Getter
    private final String title;

    CategoryType(Integer id, String title) {
        this.id = id;
        this.title = title;
    }
}
