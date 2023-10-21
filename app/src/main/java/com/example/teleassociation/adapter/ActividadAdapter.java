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
import com.example.teleassociation.R;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.evento;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<actividad> actividadList;
    private Context context;

    public ActividadAdapter() {
        this.context = context;
    }

    public void setActividadList(List<actividad> actividadList) {
        this.actividadList = actividadList;
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
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actividades_admin, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        actividad actividad = actividadList.get(position);

        // Asigna los datos a los elementos de la vista
        holder.titleActividad.setText(actividad.getActividad());
        holder.delegado.setText(actividad.getDelegado());
        holder.descripcion.setText(actividad.getDescripcion());
        holder.buttonVerEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Maneja el clic del botón "Ver Evento" aquí
            }
        });
    }

    @Override
    public int getItemCount() {
        return actividadList != null ? actividadList.size() : 0;
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView titleActividad;
        TextView delegado;
        TextView descripcion;
        Button buttonVerEvento;
        ImageView imageActividad;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActividad = itemView.findViewById(R.id.titleActividad);
            delegado = itemView.findViewById(R.id.delegado);
            descripcion = itemView.findViewById(R.id.descripcion);
            buttonVerEvento = itemView.findViewById(R.id.buttonVerEvento);
            imageActividad = itemView.findViewById(R.id.imageActividad);
        }
    }
}
