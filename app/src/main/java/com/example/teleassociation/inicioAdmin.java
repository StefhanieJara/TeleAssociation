package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.teleassociation.adapter.ActividadAdapter;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.actividadDTO;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoDTO;
import com.example.teleassociation.services.ActividadService;
import com.example.teleassociation.services.EventService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class inicioAdmin extends AppCompatActivity {

    ActividadService actividadService;
    private RecyclerView recyclerView;

    private static String TAG = "aqui estoy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_admin);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button button3 = findViewById(R.id.btnVerUsuarios);
        button3.setOnClickListener(v -> {
            Intent intent = new Intent(inicioAdmin.this, listaGeneralActividadAdmin.class);
            startActivity(intent);
        });


    }

    public void crearActividad(View view){
        Intent intent=new Intent(this, CrearActividadDelactvActivity.class);
        startActivity(intent);
    }

}