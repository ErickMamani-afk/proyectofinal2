package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import db.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sembrar datos de prueba (Iquique) si la BD está vacía
        new DatabaseHelper(this).checkAndInsertDummyData();

        // 1. Botón Mapa
        findViewById(R.id.btnGoToMap).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MapsActivity.class))
        );

        // 2. Botón Crear Reseña (Redirige a lista para elegir local)
        findViewById(R.id.btnGoToReview).setOnClickListener(v -> {
            Toast.makeText(this, "Primero elige el restaurante", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, RestaurantListActivity.class));
        });

        // 3. Botón Buscar
        findViewById(R.id.btnGoToSearch).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SearchActivity.class))
        );

        // 4. Botón Lista Completa
        findViewById(R.id.btnGoToList).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RestaurantListActivity.class))
        );
    }
}