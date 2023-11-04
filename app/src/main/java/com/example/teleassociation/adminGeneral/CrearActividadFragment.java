package com.example.teleassociation.adminGeneral;

import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.inicio_usuario;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.pagos;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class CrearActividadFragment extends Fragment {

    FirebaseFirestore db;

    private AutoCompleteTextView delegado;
    private ArrayAdapter<String> adapterItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crear_actividad, container, false);
        db = FirebaseFirestore.getInstance();

        Button button12 = rootView.findViewById(R.id.button12);
        TextInputEditText actividad = rootView.findViewById(R.id.actividad);
        TextInputEditText descripcion = rootView.findViewById(R.id.descripcion);
        TextInputEditText imagen = rootView.findViewById(R.id.imagen);

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
            String imagenUrl = imagen.getText().toString().trim();
            String delegadoName = delegado.getText().toString().trim();

            if (nombreActividad.isEmpty() || descripcionActividad.isEmpty() || imagenUrl.isEmpty() || delegadoName.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                actividad actividad1 = new actividad();
                actividad1.setDelegado(delegadoName);
                actividad1.setDescripcion(descripcionActividad);
                actividad1.setNombre(nombreActividad);
                actividad1.setUrl_imagen(imagenUrl);
                String cod_al = generateRandomCode();

                Log.d("msg-test", actividad1.getDelegado() + " " + actividad1.getNombre() + " .");

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
            }
        });

        ImageView btnStats = rootView.findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), estadisticasAdmin.class);
                startActivity(intent);

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