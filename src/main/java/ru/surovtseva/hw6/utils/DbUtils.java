package ru.surovtseva.hw6.utils;

import lombok.experimental.UtilityClass;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.surovtseva.hw6.db.dao.CategoriesMapper;
import ru.surovtseva.hw6.db.dao.ProductsMapper;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class DbUtils {
    private static String resource = "mybatisConfig.xml";

    private static SqlSession getSqlSession () throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession =sqlSessionFactory.openSession(true);
        return  sqlSession;
    }

    public CategoriesMapper getCategoriesMapper() throws IOException {
        return getSqlSession().getMapper(CategoriesMapper.class);
    }

    public ProductsMapper getProductsMapper() throws IOException {
        return getSqlSession().getMapper(ProductsMapper.class);
    }
}
