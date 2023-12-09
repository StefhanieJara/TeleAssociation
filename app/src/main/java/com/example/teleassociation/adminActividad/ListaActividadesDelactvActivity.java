package com.example.teleassociation.adminActividad;

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
import android.widget.Toast;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.adminActividad.AdminActividadInicioFragment;
import com.example.teleassociation.adminActividad.CrearEventoFragment;
import com.example.teleassociation.adminActividad.DonacionesAdminActividadFragment;
import com.example.teleassociation.adminActividad.MisEventosCreadosFragment;
import com.example.teleassociation.adminActividad.ListaActividadesDelactvActivity;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.example.teleassociation.listElement;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class ListaActividadesDelactvActivity extends AppCompatActivity {
    List<listElement> elements;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    AdminActividadInicioFragment adminActividadInicioFragment = new AdminActividadInicioFragment();
    CrearEventoFragment crearEventoFragment = new CrearEventoFragment();
    DonacionesAdminActividadFragment donacionesAdminActividadFragment = new DonacionesAdminActividadFragment();
    MisEventosCreadosFragment misEventosCreadosFragment = new MisEventosCreadosFragment();
    usuario usuario = new usuario();
    TextView nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_actividades_delactv);
        if (getIntent().getBooleanExtra("Evento creado.", false)) {
            Toast.makeText(this, "Evento creado con éxito.", Toast.LENGTH_SHORT).show();
        }
        if (getIntent().getBooleanExtra("Pago con éxito.", false)) {
            Toast.makeText(this, "Pago enviado. Esperar su confirmación.", Toast.LENGTH_SHORT).show();
        }
        if (getIntent().getBooleanExtra("Evento actualizado.", false)) {
            Toast.makeText(this, "Evento actualizado con éxito.", Toast.LENGTH_SHORT).show();
        }
        db = FirebaseFirestore.getInstance();
        obtenerDatosUsuario(usuario -> {
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:
            nameUser = findViewById(R.id.nameUser);
            nameUser.setText(usuario.getNombre());

        });


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

    private void obtenerDatosUsuario(ListaActividadesDelactvActivity.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuario usuario = new usuario();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");

                                if (correo.equals(email)) {
                                    usuarioSesion.setId(codigo);
                                    usuarioSesion.setNombre(nombre);
                                    usuarioSesion.setCorreo(correo);
                                    // Llamada al método de la interfaz con el nombre del usuario
                                    callback.onCallback(usuarioSesion);
                                    return;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
    }

    public interface FirestoreCallback {
        void onCallback(usuarioSesion usuario);
    }

}