package ru.surovtseva.hw5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import ru.surovtseva.hw5.dto.positive.Category;
import ru.surovtseva.hw5.enums.CategoryType;
import ru.surovtseva.hw5.services.CategoryService;
import ru.surovtseva.hw5.utils.RetrofitUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTests {
    static CategoryService categoryService;

    @BeforeAll
    static void beforeAll(){
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @Test
    void getCategoryPositiveTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(CategoryType.FOOD.getId())
                .execute();

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getId()).isEqualTo(1);
        assertThat(response.body().getTitle()).isEqualTo(CategoryType.FOOD.getTitle());
    }
}
