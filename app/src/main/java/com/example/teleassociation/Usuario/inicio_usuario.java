package com.example.teleassociation.Usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class inicio_usuario extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    usuario usuario = new usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Agrega esta línea
        setContentView(R.layout.activity_inicio_usuario);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.d("msg-test", "El correo que ingresó es: "+email);

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            Log.d("msg-test", "task2 ha sido valido");
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String comentario = (String) document.get("comentario");
                                String condicion = (String) document.get("condicion");
                                String pass = (String) document.get("contrasenha");
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");
                                String validacion = (String) document.get("validado");
                                String rol = (String) document.get("rol");

                                if(correo.equals(email)){
                                    usuario.setComentario(comentario);
                                    usuario.setCondicion(condicion);
                                    usuario.setContrasenha(pass);
                                    usuario.setCorreo(correo);
                                    usuario.setId(codigo);
                                    usuario.setNombre(nombre);
                                    usuario.setRol(rol);
                                    usuario.setValidado(validacion);
                                    Log.d("msg-test", "| codigo: " + usuario.getId() + " | nombre: " + usuario.getNombre() + "| correo: "+ usuario.getCorreo() +" | condicion: " + usuario.getCondicion() + " | validacion: " + usuario.getValidado());
                                    break;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
        /*usuario usuario = (usuario) getIntent().getSerializableExtra("usuario");
        Log.d("msg-test", "El correo realmente es: "+usuario.getCorreo()+" y el codigo es: "+usuario.getId());*/

        if (getIntent().getBooleanExtra("Pago con éxito.", false)) {
            Toast.makeText(this, "Pago enviado. Esperar su confirmación.", Toast.LENGTH_SHORT).show();
        }

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(firstFragment);

        /*db.collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();

                        for (QueryDocumentSnapshot document : eventosCollection) {
                            String nombre = (String) document.get("nombre");
                            Date date = document.getDate("fecha");
                            String fechaSt = date.toString();
                            String[] partes = fechaSt.split(" ");
                            String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                            String hora = partes[3];
                            Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora);
                        }

                    }
                });*/



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
                FirebaseAuth.getInstance().signOut();
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