package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MisEventosDelactvActivity extends AppCompatActivity {
    List<listElement> elements;
    String tbUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos_delactv);
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
    }
    public void verEvento(View view){
        Intent intent=new Intent(this, EventoDetalleDelactvActivity.class);
        startActivity(intent);
    }
}