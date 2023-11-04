package com.example.teleassociation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.Usuario.SecondFragment;
import com.example.teleassociation.Usuario.ThirdFragment;
import com.example.teleassociation.pagosAlumno;
import com.example.teleassociation.subirFotoEventAlum;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class eventoDetalleAlumno extends AppCompatActivity {
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle_alumno);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





    }
    public void apoyarEvento(View view){
        Intent intent=new Intent(this, pagosAlumno.class);
        startActivity(intent);
    }
    public void nuevaFoto(View view){
        Intent intent=new Intent(this, subirFotoEventAlum.class);
        startActivity(intent);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.firstFragment){
                loadFragment(firstFragment);
                return true;
            }
            if(item.getItemId()==R.id.secondFragment){
                loadFragment(secondFragment);
                return true;
            }
            if(item.getItemId()==R.id.thirdFragment){
                loadFragment(thirdFragment);
                return true;
            }
            return false;
        }
    };




    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        transaction.commit();

    }

}