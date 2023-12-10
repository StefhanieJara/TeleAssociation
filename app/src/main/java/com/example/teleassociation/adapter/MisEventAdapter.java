package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.Usuario.eventoDetalleAlumno;

import java.util.List;

public class MisEventAdapter extends RecyclerView.Adapter<MisEventAdapter.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;
//
    public MisEventAdapter() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mis_eventos, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        eventoListarUsuario event = eventList.get(position);

        // Asigna los datos a los elementos de la vista

        holder.miActividad.setText(event.getNombre_actividad());
        holder.miEvento.setText(event.getNombre());
        holder.fechaHoraRegistrada.setText(event.getFecha());

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

        holder.verEvento.setOnClickListener(new View.OnClickListener() {
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
        TextView miActividad;
        TextView miEvento;
        TextView fechaHoraRegistrada;
        Button verEvento;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            miActividad = itemView.findViewById(R.id.miActividad);
            miEvento = itemView.findViewById(R.id.miEvento);
            fechaHoraRegistrada = itemView.findViewById(R.id.fechaHoraRegistrada);
            verEvento = itemView.findViewById(R.id.verEvento);
        }
    }
}
