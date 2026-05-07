package com.example.iot_lab4_20220229.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iot_lab4_20220229.databinding.FragmentRecipeBinding;
import com.example.iot_lab4_20220229.dto.Recipe;
import com.example.iot_lab4_20220229.dto.RecipeResponse;
import com.example.iot_lab4_20220229.services.MealDbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private MealDbService service;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MealDbService.class);

        if (getArguments() != null) {
            String mealId = getArguments().getString("mealId");

            if (mealId != null && !mealId.isEmpty()) {
                binding.editTextMealId.setText(mealId);
                obtenerRecetaPorId(mealId);
            }
        }

        binding.buttonBuscarRecipe.setOnClickListener(v -> {
            String mealId = binding.editTextMealId
                    .getText()
                    .toString()
                    .trim();

            if (!TextUtils.isEmpty(mealId)) {
                obtenerRecetaPorId(mealId);
            } else {
                Toast.makeText(
                        requireContext(),
                        "Ingrese un ID de plato",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void obtenerRecetaPorId(String mealId) {
        service.getRecipeById(mealId).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<RecipeResponse> call,
                    @NonNull Response<RecipeResponse> response
            ) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {

                    Recipe recipe = response.body().getMeals().get(0);
                    mostrarReceta(recipe);

                } else {
                    Toast.makeText(
                            requireContext(),
                            "No se encontró la receta",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<RecipeResponse> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(
                        requireContext(),
                        "Error al obtener receta",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void mostrarReceta(Recipe recipe) {
        binding.textViewRecipeName.setText(recipe.getStrMeal());

        binding.textViewRecipeCategory.setText(
                "Categoría: " + recipe.getStrCategory()
        );

        binding.textViewRecipeArea.setText(
                "Origen: " + recipe.getStrArea()
        );

        binding.textViewRecipeIngredients.setText(
                "Ingredientes:\n"
                        + "- " + validarTexto(recipe.getStrIngredient1()) + "\n"
                        + "- " + validarTexto(recipe.getStrIngredient2()) + "\n"
                        + "- " + validarTexto(recipe.getStrIngredient3())
        );

        binding.textViewRecipeInstructions.setText(
                "Instrucciones:\n" + validarTexto(recipe.getStrInstructions())
        );
    }

    private String validarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "No disponible";
        }

        return texto;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}