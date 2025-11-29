package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RestaurantDetailActivity extends AppCompatActivity {

    private TextView tvName, tvType, tvLocation;
    private Button btnAddReview;
    private DatabaseHelper db;
    private int restaurantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail); // Crearemos este XML abajo

        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvDetailName);
        tvType = findViewById(R.id.tvDetailType);
        tvLocation = findViewById(R.id.tvDetailLocation);
        btnAddReview = findViewById(R.id.btnWriteReview);

        // Recibir el ID que nos mandó el Adaptador
        if (getIntent().hasExtra("REST_ID")) {
            restaurantId = getIntent().getIntExtra("REST_ID", -1);
            cargarDatosRestaurante(restaurantId);
        }

        // Botón para ir a escribir una reseña
        btnAddReview.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantDetailActivity.this, AddReviewActivity.class);
            // Pasamos el ID del restaurante a la pantalla de reseña para saber a quién reseñamos
            intent.putExtra("REST_ID_PARA_RESENA", restaurantId);
            startActivity(intent);
        });
    }

    private void cargarDatosRestaurante(int id) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_REST + " WHERE " + DatabaseHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Índices de columnas
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
            int typeIndex = cursor.getColumnIndex(DatabaseHelper.COL_TIPO);
            int latIndex = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
            int lngIndex = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

            if (nameIndex != -1) {
                String name = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                double lat = cursor.getDouble(latIndex);
                double lng = cursor.getDouble(lngIndex);

                tvName.setText(name);
                tvType.setText("Tipo: " + type);
                tvLocation.setText("Ubicación: " + lat + ", " + lng);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Restaurante no encontrado", Toast.LENGTH_SHORT).show();
        }
    }
}