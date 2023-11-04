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

import com.example.teleassociation.ListaParticipantes;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.participante;

import java.util.List;

public class ListAdaptParticipantes extends RecyclerView.Adapter<ListAdaptParticipantes.EventViewHolder>{
    private List<participante> participanteList;
    private Context context;
    private MisEventAdapterAdminActv.OnVerEventoClickListener listener;


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

        // Aquí puedes asignar otros datos como la descripción, estado, actividad, etc.

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
}
