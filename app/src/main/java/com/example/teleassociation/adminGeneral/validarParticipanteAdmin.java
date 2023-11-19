package com.example.teleassociation.adminGeneral;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.teleassociation.EmailSender;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class validarParticipanteAdmin extends AppCompatActivity {
    FirebaseFirestore db;
    private AutoCompleteTextView validacion;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout textInputNombre;
    TextInputLayout textInputCorreo;
    TextInputEditText textRechazo;
    FirebaseAuth mAuth;
    TextView nameUser;
    com.example.teleassociation.dto.usuario usuario = new usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_participante_admin);
        // Ocultar barra de título
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


        String[] items = {"Si", "No"};
        validacion = findViewById(R.id.validacion);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        validacion.setAdapter(adapterItems);

        validacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(validarParticipanteAdmin.this, "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener los valores de usuarioNombre y usuarioCorreo
        String usuarioNombre = getIntent().getStringExtra("usuarioNombre");
        String usuarioCorreo = getIntent().getStringExtra("usuarioCorreo");
        String usuarioCodigo = getIntent().getStringExtra("usuarioCodigo");

        // Encontrar los TextInputLayout correspondientes
        textInputNombre = findViewById(R.id.textInputLayout27);
        textInputCorreo = findViewById(R.id.textInputLayout28);

        // Establecer los hints con los valores de usuarioNombre y usuarioCorreo
        textInputNombre.setHint(usuarioNombre);
        textInputCorreo.setHint(usuarioCorreo);

        Button button16 = findViewById(R.id.button16);

        button16.setOnClickListener(view -> {
            textRechazo = findViewById(R.id.rechazo);
            String rechazo = textRechazo.getText().toString().trim();
            String validacionStr = validacion.getText().toString().trim();

            //Log.d("msg-test", "El codigo del usuario es: "+usuarioCodigo);

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            Log.d("msg-test", "task2 ha sido valido");
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");

                                if(correo.equals(usuarioCorreo)){
                                    usuario.setCorreo(correo);
                                    usuario.setId(codigo);
                                    break;
                                }
                            }
                            Log.d("msg-test", "El codigo del usuario es: "+usuario.getId());
                            DocumentReference usuarioRef = db.collection("usuarios").document(usuario.getId());
                            if(validacionStr.equals("Si")){
                                usuarioRef
                                        .update("validado", validacionStr)
                                        .addOnSuccessListener(unused -> {
                                            String id = (String) usuarioRef.getId();
                                            Log.d("msg-test", "el ID de del usuario aceptado es es: " + id);
                                            Log.d("msg-test", "Validacion: " + validacionStr);
                                            EmailSender.sendEmail(usuarioCorreo,"Usuario valido en TeleAssociation","Su usuario ha sido valido para estar dentro de la aplicación.");
                                            Intent intent = new Intent(this, inicioAdmin.class);
                                            intent.putExtra("Usuario validado.", true);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                        });
                            }else{
                                if(rechazo.isEmpty()){
                                    Toast.makeText(this, "El campo del motivo de rechazo no puede estar vacio", Toast.LENGTH_SHORT).show();
                                }else{
                                    usuarioRef
                                            .update("validado", validacionStr)
                                            .addOnSuccessListener(unused -> {
                                                usuarioRef
                                                        .update("comentario", rechazo)
                                                        .addOnSuccessListener(unused2 -> {
                                                            String id = (String) usuarioRef.getId();
                                                            Log.d("msg-test", "el ID de del usuario denegado es es: " + id);
                                                            Log.d("msg-test", "Validacion: "+ validacionStr);
                                                            EmailSender.sendEmail(usuarioCorreo,"Usuario invalido en TeleAssociation",rechazo);
                                                            Intent intent = new Intent(this, inicioAdmin.class);
                                                            intent.putExtra("Usuario validado.", true);
                                                            startActivity(intent);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });


        });

    }
}