package com.example.teleassociation.adminActividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MisEventosCreadosFragment extends Fragment implements MisEventAdapterAdminActv.OnVerEventoClickListener {

    ListenerRegistration snapshotListener;
    FirebaseFirestore db;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;
    FirebaseAuth mAuth;
    private String nombreActividad;
    private String nombreUsuario;
    private MisEventAdapterAdminActv adapter;
    TextView nameUser;
    String nombreDelegado;
    TextView textView20;
    Button btnBorrarEvento;
    Button btnVerEventosFinalizados;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventLista.clear(); // Limpiar la lista antes de agregar nuevos elementos
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mis_eventos_creados, container, false);
        btnBorrarEvento = rootView.findViewById(R.id.btnBorrar);
        btnVerEventosFinalizados = rootView.findViewById(R.id.verEventosFinalizados);
        textView20 = rootView.findViewById(R.id.textView20);

        obtenerDatosUsuario(usuarioSesion -> {
            nombreDelegado= usuarioSesion.getNombre();
            Log.d("msg-test", "El nombre del usuario fuera del collection es deleact: " + nombreDelegado);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);
        });

        btnBorrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tu lógica para el botón BorrarEvento
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirmar");
                builder.setMessage("¿Estás seguro de que quieres borrar esta Actividad? No hay vuelta atrás.");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Realiza la acción de borrado aquí (puedes llamar al método que ya tienes para borrar el evento)
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
                                                            // Éxito al actualizar el campo "activo"
                                                            Log.d("msg-test", "Campo 'activo' actualizado a 0 con éxito");

                                                            // Ahora, actualiza el campo "estado" a "finalizado" en la colección "eventos"
                                                            db.collection("eventos")
                                                                    .whereEqualTo("nombre_actividad", nombreActividad)
                                                                    .get()
                                                                    .addOnCompleteListener(eventosTask -> {
                                                                        if (eventosTask.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot eventoDocument : eventosTask.getResult()) {
                                                                                // Actualiza el campo "estado" a "finalizado"
                                                                                eventoDocument.getReference().update("estado", "finalizado")
                                                                                        .addOnSuccessListener(aVoid1 -> {
                                                                                            // Éxito al actualizar el campo "estado"
                                                                                            Log.d("msg-test", "Campo 'estado' actualizado a 'finalizado' con éxito");
                                                                                            // Puedes realizar alguna acción adicional después de la actualización, si es necesario
                                                                                        })
                                                                                        .addOnFailureListener(e -> {
                                                                                            // Error al actualizar el campo "estado"
                                                                                            Log.e("msg-test", "Error al actualizar el campo 'estado'", e);
                                                                                        });
                                                                            }
                                                                        } else {
                                                                            Log.e("msg-test", "Error al buscar documentos en la colección 'eventos'", eventosTask.getException());
                                                                        }
                                                                    });

                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Error al actualizar el campo "activo"
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
                        // Actualiza la página o RecyclerView después de borrar el evento
                        AdminActividadInicioFragment fragment = AdminActividadInicioFragment.newInstance();

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si el usuario cancela, no es necesario realizar ninguna acción adicional
                    }
                });

                builder.show();


            }
        });

        btnVerEventosFinalizados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tu lógica para el botón VerEventosFinalizados

                //EventosApoyadosFragment fragment = EventosApoyadosFragment.newInstance(nombreActividad);
                EventosFinalizadosFragment fragment1 = EventosFinalizadosFragment.newInstance(nombreActividad);
                /*btnBorrarEvento.setVisibility(View.GONE);
                btnVerEventosFinalizados.setVisibility(View.GONE);
                textView20.setVisibility(View.GONE);*/
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment1)
                        .addToBackStack(null)
                        .commit();
            }
        });

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
                                            consultarActividades(btnBorrarEvento);
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

    private void consultarActividades(Button btnBorrarEvento) {
        // Realizar la consulta en Firestore para actividades
        db.collection("actividad")
                .whereEqualTo("delegado", nombreUsuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el campo "activo"
                            long activo = document.getLong("activo");

                            // Verificar si el campo "activo" es igual a 0 y ocultar el botón en consecuencia
                            if (activo == 0) {
                                textView20.setText("No hay eventos asignados ");
                                btnBorrarEvento.setVisibility(View.GONE);
                            } else {
                                // Continuar con la lógica anterior
                                // Manejar cada documento que cumple con la condición
                                // Aquí puedes acceder a los datos del documento según tus necesidades
                                nombreActividad = document.getString("nombre");
                                textView20.setText("Mis eventos actividad: " + nombreActividad);
                                listar(nombreActividad);
                                // ...
                                Log.d("msg-test", "Nombre de actividad: " + nombreActividad);
                            }
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
                .whereEqualTo("estado", "proceso")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String fechaSt = date.toString();
                                String url_imagen = (String) document.get("url_imagen");
                                String[] partes = fechaSt.split(" ");
                                //String fecha = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                //String fechaEvento = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaEsp.format(date));
                                String fecha = formatoFechaEsp.format(date);
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


