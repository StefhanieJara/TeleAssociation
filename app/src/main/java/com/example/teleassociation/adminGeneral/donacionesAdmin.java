package com.example.teleassociation.adminGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.adapter.AdminGeneralInicioAdapter;
import com.example.teleassociation.adapter.PersonasGeneralAdapter;
import com.example.teleassociation.adapter.donacionesAdminAdapter;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.notificacion;
import com.example.teleassociation.dto.pagos;
import com.example.teleassociation.dto.usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class donacionesAdmin extends AppCompatActivity {
    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();

    FirebaseFirestore db;
    RecyclerView recyclerView;
    TextInputLayout CondicionDonacion;

    List<pagos> donacionesLista = new ArrayList<>();


    FirebaseAuth mAuth;
    TextView nameUser;
    usuario usuario = new usuario();

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        donacionesLista.clear();
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

        recyclerView = findViewById(R.id.listaDonaciones);

        cargarDatosDesdeFirebase();

        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(donacionesLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());  // Establecer el ID del documento
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setMonto(monto);
                                pagos.setValidado(validado);
                                pagos.setUrl_imagen(url_imagen);
                                donacionesLista.add(pagos);

                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);


                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });

        String[] opciones = {"Total","Validado" ,"Invalidado", "Pendiente"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CondicionDonacion = findViewById(R.id.CondicionDonacion);
        spinner = findViewById(R.id.spinnerCondicionDonacion);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = opciones[position];

                if ("Total".equals(selectedOption)) {
                    Total();
                } else if("Validado".equals(selectedOption)) {
                    Validado();
                } else if("Invalidado".equals(selectedOption)) {
                    Invalidado();
                } else {
                    Pendiente();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Método necesario pero no utilizado en este caso
            }
        });

        ImageView btnStats = findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(donacionesAdmin.this, estadisticasAdmin.class);
                startActivity(intent);

            }
        });



    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.firstFragment){
                loadFragment(adminGeneralInicioFragment);
                recyclerView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                CondicionDonacion.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                CondicionDonacion.setVisibility(View.GONE);
                return true;
            }
            if(item.getItemId()==R.id.secondFragment){
                loadFragment(listaActividadesGeneralFragment);
                recyclerView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                CondicionDonacion.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                CondicionDonacion.setVisibility(View.GONE);
                return true;
            }
            if(item.getItemId()==R.id.thirdFragment){
                loadFragment(crearActividadFragment);
                recyclerView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                CondicionDonacion.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                CondicionDonacion.setVisibility(View.GONE);
                return true;
            }
            if(item.getItemId()==R.id.fourFragment){
                loadFragment(personasGeneralFragment);
                recyclerView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                CondicionDonacion.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                CondicionDonacion.setVisibility(View.GONE);
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

    private void Total() {
        donacionesLista.clear();
        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(donacionesLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());  // Establecer el ID del documento
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setValidado(validado);
                                pagos.setMonto(monto);
                                pagos.setUrl_imagen(url_imagen);
                                donacionesLista.add(pagos);

                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);


                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });
    }

    private void Validado() {
        donacionesLista.clear();
        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(donacionesLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());  // Establecer el ID del documento
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setMonto(monto);
                                pagos.setValidado(validado);
                                pagos.setUrl_imagen(url_imagen);
                                if(validado.equals("Sí")){
                                    donacionesLista.add(pagos);
                                }
                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);


                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });
    }

    private void Invalidado() {
        donacionesLista.clear();
        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(donacionesLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());  // Establecer el ID del documento
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setMonto(monto);
                                pagos.setValidado(validado);
                                pagos.setUrl_imagen(url_imagen);
                                if(validado.equals("No")){
                                    donacionesLista.add(pagos);
                                }
                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);


                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });
    }

    private void Pendiente() {
        donacionesLista.clear();
        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(donacionesLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());  // Establecer el ID del documento
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setValidado(validado);
                                pagos.setMonto(monto);
                                pagos.setUrl_imagen(url_imagen);
                                if(validado.equals("Pendiente")){
                                    donacionesLista.add(pagos);
                                }
                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);


                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });
    }

    private void cargarDatosDesdeFirebase() {
        donacionesLista.clear();

        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if (donacionesLista.isEmpty()) {
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String codigo_usuario = (String) document.get("codigo_usuario");
                                String monto = (String) document.get("monto");
                                String validado = (String) document.get("validado");
                                String url_imagen = (String) document.get("url_imagen");
                                pagos pagos = new pagos();
                                pagos.setId(document.getId());
                                pagos.setValidado(validado);
                                pagos.setCodigo_usuario(codigo_usuario);
                                pagos.setMonto(monto);
                                pagos.setUrl_imagen(url_imagen);
                                donacionesLista.add(pagos);
                            }
                        }

                        donacionesAdminAdapter donacionesAdminAdapter = new donacionesAdminAdapter();
                        donacionesAdminAdapter.setActividadDonaciones(donacionesLista);
                        donacionesAdminAdapter.setContext(this);

                        recyclerView.setAdapter(donacionesAdminAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Recargar los datos al volver a la actividad
        cargarDatosDesdeFirebase();
    }


}