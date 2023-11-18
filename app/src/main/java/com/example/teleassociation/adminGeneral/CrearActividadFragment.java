package com.example.teleassociation.adminGeneral;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.inicio_usuario;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.pagos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class CrearActividadFragment extends Fragment {

    FirebaseFirestore db;

    private AutoCompleteTextView delegado;
    FirebaseStorage storage;

    StorageReference reference;

    private ArrayAdapter<String> adapterItems;
    private View rootView; // Declarar rootView aquí
    // URI de la imagen seleccionada
    private Uri uri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_crear_actividad, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        reference=storage.getReference();



        Button button12 = rootView.findViewById(R.id.button12);
        Button botonimage = rootView.findViewById(R.id.imagen);

        TextInputEditText actividad = rootView.findViewById(R.id.actividad);
        TextInputEditText descripcion = rootView.findViewById(R.id.descripcion);

        String[] items = {"Diego Lavado", "Leonardo Abanto", "Miguel Ahumada"};

        delegado = rootView.findViewById(R.id.delegado);
        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
        delegado.setAdapter(adapterItems);
        delegado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                // Puedes realizar acciones con el nombre seleccionado aquí
                Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        button12.setOnClickListener(view -> {
            String nombreActividad = actividad.getText().toString().trim();
            String descripcionActividad = descripcion.getText().toString().trim();
            String delegadoName = delegado.getText().toString().trim();

            if (nombreActividad.isEmpty() || descripcionActividad.isEmpty()  || delegadoName.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                actividad actividad1 = new actividad();
                actividad1.setDelegado(delegadoName);
                actividad1.setDescripcion(descripcionActividad);
                actividad1.setNombre(nombreActividad);
                String cod_al = generateRandomCode();
                // Subir la imagen a Firebase Storage
                StorageReference imageRef = reference.child("actividades/" + uri.getLastPathSegment());
                UploadTask uploadTask = imageRef.putFile(uri);

                // Observadores para manejar el éxito, el fallo y el progreso de la carga
                uploadTask.addOnFailureListener(exception -> {
                    exception.printStackTrace();
                }).addOnSuccessListener(taskSnapshot -> {
                    // Obtén la URL de la imagen subida
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Guarda la URL de la imagen en la actividad
                        actividad1.setUrl_imagen(downloadUri.toString());

                        // Guarda la actividad en la base de datos
                        db.collection("actividad")
                                .document(cod_al)
                                .set(actividad1)
                                .addOnSuccessListener(unused -> {
                                    Intent intent = new Intent(getContext(), inicioAdmin.class);
                                    intent.putExtra("Actividad creada.", true);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                });
                    });
                }).addOnProgressListener(snapshot -> {
                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();
                    double porcentajeSubida = Math.round(bytesTransferred * 1.0f / totalByteCount * 100);
                    TextView textoSubida = rootView.findViewById(R.id.subiendo);
                    textoSubida.setText(porcentajeSubida + "%");
                });
            }
        });

        botonimage.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());

        });


        //Para el boton estadisticas

        ImageView btnStats = rootView.findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), estadisticasAdmin.class);
                startActivity(intent);

            }
        }); //aca termina lo del boton estadisticas


        return rootView;
    }

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomCode = new StringBuilder();
        int length = 6; // Puedes ajustar la longitud del código como desees

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            randomCode.append(characters.charAt(index));
        }

        return randomCode.toString();
    }


    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result != null) {
                    uri = result;
                    // Cargar la imagen seleccionada en un ImageView (opcional)
                    ImageView imageView = rootView.findViewById(R.id.imageView);
                    Glide.with(requireContext()).load(uri).into(imageView);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });





}