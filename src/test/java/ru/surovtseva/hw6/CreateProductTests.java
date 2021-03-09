package ru.surovtseva.hw6;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Converter;
import retrofit2.Response;
import ru.surovtseva.hw6.db.dao.ProductsMapper;
import ru.surovtseva.hw6.dto.negative.ErrorBody;
import ru.surovtseva.hw6.dto.positive.Product;
import ru.surovtseva.hw6.enums.CategoryType;
import ru.surovtseva.hw6.services.ProductService;
import ru.surovtseva.hw6.utils.DbUtils;
import ru.surovtseva.hw6.utils.GenerateDataUtils;
import ru.surovtseva.hw6.utils.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Создание продукта")
public class CreateProductTests {
    static ProductService productService;
    static ProductsMapper productsMapper;
    Product product;
    Faker faker = new Faker();
    Integer productID;
    Integer productPrice;
    String productTitle;
    long priceOverInt;

    @BeforeAll
    static void beforeAll() throws IOException {
        productsMapper = DbUtils.getProductsMapper();
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

    @Story("Позитивные")
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

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(productTitle);
        assertThat(response.body().getPrice()).isEqualTo(productPrice);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(productPrice);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isEqualTo(productTitle);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.ELECTRONIC.getId()));
    }

    @Story("Позитивные")
    @Step("Тест: Создание продукта: заполнены не все поля")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: заполнены не все поля")
    @Test
    void createNewProductNotAllFieldsPositiveTest(){
        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo(0);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(0);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isNull();
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.ELECTRONIC.getId()));
    }

    @Story("Позитивные")
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

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo((int)(Math.floor(productPrice)));

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo((int)(Math.floor(productPrice)));
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isNull();
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.ELECTRONIC.getId()));

    }

    @Story("Позитивные")
    @Step("Тест: Создание продукта: в Price передается строка с цифрами")
    @SneakyThrows
    @DisplayName("(+) Создание продукта: в поле Цена передается строка с цифрами")
    @Test
    void createNewProductStringNumPricePositiveTest(){
        String productPriceString = String.valueOf(productPrice);

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPriceString)).execute();

        productID = response.body().getId();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo(Integer.parseInt(productPriceString));

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(Integer.parseInt(productPriceString));
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isNull();
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.ELECTRONIC.getId()));
    }

    @Story("Негативные")
    @Step("Тест: Создание продукта: в Price передается значение больше Int ")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: в поле Цена передается значение больше Int")
    @Test
    void createNewProductPriceOverIntNegativeTest(){

        Response<Product> response = productService
                .createProduct(product
                        .withPrice(priceOverInt)).execute();

        assertThat(response.code()).isEqualTo(400);
    }

    @Story("Негативные")
    @Step("Тест: Создание продукта: в Price передается строка с буквами")
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

    @Story("Негативные")
    @Step("Тест: Создание продукта: заполнено поле ID")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: заполнено поле ID")
    @Test
    void createNewProductWithIdNegativeTest(){
        productID = (int)(Math.random() * 10000 + 10000);
        Response<Product> response = productService
                .createProduct(product
                        .withId(productID)).execute();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");
        }

        //Проверка: запись в базе отсутствует
        assertThat(productsMapper.selectByPrimaryKey((long)productID)).isNull();
    }

    //Fail Tests

    @Story("Дефекты")
    @Step("Тест: Создание продукта: строка больше 255 в поле Title")
    @SneakyThrows
    @DisplayName("(-) Создание продукта: в поле Наименоване передается строка больше 255 символов")
    @Test
    void createNewProductTooLongStringToTitleNegativeTest(){
        Response<Product> response = productService
                .createProduct(product
                        .withTitle(GenerateDataUtils.generateString(256))).execute();

        assertThat(response.code()).as("Expect 400 Bad Bad Request").isEqualTo(400);
    }

    @Story("Дефекты")
    @Step("Тест: Создание продукта: пустое значение в поле сategoryTitle")
    @SneakyThrows
    @DisplayName("(-) Создание продукта:в поле Наименование Категори передается пустое значение")
    @Test
    void createNewProductEmptyCategoryNegativeTest(){
        Response<Product> response = productService
                .createProduct(new Product()
                .withTitle(productTitle)).execute();

        Consumer<Integer> code400 = code -> assertThat(response.code()).isEqualTo(400);
        Consumer<Integer> code201 = code -> assertThat(response.code()).isEqualTo(201);

        assertThat(response.code()).as("If Category is required then expect code 400, else expect 201").satisfiesAnyOf(code400,code201);
    }

    @Story("Дефекты")
    @Step("Тест: Создание продукта: невалидное значение в поле сategoryTitle")
    @SneakyThrows
    @DisplayName("(-) Создание продукта:в поле Наименование Категори передается невалидное значение")
    @Test
    void createNewProductFakeCategoryNegativeTest(){
        Response<Product> response = productService
                .createProduct(new Product()
                        .withTitle(productTitle)
                        .withCategoryTitle(faker.music().instrument())).execute();

        assertThat(response.code()).as("Expect 400 Bad Bad Request").isEqualTo(400);
    }

    @Story("Дефекты")
    @Step("Тест: Создание продукта: отрицательное значение в поле Price")
    @SneakyThrows
    @DisplayName("(-) Создание продукта:в поле Цена передается отрицательное значение")
    @Test
    void createNewProductNegativeValueToPriceNegativeTest(){
        Response<Product> response = productService
                .createProduct(product
                        .withPrice(productPrice * -1)).execute();

        productID = response.body().getId();

        assertThat(response.code()).as("Expect 400 Bad Bad Request. Price don't be negative").isEqualTo(400);
    }

    @Step("Удаление продукта после теста")
    @AfterEach
    void tearDown() {
        if (productID!=null)
            productsMapper.deleteByPrimaryKey((long)productID);
    }
}
