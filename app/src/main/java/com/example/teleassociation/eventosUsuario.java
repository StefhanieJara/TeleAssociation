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

        eventService = new Retrofit.Builder()
                .baseUrl("http://10.100.114.139:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EventService.class);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        cargarListaEventosWS();
    }

    public void cargarListaEventosWS() {
        eventService.getEventList().enqueue(new Callback<eventoDTO>() {
            @Override
            public void onResponse(Call<eventoDTO> call, Response<eventoDTO> response) {
                if (response.isSuccessful()) {
                    eventoDTO body = response.body();
                    List<evento> eventoList = body.getLista();

                    ListAdapEvent1 listAdapEvent = new ListAdapEvent1();
                    listAdapEvent.setEventList(eventoList);
                    listAdapEvent.setContext(eventosUsuario.this);

                    // Inicializa el RecyclerView y el adaptador
                    recyclerView = findViewById(R.id.listRecyclerView10);
                    recyclerView.setAdapter(listAdapEvent);
                    recyclerView.setLayoutManager(new LinearLayoutManager(eventosUsuario.this));


                } else {
                    Log.d(TAG, "response unsuccessful");
                }
            }
            @Override
            public void onFailure(Call<eventoDTO> call, Throwable t) {
                Log.d(TAG, "algo pasó!!!");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }



    public void verEvento(View view){
        Intent intent=new Intent(this, eventoDetalleAlumno.class);
        startActivity(intent);
    }
}