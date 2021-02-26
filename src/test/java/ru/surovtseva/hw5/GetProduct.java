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
//@Feature("Получение продукта")
//public class GetProductTests {
//    static ProductService productService;
//    Integer productID;
//    Product<Integer> product;
//    Integer productPrice;
//    String productTitle;
//    Faker faker = new Faker();
//
//    @BeforeAll
//    static void beforeAll() {
//        productService = RetrofitUtils.getRetrofit()
//                .create(ProductService.class);
//    }
//
//
//    @Step("Создание продукта перед тестом")
//    @SneakyThrows
//    @BeforeEach
//    void setUp() {
//        productPrice = (int)(Math.random() * 1000 + 1);
//        productTitle = faker.witcher().monster();
//        product = new Product<Integer>()
//                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle())
//                .withPrice(productPrice)
//                .withTitle(productTitle);
//
//        Response<Product<Integer>> response = productService
//                .createProduct(product).execute();
//
//        productID = response.body().getId();
//    }
//
//    @Step("Тест: Получение продукта по ID")
//    @SneakyThrows
//    @DisplayName("(+) Получение продукта по ID")
//    @Test
//    void GetProductPositiveTest() {
//        Response<Product<Integer>> response = productService
//                .getProduct(productID).execute();
//
//        assertThat(response.code()).isEqualTo(200);
//        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.ELECTRONIC.getTitle());
//        assertThat(response.body().getTitle()).isEqualTo(productTitle);
//        assertThat(response.body().getPrice()).isEqualTo(productPrice);
//    }
//
//    @Step("Тест: Получение всех продуктов")
//    @SneakyThrows
//    @DisplayName("(+) Получение всех продуктов")
//    @Test
//    void GetAllProductsPositiveTest() {
//        Response<ResponseBody> response = productService.getAllProducts().execute();
//
//        assertThat(response.code()).isEqualTo(200);
//    }
//
//    @Step("Тест: Получение продукта по невалидному ID")
//    @SneakyThrows
//    @DisplayName("(-) Получение продукта по  несуществующему ID")
//    @Test
//    void GetProductNegativeTest() {
//        int fakeID = GenerateDataUtils.generateFakeID();
//        Response<Product<Integer>> response = productService
//                .getProduct(fakeID).execute();
//
//
//        assertThat(response.code()).isEqualTo(404);
//        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
//            ResponseBody body = response.errorBody();
//            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
//                    .responseBodyConverter(ErrorBody.class, new Annotation[0]);
//            ErrorBody errorBody = converter.convert(body);
//            assertThat(errorBody.getMessage()).contains("Unable to find product");
//        }
//    }
//
//    @Step("Удаление продукта после теста")
//    @AfterEach
//    void tearDown() {
//        if (productID!=null)
//            try {
//                Response<ResponseBody> response =
//                        productService.deleteProduct(productID)
//                                .execute();
//                assertThat(response.isSuccessful()).isTrue();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//    }
//}
