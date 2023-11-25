package com.example.teleassociation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.Usuario.SecondFragment;
import com.example.teleassociation.Usuario.ThirdFragment;
import com.example.teleassociation.databinding.ActivityEventoDetalleAlumnoBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class eventoDetalleAlumno extends AppCompatActivity {
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FirebaseFirestore db;

    private String fechaEvento;
    private String nombreEvento;

    private String urlImagenEvento;  // Declarar la variable
    private Date date;
    private String apoyosEvento;
    private String descripcion;
    ActivityEventoDetalleAlumnoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEventoDetalleAlumnoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String eventoId = intent.getStringExtra("eventoId");
        Log.d("msg-test", "Llegó el codigo: "+eventoId);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        db=FirebaseFirestore.getInstance();
        if (actionBar != null) {
            actionBar.hide();
        }

        db.collection("eventos")
                .document(eventoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtén los datos del documento
                        nombreEvento = documentSnapshot.getString("nombre");
                        date = documentSnapshot.getDate("fecha");
                        apoyosEvento = (String) documentSnapshot.get("apoyos");
                        descripcion = (String) documentSnapshot.get("descripcion");
                        // Actualiza la vista con la información obtenida
                        updateUIWithEventData(documentSnapshot);

                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja posibles errores aquí
                });
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            if(item.getItemId()==R.id.fourFragment){
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
    private void updateUIWithEventData(DocumentSnapshot documentSnapshot) {
        // Actualiza los elementos de la vista con los valores de fechaEvento y apoyosEvento
        TextView nombreTexView = findViewById(R.id.nombreEvento);
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView horaTextView = findViewById(R.id.hora);
        TextView apoyosTextView = findViewById(R.id.cantApoyos);
        TextView descripcionTextView = findViewById(R.id.decripcionEvento);
        ImageView imageViewEvento = findViewById(R.id.imagenView);  // Asegúrate de tener este ID en tu XML
        urlImagenEvento = documentSnapshot.getString("url_imagen");




        nombreTexView.setText(nombreEvento);
        String fechaSt = date.toString();
        String[] partesFechaHora = fechaSt.split(" ");
        if (partesFechaHora.length >= 2) {
            fechaTextView.setText("Fecha: " + partesFechaHora[0] + " " + partesFechaHora[1] + " " + partesFechaHora[2]);
            horaTextView.setText("Hora: " + partesFechaHora[3]);
        } else {
            fechaTextView.setText("Fecha: No disponible");
            horaTextView.setText("Hora: No disponible");
        }
        apoyosTextView.setText("Apoyos: " + apoyosEvento);
        descripcionTextView.setText(descripcion);

        if (urlImagenEvento != null && !urlImagenEvento.isEmpty()) {
            Glide.with(this)
                    .load(urlImagenEvento)
                    .into(imageViewEvento);
        }

    }


}