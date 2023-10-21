package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.teleassociation.adapter.ListAdapEvent;
import com.example.teleassociation.adapter.ListAdaptEventosFinalizados;

import java.util.ArrayList;
import java.util.List;

public class MisEventosDelactvActivity extends AppCompatActivity {
    List<listElement> elements;
    List<listElementFin> elementFins;
    String tbUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos_delactv);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
    }
    public void init(){
        elements=new ArrayList<>();
        elements.add(new listElement("Ensayo de peña","15:20"));
        elements.add(new listElement("Ensayo de peña","16:20"));
        elements.add(new listElement("Ensayo de peña","13:20"));
        elements.add(new listElement("Ensayo de peña","12:20"));
        elements.add(new listElement("Ensayo de peña","14:20"));

        ListAdapEvent listAdapEvent = new ListAdapEvent(elements,this);
        RecyclerView recyclerView = findViewById(R.id.listMisEventos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapEvent);

        elementFins=new ArrayList<>();
        elementFins.add(new listElementFin("Ensayo de danza"));
        elementFins.add(new listElementFin("Ensayo de danza"));
        elementFins.add(new listElementFin("Ensayo de danza"));
        elementFins.add(new listElementFin("Ensayo de danza"));
        elementFins.add(new listElementFin("Ensayo de danza"));

        ListAdaptEventosFinalizados listAdaptEventosFinalizados = new ListAdaptEventosFinalizados(elementFins,this);
        RecyclerView recyclerView1 = findViewById(R.id.listEventosFinalizados);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(listAdaptEventosFinalizados);
    }
    public void verEvento(View view){
        Intent intent=new Intent(this, EventoDetalleDelactvActivity.class);
        startActivity(intent);
    }
}