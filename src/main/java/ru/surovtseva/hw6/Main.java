package ru.surovtseva.hw6;

import ru.surovtseva.hw6.db.dao.CategoriesMapper;
import ru.surovtseva.hw6.db.dao.ProductsMapper;
import ru.surovtseva.hw6.db.model.Products;
import ru.surovtseva.hw6.db.model.ProductsExample;
import ru.surovtseva.hw6.utils.DbUtils;

import java.io.IOException;

public class Main {
    private static String resource = "mybatisConfig.xml";

    public static void main(String[] args) throws IOException {
        CategoriesMapper categoriesMapper = DbUtils.getCategoriesMapper();

//        System.out.println(categoriesMapper.countByExample(new CategoriesExample()));
        ProductsMapper productsMapper = DbUtils.getProductsMapper();

//        productsMapper.deleteByPrimaryKey(3459L);

        Products product = new Products()
                .withId(3462L)
                .withTitle("my updated")
                .withPrice(777)
                .withCategory_id(1L);

        ProductsExample productsExample = new ProductsExample();
        productsExample.createCriteria().andIdEqualTo(3462L);
        productsMapper.updateByExample(product, productsExample);

    }
}
