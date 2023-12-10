package com.example.teleassociation.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.EmailSender;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.adminGeneral.validarParticipanteAdmin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class PersonasGeneralAdapter extends RecyclerView.Adapter<PersonasGeneralAdapter.PersonasGeneralViewHolder> {

    private List<usuario> usuarioLista;
    private Context context;
    FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();
        usuario usuario = usuarioLista.get(position);

        Log.d("msg-test", " Dentro del onBind | codigo: " + usuario.getId() + " | nombre: " + usuario.getNombre() + " | condicion: " + usuario.getCondicion() + " | validacion: " + usuario.getValidado());

        // Asigna los datos a los elementos de la vista
        holder.usuario.setText(usuario.getNombre());
        holder.condicion.setText(usuario.getCondicion());
        holder.validacion.setText(usuario.getValidado());


        if ("Si".equals(usuario.getValidado())) {
            // Si el valor de usuario.getValidado() es "Si", ocultar el botón validarUsuario
            holder.validarUsuario.setVisibility(View.INVISIBLE);
            holder.denegarUsuario.setVisibility(View.VISIBLE);

            holder.denegarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Estás seguro de banear al usuario? No hay vuelta atrás.");

                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("msg-test", "El codigo del usuario a poder banear es: "+usuario.getId());
                            Log.d("msg-test", "El nombre del usuario a poder banear es: "+usuario.getNombre());
                            db.collection("actividad")
                                    .whereEqualTo("delegado", usuario.getNombre())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            // El usuario es delegado en al menos una actividad, mostrar un Toast
                                            Toast.makeText(context, "No se puede banear a un delegado de actividad.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            db.collection("participantes")
                                                    .whereEqualTo("codigo", usuario.getId())
                                                    .get()
                                                    .addOnCompleteListener(participantesTask -> {
                                                        if (participantesTask.isSuccessful()) {
                                                            for (QueryDocumentSnapshot participanteDocument : participantesTask.getResult()) {
                                                                String eventoParticipante  = (String) participanteDocument.get("evento");
                                                                Log.d("msg-test", "Evento que fue eliminado: " + eventoParticipante );
                                                                participanteDocument.getReference().delete();
                                                                db.collection("eventos")
                                                                        .whereEqualTo("nombre", eventoParticipante)
                                                                        .get()
                                                                        .addOnCompleteListener(eventosTask -> {
                                                                            if (eventosTask.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot eventoDocument : eventosTask.getResult()) {
                                                                                    // Obtener el valor actual del campo "apoyos"
                                                                                    String apoyosActual = (String) eventoDocument.get("apoyos");

                                                                                    // Convertir el valor a entero
                                                                                    int apoyosEntero = Integer.parseInt(apoyosActual);

                                                                                    // Disminuir en una unidad el valor de apoyos
                                                                                    apoyosEntero--;

                                                                                    // Actualizar el valor del campo "apoyos" en el documento de "eventos"
                                                                                    int finalApoyosEntero = apoyosEntero;
                                                                                    eventoDocument.getReference().update("apoyos", String.valueOf(apoyosEntero))
                                                                                            .addOnSuccessListener(aVoid -> {
                                                                                                Log.d("msg-test", "Campo 'apoyos' actualizado exitosamente. Nuevo valor: " + finalApoyosEntero);
                                                                                            })
                                                                                            .addOnFailureListener(e -> {
                                                                                                Log.e("msg-test", "Error al actualizar el campo 'apoyos'", e);
                                                                                            });

                                                                                    // Ahora puedes realizar otras acciones necesarias con el documento encontrado
                                                                                }
                                                                            } else {
                                                                                // Manejar el error al realizar la consulta en la colección "eventos"
                                                                                Log.e("msg-test", "Error al consultar la colección eventos", eventosTask.getException());
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            // Manejar el error al realizar la consulta en la colección "participantes"
                                                            Log.e("msg-test", "Error al consultar la colección participantes", participantesTask.getException());
                                                        }

                                                        DocumentReference usuarioRef = db.collection("usuarios").document(usuario.getId());
                                                        usuarioRef
                                                                .update("validado", "Baneado")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        EmailSender.sendEmail(usuario.getCorreo(),"Usuario Baneado de TeleAssociation","El usuario ha sido baneado de la plataforma.");
                                                                        holder.validacion.setText("Baneado");
                                                                        holder.denegarUsuario.setVisibility(View.INVISIBLE);
                                                                        Log.d("msg-test", "Usuario baneado correctamente.");
                                                                        Toast.makeText(context, "Usuario baneado correctamente.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e("msg-test", "Error al banear al usuario.", e);
                                                                    }
                                                                });
                                                    });
                                        }
                                    });

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });


        } else if ("No".equals(usuario.getValidado())) {
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
        } else {
            holder.validarUsuario.setVisibility(View.INVISIBLE);
            holder.denegarUsuario.setVisibility(View.INVISIBLE);
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
        Button denegarUsuario;


        public PersonasGeneralViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuario);
            condicion = itemView.findViewById(R.id.condicion);
            validacion = itemView.findViewById(R.id.validacion);
            validarUsuario = itemView.findViewById(R.id.validarUsuario);
            denegarUsuario = itemView.findViewById(R.id.denegarUsuario);
        }
    }
}
