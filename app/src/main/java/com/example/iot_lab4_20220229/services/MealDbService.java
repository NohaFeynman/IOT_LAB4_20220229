package com.example.iot_lab4_20220229.services;

import com.example.iot_lab4_20220229.dto.CategoriesResponse;
import com.example.iot_lab4_20220229.dto.MealsResponse;
import com.example.iot_lab4_20220229.dto.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDbService {

    @GET("categories.php")
    Call<CategoriesResponse> getCategories();

    @GET("filter.php")
    Call<MealsResponse> getMealsByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<MealsResponse> getMealsByIngredient(@Query("i") String ingredient);

    @GET("lookup.php")
    Call<RecipeResponse> getRecipeById(@Query("i") String idMeal);

    @GET("random.php")
    Call<RecipeResponse> getRandomRecipe();
}