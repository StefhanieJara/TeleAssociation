package com.example.teleassociation.adminActividad;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.inicio_usuario;
import com.example.teleassociation.dto.pagos;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class DonacionesAdminActividadFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String nombreDelegado;
    TextView nameUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donaciones_admin_actividad, container, false);
        db = FirebaseFirestore.getInstance();

        obtenerDatosUsuario(usuarioSesion -> {

            //Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuarioSesion.getNombre());
            nombreDelegado = usuarioSesion.getNombre();
            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:

            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + nombreDelegado);

            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(nombreDelegado);

            Button button19 = rootView.findViewById(R.id.button19);
            TextInputEditText donativo = rootView.findViewById(R.id.donativoAdminActividad);
            button19.setOnClickListener(view -> {
                String donativoStr = donativo.getText().toString();

                try {
                    // Intenta convertir donativoStr a un número
                    double monto = Double.parseDouble(donativoStr);

                    // Si la conversión tiene éxito, procede a crear y guardar el objeto 'pagos'
                    pagos pagos = new pagos();
                    pagos.setCodigo_usuario("20201010");
                    pagos.setMonto(String.valueOf(monto));
                    pagos.setValidado("No");
                    pagos.setUrl_imagen("sas");

                    Log.d("msg-test", pagos.getCodigo_usuario() + " el siguiente pago es: " + pagos.getMonto() + " xd.");
                    String cod_al = generateRandomCode();

                    Log.d("msg-test", pagos.getCodigo_usuario() + " " + pagos.getMonto() + " " + pagos.getValidado() + " " + pagos.getUrl_imagen());

                    db.collection("pagos")
                            .document(cod_al)
                            .set(pagos)
                            .addOnSuccessListener(unused -> {
                                // Toast.makeText(getContext(), "Pagando", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), ListaActividadesDelactvActivity.class);
                                intent.putExtra("Pago con éxito.", true); // Agregar una marca de registro exitoso al intent
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                            });
                } catch (NumberFormatException e) {
                    // Si no se puede convertir a número, muestra un mensaje de error
                    Toast.makeText(getContext(), "El valor tiene que ser numérico", Toast.LENGTH_SHORT).show();
                }
            });
        });



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

    private void obtenerDatosUsuario(DonacionesAdminActividadFragment.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
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