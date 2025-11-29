package com.example.proyectofinal;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchNotif = findViewById(R.id.switchNotif);
        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) Toast.makeText(this, "Notificaciones Activadas", Toast.LENGTH_SHORT).show();
        });
    }
}