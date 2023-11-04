package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.adminActividad.EventoDetalleAdminActvidadFragment;
import com.example.teleassociation.adminActividad.ListaActividadesDelactvActivity;
import com.example.teleassociation.adminActividad.MisEventosCreadosFragment;
import com.example.teleassociation.dto.eventoListarUsuario;

import java.util.List;

public class MisEventAdapterAdminActv extends RecyclerView.Adapter<MisEventAdapterAdminActv.EventViewHolder> {

    private List<eventoListarUsuario> eventList;
    private Context context;
    private OnVerEventoClickListener listener;

    // Interfaz para manejar clics en el botón "verEvento"
    public interface OnVerEventoClickListener {
        void onVerEventoClick(eventoListarUsuario evento);
    }
    //
    public MisEventAdapterAdminActv() {
        this.context = context;
    }
    public void setListener(OnVerEventoClickListener listener) {
        this.listener = listener;
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

        // Manejar el clic del botón "Ver Evento"

        holder.verEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVerEventoClick(event);
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
