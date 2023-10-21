package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoDTO;
import com.example.teleassociation.services.EventService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class inicio_usuario extends AppCompatActivity {

    EventService eventService;

    private RecyclerView recyclerView;

    private static String TAG = "aqui estoy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Agrega esta línea
        setContentView(R.layout.activity_inicio_usuario);

        eventService = new Retrofit.Builder()
                .baseUrl("http://192.168.1.40:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EventService.class);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Llama al método para cargar la lista de eventos
        cargarListaEventosWS();
    }

    public void cargarListaEventosWS() {
        eventService.getEventList().enqueue(new Callback<eventoDTO>() {
            @Override
            public void onResponse(Call<eventoDTO> call, Response<eventoDTO> response) {
                if (response.isSuccessful()) {
                    eventoDTO body = response.body();
                    List<evento> eventoList = body.getLista();

                    EventAdapter eventAdapter = new EventAdapter();
                    eventAdapter.setEventList(eventoList);
                    eventAdapter.setContext(inicio_usuario.this);

                    // Inicializa el RecyclerView y el adaptador
                    recyclerView = findViewById(R.id.listRecyclerActividad);
                    recyclerView.setAdapter(eventAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(inicio_usuario.this));

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


    public void verMasEventos(View view){
        Intent intent=new Intent(this, eventosUsuario.class);
        startActivity(intent);
    }

    public void favoritos(View view){
        Intent intent=new Intent(this, misEventosUsuario.class);
        startActivity(intent);
    }
    public void inicio (View view){
        Intent intent=new Intent(this, inicio_usuario.class);
        startActivity(intent);
    }
    public void donacion(View view){
        Intent intent=new Intent(this, pagosAlumno.class);
        startActivity(intent);
    }
}