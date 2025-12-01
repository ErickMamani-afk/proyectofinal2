package com.example.proyectofinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import db.DatabaseHelper;

public class AddReviewActivity extends AppCompatActivity {

    private String fotoPathReal = "";
    private int restId;
    private DatabaseHelper db;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        db = new DatabaseHelper(this);
        restId = getIntent().getIntExtra("REST_ID_PARA_RESENA", -1);
        imgPreview = findViewById(R.id.imageViewFoto);

        // Botón Cámara
        findViewById(R.id.btnTomarFoto).setOnClickListener(v ->
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 101)
        );

        // Botón Guardar
        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String comment = ((EditText)findViewById(R.id.etComentario)).getText().toString();
            float rating = ((RatingBar)findViewById(R.id.ratingBar)).getRating();

            db.addReview(restId, comment, rating, fotoPathReal);
            finish();
        });
    }

    // Procesar foto de la cámara
    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == 101 && res == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(bitmap);

            // Guardar archivo físico
            try {
                File file = new File(getFilesDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                fotoPathReal = file.getAbsolutePath();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}