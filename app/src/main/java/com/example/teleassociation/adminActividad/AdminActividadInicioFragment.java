package com.example.teleassociation.adminActividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.EventAdapterAdminActividad;
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.dto.eventoListarUsuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminActividadInicioFragment extends Fragment implements EventAdapterAdminActividad.OnVerEventoClickListener{

    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;
    public static AdminActividadInicioFragment newInstance() {
        AdminActividadInicioFragment fragment = new AdminActividadInicioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventLista.clear(); // Limpiar la lista antes de agregar nuevos elementos

        View rootView = inflater.inflate(R.layout.fragment_admin_actividad_inicio, container, false);
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listRecyclerActv);

        db.collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();

                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String eventoId = document.getId();
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String url_imagen = (String) document.get("url_imagen");
                                String fechaSt = date.toString();
                                String[] partes = fechaSt.split(" ");
                                String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                String hora = partes[3];
                                eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos,nombre_actividad,url_imagen);
                                eventos.setId(eventoId);
                                eventLista.add(eventos);
                                Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora);
                            }
                        }

                        EventAdapterAdminActividad eventAdapter = new EventAdapterAdminActividad();
                        eventAdapter.setEventList(eventLista);
                        eventAdapter.setContext(getContext());
                        eventAdapter.setListener(this);


                        // Inicializa el RecyclerView y el adaptador
                        recyclerView.setAdapter(eventAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                        Button verEventosApoyados = rootView.findViewById(R.id.verEventosApoyados);
                        verEventosApoyados.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragmentoB = new EventosApoyadosFragment(); // Crea una instancia del FragmentoB
                                FragmentManager fragmentManager = getParentFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_container, fragmentoB) // Reemplaza el fragmento actual con el FragmentoB
                                        .addToBackStack(null) // Opcional: Agrega la transacción a la pila de retroceso
                                        .commit(); // Realiza la transacción
                            }
                        });

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
}