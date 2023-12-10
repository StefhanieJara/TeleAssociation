package com.example.teleassociation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.bumptech.glide.Glide;
import com.example.teleassociation.adminGeneral.donacionesAdmin;
import com.example.teleassociation.dto.pagos;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class donacionesAdminAdapter extends RecyclerView.Adapter<donacionesAdminAdapter.EventViewHolder> {

    private List<pagos> actividadDonaciones;
    private Context context;


    public donacionesAdminAdapter() {
        this.context = context;
    }
    public void setActividadDonaciones(List<pagos> actividadLista) {
        this.actividadDonaciones = actividadLista;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_donaciones, parent, false);
        return new EventViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull donacionesAdminAdapter.EventViewHolder holder, int position) {
        pagos pagos = actividadDonaciones.get(position);

        // Asigna los datos a los elementos de la vista
        holder.monto.setText("Monto: " + pagos.getMonto());

        // Obtén el nombre y la condición del usuario asociado al código
        obtenerInformacionUsuario(pagos.getCodigo_usuario(), new OnUsuarioInformacionObtenidaListener() {
            @Override
            public void onUsuarioInformacionObtenida(String nombreUsuario, String condicionUsuario) {
                // Carga el nombre y la condición del usuario en los TextView correspondientes
                holder.nombreUsuario.setText("Nombre: " + nombreUsuario);
                holder.condicion.setText("Condición: " + condicionUsuario);
            }
        });

        // Carga la imagen usando Glide
        if (pagos.getUrl_imagen() != null && !pagos.getUrl_imagen().isEmpty()) {
            Glide.with(context)
                    .load(pagos.getUrl_imagen())
                    .into(holder.imagenDonación);
        }
        Log.d("message", "validado: "+pagos.getValidado());

        if("Sí".equals(pagos.getValidado())){
            holder.confirmar.setVisibility(View.GONE);
            holder.rechazar.setVisibility(View.GONE);
            holder.validado.setVisibility(View.VISIBLE);
            holder.validado.setText("Donación confirmada");
        } else if ("No".equals(pagos.getValidado())) {
            holder.confirmar.setVisibility(View.GONE);
            holder.rechazar.setVisibility(View.GONE);
            holder.validado.setVisibility(View.VISIBLE);
            holder.validado.setText("Donación rechazada");

        }else if("Pendiente".equals(pagos.getValidado())){
            // Mostrar botones y ocultar mensaje de validación
            holder.confirmar.setVisibility(View.VISIBLE);
            holder.rechazar.setVisibility(View.VISIBLE);
            holder.validado.setVisibility(View.GONE);

            holder.confirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialogoConfirmacion(pagos, holder);
                }
            });

            holder.rechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialogoRechazo(pagos, holder);
                }
            });
        }

    }

    private void mostrarDialogoConfirmacion(pagos pagos, EventViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar Pago");
        builder.setMessage("¿Está seguro de confirmar el pago?");

        // Agregar botón "Sí"
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Acción a realizar si el usuario hace clic en "Sí"
                confirmarPago(pagos, holder);
            }
        });

        // Agregar botón "No"
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Acción a realizar si el usuario hace clic en "No"
                dialog.dismiss();  // Cerrar el diálogo
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    private void confirmarPago(pagos pagos, EventViewHolder holder) {
        // Actualizar el estado "validado" del pago en Firestore
        FirebaseFirestore.getInstance().collection("pagos")
                .document(pagos.getId())
                .update("validado", "Sí")
                .addOnSuccessListener(aVoid -> {
                    // Actualización exitosa, puedes realizar acciones adicionales si es necesario
                    pagos.setValidado("Sí");
                    mostrarMensajeValidacion(true, holder);
                    Toast.makeText(context, "Pago confirmado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar la falla de la actualización
                    Toast.makeText(context, "Error al confirmar el pago", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDialogoRechazo(pagos pagos, EventViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rechazar Pago");
        builder.setMessage("¿Está seguro de rechazar el pago?");

        // Agregar botón "Sí"
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Acción a realizar si el usuario hace clic en "Sí"
                rechazarPago(pagos,holder);
            }
        });

        // Agregar botón "No"
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Acción a realizar si el usuario hace clic en "No"
                dialog.dismiss();  // Cerrar el diálogo
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    private void rechazarPago(pagos pagos, EventViewHolder holder) {
        // Actualizar el estado "validado" del pago en Firestore
        FirebaseFirestore.getInstance().collection("pagos")
                .document(pagos.getId())
                .update("validado", "No")
                .addOnSuccessListener(aVoid -> {
                    // Actualización exitosa, puedes realizar acciones adicionales si es necesario
                    pagos.setValidado("No");
                    mostrarMensajeValidacion(false, holder);
                    Toast.makeText(context, "Pago rechazado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar la falla de la actualización
                    Toast.makeText(context, "Error al rechazar el pago", Toast.LENGTH_SHORT).show();
                });
    }




    private void obtenerInformacionUsuario(String codigoUsuario, OnUsuarioInformacionObtenidaListener listener) {
        // Realiza una consulta a Firestore para obtener el nombre y la condición del usuario
        FirebaseFirestore.getInstance().collection("usuarios")
                .document(codigoUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento del usuario existe, obtén el nombre y la condición
                        String nombreUsuario = documentSnapshot.getString("nombre");
                        String condicionUsuario = documentSnapshot.getString("condicion");
                        listener.onUsuarioInformacionObtenida(nombreUsuario, condicionUsuario);
                    } else {
                        // El documento del usuario no existe, puedes manejarlo según tus necesidades
                        listener.onUsuarioInformacionObtenida("Usuario no encontrado", "Condición no encontrada");
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja la excepción que ocurra al intentar obtener el documento
                    listener.onUsuarioInformacionObtenida("Error al obtener el usuario", "Error al obtener la condición");
                });
    }

    // Interfaz para manejar la devolución del nombre y la condición del usuario
    interface OnUsuarioInformacionObtenidaListener {
        void onUsuarioInformacionObtenida(String nombreUsuario, String condicionUsuario);
    }

    @Override
    public int getItemCount() {
        return actividadDonaciones != null ? actividadDonaciones.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuario;
        TextView condicion;
        TextView monto;
        ImageView imagenDonación;  // Asegúrate de tener esta línea
        Button confirmar;
        Button rechazar;
        TextView validado;



        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            condicion = itemView.findViewById(R.id.condicion);
            monto = itemView.findViewById(R.id.monto);
            imagenDonación = itemView.findViewById(R.id.imagenDonacion);  // Asegúrate de tener esta línea
            confirmar = itemView.findViewById(R.id.confirmar);
            rechazar = itemView.findViewById(R.id.rechazar);
            validado = itemView.findViewById(R.id.valido);

        }
    }

    public void mostrarMensajeValidacion(boolean confirmado, EventViewHolder holder) {
        if (confirmado) {
            holder.confirmar.setVisibility(View.GONE);
            holder.rechazar.setVisibility(View.GONE);
            holder.validado.setVisibility(View.VISIBLE);
            holder.validado.setText("Donación confirmada");
        } else {
            holder.confirmar.setVisibility(View.GONE);
            holder.rechazar.setVisibility(View.GONE);
            holder.validado.setVisibility(View.VISIBLE);
            holder.validado.setText("Donación rechazada");
        }
    }



}
