package com.example.teleassociation.adapter;

import static com.android.volley.VolleyLog.TAG;

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
import com.example.teleassociation.dto.notificacion;
import com.example.teleassociation.dto.pagos;
import com.example.teleassociation.dto.usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

                String mensaje;
                if ("Egresado".equals(holder.condicion)) {
                    mensaje = "¡Gracias por su donación! Puede pasar a recoger su kit teleco a la 1:00 pm del 14/12/2023.";
                } else if ("Estudiante".equals(holder.condicion)) {
                    mensaje = "¡Gracias por su donación! Tu donación ha sido recibida.";
                }

                // Obtener el token de Firebase Messaging
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String token = task.getResult();
                                // Luego, envías la notificación con el token obtenido
                                enviarNot(token, mensaje);
                            } else {
                                // Manejar el error al obtener el token
                                Log.w(TAG, "Error al obtener el token.", task.getException());
                            }
                        });
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
    private void enviarNot(String token, String mensaje) {
        pagos pagos = new pagos();

        try {
            JSONObject jsonObject = new JSONObject();


            JSONObject notification = new JSONObject();
            notification.put("title", "TeleAssociation");
            notification.put("body", mensaje);
            notification.put("priority", "high");
            JSONObject dataObj = new JSONObject();


            jsonObject.put("notification", notification);
            jsonObject.put("data",dataObj);
            jsonObject.put("to", token);
            callApi(jsonObject);

            // Crear instancia de FirebaseFirestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Obtener la referencia de la colección "notificaciones"
            CollectionReference notificacionesRef = firestore.collection("notificaciones");

            // Crear una nueva instancia de la clase notificacion con los datos necesarios
            notificacion nuevaNotificacion = new notificacion("TeleAssociation", Timestamp.now(), mensaje, pagos.getCodigo_usuario());
            notificacionesRef.add(nuevaNotificacion)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("msg-test", "Notificación almacenada en Firestore con ID: " + documentReference.getId());
                        // Continuar con tu lógica aquí, como enviar la notificación FCM
                    })
                    .addOnFailureListener(e -> {
                        Log.e("msg-test", "Error al almacenar la notificación en Firestore", e);
                        // Manejar el error según sea necesario
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void callApi(JSONObject jsonObject){
        okhttp3.MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        okhttp3.Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAEzmjgrY:APA91bEN69zZ8gYBGdJOEWq8RWoff5Fi9A4eHhYk9q-Q5ITiBEXq66mzC_UvFTQARX53-7dh7aQKPjVIfeC4QWV02_ZAjQzbzAshXRswNoFtxq6gRB3cmH5aekYiM-dt6tHOG1T6gfUx")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });


    }




}
