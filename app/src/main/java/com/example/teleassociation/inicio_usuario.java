package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.system_bar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.system_bar)));
        actionBar.setTitle("Teletoxic");
        getMenuInflater().inflate(R.menu.menu_1,menu);
        return true;
    }
}