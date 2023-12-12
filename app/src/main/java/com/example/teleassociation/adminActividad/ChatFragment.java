package com.example.teleassociation.adminActividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.NotificationActivity;
import com.example.teleassociation.adapter.MensajeAdapter;
import com.example.teleassociation.dto.Mensaje;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ChatFragment extends Fragment {

    private EditText editTextMensaje;
    private Button btnEnviar;
    private RecyclerView recyclerView;
    private MensajeAdapter mensajeAdapter;
    private List<Mensaje> listaMensajes;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String userName;


    public static ChatFragment newInstance(String id, String nombreEvento) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("idDocumento", id); // Agrega el ID del documento al Bundle
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    /*private void loadBlurredImage(ImageView imageView) {
        // URL o recurso de la imagen que quieres usar como fondo
        int drawableResourceId = R.drawable.go; // Reemplaza "nombre_de_tu_imagen" con el nombre real de tu archivo de imagen

        // Aplicar el efecto de desenfoque usando Glide Transformations
        MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(
                new BlurTransformation(25, 3)
                // Puedes ajustar los parámetros del desenfoque según tus preferencias
        );

        RequestOptions requestOptions = RequestOptions.bitmapTransform(multiTransformation)
                .placeholder(R.drawable.hiro)  // Puedes agregar un placeholder mientras se carga la imagen
                .error(R.drawable.telito);  // Puedes agregar una imagen de error en caso de que la carga falle

        Glide.with(this)
                .load(drawableResourceId)
                .apply(requestOptions)
                .into(imageView);
    }*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ImageView imageView = view.findViewById(R.id.imageViewBackground);
        //loadBlurredImage(imageView);

        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaMensajes = new ArrayList<>();
        mensajeAdapter = new MensajeAdapter(requireContext(), listaMensajes);
        recyclerView.setAdapter(mensajeAdapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Obten el ID del documento del Bundle
        String idDocumento = getArguments().getString("idDocumento");

        // Actualiza la referencia de la base de datos para apuntar al nodo específico del evento
        databaseReference = FirebaseDatabase.getInstance().getReference("eventos").child(idDocumento).child("chat");

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });

        leerMensajes();
    }

    private void enviarMensaje() {
        String mensajeTexto = editTextMensaje.getText().toString().trim();
        String userId = firebaseUser.getUid();
        // Obtén el email del usuario autenticado
        String userEmail = firebaseUser.getEmail();
        Log.d("sudo", "Email del usuario: " + userEmail);


// Referencia a la colección de usuarios en Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuariosCollection = db.collection("usuarios");

// Realiza la consulta para encontrar el documento con el email correspondiente
        usuariosCollection.whereEqualTo("correo", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Verifica si hay algún documento que coincida con el email
                            if (!task.getResult().isEmpty()) {
                                // Obtén el primer documento (puedes ajustar esto según tus necesidades)
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                // Obtén el campo "nombre" del documento
                                userName = document.getString("nombre");
                                if (!mensajeTexto.isEmpty()) {
                                    Mensaje nuevoMensaje = new Mensaje(userName, userId,mensajeTexto);
                                    databaseReference.push().setValue(nuevoMensaje);
                                    editTextMensaje.setText("");
                                }

                                // Haz algo con el nombre del usuario
                                Log.d("sudo", "Nombre del usuario: " + userName);
                            } else {
                                // El email no coincide con ningún usuario en la colección
                                Log.d("sudo", "No se encontró ningún usuario con el email proporcionado.");
                            }
                        } else {
                            // Error al realizar la consulta
                            Log.e("sudo", "Error al obtener el documento de usuario:", task.getException());
                        }
                    }
                });

    }

    private void leerMensajes() {
        // Utiliza ChildEventListener para escuchar los cambios en la base de datos en tiempo real
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);

                // Agrega el nuevo mensaje directamente al adaptador
                mensajeAdapter.agregarMensaje(mensaje);

                // Desplázate al último mensaje si la lista no está vacía
                if (!listaMensajes.isEmpty()) {
                    recyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount() - 1);
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Manejar cambios en los mensajes si es necesario
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Manejar mensajes eliminados si es necesario
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Manejar cambios en el orden de los mensajes si es necesario
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });
    }
    public void onCampanaClick(View view) {
        // Abre la NotificationActivity al hacer clic en la campana
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        startActivity(intent);
    }
}
