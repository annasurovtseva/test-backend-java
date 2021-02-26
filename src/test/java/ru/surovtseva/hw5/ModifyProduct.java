//package ru.surovtseva.hw5;
//
//import com.github.javafaker.Faker;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Step;
//import lombok.SneakyThrows;
//import okhttp3.ResponseBody;
//import org.junit.jupiter.api.*;
//import retrofit2.Converter;
//import retrofit2.Response;
//import ru.surovtseva.hw5.dto.negative.ErrorBody;
//import ru.surovtseva.hw5.dto.positive.Product;
//import ru.surovtseva.hw5.enums.CategoryType;
//import ru.surovtseva.hw5.services.ProductService;
//import ru.surovtseva.hw5.utils.GenerateDataUtils;
//import ru.surovtseva.hw5.utils.RetrofitUtils;
//
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Feature("Изменение продукта")
//public class ModifyProductTest {
//    static ProductService productService;
//    Integer productID;
//    Product product;
//    Faker faker = new Faker();
//    String newProdTitle = faker.witcher().monster();
//    Double newProdPrice = Math.floor(Math.random() * 1000 + 1);
//
//    @BeforeAll
//    static void beforeAll() {
//        productService = RetrofitUtils.getRetrofit()
//                .create(ProductService.class);
//    }
//
//    @Step("Создание продукта перед тестом")
//    @SneakyThrows
//    @BeforeEach
//    void setUp() {
//        product = new Product()
//                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
//                .withPrice(Math.floor(Math.random() * 1000 + 1))
//                .withTitle(faker.witcher().monster());
//
//        Response<Product> response = productService
//                .createProduct(product).execute();
//
//        productID = response.body().getId();
//     }
//
//    @Step("Тест: Изменение продукта по валидному ID: все поля")
//    @SneakyThrows
//    @DisplayName("(+) Изменение продукта по валидному ID: все поля")
//    @Test
//    void modifyProductPositiveTest(){
//
//        Response<Product> response = productService
//                .modifyProduct(product
//                        .withId(productID)
//                        .withTitle(newProdTitle)
//                        .withPrice(newProdPrice)
//                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();
//
//        assertThat(response.code()).isEqualTo(200);
//        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
//        assertThat(response.body().getTitle()).isEqualTo(newProdTitle);
//        assertThat(response.body().getPrice()).isEqualTo(newProdPrice);
//    }
//
//    @Step("Тест: Изменение продукта по валидному ID: не все поля")
//    @SneakyThrows
//    @DisplayName("(+) Изменение продукта по валидному ID: заполнены не все поля")
//    @Test
//    void modifyProductWithEmptyFieldsPositiveTest(){
//        Response<Product> response = productService
//                .modifyProduct(new Product()
//                        .withId(productID)
//                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();
//
//        assertThat(response.code()).isEqualTo(200);
//        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
//        assertThat(response.body().getTitle()).isNull();
//        assertThat(response.body().getPrice()).isEqualTo(0);
//    }
//
//    @Step("Тест: Изменение продукта по пустому ID")
//    @SneakyThrows
//    @DisplayName("(-) Изменение продукта: передан пустой ID")
//    @Test
//    void modifyProductEmptyIDNegativeTest(){
//        Response<Product> response = productService
//                .modifyProduct(product
//                        .withPrice(newProdPrice)
//                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();
//
//        assertThat(response.code()).isEqualTo(400);
//        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
//            ResponseBody body = response.errorBody();
//            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
//                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
//            ErrorBody errorBody = converter.convert(body);
//            assertThat(errorBody.getMessage()).isEqualTo("Id must be not null for new entity");
//        }
//    }
//
//    //Fail Test
//    @Step("Тест: Изменение продукта по невалидному ID")
//    @SneakyThrows
//    @DisplayName("(-) Изменение продукт по невалидному ID")
//    @Test
//    void modifyProductFakeIDNegativeTest(){
//        int fakeID = GenerateDataUtils.generateFakeID();
//        Response<Product> response = productService
//                .modifyProduct(product
//                        .withId(fakeID)
//                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();
//
//        assertThat(response.code()).isEqualTo(404);
//    }
//
//    @Step("Удаление продукта после теста")
//    @AfterEach
//    void tearDown() {
//        if (productID!=null)
//            try {
//                retrofit2.Response<ResponseBody> response =
//                        productService.deleteProduct(productID)
//                                .execute();
//                assertThat(response.isSuccessful()).isTrue();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//    }
//}
