package com.example.proyectofinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddReviewActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private ImageView imageViewFoto;
    private EditText etComentario;
    private RatingBar ratingBar;
    private Button btnTomarFoto, btnGuardar;
    private DatabaseHelper db;

    // ID del restaurante que estamos reseñando (Debería venir por Intent)
    private int currentRestaurantId = 1;
    private String fotoPathTemp = ""; // Simulación de ruta de archivo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review); // Recuerda crear este layout XML

        db = new DatabaseHelper(this);

        // Referencias a la UI (Asegúrate que los IDs coincidan en tu XML)
        imageViewFoto = findViewById(R.id.imageViewFoto);
        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBar);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Lógica de la Cámara
        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                abrirCamara();
            }
        });

        // Guardar en BD
        btnGuardar.setOnClickListener(v -> {
            String comentario = etComentario.getText().toString();
            float rating = ratingBar.getRating();

            boolean exito = db.addReview(currentRestaurantId, comentario, rating, fotoPathTemp);
            if(exito){
                Toast.makeText(this, "Reseña guardada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewFoto.setImageBitmap(imageBitmap);
            // Aquí deberías guardar el bitmap en el almacenamiento interno y obtener la ruta real
            fotoPathTemp = "ruta/ficticia/imagen.jpg";
        }
    }
}