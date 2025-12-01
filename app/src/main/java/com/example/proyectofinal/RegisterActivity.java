package com.example.proyectofinal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import db.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUser, etRegPass, etRegConfirmPass;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etRegUser = findViewById(R.id.etRegUser);
        etRegPass = findViewById(R.id.etRegPass);
        etRegConfirmPass = findViewById(R.id.etRegConfirmPass);
        btnRegister = findViewById(R.id.btnRegisterAction);

        btnRegister.setOnClickListener(v -> {
            String user = etRegUser.getText().toString().trim();
            String pass = etRegPass.getText().toString().trim();
            String confirm = etRegConfirmPass.getText().toString().trim();

            if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                // Intentar registrar
                boolean exito = db.registerUser(user, pass);
                if(exito) {
                    Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    finish(); // Vuelve al Login
                } else {
                    Toast.makeText(this, "Error: El usuario ya existe o falló la BD", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}