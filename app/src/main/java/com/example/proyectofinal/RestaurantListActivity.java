package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseHelper;

public class RestaurantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        db = new DatabaseHelper(this);
        restaurantList = new ArrayList<>();

        recyclerView = findViewById(R.id.rvAllRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddRestaurant);

        // Cargar datos de la BD
        loadRestaurants();

        // Configurar el botón flotante para ir a agregar restaurante
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, AddRestaurantActivity.class);
            startActivity(intent);
        });
    }

    // Método para refrescar la lista si volvemos de agregar uno nuevo
    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurants();
    }

    private void loadRestaurants() {
        restaurantList.clear(); // Limpiar lista anterior
        Cursor cursor = db.getAllRestaurants();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extraer datos usando los nombres de columnas de DatabaseHelper
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
                int typeIndex = cursor.getColumnIndex(DatabaseHelper.COL_TIPO);
                int latIndex = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int lngIndex = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

                // Verificación de seguridad
                if(idIndex != -1 && nameIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String type = (typeIndex != -1) ? cursor.getString(typeIndex) : "General";
                    double lat = (latIndex != -1) ? cursor.getDouble(latIndex) : 0.0;
                    double lng = (lngIndex != -1) ? cursor.getDouble(lngIndex) : 0.0;

                    // Agregar a la lista
                    restaurantList.add(new Restaurant(id, name, type, lat, lng));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Crear o notificar al adaptador
        if (adapter == null) {
            adapter = new RestaurantAdapter(this, restaurantList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}