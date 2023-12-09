package com.example.teleassociation.adminActividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class EventosFinalizadosFragment extends Fragment implements MisEventAdapterAdminActv.OnVerEventoClickListener{
    private FirebaseFirestore db;
    private String nombreEventoParticipante;
    private String nombreActividad;
    private String nombreUsuario;
    private MisEventAdapterAdminActv adapter;
    TextView nameUser;
    String nombreDelegado;
    TextView textView20;
    Button btnBorrarEvento;
    Button btnVerEventosFinalizados;

    public static EventosFinalizadosFragment newInstance(String nombreEvento) {
        EventosFinalizadosFragment fragment = new EventosFinalizadosFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }
    ListenerRegistration snapshotListener;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventLista.clear(); // Limpiar la lista antes de agregar nuevos elementos
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mis_eventos_creados, container, false);
        btnBorrarEvento = rootView.findViewById(R.id.btnBorrar);
        btnVerEventosFinalizados = rootView.findViewById(R.id.verEventosFinalizados);
        textView20 = rootView.findViewById(R.id.textView20);
        btnBorrarEvento.setVisibility(View.GONE);
        btnVerEventosFinalizados.setVisibility(View.GONE);
        textView20.setText("Eventos finalizados");
        obtenerDatosUsuario(usuarioSesion -> {
            nombreDelegado= usuarioSesion.getNombre();
            Log.d("msg-test", "El nombre del usuario fuera del collection es deleact: " + nombreDelegado);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);

            recyclerView = rootView.findViewById(R.id.listMisEventos);
            // Configurar el LayoutManager y otros ajustes necesarios para tu RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            // Crear e instanciar tu adaptador
            adapter = new MisEventAdapterAdminActv();

            // Establecer el adaptador en el RecyclerView
            recyclerView.setAdapter(adapter);
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Log.d("msg-test", "clash");
            if (currentUser != null) {
                String emailUsuario = currentUser.getEmail();
                Log.d("msg-test", emailUsuario);
                if (emailUsuario != null && !emailUsuario.isEmpty()) {
                    // Realizar la consulta en Firestore para obtener el nombre asociado al correo electrónico
                    db.collection("usuarios")
                            .whereEqualTo("correo", emailUsuario)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        nombreUsuario = document.getString("nombre");
                                        Log.d("msg-test", "Nombre de usuario: " + nombreUsuario);
                                        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                                            // El nombre de usuario está disponible, ahora realiza la consulta a actividades
                                            consultarActividades();
                                        } else {
                                            // El nombre de usuario está ausente o vacío
                                            Log.e("msg-test", "Nombre de usuario ausente o vacío");
                                        }
                                    }
                                } else {
                                    // Manejar el error al realizar la consulta
                                    Log.e("msg-test", "Error al realizar la consulta en Firestore", task.getException());
                                }
                            });

                } else {
                    // El correo electrónico del usuario está ausente o vacío
                    Log.e("msg-test", "Correo electrónico del usuario ausente o vacío");
                }
            } else {
                // El usuario no está autenticado
                Log.e("msg-test", "Usuario no autenticado");
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

    private void consultarActividades() {
        // Realizar la consulta en Firestore para actividades
        db.collection("actividad")
                .whereEqualTo("delegado", nombreUsuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Manejar cada documento que cumple con la condición
                            // Aquí puedes acceder a los datos del documento según tus necesidades
                            nombreActividad = document.getString("nombre");
                            listar(nombreActividad);
                            // ...
                            Log.d("msg-test", "Nombre de actividad: " + nombreActividad);
                        }
                    } else {
                        // Manejar el error
                        Log.e("msg-test", "Error al realizar la consulta", task.getException());
                    }
                });
    }

    private void listar(String nombreActividad){

        db.collection("eventos")
                .whereEqualTo("nombre_actividad", nombreActividad)
                .whereEqualTo("estado", "finalizado")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                        SimpleDateFormat formatoFechaNuevo = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String url_imagen = (String) document.get("url_imagen");
                                String fechaSt = date.toString();
                                String[] partes = fechaSt.split(" ");
                                //String fecha = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                //String fechaEvento = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaNuevo.format(date));
                                String fecha = formatoFechaNuevo.format(date);
                                String hora = partes[3];
                                eventoListarUsuario eventos = new eventoListarUsuario(nombre, fecha, hora, apoyos, nombre_actividad,url_imagen);
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

    }

    private void obtenerDatosUsuario(EventosFinalizadosFragment.FirestoreCallback callback) {
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
