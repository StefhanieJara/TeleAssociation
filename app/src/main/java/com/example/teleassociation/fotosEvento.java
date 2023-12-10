package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.teleassociation.adapter.FotosEventoAdapter;
import com.example.teleassociation.dto.EventoFoto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class fotosEvento extends AppCompatActivity {

    private ViewPager2 viewPagerFotos;
    private FotosEventoAdapter fotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_evento);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        viewPagerFotos = findViewById(R.id.viewPagerFotos);

        // Obtener el ID del evento desde la intención
        String eventoId = getIntent().getStringExtra("eventoId");

        // Obtener la lista de fotos del evento desde Firebase
        obtenerListaDeFotosDesdeFirebase(eventoId);

    }


    private void obtenerListaDeFotosDesdeFirebase(String eventoId) {
        FirebaseFirestore.getInstance()
                .collection("eventoFotos")
                .whereEqualTo("eventoId", eventoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<EventoFoto> fotos = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String url = document.getString("url_imagen");
                        fotos.add(new EventoFoto(document.getId(), url));
                    }
                    configurarViewPager(fotos);
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al obtener fotos desde Firebase
                    e.printStackTrace();
                });
    }

    // Método para configurar el ViewPager2 con el adaptador
    private void configurarViewPager(List<EventoFoto> fotos) {
        // Configurar el adaptador
        fotosAdapter = new FotosEventoAdapter(fotos);
        viewPagerFotos.setAdapter(fotosAdapter);
    }
}