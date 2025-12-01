package com.example.proyectofinal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import db.DatabaseHelper;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DatabaseHelper db;
    private RestaurantAdapter adapter;
    private List<Restaurant> listaResultados;
    private double miLat = -20.2170, miLng = -70.1520; // Default Iquique

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = new DatabaseHelper(this);
        listaResultados = new ArrayList<>();
        rv = findViewById(R.id.rvSearchResults);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, listaResultados);
        rv.setAdapter(adapter);

        // Obtener GPS Real
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    miLat = loc.getLatitude();
                    miLng = loc.getLongitude();
                    performSearch(""); // Refrescar lista con distancia real
                }
            });
        }

        // Listener para escribir en tiempo real
        ((EditText)findViewById(R.id.etSearchQuery)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        listaResultados.clear();
        android.database.Cursor c = db.getAllRestaurants();
        String q = query.toLowerCase();

        if (c != null && c.moveToFirst()) {
            do {
                String nombre = c.getString(c.getColumnIndex(DatabaseHelper.COL_NOMBRE));
                String tipo = c.getString(c.getColumnIndex(DatabaseHelper.COL_TIPO));

                // Filtrar por Nombre O Tipo
                if (query.isEmpty() || nombre.toLowerCase().contains(q) || tipo.toLowerCase().contains(q)) {
                    int id = c.getInt(c.getColumnIndex(DatabaseHelper.COL_ID));
                    double lat = c.getDouble(c.getColumnIndex(DatabaseHelper.COL_LAT));
                    double lng = c.getDouble(c.getColumnIndex(DatabaseHelper.COL_LNG));

                    Restaurant r = new Restaurant(id, nombre, tipo, lat, lng);

                    // Calcular Distancia
                    float[] res = new float[1];
                    Location.distanceBetween(miLat, miLng, lat, lng, res);

                    // Formatear texto (ej: "A 500 metros")
                    if(res[0] < 1000) r.setDistanciaStr(String.format("A %.0f m", res[0]));
                    else r.setDistanciaStr(String.format("A %.1f km", res[0]/1000));

                    listaResultados.add(r);
                }
            } while (c.moveToNext());
            c.close();
        }

        // Ordenar por cercanía (usando Location)
        Collections.sort(listaResultados, (r1, r2) -> {
            float[] d1 = new float[1]; Location.distanceBetween(miLat, miLng, r1.getLat(), r1.getLng(), d1);
            float[] d2 = new float[1]; Location.distanceBetween(miLat, miLng, r2.getLat(), r2.getLng(), d2);
            return Float.compare(d1[0], d2[0]);
        });

        adapter.notifyDataSetChanged();
    }
}