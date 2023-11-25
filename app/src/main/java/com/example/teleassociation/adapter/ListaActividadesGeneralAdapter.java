package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;

import java.util.List;

public class ListaActividadesGeneralAdapter extends RecyclerView.Adapter<ListaActividadesGeneralAdapter.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;

    public ListaActividadesGeneralAdapter() {
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

        // Carga la imagen usando Glide
        if (event.getUrl_imagen() != null && !event.getUrl_imagen().isEmpty()) {
            Glide.with(context)
                    .load(event.getUrl_imagen())
                    .into(holder.imageEvento);
        }

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(event.getNombre());
        holder.fecha.setText("Fecha: " + event.getFecha());
        holder.hora.setText("Hora: " + event.getHora());
        holder.cantApoyos.setText("Apoyos: " + event.getApoyos());

        holder.buttonApoyar.setVisibility(View.GONE);
        holder.buttonVerEvento.setVisibility(View.GONE);

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
        Button buttonApoyar;
        ImageView imageEvento;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            cantApoyos = itemView.findViewById(R.id.cantApoyos);
            buttonVerEvento = itemView.findViewById(R.id.buttonVerEvento);
            buttonApoyar = itemView.findViewById(R.id.buttonApoyar);
            imageEvento = itemView.findViewById(R.id.imageActividad);
        }
    }
}
