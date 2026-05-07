package com.example.iot_lab4_20220229;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_lab4_20220229.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonIngresar.setOnClickListener(view -> {
            if (hayConexionInternet()) {
                Intent intent = new Intent(MainActivity.this, AppActivity.class);
                startActivity(intent);
            } else {
                mostrarDialogoSinInternet();
            }
        });
    }

    private boolean hayConexionInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }

    private void mostrarDialogoSinInternet() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.no_internet_title))
                .setMessage(getString(R.string.no_internet_message))
                .setPositiveButton(getString(R.string.settings), (dialogInterface, i) -> {

                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}