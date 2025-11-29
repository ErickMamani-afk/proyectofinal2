package com.example.proyectofinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
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

    // ID por defecto, se actualizará con el Intent
    private int currentRestaurantId = -1;
    private String fotoPathTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        db = new DatabaseHelper(this);

        // 1. Recibir el ID del Restaurante desde la actividad anterior
        if (getIntent().hasExtra("REST_ID_PARA_RESENA")) {
            currentRestaurantId = getIntent().getIntExtra("REST_ID_PARA_RESENA", -1);
        } else {
            Toast.makeText(this, "Error: No se identificó el restaurante.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no hay ID
            return;
        }

        // Referencias a la UI
        imageViewFoto = findViewById(R.id.imageViewFoto);
        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBar);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Opcional: Mostrar ID en log o título para verificar
        setTitle("Reseñando Restaurante #" + currentRestaurantId);

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
            validarYGuardar();
        });
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validarYGuardar() {
        String comentario = etComentario.getText().toString().trim();
        float rating = ratingBar.getRating();

        // 2. Validaciones antes de guardar
        if (TextUtils.isEmpty(comentario)) {
            etComentario.setError("Escribe un comentario por favor");
            return;
        }

        if (rating == 0) {
            Toast.makeText(this, "Por favor selecciona una calificación de estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar
        boolean exito = db.addReview(currentRestaurantId, comentario, rating, fotoPathTemp);

        if(exito){
            Toast.makeText(this, "¡Reseña guardada!", Toast.LENGTH_LONG).show();

            // Opcional: Volver directamente al detalle del restaurante
            finish();
        } else {
            Toast.makeText(this, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageViewFoto.setImageBitmap(imageBitmap);

                // NOTA: Aquí simulamos la ruta. Para un proyecto profesional,
                // deberías guardar el Bitmap en un archivo File y obtener su Uri real.
                fotoPathTemp = "foto_capturada_" + System.currentTimeMillis() + ".jpg";

                Toast.makeText(this, "Foto capturada temporalmente", Toast.LENGTH_SHORT).show();
            }
        }
    }
}