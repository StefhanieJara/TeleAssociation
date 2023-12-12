package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.teleassociation.adminGeneral.EditarActividadAdmin;
import com.example.teleassociation.dto.actividad;

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

        // Carga la imagen usando Glide
        if (actividad.getUrl_imagen() != null && !actividad.getUrl_imagen().isEmpty()) {
            Glide.with(context)
                    .load(actividad.getUrl_imagen())
                    .into(holder.imageActividad);
        }

        holder.buttonEditarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditarActividadAdmin.class);

                intent.putExtra("actividadNombre", actividad.getNombre());
                intent.putExtra("actividadDescripcion", actividad.getDescripcion());
                intent.putExtra("actividadDelegado", actividad.getDelegado());
                intent.putExtra("actividadImagen", actividad.getUrl_imagen());

                // Iniciar la nueva actividad
                context.startActivity(intent);
            }
        });

        /*holder.buttonVerEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, estadisticasPorActividad.class);
                intent.putExtra("actividadNombre", actividad.getNombre());
                context.startActivity(intent);

            }
        });*/




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
        ImageView imageActividad;  // Asegúrate de tener esta línea
        Button buttonVerEstadisticas;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            delegado = itemView.findViewById(R.id.delegado);
            descripcion = itemView.findViewById(R.id.descripcion);
            imageActividad = itemView.findViewById(R.id.imageActividad);  // Asegúrate de tener esta línea
            buttonEditarActividad = itemView.findViewById(R.id.buttonEditarActividad);
            buttonVerEstadisticas = itemView.findViewById(R.id.buttonVerEstadisticas);
        }
    }
}
