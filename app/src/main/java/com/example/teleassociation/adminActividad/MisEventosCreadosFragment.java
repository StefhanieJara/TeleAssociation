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
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MisEventosCreadosFragment extends Fragment implements MisEventAdapterAdminActv.OnVerEventoClickListener, MisEventAdapterAdminActv.OnVerEventosFinalizadosClickListener,
        MisEventAdapterAdminActv.OnBorrarEventoClickListener {

    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String nombreActividad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventLista.clear(); // Limpiar la lista antes de agregar nuevos elementos
        if (currentUser != null) {
            String nombreUsuario = currentUser.getDisplayName();

            // Realizar la consulta en Firestore
            db.collection("actividad")
                    .whereEqualTo("delegado", nombreUsuario)
                    .whereEqualTo("activo", 1)  // Reemplaza con tu otra condición
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Manejar cada documento que cumple con la condición
                                // Aquí puedes acceder a los datos del documento según tus necesidades
                                nombreActividad = document.getString("nombre");
                                // ...
                            }
                        } else {
                            // Manejar el error
                            Log.e("msg-test", "Error al realizar la consulta", task.getException());
                        }
                    });
        } else {
            // El usuario no está autenticado
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mis_eventos_creados, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listMisEventos);

        db.collection("eventos")
                .whereEqualTo("nombre_actividad", nombreActividad)  // Filtra por documentos con el campo "nombre" igual a nombreEvento
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String fechaSt = date.toString();
                                String[] partes = fechaSt.split(" ");
                                String fecha = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                String hora = partes[3];
                                eventoListarUsuario eventos = new eventoListarUsuario(nombre, fecha, hora, apoyos, nombre_actividad);
                                eventLista.add(eventos);
                                Log.d("msg-test", "Tamaño de la lista: " + eventLista.size());
                                Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora);
                            }
                        }
                        if (task.isSuccessful()) {
                            MisEventAdapterAdminActv eventAdapter = new MisEventAdapterAdminActv();
                            eventAdapter.setEventList(eventLista);
                            eventAdapter.setContext(getContext());
                            eventAdapter.setListener(this);

                            recyclerView.setAdapter(eventAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        }
                    }
                });

        return rootView;
    }

    @Override
    public void onVerEventoClick(eventoListarUsuario evento) {
        String nombreEvento = evento.getNombre();

        EventoDetalleAdminActvidadFragment fragment = EventoDetalleAdminActvidadFragment.newInstance(nombreEvento);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onVerEventosFinalizadosClick(eventoListarUsuario evento) {
        String nombreEvento = evento.getNombre();

        EventosFinalizadosFragment fragment = EventosFinalizadosFragment.newInstance(nombreEvento);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onBorrarEventoClick(eventoListarUsuario evento) {
        String nombreActividad = evento.getNombre_actividad();

        // Verifica que el nombre de la actividad no sea nulo o vacío antes de intentar modificar
        if (nombreActividad != null && !nombreActividad.isEmpty()) {
            // Buscar el documento en la colección "actividad" con el mismo nombre de actividad
            db.collection("actividad")
                    .whereEqualTo("nombre", nombreActividad)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Actualiza el campo "activo" a 0 en lugar de borrar el documento
                                document.getReference().update("activo", 0)
                                        .addOnSuccessListener(aVoid -> {
                                            // Éxito al actualizar el campo
                                            Log.d("msg-test", "Campo 'activo' actualizado a 0 con éxito");
                                            // Puedes realizar alguna acción adicional después de la actualización, si es necesario
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error al actualizar el campo
                                            Log.e("msg-test", "Error al actualizar el campo 'activo'", e);
                                        });
                            }
                        } else {
                            Log.e("msg-test", "Error al buscar el documento de actividad", task.getException());
                        }
                    });
        } else {
            Log.e("msg-test", "Nombre de la actividad es nulo o vacío");
        }
    }

}


