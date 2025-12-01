package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import db.DatabaseHelper;

public class RestaurantDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int restaurantId;
    private TextView tvName, tvType, tvLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        db = new DatabaseHelper(this);

        restaurantId = getIntent().getIntExtra("REST_ID", -1);
        tvName = findViewById(R.id.tvDetailName);
        tvType = findViewById(R.id.tvDetailType);
        tvLoc = findViewById(R.id.tvDetailLocation);

        cargarDatos();

        // Configurar lista de reseñas
        RecyclerView rv = findViewById(R.id.rvReviews);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Botón "Escribir Reseña"
        findViewById(R.id.btnWriteReview).setOnClickListener(v -> {
            Intent i = new Intent(this, AddReviewActivity.class);
            i.putExtra("REST_ID_PARA_RESENA", restaurantId);
            startActivity(i);
        });
    }

    @Override protected void onResume() { super.onResume(); cargarResenas(); }

    private void cargarDatos() {
        Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_REST + " WHERE id=" + restaurantId, null);
        if (c.moveToFirst()) {
            tvName.setText(c.getString(c.getColumnIndex(DatabaseHelper.COL_NOMBRE)));
            tvType.setText(c.getString(c.getColumnIndex(DatabaseHelper.COL_TIPO)));
            tvLoc.setText("GPS: " + c.getDouble(c.getColumnIndex(DatabaseHelper.COL_LAT)) + ", " + c.getDouble(c.getColumnIndex(DatabaseHelper.COL_LNG)));
        }
        c.close();
        cargarResenas();
    }

    private void cargarResenas() {
        // Obtenemos reseñas y configuramos el "Long Click" para editar/borrar
        ReviewAdapter adapter = new ReviewAdapter(db.getReviewsByRestaurant(restaurantId), review -> {
            new AlertDialog.Builder(this)
                    .setTitle("Opciones")
                    .setItems(new String[]{"Editar", "Borrar"}, (dialog, which) -> {
                        if (which == 0) { // Editar
                            Intent i = new Intent(this, EditReviewActivity.class);
                            i.putExtra("ID", review.getId());
                            i.putExtra("COMMENT", review.getComentario());
                            i.putExtra("RATING", review.getRating());
                            i.putExtra("PHOTO", review.getFotoUri());
                            startActivity(i);
                        } else { // Borrar
                            db.deleteReview(review.getId());
                            cargarResenas(); // Refrescar
                        }
                    }).show();
        });
        ((RecyclerView)findViewById(R.id.rvReviews)).setAdapter(adapter);
    }
}