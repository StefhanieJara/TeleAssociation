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
import com.example.teleassociation.dto.usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CrearActividadFragment extends Fragment {
    FirebaseFirestore db;
    private AutoCompleteTextView delegado;
    FirebaseStorage storage;
    StorageReference reference;
    private ArrayAdapter<String> adapterItems;
    private View rootView; // Declarar rootView aquí
    // URI de la imagen seleccionada
    private Uri uri;
    usuario usuario = new usuario();
    FirebaseAuth mAuth;
    TextView nameUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_crear_actividad, container, false);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.d("msg-test", "El correo que ingresó es: "+email);

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            Log.d("msg-test", "task2 ha sido valido");
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String comentario = (String) document.get("comentario");
                                String condicion = (String) document.get("condicion");
                                String pass = (String) document.get("contrasenha");
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");
                                String validacion = (String) document.get("validado");
                                String rol = (String) document.get("rol");

                                if(correo.equals(email)){
                                    usuario.setComentario(comentario);
                                    usuario.setCondicion(condicion);
                                    usuario.setContrasenha(pass);
                                    usuario.setCorreo(correo);
                                    usuario.setId(codigo);
                                    usuario.setNombre(nombre);
                                    usuario.setRol(rol);
                                    usuario.setValidado(validacion);
                                    Log.d("msg-test", "| codigo: " + usuario.getId() + " | nombre: " + usuario.getNombre() + "| correo: "+ usuario.getCorreo() +" | condicion: " + usuario.getCondicion() + " | validacion: " + usuario.getValidado());
                                    break;
                                }
                            }
                            nameUser = rootView.findViewById(R.id.nameUser);
                            Log.d("msg-test", "El nombre del usuario es: "+usuario.getNombre());
                            nameUser.setText(usuario.getNombre());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }

        storage = FirebaseStorage.getInstance();
        reference=storage.getReference();

        Button button12 = rootView.findViewById(R.id.button12);
        Button botonimage = rootView.findViewById(R.id.imagen);

        TextInputEditText actividad = rootView.findViewById(R.id.actividad);
        TextInputEditText descripcion = rootView.findViewById(R.id.descripcion);
        List<String> nombreUsuario = new ArrayList<>();
        delegado = rootView.findViewById(R.id.delegado);

        db.collection("usuarios")
                .get()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        QuerySnapshot usuariosCollection = task2.getResult();
                        Log.d("msg-test", "task2 ha sido valido");
                        for (QueryDocumentSnapshot document : usuariosCollection) {
                            String codigo = document.getId();
                            String comentario = (String) document.get("comentario");
                            String condicion = (String) document.get("condicion");
                            String pass = (String) document.get("contrasenha");
                            String correo = (String) document.get("correo");
                            String nombre = (String) document.get("nombre");
                            String validacion = (String) document.get("validado");
                            String rol = (String) document.get("rol");

                            if (rol.equals("Usuario") && validacion.equals("Si") && condicion.equals("Estudiante")) {
                                // usuario.setComentario(comentario);  // Esto no parece ser necesario en este contexto
                                // ... (Resto de tu código para configurar el objeto usuario)

                                Log.d("msg-test", "| codigo: " + codigo + " | nombre: " + nombre + "| correo: " + correo + " | condicion: " + condicion + " | validacion: " + validacion);
                                nombreUsuario.add(nombre);
                            }
                        }

                        // Convertir la lista de nombres de usuario a un arreglo de String
                        String[] items = nombreUsuario.toArray(new String[0]);

                        // Inicializar el ArrayAdapter con el nuevo arreglo y establecerlo en el ListView
                        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
                        delegado.setAdapter(adapterItems);
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja la excepción que ocurra al intentar obtener los documentos
                    Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                });

        delegado.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedName = adapterView.getItemAtPosition(i).toString();

            Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
        });


        button12.setOnClickListener(view -> {
            String nombreActividad = actividad.getText().toString().trim();
            String descripcionActividad = descripcion.getText().toString().trim();
            String delegadoName = delegado.getText().toString().trim();

            if (nombreActividad.isEmpty() || descripcionActividad.isEmpty()  || delegadoName.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {

                db.collection("actividad")
                        .whereEqualTo("nombre", nombreActividad)
                        .get()
                        .addOnCompleteListener(task100 ->{
                            if (task100.isSuccessful()){
                                if (!task100.getResult().isEmpty()) {
                                    // Evento con el mismo nombre encontrado, muestra el mensaje de error
                                    Toast.makeText(getContext(), "No se puede repetir el nombre de la actividad", Toast.LENGTH_SHORT).show();
                                }else{


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
                                                        db.collection("usuarios")
                                                                .whereEqualTo("nombre", delegadoName)
                                                                .get()
                                                                .addOnCompleteListener(task -> {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            // Obtener el ID del documento
                                                                            String usuarioId = document.getId();

                                                                            // Crear un mapa con los campos que deseas actualizar
                                                                            Map<String, Object> updates = new HashMap<>();
                                                                            updates.put("rol", "DelegadoActividad");

                                                                            // Actualizar el documento en la colección "usuarios"
                                                                            db.collection("usuarios")
                                                                                    .document(usuarioId)
                                                                                    .update(updates)
                                                                                    .addOnSuccessListener(aVoid -> {
                                                                                        // La actualización fue exitosa
                                                                                        Intent intent = new Intent(getContext(), inicioAdmin.class);
                                                                                        intent.putExtra("Actividad creada.", true);
                                                                                        startActivity(intent);
                                                                                        Log.d("msg-test", "Usuario actualizado con éxito.");
                                                                                    })
                                                                                    .addOnFailureListener(e -> {
                                                                                        // Manejar el error en caso de falla en la actualización
                                                                                        Log.e("msg-test", "Error al actualizar usuario: " + e.getMessage());
                                                                                    });
                                                                        }
                                                                    } else {
                                                                        // Manejar el error en caso de falla en la consulta
                                                                        Log.e("msg-test", "Error al obtener documentos: " + task.getException());
                                                                    }
                                                                });
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
                            }else{
                                Log.d("msg-test", "Error al consultar la colección eventos", task100.getException());
                            }
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