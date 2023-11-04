package com.example.teleassociation.adminActividad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.adminActividad.AdminActividadInicioFragment;
import com.example.teleassociation.adminActividad.CrearEventoFragment;
import com.example.teleassociation.adminActividad.DonacionesAdminActividadFragment;
import com.example.teleassociation.adminActividad.MisEventosCreadosFragment;
import com.example.teleassociation.listElement;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class ListaActividadesDelactvActivity extends AppCompatActivity {
    List<listElement> elements;

    FirebaseFirestore db;
    AdminActividadInicioFragment adminActividadInicioFragment = new AdminActividadInicioFragment();
    CrearEventoFragment crearEventoFragment = new CrearEventoFragment();
    DonacionesAdminActividadFragment donacionesAdminActividadFragment = new DonacionesAdminActividadFragment();
    MisEventosCreadosFragment misEventosCreadosFragment = new MisEventosCreadosFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_actividades_delactv);
        db = FirebaseFirestore.getInstance();
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
            if(item.getItemId()==R.id.fiveFragment){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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