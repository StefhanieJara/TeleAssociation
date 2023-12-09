package com.example.teleassociation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class subirFotoEventAlum extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_foto_event_alum);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        Button selectImageBtn = findViewById(R.id.selectImageBtn);
        Button subirImagenBtn = findViewById(R.id.fechaDelActv);

        selectImageBtn.setOnClickListener(v -> seleccionarImagen());
        subirImagenBtn.setOnClickListener(v -> subirImagen());
    }
    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void subirImagen() {
        if (selectedImageUri != null) {
            // Generar un nombre único para la imagen
            String imageName = "eventoFoto_" + System.currentTimeMillis() + ".jpg";

            // Crear una referencia al lugar en Firebase Storage donde se guardará la imagen
            StorageReference imageRef = storageReference.child("eventosFotosUsuarios/" + imageName);

            // Subir la imagen a Firebase Storage
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Imagen subida con éxito, obtenemos la URL de descarga
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Guardar la URL de la imagen en la colección eventoFotos
                    guardarUrlImagenEnFirestore(uri.toString());
                });
            }).addOnFailureListener(exception -> {
                // Manejar errores durante la subida
                exception.printStackTrace();
            });
        } else {
            // No se ha seleccionado ninguna imagen
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarUrlImagenEnFirestore(String imageUrl) {
        // Obtener el ID del evento desde la actividad anterior (puedes pasarlo como extra)
        String eventoId = getIntent().getStringExtra("eventoId");

        // Crear un nuevo documento en la colección eventoFotos
        Map<String, Object> fotoMap = new HashMap<>();
        fotoMap.put("eventoId", eventoId);
        fotoMap.put("url_imagen", imageUrl);

        FirebaseFirestore.getInstance()
                .collection("eventoFotos")
                .add(fotoMap)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar la URL en Firestore
                    Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    // Puedes cerrar la actividad o realizar otras acciones aquí
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al guardar en Firestore
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar la imagen en Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtener la URI de la imagen seleccionada
            selectedImageUri = data.getData();
        }
    }
}