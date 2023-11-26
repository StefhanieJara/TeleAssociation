package com.example.teleassociation.adminActividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.MisEventAdapter;
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EventosApoyadosFragment extends Fragment implements MisEventAdapterAdminActv.OnVerEventoClickListener{
    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    ArrayList<String> eventosParticipa = new ArrayList<>();
    private RecyclerView recyclerView;
    TextView nameUser;
    String nombreDelegado;

    public static EventosApoyadosFragment newInstance(String nombreEvento) {
        EventosApoyadosFragment fragment = new EventosApoyadosFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventLista.clear(); // Limpiar la lista antes de agregar nuevos elementos
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_eventos_apoyados, container, false);


        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listMisEventos);

        obtenerDatosUsuario(usuarioSesion -> {
            nombreDelegado= usuarioSesion.getNombre();
            Log.d("msg-test", "El nombre del usuario fuera del collection es deleact: " + nombreDelegado);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);

            Log.d("msg-test", " inicio");
            db.collection("participantes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot eventosCollection = task.getResult();
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String asignacion = (String) document.get("asignacion");
                                String codigo = (String) document.get("codigo");
                                String evento = (String) document.get("evento");
                                if (usuarioSesion.getId().equals(codigo)) {
                                    Log.d("msg-test", " | evento: " + evento);
                                    eventosParticipa.add(evento);}}

                            // Itera a través de eventos de la colección "eventos"
                            db.collection("eventos")
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            QuerySnapshot eventosCollection2 = task2.getResult();
                                            SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                                            if(eventLista.isEmpty()){
                                                for (QueryDocumentSnapshot document2 : eventosCollection2) {
                                                    String eventoId = document2.getId();
                                                    String nombre = (String) document2.get("nombre");
                                                    String nombre_actividad = (String) document2.get("nombre_actividad");
                                                    Date date = document2.getDate("fecha");
                                                    String apoyos = (String) document2.get("apoyos");
                                                    String url_imagen = (String) document2.get("url_imagen");
                                                    String fechaSt = date.toString();
                                                    String[] partes = fechaSt.split(" ");
                                                    //String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                                    Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaEsp.format(date));
                                                    Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaEsp.format(date));
                                                    String fecha = formatoFechaEsp.format(date);
                                                    String hora = partes[3];
                                                    String fecha_hora = fecha+" "+hora;

                                                    //Log.d("msg-test", " | nombre de eventos: " + nombre);

                                                    // Verifica si el nombre del evento está en eventosParticipa
                                                    if (eventosParticipa.contains(nombre)) {
                                                        eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos, nombre_actividad,url_imagen);
                                                        eventos.setId(eventoId);
                                                        eventLista.add(eventos);
                                                        Log.d("msg-test", " | nombre: " + nombre + "| actividad: "+ nombre_actividad + " | fecha: " + fecha + " | hora: " + hora);
                                                    }
                                                }
                                            }
                                            MisEventAdapter misEventAdapter = new MisEventAdapter();
                                            misEventAdapter.setEventList(eventLista);
                                            misEventAdapter.setContext(getContext());

                                            recyclerView.setAdapter(misEventAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


                                        }
                                    });


                        }
                    });
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

    private void obtenerDatosUsuario(EventosApoyadosFragment.FirestoreCallback callback) {
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