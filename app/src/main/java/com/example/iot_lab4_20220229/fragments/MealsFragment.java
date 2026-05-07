package com.example.iot_lab4_20220229.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.iot_lab4_20220229.dto.Recipe;
import com.example.iot_lab4_20220229.dto.RecipeResponse;
import com.example.iot_lab4_20220229.services.MealDbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealsFragment extends Fragment implements SensorEventListener {

    private FragmentMealsBinding binding;

    private MealDbService service;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastShakeTime = 0;

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

        sensorManager = (SensorManager)
                requireContext().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_ACCELEROMETER
                    );
        }

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

    private void obtenerRecetaAleatoria() {

        service.getRandomRecipe()
                .enqueue(new Callback<RecipeResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<RecipeResponse> call,
                            @NonNull Response<RecipeResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getMeals() != null
                                && !response.body().getMeals().isEmpty()) {

                            Recipe recipe =
                                    response.body().getMeals().get(0);

                            Bundle bundle = new Bundle();

                            bundle.putString(
                                    "mealId",
                                    recipe.getIdMeal()
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
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<RecipeResponse> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                requireContext(),
                                "Error al obtener receta aleatoria",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration =
                Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 14) {

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastShakeTime > 2000) {

                lastShakeTime = currentTime;

                Toast.makeText(
                        requireContext(),
                        "¡Receta aleatoria!",
                        Toast.LENGTH_SHORT
                ).show();

                obtenerRecetaAleatoria();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        super.onResume();

        if (accelerometer != null) {

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}