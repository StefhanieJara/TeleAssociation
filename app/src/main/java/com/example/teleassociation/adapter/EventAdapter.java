package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.example.teleassociation.eventoDetalleAlumno;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private String delegadoActividad;

    public EventAdapter(String delegadoActividad) {
        this.delegadoActividad = delegadoActividad;
    }

    public EventAdapter() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actividades_usuario, parent, false);
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

        // Carga la imagen usando Glide
        if (event.getUrl_imagen() != null && !event.getUrl_imagen().isEmpty()) {
            Glide.with(context)
                    .load(event.getUrl_imagen())
                    .into(holder.imageEvento);
        }

        // Aquí agregamos la condición para ocultar el botón
        if (delegadoActividad != null && delegadoActividad.equals(event.getNombre())) {
            holder.buttonApoyarEvento.setVisibility(View.GONE);
        } else {
            holder.buttonApoyarEvento.setVisibility(View.VISIBLE);
            // Configuramos el OnClickListener solo si el botón está visible
            holder.buttonApoyarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EventViewHolder finalHolder = holder;
                    showConfirmationDialog(event, finalHolder);
                }
            });
        }

        holder.buttonVerEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, eventoDetalleAlumno.class);
                intent.putExtra("eventoId", event.getId());
                context.startActivity(intent);
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
        Button buttonVerEvento;
        Button buttonApoyarEvento;

        ImageView imageEvento;  // Asegúrate de tener esta línea


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            cantApoyos = itemView.findViewById(R.id.cantApoyos);
            buttonVerEvento = itemView.findViewById(R.id.buttonVerEvento);
            buttonApoyarEvento = itemView.findViewById(R.id.buttonApoyar);
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
        String codigo = "20200839";
        String evento = nombreEvento;
        String nombre = "Rex";


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
}