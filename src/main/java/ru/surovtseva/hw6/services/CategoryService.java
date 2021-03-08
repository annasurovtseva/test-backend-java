package ru.surovtseva.hw6.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.surovtseva.hw6.Endpoints;
import ru.surovtseva.hw6.dto.positive.Category;

public interface CategoryService {

    @GET(Endpoints.GET_CATEGORY)
    Call<Category> getCategory(@Path("id") Integer id);
}
