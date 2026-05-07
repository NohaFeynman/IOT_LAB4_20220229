package com.example.iot_lab4_20220229.services;

import com.example.iot_lab4_20220229.dto.CategoriesResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MealDbService {

    @GET("categories.php")
    Call<CategoriesResponse> getCategories();
}