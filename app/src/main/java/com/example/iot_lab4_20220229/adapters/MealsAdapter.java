package com.example.iot_lab4_20220229.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20220229.databinding.ItemMealBinding;
import com.example.iot_lab4_20220229.dto.Meal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    private final List<Meal> mealList;
    private final OnMealClickListener listener;

    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(4);

    private static final HashMap<String, Bitmap> imageCache =
            new HashMap<>();

    public MealsAdapter(List<Meal> mealList, OnMealClickListener listener) {
        this.mealList = mealList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealBinding binding = ItemMealBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);

        holder.binding.textViewMealName.setText(meal.getStrMeal());
        holder.binding.textViewMealId.setText("ID: " + meal.getIdMeal());
        holder.binding.imageViewMeal.setImageBitmap(null);

        cargarImagen(meal.getStrMealThumb(), holder);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {

        ItemMealBinding binding;

        public MealViewHolder(ItemMealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void cargarImagen(String imageUrl, MealViewHolder holder) {
        Bitmap cachedBitmap = imageCache.get(imageUrl);

        if (cachedBitmap != null) {
            holder.binding.imageViewMeal.setImageBitmap(cachedBitmap);
            return;
        }

        executorService.execute(() -> {
            try {
                URL url = new URL(imageUrl);

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                imageCache.put(imageUrl, bitmap);

                holder.binding.imageViewMeal.post(() ->
                        holder.binding.imageViewMeal.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}