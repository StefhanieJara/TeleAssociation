package com.example.teleassociation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventAdapterAdminActividad extends RecyclerView.Adapter<EventAdapterAdminActividad.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;
    private Set<String> hiddenButtonEventIds = new HashSet<>();
    private SharedPreferences sharedPreferences;
    private EventAdapterAdminActividad.OnVerEventoClickListener listener;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private String nombreUsuario;

    // Interfaz para manejar clics en el botón "verEvento"
    public interface OnVerEventoClickListener {
        void onVerEventoClick(eventoListarUsuario evento);
    }
    public void setListener(EventAdapterAdminActividad.OnVerEventoClickListener listener) {
        this.listener = listener;
    }

    private String delegadoAct;
    private String codigoDelegadoAct;
    public EventAdapterAdminActividad(String delegadoAct, String codigoDelegadoAct) {
        this.delegadoAct = delegadoAct;
        this.codigoDelegadoAct = codigoDelegadoAct;
    }

    public EventAdapterAdminActividad() {
        this.context = context;
    }

    public void setEventList(List<eventoListarUsuario> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_eventos, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        eventoListarUsuario event = eventList.get(position);
        db = FirebaseFirestore.getInstance();

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(event.getNombre());
        holder.fecha.setText("Fecha: " + event.getFecha());
        holder.hora.setText("Hora: " + event.getHora());
        holder.cantApoyos.setText("Apoyos: " + event.getApoyos());

        // Carga la imagen usando Glide
        if (event.getUrl_imagen() != null && !event.getUrl_imagen().isEmpty()) {
            Glide.with(context)
                    .load(event.getUrl_imagen())
                    .into(holder.imageEvento);
        }

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

        // Configura el OnClickListener para el botón "Apoyar"

        obtenerDatosUsuario(usuario -> {


            nombreUsuario = usuario.getNombre();
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());

            String nombreEvento = event.getNombre();
            String nombreUsuario = usuario.getNombre();

            if(delegadoAct.equals(event.getDelegado())){
                holder.apoyarListaEvento.setVisibility(View.INVISIBLE);
            }else{


                // Obtén la referencia a la colección "participantes"
                CollectionReference participantesRef = db.collection("participantes");

                // Realiza la consulta para buscar el documento en participantes
                Query consulta = participantesRef
                        .whereEqualTo("evento", nombreEvento)
                        .whereEqualTo("nombre", nombreUsuario);

                consulta.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verifica si se encontraron documentos que cumplen con las condiciones
                        if (!task.getResult().isEmpty()) {
                            // Existe un documento que cumple con las condiciones, oculta el botón
                            Log.d("msg-test", "Ya existe el participante en el evento: " +event.getNombre());
                            holder.apoyarListaEvento.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d("msg-test", "No existe el participante en el evento: " +event.getNombre());
                            // No existe un documento que cumpla con las condiciones, muestra el botón
                            holder.apoyarListaEvento.setVisibility(View.VISIBLE);
                            holder.apoyarListaEvento.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final EventAdapterAdminActividad.EventViewHolder finalHolder = holder;
                                    showConfirmationDialog(event, finalHolder);
                                }
                            });
                        }
                    } else {
                        // Manejar el error al realizar la consulta en la colección "participantes"
                        Log.e("msg-test", "Error al consultar la colección participantes", task.getException());
                    }
                });

                /*if (context != null) {
                    // Utiliza la clave correcta para verificar la visibilidad
                    boolean isButtonVisible = sharedPreferences.getBoolean("apoyarListaEventoVisibility_" + event.getId(), true);
                    holder.apoyarListaEvento.setVisibility(isButtonVisible ? View.VISIBLE : View.GONE);
                }*/


            }


        });



        holder.verListaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVerEventoClick(event);  // Aquí maneja el clic del botón "Ver Evento"
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleActividad;
        TextView fecha;
        TextView hora;
        TextView cantApoyos;
        Button verListaEvento;
        Button apoyarListaEvento;
        Button verEventosApoyados;
        ImageView imageEvento;  // Asegúrate de tener esta línea


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            cantApoyos = itemView.findViewById(R.id.cantApoyos);
            verListaEvento = itemView.findViewById(R.id.verListaEvento);
            apoyarListaEvento = itemView.findViewById(R.id.apoyarListaEvento);
            imageEvento = itemView.findViewById(R.id.imageActividad);  // Asegúrate de tener esta línea

        }
    }
    private void actualizarCampoApoyos(String eventoId, String nuevoValorApoyos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventoRef = db.collection("eventos").document(eventoId);

        eventoRef
                .update("apoyos", nuevoValorApoyos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Apoyo registrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error al registrar el apoyo", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void registrarParticipantes(String nombreEvento) {
        // Código para registrar al participante
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String asignacion = "barra";
        String codigo = codigoDelegadoAct;
        String evento = nombreEvento;
        String nombre = delegadoAct;


        // Crea un documento para el participante
        db.collection("participantes")
                .add(new participante(asignacion, codigo, evento,nombre))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Participante registrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error al registrar al participante", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showConfirmationDialog(eventoListarUsuario event,  EventViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar Apoyo");
        builder.setMessage("¿Estás seguro de que quieres apoyar este evento?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Usuario hizo clic en "Sí", realiza la acción de apoyo
                int apoyosActual = Integer.parseInt(event.getApoyos());
                apoyosActual++;
                String nuevoValorApoyos = String.valueOf(apoyosActual);

                actualizarCampoApoyos(event.getId(), nuevoValorApoyos);
                registrarParticipantes(event.getNombre());
                holder.cantApoyos.setText("Apoyos: " + nuevoValorApoyos);

                holder.apoyarListaEvento.setVisibility(View.GONE);

                // Actualiza la preferencia compartida para este evento
                if (context != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("apoyarListaEventoVisibility_" + event.getId(), false);
                    editor.apply();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Usuario hizo clic en "No", no hace nada o puedes mostrar un mensaje de cancelación
            }
        });
        builder.show();
    }


    private void obtenerDatosUsuario(FirstFragment.FirestoreCallback callback) {
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