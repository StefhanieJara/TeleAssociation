package com.example.teleassociation.adminActividad;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.inicio_usuario;
import com.example.teleassociation.dto.pagos;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class DonacionesAdminActividadFragment extends Fragment {

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donaciones_admin_actividad, container, false);
        db = FirebaseFirestore.getInstance();

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
}