package com.example.teleassociation.adminGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class donacionesAdmin extends AppCompatActivity {
    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView nameUser;
    usuario usuario = new usuario();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donaciones_admin);

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
                            nameUser = findViewById(R.id.nameUser);
                            Log.d("msg-test", "El nombre del usuario es: "+usuario.getNombre());
                            nameUser.setText(usuario.getNombre());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Ocultar barra de título

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.firstFragment){
                loadFragment(adminGeneralInicioFragment);
                return true;
            }
            if(item.getItemId()==R.id.secondFragment){
                loadFragment(listaActividadesGeneralFragment);
                return true;
            }
            if(item.getItemId()==R.id.thirdFragment){
                loadFragment(crearActividadFragment);
                return true;
            }
            if(item.getItemId()==R.id.fourFragment){
                loadFragment(personasGeneralFragment);
                return true;
            }
            if(item.getItemId()==R.id.sixtFragment){
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