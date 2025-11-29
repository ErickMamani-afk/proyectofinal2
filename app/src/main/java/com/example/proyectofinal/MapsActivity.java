package com.example.proyectofinal;

import androidx.fragment.app.FragmentActivity;
import android.database.Cursor;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Asegúrate que este XML tenga el fragmento de mapa

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

        // Leer todos los restaurantes y poner pines
        Cursor cursor = db.getAllRestaurants();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Índices seguros
                int idxNombre = cursor.getColumnIndex(DatabaseHelper.COL_NOMBRE);
                int idxLat = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int idxLng = cursor.getColumnIndex(DatabaseHelper.COL_LNG);

                if (idxNombre != -1 && idxLat != -1 && idxLng != -1) {
                    String nombre = cursor.getString(idxNombre);
                    double lat = cursor.getDouble(idxLat);
                    double lng = cursor.getDouble(idxLng);

                    LatLng pos = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(pos).title(nombre));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}