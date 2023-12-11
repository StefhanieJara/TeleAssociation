package com.example.teleassociation.Usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.NotificacionAdapter;
import com.example.teleassociation.databinding.ActivityNotificationBinding;
import com.example.teleassociation.dto.notificacion;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    private RecyclerView recyclerView;
    private NotificacionAdapter adapter;
    private List<notificacion> notificaciones;
    private FirebaseFirestore db;
    private String codigoAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.listRecyclerNotificacion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificaciones = new ArrayList<>();
        adapter = new NotificacionAdapter(this, notificaciones);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Inicializa Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d("msg-test", "Ingresaste a notificaciones: ");


        if (mAuth.getCurrentUser() != null) {
            obtenerDatosUsuario(usuario -> {
                // Aquí puedes realizar acciones con la información del usuario
                Log.d("msg-test", "Nombre del usuario: " + usuario.getNombre());

                // Obtén el código del usuario
                String codigoUsuario = usuario.getId();
                Log.d("msg-test", "Código del usuario: " + codigoUsuario);
                cargarNotificacionesDesdeFirestore(codigoUsuario);
                // Ahora puedes usar el código del usuario según tus necesidades.
            });
        }

    }
    private void obtenerDatosUsuario(NotificationActivity.FirestoreCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
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
    private void cargarNotificacionesDesdeFirestore(String codigoAlumno) {
        if (codigoAlumno != null && !codigoAlumno.isEmpty()) {
            db.collection("notificaciones")
                    .whereEqualTo("codigo", codigoAlumno)  // Reemplaza "codigoAlumno" con el nombre del campo en Firestore
                    .orderBy("fecha", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            notificaciones.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                notificacion notif = document.toObject(notificacion.class);
                                notificaciones.add(notif);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // Manejar el error
                            Log.e("msg-test", "Error al obtener notificaciones: ", task.getException());
                        }
                    });
        } else {
            // El código del alumno no está disponible, manejar según sea necesario
            Log.e("msg-test", "El código del alumno no está disponible");
        }
    }


}