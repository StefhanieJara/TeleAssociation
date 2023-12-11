package com.example.teleassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.notificacion;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private List<notificacion> notificaciones;
    private Context context;

    public NotificacionAdapter(Context context, List<notificacion> notificaciones) {
        this.context = context;
        this.notificaciones = notificaciones;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notif, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));

        notificacion notif = notificaciones.get(position);

        Timestamp fecha = notif.getFecha();
        Date date = fecha.toDate();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM HH:mm", new Locale("es", "ES"));
        String fechaFormateada = formatoFecha.format(date);

        holder.titulo.setText(notif.getTitulo());
        holder.detalle.setText(notif.getDetalle());
        holder.fecha.setText(fechaFormateada);
        // Puedes personalizar cómo manejas la imagen de notificación (notif.icon) según tus necesidades
        holder.icon.setImageDrawable(context.getDrawable(R.drawable.baseline_notifications_24));
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView fecha;
        TextView titulo;
        TextView detalle;
        ImageView icon;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloNotif);
            fecha = itemView.findViewById(R.id.fechaNotif);
            detalle = itemView.findViewById(R.id.detalleNotif);
            icon = itemView.findViewById(R.id.iconNotif);
        }
    }
}
