package com.example.iot_lab4_20220229;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.iot_lab4_20220229.databinding.ActivityAppBinding;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.navHostFragment);

        if (navHostFragment != null) {

            NavController navController = navHostFragment.getNavController();

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                int itemId = item.getItemId();

                NavOptions navOptions = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build();

                if (itemId == R.id.categoriesFragment) {

                    navController.navigate(R.id.categoriesFragment, null, navOptions);
                    return true;

                } else if (itemId == R.id.mealsFragment) {

                    navController.navigate(R.id.mealsFragment, null, navOptions);
                    return true;

                } else if (itemId == R.id.recipeFragment) {

                    navController.navigate(R.id.recipeFragment, null, navOptions);
                    return true;
                }

                return false;
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}