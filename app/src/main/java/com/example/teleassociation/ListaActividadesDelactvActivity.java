package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ListaActividadesDelactvActivity extends AppCompatActivity {
    List<listElement> elements;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_actividades_delactv);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
    }
    public void init(){
        elements=new ArrayList<>();
        elements.add(new listElement("Practicar 2vs2","15:20"));
        elements.add(new listElement("Practicar 2vs2","16:20"));
        elements.add(new listElement("Practicar 2vs2","13:20"));
        elements.add(new listElement("Practicar 2vs2","12:20"));
        elements.add(new listElement("Practicar 2vs2","14:20"));

        ListAdapEvent listAdapEvent = new ListAdapEvent(elements,this);
        RecyclerView recyclerView = findViewById(R.id.listaActividades);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapEvent);
    }
    public void verEvento(View view){
        Intent intent=new Intent(this, EventoDetalleDelactvActivity.class);
        startActivity(intent);
    }
}