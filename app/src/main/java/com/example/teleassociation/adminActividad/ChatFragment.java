package com.example.teleassociation.adminActividad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.MensajeAdapter;
import com.example.teleassociation.dto.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private EditText editTextMensaje;
    private Button btnEnviar;
    private RecyclerView recyclerView;
    private MensajeAdapter mensajeAdapter;
    private List<Mensaje> listaMensajes;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;


    public static ChatFragment newInstance(String nombreEvento, String id) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        args.putString("idDocumento", id); // Agrega el ID del documento al Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        String userName = firebaseUser.getDisplayName();
        if (!mensajeTexto.isEmpty()) {
            Mensaje nuevoMensaje = new Mensaje(userName, userId,mensajeTexto);
            databaseReference.push().setValue(nuevoMensaje);
            editTextMensaje.setText("");
        }
    }

    private void leerMensajes() {
        // Utiliza ChildEventListener para escuchar los cambios en la base de datos en tiempo real
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);
                listaMensajes.add(mensaje);
                mensajeAdapter.notifyDataSetChanged();

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
}
