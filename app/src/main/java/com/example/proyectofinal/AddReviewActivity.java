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
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import db.DatabaseHelper;

public class AddReviewActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private ImageView imageViewFoto;
    private EditText etComentario;
    private RatingBar ratingBar;
    private Button btnTomarFoto, btnGuardar;
    private DatabaseHelper db;

    private int currentRestaurantId = -1;
    private String fotoPathReal = ""; // Aquí guardaremos la ruta VERDADERA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        db = new DatabaseHelper(this);

        // Validar que recibimos el ID del restaurante
        if (getIntent().hasExtra("REST_ID_PARA_RESENA")) {
            currentRestaurantId = getIntent().getIntExtra("REST_ID_PARA_RESENA", -1);
        } else {
            Toast.makeText(this, "Error: Restaurante no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageViewFoto = findViewById(R.id.imageViewFoto);
        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBar);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                abrirCamara();
            }
        });

        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                // En algunos emuladores esto falla, intentamos lanzarlo igual
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                // 1. Obtenemos la foto en baja resolución (thumbnail)
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageViewFoto.setImageBitmap(imageBitmap);

                // 2. LA GUARDAMOS EN EL DISPOSITIVO (IMPORTANTE)
                fotoPathReal = guardarImagenEnDispositivo(imageBitmap);
            }
        }
    }

    // --- MÉTODO NUEVO: Guarda el Bitmap como archivo JPG ---
    private String guardarImagenEnDispositivo(Bitmap bitmap) {
        // Nombre único para la foto
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";

        // Guardamos en la carpeta privada de la app (no requiere permisos extra)
        File file = new File(getFilesDir(), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            // Comprimir y escribir
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath(); // Retornamos la ruta: /data/user/0/com.tuapp/files/IMG_123.jpg
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void validarYGuardar() {
        String comentario = etComentario.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (TextUtils.isEmpty(comentario)) {
            etComentario.setError("Escribe un comentario");
            return;
        }

        // Guardamos usando la ruta REAL que obtuvimos arriba
        boolean exito = db.addReview(currentRestaurantId, comentario, rating, fotoPathReal);

        if(exito){
            Toast.makeText(this, "Reseña guardada con foto", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}