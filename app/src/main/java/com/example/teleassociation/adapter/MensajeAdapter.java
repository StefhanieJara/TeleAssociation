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
import com.example.teleassociation.dto.Mensaje;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private List<Mensaje> dataList;
    private Context context;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    public MensajeAdapter(Context context, List<Mensaje> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.firestore = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = dataList.get(position);

        // Configuración del contenido del elemento
        holder.textMessage.setText(mensaje.getMensaje());

        // Cargar informaa2ción del usuario si el campo usuario es válido
        if (mensaje.getUsuario() != null && !mensaje.getUsuario().isEmpty()) {
            loadUserInfo(mensaje.getUsuario(), holder);
        } else {
            // Manejar el caso en que el campo usuario no es válido
            // Puedes establecer un valor predeterminado en el ViewHolder o dejar los campos en blanco
            holder.textUserName.setText("Usuario Desconocido");
            // También puedes manejar la carga de la imagen del avatar aquí si es necesario
        }
    }

    private void loadUserInfo(String userId, ViewHolder holder) {
        DocumentReference userRef = firestore.collection("usuarios").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("nombre");
                String userProfileImageUrl = documentSnapshot.getString("url_imagen");

                // Configurar la información del usuario en el ViewHolder
                holder.textUserName.setText(userName);
                // Puedes cargar la imagen del avatar usando una biblioteca como Picasso o Glide
                // Glide.with(context).load(userProfileImageUrl).into(holder.imageAvatar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textUserName;
        TextView textMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textUserName = itemView.findViewById(R.id.textUsuario);
            textMessage = itemView.findViewById(R.id.textMensaje);
        }
    }
}
