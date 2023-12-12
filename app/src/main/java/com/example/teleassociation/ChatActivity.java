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
import android.widget.Button;
import android.widget.TextView;

import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.Usuario.SecondFragment;
import com.example.teleassociation.Usuario.ThirdFragment;
import com.example.teleassociation.adminActividad.ChatFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends AppCompatActivity {

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Asegúrate de que el layout coincide con tu actividad

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Obtener datos necesarios, por ejemplo, desde el Intent
        Intent intent = getIntent();
        //String nombreEvento = intent.getStringExtra("nombreEvento");
        String idDocumento = intent.getStringExtra("idDocumento");
        String nombreEvento =intent.getStringExtra("nombreEvento");
        // Crear una instancia de ChatFragment y asignarle argumentos
        ChatFragment chatFragment = ChatFragment.newInstance(idDocumento,nombreEvento);

        // Reemplazar o agregar el fragmento en la actividad
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, chatFragment) // Asegúrate de usar el ID correcto del contenedor
                .commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.firstFragment) {
                loadFragment(firstFragment);
                return true;
            }
            if (item.getItemId() == R.id.secondFragment) {
                loadFragment(secondFragment);
                return true;
            }
            if (item.getItemId() == R.id.thirdFragment) {
                loadFragment(thirdFragment);
                return true;
            }
            if (item.getItemId() == R.id.fourFragment) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    };


    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }
}

