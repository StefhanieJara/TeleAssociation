package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListaEventosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos_delactv);
        Button boton2 = findViewById(R.id.button3);
        Button boton3 = findViewById(R.id.button19);
        Button boton4 = findViewById(R.id.button5);

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaEventosActivity.this, EventoDetalleDelactvActivity.class);
                startActivity(intent);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaEventosActivity.this, MasEventosDelactvActivity.class);
                startActivity(intent);
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaEventosActivity.this, MasEventosDelactvActivity.class);
                startActivity(intent);
            }
        });
    }

}