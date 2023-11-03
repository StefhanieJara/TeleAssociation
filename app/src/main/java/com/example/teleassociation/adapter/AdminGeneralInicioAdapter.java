package com.example.teleassociation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.eventoListarUsuario;

import org.w3c.dom.Text;

import java.util.List;

public class AdminGeneralInicioAdapter extends RecyclerView.Adapter<AdminGeneralInicioAdapter.EventViewHolder> {

    private List<actividad> actividadLista;
    private Context context;

    public AdminGeneralInicioAdapter() {
        this.context = context;
    }

    public void setactividadLista(List<actividad> actividadLista) {
        this.actividadLista = actividadLista;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actividades_admin, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        actividad actividad = actividadLista.get(position);

        Log.d("msg-test", " | nombre: " + actividad.getNombre() + " | delegado: " + actividad.getDelegado() + " | descripcion: " + actividad.getDescripcion());

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(actividad.getNombre());
        holder.delegado.setText("Delegado: "+actividad.getDelegado());
        holder.descripcion.setText("Descripción: "+actividad.getDescripcion());

        holder.buttonEditarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí maneja el clic del botón "Ver Evento"
            }
        });
    }

    @Override
    public int getItemCount() {
        return actividadLista != null ? actividadLista.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleActividad;
        TextView delegado;
        TextView descripcion;
        Button buttonEditarActividad;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            delegado = itemView.findViewById(R.id.delegado);
            descripcion = itemView.findViewById(R.id.descripcion);
            buttonEditarActividad = itemView.findViewById(R.id.buttonEditarActividad);
        }
    }
}
