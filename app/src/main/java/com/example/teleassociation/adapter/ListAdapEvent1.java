package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.evento;
import java.util.List;

public class ListAdapEvent1 extends RecyclerView.Adapter<ListAdapEvent1.EventViewHolder> {

    private List<evento> eventList;
    private Context context;

    public ListAdapEvent1() {
        this.context = context;
    }

    public void setEventList(List<evento> eventList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        evento event = eventList.get(position);

        // Asigna los datos a los elementos de la vista
        holder.miActividad.setText(event.getEvento());
        holder.fechaHoraRegistrada.setText("Fecha: " + event.getFecha()+" Hora: "+event.getHora());

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

        holder.verEvento.setOnClickListener(new View.OnClickListener() {
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
        TextView miActividad;
        TextView fechaHoraRegistrada;
        Button verEvento;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            miActividad = itemView.findViewById(R.id.miActividad);
            fechaHoraRegistrada = itemView.findViewById(R.id.fechaHoraRegistrada);
            verEvento = itemView.findViewById(R.id.verEvento);
        }
    }
}
