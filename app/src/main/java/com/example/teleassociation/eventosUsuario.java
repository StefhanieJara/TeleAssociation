package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.ListAdapEvent;
import com.example.teleassociation.adapter.ListAdapEvent1;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoDTO;
import com.example.teleassociation.services.EventService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class eventosUsuario extends AppCompatActivity {

    List<listElement> elements;
    private static String TAG = "aqui estoy";
    EventService eventService;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_usuario);


        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    public void verEvento(View view){
        Intent intent=new Intent(this, eventoDetalleAlumno.class);
        startActivity(intent);
    }
}