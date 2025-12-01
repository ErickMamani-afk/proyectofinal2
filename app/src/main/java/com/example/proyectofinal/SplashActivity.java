package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Carga el XML con el logo

        // Handler permite ejecutar código después de un tiempo (3000ms = 3 seg)
        new Handler().postDelayed(() -> {
            // Creamos el Intent para ir al Login
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            // 'finish()' cierra el Splash para que el usuario no pueda volver atrás
            finish();
        }, 3000);
    }
}