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
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.adminGeneral.validarParticipanteAdmin;

import java.util.List;

public class PersonasGeneralAdapter extends RecyclerView.Adapter<PersonasGeneralAdapter.PersonasGeneralViewHolder> {

    private List<usuario> usuarioLista;
    private Context context;

    public PersonasGeneralAdapter() {
        this.context = context;
    }

    public void setUsuarioLista(List<usuario> usuarioLista) {
        this.usuarioLista = usuarioLista;
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
    public PersonasGeneralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_usuarios_generaladmin, parent, false);
        return new PersonasGeneralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonasGeneralViewHolder holder, int position) {
        usuario usuario = usuarioLista.get(position);

        // Asigna los datos a los elementos de la vista
        holder.usuario.setText(usuario.getNombre());
        holder.condicion.setText(usuario.getCondicion());
        holder.validacion.setText(usuario.getValidado());


        if ("Si".equals(usuario.getValidado())) {
            // Si el valor de usuario.getValidado() es "Si", ocultar el botón validarUsuario
            holder.validarUsuario.setVisibility(View.GONE);
        } else {
            // En caso contrario, mostrar el botón validarUsuario
            holder.validarUsuario.setVisibility(View.VISIBLE);

            // Manejar el clic del botón validarUsuario
            holder.validarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Crear un Intent para cambiar a la actividad "validarParticipanteAdmin"
                    Intent intent = new Intent(context, validarParticipanteAdmin.class);

                    intent.putExtra("usuarioCorreo", usuario.getCorreo());
                    intent.putExtra("usuarioNombre", usuario.getNombre());
                    intent.putExtra("usuarioCodigo", usuario.getId());

                    // Iniciar la nueva actividad
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return usuarioLista != null ? usuarioLista.size() : 0;
    }

    public static class PersonasGeneralViewHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        TextView condicion;
        TextView validacion;
        Button validarUsuario;


        public PersonasGeneralViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuario);
            condicion = itemView.findViewById(R.id.condicion);
            validacion = itemView.findViewById(R.id.validacion);
            validarUsuario = itemView.findViewById(R.id.validarUsuario);
        }
    }
}
