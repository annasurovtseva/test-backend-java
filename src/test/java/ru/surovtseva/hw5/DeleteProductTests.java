package ru.surovtseva.hw5;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import ru.surovtseva.hw5.dto.positive.Product;
import ru.surovtseva.hw5.enums.CategoryType;
import ru.surovtseva.hw5.services.ProductService;
import ru.surovtseva.hw5.utils.RetrofitUtils;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Удаление продукта")
public class DeleteProductTests {
    static ProductService productService;
    Integer productID;
    Product product;
    int fakeID;
    Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @Step("Создание продукта перед тестом")
    @SneakyThrows
    @BeforeEach
    void setUp() {
        product = new Product()
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
                .withPrice(Math.floor(Math.random() * 1000 + 1))
                .withTitle(faker.witcher().monster());

        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();

        fakeID = (int)(Math.random() * 3000 + 3000);
    }

    @Step("Тест: удаление продукта")
    @SneakyThrows
    @DisplayName("Удаление продукта")
    @Test
    void deleteProductPositiveTest() {
        Response<ResponseBody> response =
                productService.deleteProduct(productID).execute();

        assertThat(response.code()).isEqualTo(200);
    }

    //Fail Test
    @Step("Тест: удаление продукта по невалидному ID")
    @SneakyThrows
    @DisplayName("Удаление продукта по невалидному ID")
    @Test
    void deleteProductFakeIDNegativeTest() {

        Response<ResponseBody> response =
                productService.deleteProduct(fakeID).execute();

        Consumer<Integer> code200 = code -> assertThat(response.code()).isEqualTo(200);
        Consumer<Integer> code204 = code -> assertThat(response.code()).isEqualTo(204);

        assertThat(response.code()).as("Expect code 200 or 204").satisfiesAnyOf(code200,code204);

    }
}
