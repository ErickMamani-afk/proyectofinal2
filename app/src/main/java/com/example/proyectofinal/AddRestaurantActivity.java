package com.example.proyectofinal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText etName, etType, etLat, etLng;
    private Button btnSave;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        // Inicializar Base de Datos
        db = new DatabaseHelper(this);

        // Referencias UI
        etName = findViewById(R.id.etRestName);
        etType = findViewById(R.id.etRestType);
        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        btnSave = findViewById(R.id.btnSaveRestaurant);

        // --- BLOQUE NUEVO: RECIBIR COORDENADAS DEL MAPA (SI EXISTEN) ---
        if (getIntent().hasExtra("LAT_SELECCIONADA")) {
            double latRecibida = getIntent().getDoubleExtra("LAT_SELECCIONADA", 0.0);
            double lngRecibida = getIntent().getDoubleExtra("LNG_SELECCIONADA", 0.0);

            // Llenar los campos automáticamente
            etLat.setText(String.valueOf(latRecibida));
            etLng.setText(String.valueOf(lngRecibida));

            Toast.makeText(this, "Coordenadas capturadas del mapa", Toast.LENGTH_SHORT).show();
        }
        // ---------------------------------------------------------------

        btnSave.setOnClickListener(v -> saveRestaurant());
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA ---
    private void saveRestaurant() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String latStr = etLat.getText().toString().trim();
        String lngStr = etLng.getText().toString().trim();

        // 1. Validaciones básicas
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(latStr) || TextUtils.isEmpty(lngStr)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 2. Convertir coordenadas a números
            double lat = Double.parseDouble(latStr);
            double lng = Double.parseDouble(lngStr);

            // 3. Guardar en SQLite
            boolean success = db.addRestaurant(name, lat, lng, type);

            if (success) {
                Toast.makeText(this, "Restaurante guardado con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Cierra esta pantalla y vuelve al mapa o lista
            } else {
                Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Las coordenadas deben ser números válidos (usa punto . para decimales)", Toast.LENGTH_LONG).show();
        }
    }
}