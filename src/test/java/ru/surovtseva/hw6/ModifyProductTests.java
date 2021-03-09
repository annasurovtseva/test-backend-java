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
import ru.surovtseva.hw6.utils.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Изменение продукта")
public class ModifyProductTests {
    static ProductService productService;
    static ProductsMapper productsMapper;
    Integer productID;
    Product product;
    Faker faker = new Faker();
    int fakeID;
    String newProdTitle;
    Integer newProdPrice;

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
        newProdTitle = faker.witcher().monster();
        newProdPrice = (int)(Math.random() * 1000 + 1);
        fakeID = (int)(Math.random() * 10000 + 10000);

        product = new Product()
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
                .withPrice((int)(Math.random() * 1000 + 1))
                .withTitle(faker.witcher().monster());

        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();
     }

    @Story("Позитивные")
    @Step("Тест: Изменение продукта по валидному ID: все поля")
    @SneakyThrows
    @DisplayName("(+) Изменение продукта по валидному ID: все поля")
    @Test
    void modifyProductPositiveTest(){

        Response<Product> response = productService
                .modifyProduct(product
                        .withId(productID)
                        .withTitle(newProdTitle)
                        .withPrice(newProdPrice)
                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(newProdTitle);
        assertThat(response.body().getPrice()).isEqualTo(newProdPrice);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(newProdPrice);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isEqualTo(newProdTitle);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.FOOD.getId()));
    }

    @Story("Позитивные")
    @Step("Тест: Изменение продукта по валидному ID: не все поля")
    @SneakyThrows
    @DisplayName("(+) Изменение продукта по валидному ID: заполнены не все поля")
    @Test
    void modifyProductWithEmptyFieldsPositiveTest(){
        Response<Product> response = productService
                .modifyProduct(new Product()
                        .withId(productID)
                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
        assertThat(response.body().getTitle()).isNull();
        assertThat(response.body().getPrice()).isEqualTo(0);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(0);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isNull();
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.FOOD.getId()));
    }

    @Story("Негативные")
    @Step("Тест: Изменение продукта по пустому ID")
    @SneakyThrows
    @DisplayName("(-) Изменение продукта: передан пустой ID")
    @Test
    void modifyProductEmptyIDNegativeTest(){
        Response<Product> response = productService
                .modifyProduct(product
                        .withPrice(newProdPrice)
                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();


        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be not null for new entity");
        }

        //Проверка записи в базе - не изменилась
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice())
                .isNotEqualTo(newProdPrice);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id())
                .isEqualTo((long)(CategoryType.ELECTRONIC.getId()));
    }

    //Fail Test
    @Story("Дефекты")
    @Step("Тест: Изменение продукта по невалидному ID")
    @SneakyThrows
    @DisplayName("(-) Изменение продукт по невалидному ID")
    @Test
    void modifyProductFakeIDNegativeTest(){
        Response<Product> response = productService
                .modifyProduct(product
                        .withId(fakeID)
                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();

        //Проверка ответа сервера
        assertThat(response.code()).as("Expect code 404. Product not found").isEqualTo(404);

        //Проверка запись в базе отсутствует
        assertThat(productsMapper.selectByPrimaryKey((long)fakeID)).isNull();
    }

    @Step("Удаление продукта после теста")
    @AfterEach
    void tearDown() {
        if (productID!=null)
            productsMapper.deleteByPrimaryKey((long)productID);
    }
}
