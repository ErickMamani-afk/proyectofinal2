package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los botones
        Button btnMap = findViewById(R.id.btnGoToMap);
        Button btnReview = findViewById(R.id.btnGoToReview);
        Button btnSearch = findViewById(R.id.btnGoToSearch);
        Button btnList = findViewById(R.id.btnGoToList);

        // 1. Navegar al Mapa
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // 2. Navegar a Crear Reseña (Cámara)
        btnReview.setOnClickListener(v -> {
            // Avisamos al usuario que primero debe elegir el local
            Toast.makeText(MainActivity.this, "Selecciona un restaurante para reseñar", Toast.LENGTH_SHORT).show();

            // Lo mandamos a la lista para que elija uno
            Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
            startActivity(intent);
        });
        // 3. Navegar a Buscar (Debes crear SearchActivity.java aunque esté vacía)
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // 4. Navegar a Lista (Debes crear RestaurantListActivity.java aunque esté vacía)
        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
            startActivity(intent);
        });
    }
}