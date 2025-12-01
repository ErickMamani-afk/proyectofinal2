package com.example.proyectofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import db.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUser, etPass, etConfirm;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        etUser = findViewById(R.id.etRegUser);
        etPass = findViewById(R.id.etRegPass);
        etConfirm = findViewById(R.id.etRegConfirmPass);
        btnRegister = findViewById(R.id.btnRegisterAction);

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String conf = etConfirm.getText().toString().trim();

            // Validaciones
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(conf)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar en BD
                if (db.registerUser(user, pass)) {
                    Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // Vuelve al login automáticamente
                } else {
                    Toast.makeText(this, "Error: El usuario quizás ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}