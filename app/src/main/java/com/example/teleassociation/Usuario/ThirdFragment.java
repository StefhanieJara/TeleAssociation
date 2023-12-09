package com.example.teleassociation.Usuario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.pagos;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.teleassociation.Usuario.ThirdFragment;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ThirdFragment extends Fragment {
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference reference;
    private View rootView;  // Declarar rootView como variable de instancia

    FirebaseAuth mAuth;

    private Uri uri;
    TextView nameUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }


    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_third, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        Button button9 = rootView.findViewById(R.id.button9);
        TextInputEditText donativo = rootView.findViewById(R.id.donativo);
        ImageView imageView14 = rootView.findViewById(R.id.imageView14);
        Button subirBoleta = rootView.findViewById(R.id.subirBoleta);

        subirBoleta.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Obtener datos de usuario y realizar acciones después de obtenerlos
        obtenerDatosUsuario(usuario -> {
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());

            // Ahora puedes utilizar el nombre y el código del usuario como lo necesites
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(usuario.getNombre());

            // Obtén el código del usuario
            String codigoUsuario = usuario.getId();

            button9.setOnClickListener(view -> {
                String donativoStr = donativo.getText().toString();

                try {
                    // Intenta convertir donativoStr a un número
                    double monto = Double.parseDouble(donativoStr);

                    // Si la conversión tiene éxito, procede a crear y guardar el objeto 'pagos'
                    pagos pagos = new pagos();
                    pagos.setCodigo_usuario(codigoUsuario);
                    pagos.setMonto(String.valueOf(monto));
                    pagos.setValidado("Pendiente");

                    // Subir la imagen a Firebase Storage
                    if (uri != null) {
                        StorageReference imageRef = reference.child("donaciones/" + uri.getLastPathSegment());
                        UploadTask uploadTask = imageRef.putFile(uri);

                        uploadTask.addOnFailureListener(exception -> {
                            exception.printStackTrace();
                            Log.e("msg-test", "Error en la carga de la imagen", exception);
                        }).addOnSuccessListener(taskSnapshot -> {
                            // Obtén la URL de la imagen subida
                            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                // Guarda la URL de la imagen en el objeto pagos
                                pagos.setUrl_imagen(downloadUri.toString());

                                // Cargar la imagen seleccionada en el ImageView
                                Glide.with(requireContext()).load(uri).into(imageView14);

                                // Guarda el objeto pagos en Firestore
                                String cod_al = generateRandomCode();

                                db.collection("pagos")
                                        .document(cod_al)
                                        .set(pagos)
                                        .addOnSuccessListener(unused -> {
                                            // Toast.makeText(getContext(), "Pagando", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getContext(), inicio_usuario.class);
                                            intent.putExtra("Pago con éxito.", true); // Agregar una marca de registro exitoso al intent
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                        });
                            });
                        });
                    } else {
                        // Si no se seleccionó una imagen, puedes manejarlo aquí
                        Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Si no se puede convertir a número, muestra un mensaje de error
                    Toast.makeText(getContext(), "El valor tiene que ser numérico", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return rootView;
    }




    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
                if (result != null) {
                    uri = result;
                    // Cargar la imagen seleccionada en un ImageView (opcional)
                    ImageView imageView14 = rootView.findViewById(R.id.imageView14);
                    Glide.with(requireContext()).load(uri).into(imageView14);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


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

    private void obtenerDatosUsuario(ThirdFragment.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuario usuario = new usuario();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");

                                if (correo.equals(email)) {
                                    usuarioSesion.setId(codigo);
                                    usuarioSesion.setNombre(nombre);
                                    usuarioSesion.setCorreo(correo);
                                    // Llamada al método de la interfaz con el nombre del usuario
                                    callback.onCallback(usuarioSesion);
                                    return;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
    }



    public interface FirestoreCallback {
        void onCallback(usuarioSesion usuario);
    }

}