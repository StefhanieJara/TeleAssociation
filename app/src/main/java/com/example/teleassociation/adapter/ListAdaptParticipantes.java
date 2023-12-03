package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.ListaParticipantes;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListAdaptParticipantes extends RecyclerView.Adapter<ListAdaptParticipantes.EventViewHolder>{
    private List<participante> participanteList;
    private Context context;
    private MisEventAdapterAdminActv.OnVerEventoClickListener listener;
    FirebaseFirestore db;
    String asignacion;


    public interface OnVerEventoClickListener {
        void onVerEventoClick(eventoListarUsuario evento);
    }
    public void setListener(MisEventAdapterAdminActv.OnVerEventoClickListener listener) {
        this.listener = listener;
    }

    public ListAdaptParticipantes() {
        this.context = context;
    }

    public void setParticipanteList(List<participante> participanteList) {
        this.participanteList = participanteList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_participantes, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        participante part = participanteList.get(position);

        // Asigna los datos a los elementos de la vista
        holder.nameParticipante.setText(part.getNombre());
        holder.codigo.setText(part.getCodigo());
        holder.asignacion.setText(part.getAsignacion());

        if(part.getAsignacion().equals("Delegado")){
            holder.verEvento.setVisibility(View.GONE);
        }else{
            // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.
            holder.verEvento.setOnClickListener(v -> {
                // Implementar lógica para abrir el popup y actualizar Firebase
                mostrarPopupYActualizarFirebase(part.getEvento(), part.getCodigo(), holder.asignacion);
            });
        }


    }

    @Override
    public int getItemCount() {
        return participanteList != null ? participanteList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameParticipante;
        TextView codigo;
        TextView asignacion;
        Button verEvento;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameParticipante = itemView.findViewById(R.id.nameParticipante);
            codigo = itemView.findViewById(R.id.codigo);
            asignacion = itemView.findViewById(R.id.asignacion);
            verEvento = itemView.findViewById(R.id.verEvento);
        }
    }

    private void mostrarPopupYActualizarFirebase(String evento, String codigo, TextView asignacionTextView) {
        // Implementa el código para mostrar un popup y permitir al usuario elegir 'barra' o 'jugador'
        // Puedes usar un AlertDialog con opciones o implementar un diálogo personalizado.

        // Ejemplo usando AlertDialog con lista de opciones
        String[] opciones = {"barra", "jugador"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, (dialog, which) -> {
            asignacion = opciones[which]; // Obtiene la opción seleccionada ('Barra' o 'Jugador')

            Log.d("msg-test", "El usuario a cambiar es " + codigo + " del evento: "+ evento + " y con nueva asignacion: "+asignacion);
            // Actualiza Firebase con la nueva asignación
            actualizarFirebase(evento, codigo, asignacion, asignacionTextView);
        });

        builder.show();
    }

    private void actualizarFirebase(String evento, String codigo, String nuevaAsignacion, TextView asignacionTextView) {
        // Accede a la colección 'participantes' y actualiza el documento correspondiente
        db = FirebaseFirestore.getInstance();
        db.collection("participantes")
                .whereEqualTo("evento", evento)
                .whereEqualTo("codigo", codigo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Actualiza el campo 'asignacion' con la nueva asignación
                        DocumentSnapshot participanteDocument = task.getResult().getDocuments().get(0);
                        participanteDocument.getReference().update("asignacion", nuevaAsignacion)
                                .addOnSuccessListener(aVoid -> {
                                    // Éxito al actualizar Firebase
                                    Log.d("msg-test", "Asignación actualizada con éxito a: " + nuevaAsignacion);

                                    // Actualiza el TextView con la nueva asignación
                                    asignacionTextView.setText(nuevaAsignacion);
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar Firebase
                                    Log.e("msg-test", "Error al actualizar asignación", e);
                                });
                    } else {
                        Log.e("msg-test", "Error al buscar el participante en Firebase", task.getException());
                    }
                });
    }
}
