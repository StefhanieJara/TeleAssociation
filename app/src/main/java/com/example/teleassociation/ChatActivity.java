package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.teleassociation.adminActividad.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Asegúrate de que el layout coincide con tu actividad

        // Obtener datos necesarios, por ejemplo, desde el Intent
        Intent intent = getIntent();
        //String nombreEvento = intent.getStringExtra("nombreEvento");
        String idDocumento = intent.getStringExtra("idDocumento");

        // Crear una instancia de ChatFragment y asignarle argumentos
        ChatFragment chatFragment = ChatFragment.newInstance(idDocumento);

        // Reemplazar o agregar el fragmento en la actividad
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, chatFragment) // Asegúrate de usar el ID correcto del contenedor
                .commit();
    }
}
