package ru.surovtseva.hw6.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.surovtseva.hw6.Endpoints;
import ru.surovtseva.hw6.dto.positive.Product;

public interface ProductService {

    @POST(Endpoints.POST_PUT_PRODUCT)
    Call<Product> createProduct(@Body Product createProductRequest);

    @PUT(Endpoints.POST_PUT_PRODUCT)
    Call<Product> modifyProduct (@Body Product createProductRequest);

    @GET(Endpoints.GET_DEL_PRODUCT)
    Call<Product> getProduct (@Path("id") int id);

    @GET(Endpoints.GET_ALL_PRODUCTS)
    Call<ResponseBody> getAllProducts ();

    @DELETE(Endpoints.GET_DEL_PRODUCT)
    Call<ResponseBody> deleteProduct(@Path("id") int id);
}
