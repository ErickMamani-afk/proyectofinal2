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
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvAllRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarLista(); // Cargar datos

        // Botón Flotante (+) para agregar restaurante manualmente
        findViewById(R.id.fabAddRestaurant).setOnClickListener(v ->
                startActivity(new Intent(this, AddRestaurantActivity.class))
        );
    }

    @Override protected void onResume() { super.onResume(); cargarLista(); }

    private void cargarLista() {
        List<Restaurant> lista = new ArrayList<>();
        Cursor cursor = db.getAllRestaurants();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID));
                String nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE));
                String tipo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TIPO));
                double lat = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_LNG));

                // Creamos objeto Restaurant
                lista.add(new Restaurant(id, nombre, tipo, lat, lng));
            } while (cursor.moveToNext());
            cursor.close();
        }
        // Asignamos el adaptador
        recyclerView.setAdapter(new RestaurantAdapter(this, lista));
    }
}