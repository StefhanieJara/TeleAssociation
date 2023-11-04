package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapterAdminActividad extends RecyclerView.Adapter<EventAdapterAdminActividad.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;
    private EventAdapterAdminActividad.OnVerEventoClickListener listener;

    // Interfaz para manejar clics en el botón "verEvento"
    public interface OnVerEventoClickListener {
        void onVerEventoClick(eventoListarUsuario evento);
    }
    public void setListener(EventAdapterAdminActividad.OnVerEventoClickListener listener) {
        this.listener = listener;
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

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(event.getNombre());
        holder.fecha.setText("Fecha: " + event.getFecha());
        holder.hora.setText("Hora: " + event.getHora());
        holder.cantApoyos.setText("Apoyos: " + event.getApoyos());

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

        // Configura el OnClickListener para el botón "Apoyar"
        holder.apoyarListaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int apoyosActual = Integer.parseInt(event.getApoyos());
                apoyosActual++;
                String nuevoValorApoyos = String.valueOf(apoyosActual);

                actualizarCampoApoyos(event.getId(), nuevoValorApoyos);
                registrarParticipantes(event.getNombre());
                holder.cantApoyos.setText("Apoyos: " + nuevoValorApoyos);
            }
        });

        holder.verListaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVerEventoClick(event);  // Aquí maneja el clic del botón "Ver Evento"
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

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            cantApoyos = itemView.findViewById(R.id.cantApoyos);
            verListaEvento = itemView.findViewById(R.id.verListaEvento);
            apoyarListaEvento = itemView.findViewById(R.id.apoyarListaEvento);
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
        String codigo = "20213839";
        String evento = nombreEvento;
        String nombre = "Hiro";


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
}