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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20220229.R;
import com.example.iot_lab4_20220229.adapters.MealsAdapter;
import com.example.iot_lab4_20220229.databinding.FragmentMealsBinding;
import com.example.iot_lab4_20220229.dto.Meal;
import com.example.iot_lab4_20220229.dto.MealsResponse;
import com.example.iot_lab4_20220229.services.MealDbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealsFragment extends Fragment {

    private FragmentMealsBinding binding;

    private MealDbService service;

    public MealsFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMealsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewMeals.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MealDbService.class);

        String categoryName = null;

        if (getArguments() != null) {
            categoryName =
                    getArguments().getString("categoryName");
        }

        if (categoryName != null && !categoryName.isEmpty()) {

            obtenerMealsPorCategoria(categoryName);
        }

        binding.buttonBuscarMeals.setOnClickListener(v -> {

            String ingredient = binding.editTextIngredient
                    .getText()
                    .toString()
                    .trim();

            if (!TextUtils.isEmpty(ingredient)) {

                obtenerMealsPorIngrediente(ingredient);
            }
        });
    }

    private void obtenerMealsPorCategoria(String category) {

        service.getMealsByCategory(category)
                .enqueue(new Callback<MealsResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<MealsResponse> call,
                            @NonNull Response<MealsResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getMeals() != null) {

                            configurarAdapter(
                                    response.body().getMeals()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<MealsResponse> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                requireContext(),
                                "Error al obtener platos",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void obtenerMealsPorIngrediente(String ingredient) {

        service.getMealsByIngredient(ingredient)
                .enqueue(new Callback<MealsResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<MealsResponse> call,
                            @NonNull Response<MealsResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getMeals() != null) {

                            configurarAdapter(
                                    response.body().getMeals()
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "No se encontraron resultados",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<MealsResponse> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                requireContext(),
                                "Error en la búsqueda",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void configurarAdapter(
            java.util.List<Meal> meals
    ) {

        MealsAdapter adapter = new MealsAdapter(
                meals,
                meal -> {

                    Bundle bundle = new Bundle();

                    bundle.putString(
                            "mealId",
                            meal.getIdMeal()
                    );

                    NavController navController =
                            NavHostFragment.findNavController(
                                    MealsFragment.this
                            );

                    navController.navigate(
                            R.id.action_mealsFragment_to_recipeFragment,
                            bundle
                    );
                }
        );

        binding.recyclerViewMeals.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}