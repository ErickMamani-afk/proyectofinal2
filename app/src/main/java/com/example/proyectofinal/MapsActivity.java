package com.example.proyectofinal;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

// IMPORTACIONES DE GOOGLE MAPS
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker; // <--- ESTA FALTABA
import com.google.android.gms.maps.model.MarkerOptions;

import db.DatabaseHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 1. Cargar restaurantes existentes (Pines Rojos)
        Cursor cursor = db.getAllRestaurants();
        LatLng iquique = new LatLng(-20.2170, -70.1520); // Centro de Iquique

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idxId = cursor.getColumnIndex(DatabaseHelper.COL_ID);
                int idxNombre = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
                int idxLat = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int idxLng = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

                if (idxNombre != -1 && idxLat != -1 && idxLng != -1) {
                    int id = cursor.getInt(idxId);
                    String nombre = cursor.getString(idxNombre);
                    double lat = cursor.getDouble(idxLat);
                    double lng = cursor.getDouble(idxLng);

                    LatLng pos = new LatLng(lat, lng);

                    // Aquí usamos la clase Marker que faltaba importar
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .title(nombre));

                    if (marker != null) {
                        marker.setTag(id);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // 2. CLIC CORTO: Ir a RESEÑAR
        mMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag != null) {
                int restaurantId = (int) tag;
                Intent intent = new Intent(MapsActivity.this, AddReviewActivity.class);
                intent.putExtra("REST_ID_PARA_RESENA", restaurantId);
                startActivity(intent);
            }
            return true;
        });

        // 3. CLIC LARGO: Ir a AÑADIR RESTAURANTE
        mMap.setOnMapLongClickListener(latLng -> {
            Intent intent = new Intent(MapsActivity.this, AddRestaurantActivity.class);
            // Pasamos las coordenadas donde el usuario puso el dedo
            intent.putExtra("LAT_SELECCIONADA", latLng.latitude);
            intent.putExtra("LNG_SELECCIONADA", latLng.longitude);
            startActivity(intent);
        });

        // Mover cámara a Iquique
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iquique, 14f));
    }
}