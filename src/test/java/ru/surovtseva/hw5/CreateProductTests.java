package ru.surovtseva.hw5;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Converter;
import retrofit2.Response;
import ru.surovtseva.hw5.dto.negative.ErrorBody;
import ru.surovtseva.hw5.dto.positive.Product;
import ru.surovtseva.hw5.enums.CategoryType;
import ru.surovtseva.hw5.services.ProductService;
import ru.surovtseva.hw5.utils.GenerateDataUtils;
import ru.surovtseva.hw5.utils.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Создание продукта")
public class CreateProductTests {
    static ProductService productService;
    Product product;
    Faker faker = new Faker();
    Integer productID;
    Integer productPrice;
    String productTitle;
    long priceOverInt;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @Step("Подготовка продукта перед тестом")
    @BeforeEach
    void setUp() {
        productPrice = (int)(Math.random() * 1000 + 1);
        productTitle = faker.witcher().monster();
        priceOverInt = Integer.MAX_VALUE+ Math.round(Math.random() * 10 + 1);

        product = new Product()
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle());
    }

    @Step("Тест: Создание продукта: все поля заполнены корректно")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: все поля заполнены корректно.")
    @Test
    void createNewProductPositiveTest(){
        Response<Product> response = productService
                .createProduct(product
                .withTitle(productTitle)
                .withPrice(productPrice)).execute();

        productID = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(productTitle);
        assertThat(response.body().getPrice()).isEqualTo(productPrice);
    }

    @Step("Тест: Создание продукта: заполнены не все поля")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: заполнены не все поля")
    @Test
    void createNewProductNotAllFieldsPositiveTest(){
        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo(0);
    }

    @Step("Тест: Создание продукта: значение Double в поле Price")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: в поле Цена передается значение Double")
    @Test
    void createNewProductDoublePricePositiveTest(){
        double productPrice = (Math.random() * 1000 + 1);

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPrice)).execute();

        productID = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo((int)(Math.floor(productPrice)));
    }

    @Step("Тест: Создание продукта: в поле Цена передается строка с цифрами")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: в поле Цена передается строка с цифрами")
    @Test
    void createNewProductStringNumPricePositiveTest(){
        String productPriceString = String.valueOf(productPrice);

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPriceString)).execute();

        productID = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo(Integer.parseInt(productPriceString));
    }

    @Step("Тест: Создание продукта: значение больше Int в поле Price")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: в поле Цена передается значение больше Int")
    @Test
    void createNewProductPriceOverIntNegativeTest(){

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(priceOverInt)).execute();

        assertThat(response.code()).isEqualTo(400);
    }

    @Step("Тест: Создание продукта: в поле Цена передается строка с буквами")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: в поле Цена передается строка с буквами")
    @Test
    void createNewProductStringCharPriceNegativeTest(){
        String productPrice = GenerateDataUtils.generateString(4);

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPrice)).execute();

        assertThat(response.code()).isEqualTo(400);
    }

    @Step("Тест: Создание продукта: заполнено поле ID")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: заполнено поле ID")
    @Test
    void createNewProductWithIdNegativeTest(){
        Response<Product> response = productService
                .createProduct(product
                        .withId((int)(Math.random() * 5000 + 5000))).execute();

        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");
        }
    }

    //Fail Tests

    @Step("Тест: Создание продукта: строка больше 255 в поле Title")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: в поле Наименоване передается строка больше 255 символов")
    @Test
    void createNewProductTooLongStringToTitleNegativeTest(){
        Response<Product> response = productService
                .createProduct(product
                        .withTitle(GenerateDataUtils.generateString(256))).execute();

        assertThat(response.code()).as("Ожидается 400 Bad Bad Request").isEqualTo(400);
    }

    @Step("Тест: Создание продукта: пустое значение в поле Category")
    @SneakyThrows
    @DisplayName("(-) Создание продукта:в поле Категоря передается пустое значение")
    @Test
    void createNewProductEmptyCategoryNegativeTest(){
        Response<Product> response = productService
                .createProduct(new Product()
                .withTitle(productTitle)).execute();

        assertThat(response.code()).as("Ожидается 400 Bad Bad Request. Category don't be null").isEqualTo(400);
    }

    @Step("Тест: Создание продукта: отрицательное значение в поле Price")
    @SneakyThrows
    @DisplayName("(-) Создание продукта:в поле Цена передается отрицательное значение")
    @Test
    void createNewProductNegativeValueToPriceNegativeTest(){
        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPrice * -1)).execute();

        productID = response.body().getId();

        assertThat(response.code()).as("Ожидается 400 Bad Bad Request. Price don't be negative").isEqualTo(400);
    }

    @Step("Удаление продукта после теста")
    @AfterEach
    void tearDown() {
        if (productID!=null)
            try {
                retrofit2.Response<ResponseBody> response =
                        productService.deleteProduct(productID)
                                .execute();
                assertThat(response.isSuccessful()).isTrue();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
