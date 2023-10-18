package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class inicio_usuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_usuario);
    }
    public void verMasEventos(View view){
        Intent intent=new Intent(this, misEventosUsuario.class);
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