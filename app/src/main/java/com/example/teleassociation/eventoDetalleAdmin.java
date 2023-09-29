package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class eventoDetalleAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle_admin);

        Button button = findViewById(R.id.apoyarEvento);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(eventoDetalleAdmin.this, crearEventoAdmin.class);
            startActivity(intent);
        });

        Button button2 = findViewById(R.id.apoyarEvento2);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(eventoDetalleAdmin.this, listaGeneralAdmin.class);
            startActivity(intent);
        });
    }
}