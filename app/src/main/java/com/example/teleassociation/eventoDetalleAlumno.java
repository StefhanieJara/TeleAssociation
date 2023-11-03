package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.teleassociation.R;
import com.example.teleassociation.pagosAlumno;
import com.example.teleassociation.subirFotoEventAlum;

public class eventoDetalleAlumno extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle_alumno);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    public void apoyarEvento(View view){
        Intent intent=new Intent(this, pagosAlumno.class);
        startActivity(intent);
    }
    public void nuevaFoto(View view){
        Intent intent=new Intent(this, subirFotoEventAlum.class);
        startActivity(intent);
    }

}