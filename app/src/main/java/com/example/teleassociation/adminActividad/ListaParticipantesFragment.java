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
import com.example.teleassociation.adapter.ListAdaptParticipantes;
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.adapter.PersonasGeneralAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.example.teleassociation.dto.usuario;
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
}