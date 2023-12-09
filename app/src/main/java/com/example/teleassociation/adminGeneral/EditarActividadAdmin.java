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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarActividadAdmin extends AppCompatActivity {

    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();

    FirebaseFirestore db;
    private AutoCompleteTextView delegadoEdit;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout tituloActividadEdit;
    TextInputLayout titulodescripcionEdit;
    usuario usuario = new usuario();
    FirebaseAuth mAuth;
    TextView nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_actividad);

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




        Button buttonEditarActividad = findViewById(R.id.buttonEditarActividad);
        TextInputEditText actividadEdit = findViewById(R.id.actividadEdit);
        TextInputEditText descripcionEdit = findViewById(R.id.descripcionEdit);
        TextInputEditText imagenEdit = findViewById(R.id.imagenEdit);

        List<String> nombreUsuario = new ArrayList<>();
        delegadoEdit = findViewById(R.id.delegadoEdit);

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

                            if (rol.equals("Usuario") && validacion.equals("Si")) {

                                Log.d("msg-test", "| codigo: " + codigo + " | nombre: " + nombre + "| correo: " + correo + " | condicion: " + condicion + " | validacion: " + validacion);
                                nombreUsuario.add(nombre);
                            }
                        }

                        // Convertir la lista de nombres de usuario a un arreglo de String
                        String[] items = nombreUsuario.toArray(new String[0]);

                        // Inicializar el ArrayAdapter con el nuevo arreglo y establecerlo en el ListView
                        adapterItems = new ArrayAdapter<>(EditarActividadAdmin.this, R.layout.list_item, items);
                        delegadoEdit.setAdapter(adapterItems);
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja la excepción que ocurra al intentar obtener los documentos
                    Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                });

        delegadoEdit.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedName = adapterView.getItemAtPosition(i).toString();
            // Puedes realizar acciones con el nombre seleccionado aquí
            Toast.makeText(EditarActividadAdmin.this, "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
        });

        // Obtener los valores restantes
        String actividadNombre = getIntent().getStringExtra("actividadNombre");
        String actividadDescripcion = getIntent().getStringExtra("actividadDescripcion");
        String actividadImagen = getIntent().getStringExtra("actividadImagen");
        String actividadID = getIntent().getStringExtra("actividadID");

        tituloActividadEdit = findViewById(R.id.tituloActividadEdit);
        titulodescripcionEdit = findViewById(R.id.titulodescripcionEdit);

        tituloActividadEdit.setHint(actividadNombre);
        titulodescripcionEdit.setHint(actividadDescripcion);

        buttonEditarActividad.setOnClickListener(view -> {
            String delegadoStr = delegadoEdit.getText().toString().trim();
            String actividadDelegadoAntiguo = getIntent().getStringExtra("actividadDelegado");

            DocumentReference actividadRef = db.collection("actividad").document(actividadID);
            //DocumentReference actividadRef2 = db.collection("actividad").document(actividadID);

            if(delegadoStr.isEmpty()){
                Toast.makeText(this, "Se debe seleccionar un delegado de actividad", Toast.LENGTH_SHORT).show();
            }else{


                CollectionReference usuariosCollection = FirebaseFirestore.getInstance().collection("usuarios");

                // Consulta para encontrar el documento con el nombre igual a actividadDelegadoAntiguo
                Query query = usuariosCollection.whereEqualTo("nombre", actividadDelegadoAntiguo);

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Se encontraron documentos que cumplen con la condición de la consulta
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Obtiene el ID del documento para realizar la actualización
                                String codigoDelegadoAntiguo = document.getId();

                                // Actualiza el documento con el nuevo valor para el campo "rol"
                                usuariosCollection.document(codigoDelegadoAntiguo)
                                        .update("rol", "Usuario")
                                        .addOnSuccessListener(aVoid -> {
                                            // La actualización fue exitosa
                                            Log.d("msg-test", "Documento delegado antiguo actualizado exitosamente.");


                                            CollectionReference usuariosCollection2 = FirebaseFirestore.getInstance().collection("usuarios");

                                            // Consulta para encontrar el documento con el nombre igual a delegadoStr
                                            Query query2 = usuariosCollection2.whereEqualTo("nombre", delegadoStr);

                                            query2.get().addOnCompleteListener(task3 -> {
                                                if (task3.isSuccessful()) {
                                                    QuerySnapshot querySnapshot2 = task3.getResult();

                                                    if (querySnapshot2 != null && !querySnapshot2.isEmpty()) {
                                                        for (QueryDocumentSnapshot document2 : querySnapshot2) {
                                                            String codigoDelegadoNuevo = document2.getId();

                                                            usuariosCollection2.document(codigoDelegadoNuevo)
                                                                    .update("rol", "DelegadoActividad")
                                                                    .addOnSuccessListener(aVoid2 -> {
                                                                        // La actualización fue exitosa
                                                                        Log.d("msg-test", "Documento actualizado exitosamente para el nuevo delegado.");
                                                                        actividadRef
                                                                                .update("delegado", delegadoStr)
                                                                                .addOnSuccessListener(unused -> {

                                                                                    // Obtener la referencia a la colección "eventos"
                                                                                    CollectionReference eventosRef = FirebaseFirestore.getInstance().collection("eventos");
                                                                                    eventosRef.whereEqualTo("delegado", actividadDelegadoAntiguo)
                                                                                            .get()
                                                                                            .addOnCompleteListener(task10 -> {
                                                                                                if (task10.isSuccessful()) {
                                                                                                    for (QueryDocumentSnapshot document10 : task10.getResult()) {

                                                                                                        DocumentReference eventoRef = document10.getReference();
                                                                                                        eventoRef.update("delegado", delegadoStr)
                                                                                                                .addOnSuccessListener(aVoid10 -> {
                                                                                                                    // Referencia a la colección "participantes"
                                                                                                                    CollectionReference participantesRef = FirebaseFirestore.getInstance().collection("participantes");
                                                                                                                    participantesRef.whereEqualTo("asignacion", "Delegado")
                                                                                                                            .whereEqualTo("codigo", codigoDelegadoAntiguo)
                                                                                                                            .get()
                                                                                                                            .addOnCompleteListener(task20 -> {
                                                                                                                                if (task20.isSuccessful()) {
                                                                                                                                    for (QueryDocumentSnapshot document20 : task20.getResult()) {
                                                                                                                                        // Obtener la referencia del documento
                                                                                                                                        DocumentReference participanteRef = document20.getReference();

                                                                                                                                        participanteRef.update("codigo", codigoDelegadoNuevo, "nombre", delegadoStr)
                                                                                                                                                .addOnSuccessListener(aVoid20 -> {
                                                                                                                                                    Intent intent = new Intent(this, inicioAdmin.class);
                                                                                                                                                    intent.putExtra("Nuevo delegado.", true);
                                                                                                                                                    startActivity(intent);
                                                                                                                                                    Log.d("msg-test", "Usuario actualizado con éxito.");
                                                                                                                                                })
                                                                                                                                                .addOnFailureListener(e -> {
                                                                                                                                                    // Error al actualizar
                                                                                                                                                    Log.e("msg-test", "Error al actualizar documento en participantes", e);
                                                                                                                                                });
                                                                                                                                    }
                                                                                                                                } else {
                                                                                                                                    // Error al obtener documentos
                                                                                                                                    Log.e("msg-test", "Error al obtener documentos en participantes", task.getException());
                                                                                                                                }
                                                                                                                            });
                                                                                                                })
                                                                                                                .addOnFailureListener(e -> {
                                                                                                                    Log.w("msg-test", "Error al actualizar documento", e);
                                                                                                                });
                                                                                                    }
                                                                                                } else {
                                                                                                    // Manejar la excepción si la consulta falla
                                                                                                    Log.w("msg-test", "Error obteniendo documentos", task.getException());
                                                                                                }
                                                                                            });
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        // Maneja la excepción en caso de error en la actualización
                                                                        Log.e("msg-test", "Error al actualizar el documento: " + e.getMessage());
                                                                    });
                                                        }
                                                    } else {
                                                        // No se encontraron documentos que cumplan con la condición de la consulta
                                                        Log.d("msg-test", "No se encontraron documentos con nombre igual a " + delegadoStr);
                                                    }
                                                } else {
                                                    // Maneja el error en caso de falla en la consulta
                                                    Log.e("msg-test", "Error al obtener documentos: " + task.getException());
                                                }
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Maneja la excepción en caso de error en la actualización
                                            Log.e("msg-test", "Error al actualizar el documento: " + e.getMessage());
                                        });
                            }
                        } else {
                            // No se encontraron documentos que cumplan con la condición de la consulta
                            Log.d("msg-test", "No se encontraron documentos con nombre igual a " + actividadDelegadoAntiguo);
                        }
                    } else {
                        // Maneja el error en caso de falla en la consulta
                        Log.e("msg-test", "Error al obtener documentos: " + task.getException());
                    }
                });


            }
        });


        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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