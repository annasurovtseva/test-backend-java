package ru.surovtseva.hw5;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import retrofit2.Response;
import ru.surovtseva.hw5.dto.negative.ErrorBody;
import ru.surovtseva.hw5.dto.positive.Product;
import ru.surovtseva.hw5.enums.CategoryType;
import ru.surovtseva.hw5.services.ProductService;
import ru.surovtseva.hw5.utils.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Интеграционный тест: Изменение - Получение - Удаление - Получение")
public class CRUDTests {
    static ProductService productService;
    static Product product;
    static String productTitle;
    static int productPrice;
    static Faker faker = new Faker();
    static Integer productID;


    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);

        product = new Product()
                .withCategoryTitle(CategoryType.ELECTRONIC.getTitle());

        Response<Product> response = productService
                .createProduct(product).execute();

        productID = response.body().getId();
        productTitle = faker.friends().character();
        productPrice =  (int)(Math.random()*1000+1);
    }

    @DisplayName("Изменение продукта")
    @SneakyThrows
    @Test
    void modifyProduct() {
        Response<Product> response = productService
                .modifyProduct(product
                        .withId(productID)
                        .withTitle(productTitle)
                        .withPrice(productPrice)
                        .withCategoryTitle(CategoryType.FOOD.getTitle())).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(productTitle);
        assertThat(response.body().getPrice()).isEqualTo(productPrice);
    }

    @Feature("Интеграционный тест: Изменение - Получение - Удаление - Получение")
    @Nested
    class whenProductModified {
        @SneakyThrows
        @DisplayName("Получение продукта")
        @Test
        void GetProduct() {
            Response<Product> response = productService
                    .getProduct(productID).execute();

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
            assertThat(response.body().getTitle()).isEqualTo(productTitle);
            assertThat(response.body().getPrice()).isEqualTo(productPrice);
        }

        @Feature("Интеграционный тест: Изменение - Получение - Удаление - Получение")
        @Nested
        class whenProductWasGot {
            @SneakyThrows
            @DisplayName("Удаление продукта")
            @Test
            void deleteProduct() {
                Response<ResponseBody> response =
                        productService.deleteProduct(productID).execute();

                Consumer<Integer> code200 = code -> assertThat(response.code()).isEqualTo(200);
                Consumer<Integer> code204 = code -> assertThat(response.code()).isEqualTo(204);

                assertThat(response.code()).satisfiesAnyOf(code200,code204);
            }

            @Feature("Интеграционный тест: Изменение - Получение - Удаление - Получение")
            @Nested
            class whenProductDeleted{
                @SneakyThrows
                @DisplayName("Получение продукта после удаление")
                @Test
                void getDeletedProduct() {
                    Response<Product> response = productService
                            .getProduct(productID).execute();

                    assertThat(response.code()).isEqualTo(404);
                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                        ResponseBody body = response.errorBody();
                        Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit()
                                .responseBodyConverter(ErrorBody.class, new Annotation[0]);
                        ErrorBody errorBody = converter.convert(body);
                        assertThat(errorBody.getMessage()).contains("Unable to find product");
                    }
                }
            }
        }
    }
}




