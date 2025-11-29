package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUser = findViewById(R.id.etUser);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();

            if (!user.isEmpty() && !pass.isEmpty()) {
                // Login exitoso (simulado)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}