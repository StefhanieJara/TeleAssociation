package com.example.teleassociation.adminActividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.adapter.ListAdaptParticipantes;
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.adapter.PersonasGeneralAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListaParticipantesFragment extends Fragment {

    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<participante> participantesLista = new ArrayList<>();
    FirebaseAuth mAuth;
    TextView nameUser;
    String nombreDelegado;

    public static ListaParticipantesFragment newInstance(String nombreEvento) {
        ListaParticipantesFragment fragment = new ListaParticipantesFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lista_participantes_admin_actv, container, false);
        String eventoParticipante = getArguments().getString("nombreEvento"); // Asegúrate de que sea "nombreEvento" y no "nombre_evento"

        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listaParticipantes);

        obtenerDatosUsuario(usuarioSesion -> {
            nombreDelegado= usuarioSesion.getNombre();
            Log.d("msg-test", "El nombre del usuario fuera del collection es deleact: " + nombreDelegado);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);
        });

        db.collection("participantes")
                .whereEqualTo("evento", eventoParticipante)  // Filtra por documentos con el campo "nombre" igual a nombreEvento
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();

                        for (QueryDocumentSnapshot document : eventosCollection) {
                            String nombre = (String) document.get("nombre");
                            String codigo = (String) document.get("codigo");
                            String asignacion = (String) document.get("asignacion");
                            String evento = (String) document.get("evento");

                            participante part = new participante(asignacion, codigo, evento, nombre);
                            participantesLista.add(part);
                            Log.d("msg-test", "Tamaño de la lista: " + participantesLista.size());
                            Log.d("msg-test", " | nombre: " + nombre + " | codigo: " + codigo + " | asignacion: " + asignacion);
                        }

                        if (task.isSuccessful()) {
                            ListAdaptParticipantes eventAdapter = new ListAdaptParticipantes();
                            eventAdapter.setParticipanteList(participantesLista);
                            eventAdapter.setContext(getContext());

                            TextView textViewTitulo = rootView.findViewById(R.id.titulo);
                            textViewTitulo.setText("Participantes de " + eventoParticipante);

                            recyclerView.setAdapter(eventAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        }
                    }
                });

        return rootView;
    }

    private void obtenerDatosUsuario(FirstFragment.FirestoreCallback callback) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();
            Log.d("msg-test", "el email es: " + email);

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
                                    Log.d("msg-test", "datos del usuario " + codigo + "  correo " + " nombre");
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