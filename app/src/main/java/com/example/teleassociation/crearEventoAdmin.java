package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class crearEventoAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento_admin);

        Button button12 = findViewById(R.id.button12);
        button12.setOnClickListener(v -> {
            Intent intent = new Intent(crearEventoAdmin.this, inicioAdmin.class);
            startActivity(intent);
        });
    }
}