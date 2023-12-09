package com.example.teleassociation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teleassociation.Usuario.eventoDetalleAlumno;
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
    private TextView progressTextView;

    private ImageView imageViewFoto;

    private Button subirImagenBtn;

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
        subirImagenBtn = findViewById(R.id.fechaDelActv);

        progressTextView = findViewById(R.id.progresoCarga);
        imageViewFoto = findViewById(R.id.foto);


        selectImageBtn.setOnClickListener(v -> seleccionarImagen());
        subirImagenBtn.setOnClickListener(v -> subirImagen());
        subirImagenBtn.setVisibility(View.INVISIBLE);

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
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(imageViewFoto);
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();

                // Volver a la actividad de detalle del evento
                volverAEventoDetalleAlumno();
            }).addOnFailureListener(exception -> {
                // Manejar errores durante la subida
                exception.printStackTrace();
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                volverAEventoDetalleAlumno();
            }).addOnProgressListener(taskSnapshot -> {
                // Actualizar el progreso de carga en el TextView
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressTextView.setText(String.format("Progreso: %.2f%%", progress));
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
            selectedImageUri = data.getData();

            // Habilitamos el botón de subir imagen
            subirImagenBtn.setVisibility(View.VISIBLE);

            // También puedes actualizar la vista de la imagen previa si es necesario
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(imageViewFoto);
        }
    }

    private void volverAEventoDetalleAlumno() {
        // Obtener el ID del evento desde el Intent actual
        String eventoId = getIntent().getStringExtra("eventoId");

        // Crea un Intent para volver a la actividad anterior (eventoDetalleAlumno)
        Intent intent = new Intent(subirFotoEventAlum.this, eventoDetalleAlumno.class);
        intent.putExtra("eventoId", eventoId); // Pasa el ID del evento a la actividad anterior

        // Inicia la actividad
        startActivity(intent);

        // Agrega un retraso breve antes de cerrar la actividad actual
        new Handler().postDelayed(() -> finish(), 100); // 100 milisegundos de retraso
    }
}