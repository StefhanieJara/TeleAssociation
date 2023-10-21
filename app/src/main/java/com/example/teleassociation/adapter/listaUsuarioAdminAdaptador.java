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
import com.example.teleassociation.dto.usuario;
import java.util.List;

public class listaUsuarioAdminAdaptador extends RecyclerView.Adapter<listaUsuarioAdminAdaptador.UsuarioViewHolder> {

    private List<usuario> usuarioList;
    private Context context;

    public listaUsuarioAdminAdaptador() {this.context = context;}

    public void setUsuarioList(List<usuario> usuarioList) {
        this.usuarioList = usuarioList;
        notifyDataSetChanged();
    }

    public Context getContext() {return context;}

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_usuarios_generaladmin, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        usuario usuario = usuarioList.get(position);

        // Asigna los datos a los elementos de la vista
        holder.usuario.setText(usuario.getNombre());
        holder.condicion.setText("Condición: " + usuario.getCondicion());
        holder.validacion.setText("Validación: " + usuario.getValidado());
    }

    @Override
    public int getItemCount() {
        return usuarioList != null ? usuarioList.size() : 0;
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        TextView condicion;
        TextView validacion;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuario);
            condicion = itemView.findViewById(R.id.condicion);
            validacion = itemView.findViewById(R.id.validacion);
        }
    }
}
