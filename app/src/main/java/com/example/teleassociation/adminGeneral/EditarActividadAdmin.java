package com.example.teleassociation.adminGeneral;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditarActividadAdmin extends AppCompatActivity {

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
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

                            if (rol.equals("Usuario")) {
                                // usuario.setComentario(comentario);  // Esto no parece ser necesario en este contexto
                                // ... (Resto de tu código para configurar el objeto usuario)

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

            DocumentReference actividadRef = db.collection("actividad").document(actividadID);

            if(delegadoStr.isEmpty()){
                Toast.makeText(this, "Se debe seleccionar un delegado de actividad", Toast.LENGTH_SHORT).show();
            }else{
                actividadRef
                        .update("delegado", delegadoStr)
                        .addOnSuccessListener(unused -> {
                            Intent intent = new Intent(this, inicioAdmin.class);
                            intent.putExtra("Nuevo delegado.", true);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}