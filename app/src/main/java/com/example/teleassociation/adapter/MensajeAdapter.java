package com.example.teleassociation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private List<Mensaje> dataList;
    private Context context;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (mensaje.getUsuario() == null || mensaje.getUsuario().isEmpty()) {
            if (user != null) {
                String userEmail = user.getEmail();
                if (userEmail != null) {
                    // Consultar la colección "usuarios" en Firestore solo si el correo del usuario no está presente
                    // en el mensaje o si está presente pero es nulo o vacío
                    if (mensaje.getUsuario() == null || mensaje.getUsuario().isEmpty()) {
                        obtenerNombreUsuarioAutenticado(userEmail, holder);
                        Log.d("clashn't","terrible");
                    }
                }
            }
        } else {
            holder.textUserName.setText(mensaje.getUsuario());
            Log.d("clash",mensaje.getUsuario());
        }

    }
    private void obtenerNombreUsuarioAutenticado(String userEmail, ViewHolder holder) {
        // Consultar la colección "usuarios" en Firestore
        db.collection("usuarios")
                .whereEqualTo("correo", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // La tarea se completó exitosamente, obtener el nombre del usuario
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String nombreUsuario = documentSnapshot.getString("nombre");
                            // Configurar el nombre del usuario solo en el ViewHolder correspondiente
                            holder.textUserName.setText(nombreUsuario);
                            Log.d("NombreUsuario", "Nombre del usuario autenticado: " + nombreUsuario);
                        } else {
                            // No se encontraron documentos que coincidan con el correo del usuario autenticado
                            Log.d("NombreUsuario", "No se encontró el usuario en la colección 'usuarios'");
                        }
                    } else {
                        // La tarea no se completó exitosamente, manejar el error
                        Log.e("NombreUsuario", "Error al obtener el nombre del usuario: " + task.getException().getMessage());
                    }
                });
    }

    public void agregarMensaje(Mensaje mensaje) {
        dataList.add(mensaje);
        notifyItemInserted(dataList.size() - 1);
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
            textUserName = itemView.findViewById(R.id.textUsuario);
            textMessage = itemView.findViewById(R.id.textMensaje);
        }
    }
}
