package com.example.teleassociation.adminActividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminActividadInicioFragment extends Fragment {

    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_actividad_inicio, container, false);
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listRecyclerActv);

        db.collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();

                        for (QueryDocumentSnapshot document : eventosCollection) {
                            String nombre = (String) document.get("nombre");
                            Date date = document.getDate("fecha");
                            String apoyos = (String) document.get("apoyos");
                            String fechaSt = date.toString();
                            String[] partes = fechaSt.split(" ");
                            String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                            String hora = partes[3];
                            eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos);
                            eventLista.add(eventos);
                            Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora);
                        }

                        EventAdapter eventAdapter = new EventAdapter();
                        eventAdapter.setEventList(eventLista);
                        eventAdapter.setContext(getContext());

                        // Inicializa el RecyclerView y el adaptador
                        recyclerView.setAdapter(eventAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                    }
                });


        return rootView;
    }
}