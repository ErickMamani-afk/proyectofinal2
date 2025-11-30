package com.example.proyectofinal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher; // Importante para búsqueda en tiempo real
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private Button btnSearchAction;
    private RecyclerView rvSearchResults;
    private RestaurantAdapter adapter;
    private List<Restaurant> searchResults;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    // Ubicación por defecto (se actualizará con tu GPS real)
    private double miLat = -20.2170;
    private double miLng = -70.1520;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        searchResults = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this, searchResults);
        rvSearchResults.setAdapter(adapter);

        // 1. Obtener GPS y cargar lista AUTOMÁTICAMENTE al abrir
        obtenerUbicacionGPS();

        // 2. Búsqueda manual con Botón
        btnSearchAction.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            performSearch(query);
        });

        // 3. OPCIONAL: Búsqueda automática mientras escribes (Tiempo real)
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString()); // Busca cada vez que escribes una letra
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void obtenerUbicacionGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        miLat = location.getLatitude();
                        miLng = location.getLongitude();
                        // Al tener el GPS, actualizamos la lista vacía ("") para mostrar TODO lo cercano
                        performSearch("");
                    } else {
                        // Si falla el GPS, cargamos igual con la ubicación por defecto
                        performSearch("");
                    }
                });
    }

    private void performSearch(String query) {
        searchResults.clear();
        android.database.Cursor cursor = dbHelper.getAllRestaurants();

        String queryMinuscula = query.toLowerCase(); // Para ignorar mayúsculas/minúsculas

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idxId = cursor.getColumnIndex(DatabaseHelper.COL_ID);
                int idxNombre = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
                int idxTipo = cursor.getColumnIndex(DatabaseHelper.COL_TIPO);
                int idxLat = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int idxLng = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

                if (idxNombre != -1) {
                    String nombre = cursor.getString(idxNombre);
                    String tipo = cursor.getString(idxTipo); // Ej: "Pizzería", "Comida China"

                    // --- LÓGICA DE FILTRADO MEJORADA ---
                    // Aceptamos si:
                    // 1. La búsqueda está vacía (Muestra todo)
                    // 2. El NOMBRE contiene lo escrito (Ej: "Domino's")
                    // 3. El TIPO contiene lo escrito (Ej: "Pizza")
                    if (query.isEmpty() ||
                            nombre.toLowerCase().contains(queryMinuscula) ||
                            tipo.toLowerCase().contains(queryMinuscula)) {

                        int id = cursor.getInt(idxId);
                        double lat = cursor.getDouble(idxLat);
                        double lng = cursor.getDouble(idxLng);

                        Restaurant rest = new Restaurant(id, nombre, tipo, lat, lng);

                        // Calcular distancia
                        float[] results = new float[1];
                        Location.distanceBetween(miLat, miLng, lat, lng, results);
                        float distanciaMetros = results[0];

                        // Formatear texto de distancia
                        if (distanciaMetros < 1000) {
                            rest.setDistanciaStr(String.format("A %.0f metros", distanciaMetros));
                        } else {
                            rest.setDistanciaStr(String.format("A %.1f km", distanciaMetros / 1000));
                        }

                        searchResults.add(rest);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // --- ORDENAMIENTO AUTOMÁTICO POR CERCANÍA ---
        Collections.sort(searchResults, (r1, r2) -> {
            float[] res1 = new float[1];
            float[] res2 = new float[1];
            Location.distanceBetween(miLat, miLng, r1.getLat(), r1.getLng(), res1);
            Location.distanceBetween(miLat, miLng, r2.getLat(), r2.getLng(), res2);
            return Float.compare(res1[0], res2[0]);
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionGPS();
        }
    }
}