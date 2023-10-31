package com.example.teleassociation.adminActividad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.teleassociation.R;
import com.example.teleassociation.adminActividad.AdminActividadInicioFragment;
import com.example.teleassociation.adminActividad.CrearEventoFragment;
import com.example.teleassociation.adminActividad.DonacionesAdminActividadFragment;
import com.example.teleassociation.adminActividad.MisEventosCreadosFragment;
import com.example.teleassociation.listElement;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ListaActividadesDelactvActivity extends AppCompatActivity {
    List<listElement> elements;

    AdminActividadInicioFragment adminActividadInicioFragment = new AdminActividadInicioFragment();
    CrearEventoFragment crearEventoFragment = new CrearEventoFragment();
    DonacionesAdminActividadFragment donacionesAdminActividadFragment = new DonacionesAdminActividadFragment();
    MisEventosCreadosFragment misEventosCreadosFragment = new MisEventosCreadosFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_admin_actividad);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(adminActividadInicioFragment);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.firstFragment){
                loadFragment(adminActividadInicioFragment);
                return true;
            }
            if(item.getItemId()==R.id.secondFragment){
                loadFragment(misEventosCreadosFragment);
                return true;
            }
            if(item.getItemId()==R.id.thirdFragment){
                loadFragment(crearEventoFragment);
                return true;
            }
            if(item.getItemId()==R.id.fourFragment){
                loadFragment(donacionesAdminActividadFragment);
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