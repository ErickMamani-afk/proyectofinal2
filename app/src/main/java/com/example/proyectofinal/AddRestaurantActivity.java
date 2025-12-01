package com.example.proyectofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import db.DatabaseHelper;

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText etLat, etLng, etName, etType;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        db = new DatabaseHelper(this);

        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        etName = findViewById(R.id.etRestName);
        etType = findViewById(R.id.etRestType);

        // Si recibimos coordenadas del mapa, las ponemos en los campos
        if (getIntent().hasExtra("LAT_SELECCIONADA")) {
            etLat.setText(String.valueOf(getIntent().getDoubleExtra("LAT_SELECCIONADA", 0)));
            etLng.setText(String.valueOf(getIntent().getDoubleExtra("LNG_SELECCIONADA", 0)));
        }

        findViewById(R.id.btnSaveRestaurant).setOnClickListener(v -> {
            String nombre = etName.getText().toString();
            String tipo = etType.getText().toString();
            String sLat = etLat.getText().toString();
            String sLng = etLng.getText().toString();

            if(nombre.isEmpty() || sLat.isEmpty()) {
                Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en SQLite
            db.addRestaurant(nombre, Double.parseDouble(sLat), Double.parseDouble(sLng), tipo);
            Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}