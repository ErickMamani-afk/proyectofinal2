package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Importante
import androidx.recyclerview.widget.RecyclerView;     // Importante
import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private TextView tvName, tvType, tvLocation;
    private Button btnAddReview;
    private RecyclerView rvReviews; // Nuevo
    private DatabaseHelper db;
    private int restaurantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvDetailName);
        tvType = findViewById(R.id.tvDetailType);
        tvLocation = findViewById(R.id.tvDetailLocation);
        btnAddReview = findViewById(R.id.btnWriteReview);
        rvReviews = findViewById(R.id.rvReviews); // Nuevo

        // Configurar RecyclerView
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("REST_ID")) {
            restaurantId = getIntent().getIntExtra("REST_ID", -1);
            cargarDatosRestaurante(restaurantId);
        }

        btnAddReview.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantDetailActivity.this, AddReviewActivity.class);
            intent.putExtra("REST_ID_PARA_RESENA", restaurantId);
            startActivity(intent);
        });
    }

    // Usamos onResume para recargar las reseñas si el usuario acaba de agregar una
    @Override
    protected void onResume() {
        super.onResume();
        if (restaurantId != -1) {
            cargarResenas(restaurantId);
        }
    }

    private void cargarDatosRestaurante(int id) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_REST + " WHERE " + DatabaseHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
            int typeIndex = cursor.getColumnIndex(DatabaseHelper.COL_TIPO);
            int latIndex = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
            int lngIndex = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

            if (nameIndex != -1) {
                tvName.setText(cursor.getString(nameIndex));
                tvType.setText("Tipo: " + cursor.getString(typeIndex));
                tvLocation.setText("Ubicación: " + cursor.getDouble(latIndex) + ", " + cursor.getDouble(lngIndex));
            }
            cursor.close();

            // Cargar las reseñas también
            cargarResenas(id);
        }
    }

    private void cargarResenas(int id) {
        List<Review> lista = db.getReviewsByRestaurant(id);
        if (lista.isEmpty()) {
            // Opcional: Mostrar mensaje si no hay reseñas
        }
        ReviewAdapter adapter = new ReviewAdapter(lista);
        rvReviews.setAdapter(adapter);
    }
}