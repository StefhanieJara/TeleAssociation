package com.example.teleassociation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ParticipantesDelactv extends AppCompatActivity {
    List<ListaParticipantes> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantes_delactv);
        init();
    }
    public void init(){
        elements=new ArrayList<>();
        elements.add(new ListaParticipantes("Carlos Ayala","Alumno","Barra"));
        elements.add(new ListaParticipantes("Rafael Fuentes","Egresado", "Equipo"));
        elements.add(new ListaParticipantes("Carlos Ayala","Alumno","Barra"));
        elements.add(new ListaParticipantes("Rafael Fuentes","Egresado","Equipo"));
        elements.add(new ListaParticipantes("Carlos Ayala","Alumno","Barra"));

        ListAdaptParticipantes listAdaptParticipantes = new ListAdaptParticipantes(elements,this);
        RecyclerView recyclerView = findViewById(R.id.listaParticipantes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdaptParticipantes);
    }
}
