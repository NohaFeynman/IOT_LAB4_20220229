package com.example.iot_lab4_20220229.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20220229.databinding.ItemCategoryBinding;
import com.example.iot_lab4_20220229.dto.Category;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;

    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(4);

    private static final HashMap<String, Bitmap> imageCache =
            new HashMap<>();

    public CategoriesAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.binding.textViewCategoryName.setText(category.getStrCategory());
        holder.binding.imageViewCategory.setImageBitmap(null);

        cargarImagen(category.getStrCategoryThumb(), holder);

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void cargarImagen(String imageUrl, CategoryViewHolder holder) {
        Bitmap cachedBitmap = imageCache.get(imageUrl);

        if (cachedBitmap != null) {
            holder.binding.imageViewCategory.setImageBitmap(cachedBitmap);
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

                holder.binding.imageViewCategory.post(() ->
                        holder.binding.imageViewCategory.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}