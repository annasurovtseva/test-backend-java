package ru.surovtseva.hw6;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import ru.surovtseva.hw6.db.dao.ProductsMapper;
import ru.surovtseva.hw6.dto.positive.Product;
import ru.surovtseva.hw6.enums.CategoryType;
import ru.surovtseva.hw6.services.ProductService;
import ru.surovtseva.hw6.utils.DbUtils;
import ru.surovtseva.hw6.utils.RetrofitUtils;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Удаление продукта")
public class DeleteProductTests {
    static ProductService productService;
    static ProductsMapper productsMapper;
    Integer productID;
    Product product;
    int fakeID;
    Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() throws IOException {
        productsMapper = DbUtils.getProductsMapper();
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

        fakeID = (int)(Math.random() * 10000 + 10000);
    }

    @Step("Тест: удаление продукта")
    @SneakyThrows
    @DisplayName("Удаление продукта")
    @Test
    void deleteProductPositiveTest() {
        Response<ResponseBody> response =
                productService.deleteProduct(productID).execute();

        //Проверка ответа сервера
        Consumer<Integer> code200 = code -> assertThat(response.code()).isEqualTo(200);
        Consumer<Integer> code204 = code -> assertThat(response.code()).isEqualTo(204);
        assertThat(response.code()).satisfiesAnyOf(code200,code204);

        //Проверка: запись в базе отсутствует
        assertThat(productsMapper.selectByPrimaryKey((long)productID)).isNull();
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

    @Step("Удаление продукта после теста")
    @AfterEach
    void tearDown() {
        if (productID!=null)
            productsMapper.deleteByPrimaryKey((long)productID);
    }
}
