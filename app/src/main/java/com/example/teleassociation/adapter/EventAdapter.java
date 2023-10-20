package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.evento;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<evento> eventList;
    private Context context;

    public EventAdapter(Context context) {
        this.context = context;
    }

    public void setEventList(List<evento> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actividades_usuario, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        evento event = eventList.get(position);

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(event.getEvento());
        holder.fecha.setText("Fecha: " + event.getFecha());
        holder.hora.setText("Hora: " + event.getHora());
        holder.cantApoyos.setText("Apoyos: " + event.getApoyos());

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

        holder.buttonVerEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí maneja el clic del botón "Ver Evento"
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

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            cantApoyos = itemView.findViewById(R.id.cantApoyos);
            buttonVerEvento = itemView.findViewById(R.id.buttonVerEvento);
        }
    }
}
