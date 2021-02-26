package ru.surovtseva.hw5.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.surovtseva.hw5.Endpoints;
import ru.surovtseva.hw5.dto.positive.Product;

public interface ProductService {


    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,Integer>> createProduct(@Body Product<Integer,String,Integer> createProductRequest);

    //POST Test Title
    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,Integer,Integer>> createProductIntTitle(@Body Product<Integer,Integer,Integer> createProductRequest);

    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,Double,Integer>> createProductDoubleTitle(@Body Product<Integer,Double,Integer> createProductRequest);

    //POST Test Price
    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,String>> createProductStringPrice(@Body Product<Integer,String,String> createProductRequest);

    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,Double>> createProductDoublePrice(@Body Product<Integer,String,Double> createProductRequest);


    //PUT Test ID
    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,Integer>> modifyProduct (@Body Product<Integer,String,Integer> createProductRequest);

    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Double,String,Integer>> modifyProductDoubleID(@Body Product<Double,String,Integer> createProductRequest);

    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<String,String,Integer>> modifyProductStringID (@Body Product<String,String,Integer> createProductRequest);

    //PUT Test Title
    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,Integer,Integer>> modifyProductIntTitle(@Body Product<Integer,Integer,Integer> createProductRequest);

    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,Double,Integer>> modifyProductDoubleTitle(@Body Product<Integer,Double,Integer> createProductRequest);

    //PUT Test Price
    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,String>> modifyProductStringPrice(@Body Product<Integer,String,String> createProductRequest);

    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product<Integer,String,Double>> modifyProductDoublePrice(@Body Product<Integer,String,Double> createProductRequest);


    @GET(Endpoints.GET_DEL_PRODUCT)
    Call<Product<Integer,String,Integer>> getProduct (@Path("id") int id);

    @GET(Endpoints.GET_ALL_PRODUCTS)
    Call<ResponseBody> getAllProducts ();

    @DELETE(Endpoints.GET_DEL_PRODUCT)
    Call<ResponseBody> deleteProduct(@Path("id") int id);
}
