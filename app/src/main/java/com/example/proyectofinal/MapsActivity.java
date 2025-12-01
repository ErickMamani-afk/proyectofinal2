package com.example.proyectofinal;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
// Imports de Google Maps
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

        // Cargar el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // A. Cargar restaurantes de SQLite
        Cursor cursor = db.getAllRestaurants();
        LatLng iquique = new LatLng(-20.2170, -70.1520); // Centro de Iquique

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID));
                String nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE));
                double lat = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_LNG));

                // Crear Pin
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(nombre));
                marker.setTag(id); // Guardamos el ID dentro del pin
            } while (cursor.moveToNext());
            cursor.close();
        }

        // B. Clic Corto: Ir a Reseñar
        mMap.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(MapsActivity.this, AddReviewActivity.class);
            intent.putExtra("REST_ID_PARA_RESENA", (int) marker.getTag());
            startActivity(intent);
            return true;
        });

        // C. Clic Largo: Añadir nuevo Restaurante
        mMap.setOnMapLongClickListener(latLng -> {
            Toast.makeText(this, "Añadiendo nuevo lugar...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MapsActivity.this, AddRestaurantActivity.class);
            // Pasamos las coordenadas exactas donde se hizo clic
            intent.putExtra("LAT_SELECCIONADA", latLng.latitude);
            intent.putExtra("LNG_SELECCIONADA", latLng.longitude);
            startActivity(intent);
        });

        // Centrar cámara
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iquique, 14f));
    }
}