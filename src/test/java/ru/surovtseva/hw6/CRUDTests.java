package ru.surovtseva.hw6;

import com.github.javafaker.Faker;
import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

@Feature("Интеграционный тест: Изменение - Получение - Удаление")
public class CRUDTests {
    static ProductService productService;
    static ProductsMapper productsMapper;
    static Product product;
    static String productTitle;
    static int productPrice;
    static Faker faker = new Faker();
    static Integer productID;


    @BeforeAll
    static void beforeAll() throws IOException {
        productsMapper = DbUtils.getProductsMapper();
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);

        product = new Product()
                .withTitle(faker.commerce().productName())
                .withPrice((int)(Math.random()*1000+1))
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

        //Проверка ответа сервера
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
        assertThat(response.body().getTitle()).isEqualTo(productTitle);
        assertThat(response.body().getPrice()).isEqualTo(productPrice);

        //Проверка записи в базе
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(productPrice);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isEqualTo(productTitle);
        assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.FOOD.getId()));
    }

    @Feature("Интеграционный тест: Изменение - Получение - Удаление")
    @Nested
    class whenProductModified {
        @SneakyThrows
        @DisplayName("Получение продукта")
        @Test
        void GetProduct() {
            Response<Product> response = productService
                    .getProduct(productID).execute();

            //Проверка ответа сервера
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().getCategoryTitle()).isEqualTo(CategoryType.FOOD.getTitle());
            assertThat(response.body().getTitle()).isEqualTo(productTitle);
            assertThat(response.body().getPrice()).isEqualTo(productPrice);

            //Проверка записи в базе
            assertThat(productsMapper.selectByPrimaryKey((long)productID).getPrice()).isEqualTo(productPrice);
            assertThat(productsMapper.selectByPrimaryKey((long)productID).getTitle()).isEqualTo(productTitle);
            assertThat(productsMapper.selectByPrimaryKey((long)productID).getCategory_id()).isEqualTo((long)(CategoryType.FOOD.getId()));
        }

        @Feature("Интеграционный тест: Изменение - Получение - Удаление")
        @Nested
        class whenProductWasGot {
            @SneakyThrows
            @DisplayName("Удаление продукта")
            @Test
            void deleteProduct() {
                Response<ResponseBody> response =
                        productService.deleteProduct(productID).execute();

                //Проверка ответа сервера
                Consumer<Integer> code200 = code -> assertThat(response.code()).isEqualTo(200);
                Consumer<Integer> code204 = code -> assertThat(response.code()).isEqualTo(204);
                assertThat(response.code()).satisfiesAnyOf(code200,code204);

                //Проверка: запись в базе отсутствует
                assertThat(productsMapper.selectByPrimaryKey((long)productID)).isNull();
            }
        }
    }
}




