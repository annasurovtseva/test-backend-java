package ru.surovtseva.hw6;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
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

@Feature("Получение продукта")
public class GetProductTests {
    static ProductService productService;
    static ProductsMapper productsMapper;
    Integer productID;
    Product product;
    Integer productPrice;
    String productTitle;
    Faker faker = new Faker();
    int fakeID;


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
        productPrice = (int)(Math.random() * 1000 + 1);
        productTitle = faker.witcher().monster();
        fakeID = (int)(Math.random() * 10000 + 10000);

        product = new Product()
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
                .withPrice(productPrice)
                .withTitle(productTitle);

        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();
    }

    @Step("Тест: Получение продукта по ID")
    @SneakyThrows
    @DisplayName("(+) Получение продукта по ID")
    @Test
    void getProductPositiveTest() {
        Response<Product> response = productService
                .getProduct(productID).execute();

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(productTitle);
        assertThat(response.body().getPrice()).isEqualTo(productPrice);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(productPrice);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isEqualTo(productTitle);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.ELECTRONIC.getId()));

    }

    @Step("Тест: Получение всех продуктов")
    @SneakyThrows
    @DisplayName("(+) Получение всех продуктов")
    @Test
    void getAllProductsPositiveTest() {
        Response<ResponseBody> response = productService.getAllProducts().execute();

        assertThat(response.code()).isEqualTo(200);
    }

    @Step("Тест: Получение продукта по невалидному ID")
    @SneakyThrows
    @DisplayName("(-) Получение продукта по  несуществующему ID")
    @Test
    void getProductNegativeTest() {
        Response<Product> response = productService
                .getProduct(fakeID).execute();


        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).contains("Unable to find product");
        }
    }

    @Step("Удаление продукта после теста")
    @AfterEach
    void tearDown() {
        if (productID!=null)
            productsMapper.deleteByPrimaryKey((long)productID);
    }
}
