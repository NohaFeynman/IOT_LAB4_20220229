package com.example.iot_lab4_20220229.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.iot_lab4_20220229.adapters.CategoriesAdapter;
import com.example.iot_lab4_20220229.databinding.FragmentCategoriesBinding;
import com.example.iot_lab4_20220229.dto.CategoriesResponse;
import com.example.iot_lab4_20220229.services.MealDbService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;

    public CategoriesFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCategoriesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewCategories.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MealDbService service = retrofit.create(MealDbService.class);

        service.getCategories().enqueue(new Callback<CategoriesResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<CategoriesResponse> call,
                    @NonNull Response<CategoriesResponse> response
            ) {

                if (response.isSuccessful() && response.body() != null) {

                    CategoriesAdapter adapter =
                            new CategoriesAdapter(response.body().getCategories());

                    binding.recyclerViewCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<CategoriesResponse> call,
                    @NonNull Throwable t
            ) {

                Toast.makeText(
                        requireContext(),
                        "Error al obtener categorías",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}