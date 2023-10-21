package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.teleassociation.adapter.ActividadAdapter;
import com.example.teleassociation.adapter.listaUsuarioAdminAdaptador;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.actividadDTO;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioDTO;
import com.example.teleassociation.services.ActividadService;
import com.example.teleassociation.services.UsuarioService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class listaGeneralActividadAdmin extends AppCompatActivity {

    UsuarioService usuarioService;
    private RecyclerView recyclerView;
    private static String TAG = "aqui estoy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_general_actividad_admin);

        usuarioService = new Retrofit.Builder()
                .baseUrl("http://192.168.18.193:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UsuarioService.class);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        cargarListaUsuariosWS();
    }

    public void cargarListaUsuariosWS() {
        usuarioService.getUsuarios().enqueue(new Callback<usuarioDTO>() {
            @Override
            public void onResponse(Call<usuarioDTO> call, Response<usuarioDTO> response) {
                if (response.isSuccessful()) {
                    usuarioDTO body = response.body();
                    List<usuario> usuarioList = body.getLista();

                    listaUsuarioAdminAdaptador usuarioAdminAdaptador = new listaUsuarioAdminAdaptador();
                    usuarioAdminAdaptador.setUsuarioList(usuarioList);
                    usuarioAdminAdaptador.setContext(listaGeneralActividadAdmin.this);

                    // Inicializa el RecyclerView y el adaptador
                    recyclerView = findViewById(R.id.listRecyclerListaGeneralUsuario);
                    recyclerView.setAdapter(usuarioAdminAdaptador);
                    recyclerView.setLayoutManager(new LinearLayoutManager(listaGeneralActividadAdmin.this));

                } else {
                    Log.d(TAG, "response unsuccessful");
                }
            }
            @Override
            public void onFailure(Call<usuarioDTO> call, Throwable t) {
                Log.d(TAG, "algo pasó!!!");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }
}