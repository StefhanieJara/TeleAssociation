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
import android.widget.TextView;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.EventAdapterAdminActividad;
import com.example.teleassociation.adapter.MisEventAdapterAdminActv;
import com.example.teleassociation.adminActividad.AdminActividadInicioFragment;
import com.example.teleassociation.dto.eventoListarUsuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminActividadInicioFragment extends Fragment implements EventAdapterAdminActividad.OnVerEventoClickListener{

    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String delegadoAct;
    String codigoDelegadoAct;
    TextView nameUser;
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

        obtenerDatosUsuario(usuario -> {
            delegadoAct= usuario.getNombre();
            codigoDelegadoAct = usuario.getId();
            Log.d("msg-test", "El nombre del usuario fuera del collection es deleact: " + delegadoAct);
            Log.d("msg-test", "El id del usuario fuera del collection es deleact: " + codigoDelegadoAct);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(delegadoAct);
            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:
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
                                    String delegado = (String) document.get("delegado");
                                    eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos,nombre_actividad,url_imagen);
                                    eventos.setId(eventoId);
                                    eventos.setDelegado(delegado);
                                    eventLista.add(eventos);
                                    Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora + "| delegado: "+delegado);
                                }
                            }

                            EventAdapterAdminActividad eventAdapter = new EventAdapterAdminActividad(delegadoAct,codigoDelegadoAct);
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


    private void obtenerDatosUsuario(AdminActividadInicioFragment.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuario usuario = new usuario();
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
}