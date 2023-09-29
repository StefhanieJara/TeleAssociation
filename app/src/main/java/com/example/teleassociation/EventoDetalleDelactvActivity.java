package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EventoDetalleDelactvActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle_delactv);
        Button boton2 = findViewById(R.id.verActividades);
        Button boton3 = findViewById(R.id.verParticipantes);
        Button boton4 = findViewById(R.id.donar);
        Button boton5 = findViewById(R.id.editarEvento);

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, ListaActividadesDelactvActivity.class);
                startActivity(intent);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, ParticipantesDelactv.class);
                startActivity(intent);
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, DonacionDelactvActivity.class);
                startActivity(intent);
            }
        });

        boton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, EditarEventoDelactvActivity.class);
                startActivity(intent);
            }
        });
    }
}