package com.example.proyectofinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import db.DatabaseHelper;

public class EditReviewActivity extends AppCompatActivity {

    private int reviewId;
    private DatabaseHelper db;
    private EditText etComentario;
    private RatingBar ratingBar;
    private String newPhotoPath = null;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review); // Reusa layout
        db = new DatabaseHelper(this);

        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBar);
        imgPreview = findViewById(R.id.imageViewFoto);
        Button btn = findViewById(R.id.btnGuardar);
        btn.setText("Actualizar");

        // Cargar datos viejos
        reviewId = getIntent().getIntExtra("ID", -1);
        etComentario.setText(getIntent().getStringExtra("COMMENT"));
        ratingBar.setRating(getIntent().getFloatExtra("RATING", 0));
        String oldPhoto = getIntent().getStringExtra("PHOTO");
        if(oldPhoto != null) imgPreview.setImageURI(Uri.parse(oldPhoto));

        // Cámara (por si quiere cambiar la foto)
        findViewById(R.id.btnTomarFoto).setOnClickListener(v ->
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 102)
        );

        // Guardar cambios (Update)
        btn.setOnClickListener(v -> {
            db.updateReview(reviewId, etComentario.getText().toString(), ratingBar.getRating(), newPhotoPath);
            Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == 102 && res == RESULT_OK) {
            // Guardar nueva foto
            Bitmap b = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(b);
            try {
                File f = new File(getFilesDir(), "EDIT_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(f);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                newPhotoPath = f.getAbsolutePath();
            } catch (Exception e) {}
        }
    }
}