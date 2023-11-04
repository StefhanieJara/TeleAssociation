package com.example.teleassociation.adminActividad;

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
import android.widget.Toast;

import com.example.teleassociation.R;
import com.example.teleassociation.adminGeneral.inicioAdmin;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.evento;
import com.example.teleassociation.dto.eventoCrear;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearEventoFragment extends Fragment {

    FirebaseFirestore db;

    private AutoCompleteTextView lugar;
    private ArrayAdapter<String> adapterItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crear_evento, container, false);
        db = FirebaseFirestore.getInstance();

        TextInputEditText eventoNombre = rootView.findViewById(R.id.evento);
        TextInputEditText descripcion = rootView.findViewById(R.id.descripcion);
        TextInputEditText SubirImagen = rootView.findViewById(R.id.SubirImagen);
        Button button80 = rootView.findViewById(R.id.button80);

        String[] items = {"Minas", "Bati", "Digimundo", "Estacionamiento de Letras", "Polideportivo"};

        lugar = rootView.findViewById(R.id.lugar);
        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
        lugar.setAdapter(adapterItems);

        lugar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                // Puedes realizar acciones con el nombre seleccionado aquí
                Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        button80.setOnClickListener(view -> {
            String nombreEvento = eventoNombre.getText().toString().trim();
            String descripcionEvento = descripcion.getText().toString().trim();
            String SubirImagenEvento = SubirImagen.getText().toString().trim();
            String lugarName = lugar.getText().toString().trim();

            if (nombreEvento.isEmpty() || descripcionEvento.isEmpty() || lugarName.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {

                String cod_al = generateRandomCode();

                eventoCrear evento = new eventoCrear();
                evento.setApoyos("0");
                evento.setDescripcion(descripcionEvento);
                evento.setEstado("proceso");
                evento.setNombre(nombreEvento);
                evento.setNombre_actividad("");
                evento.setNombre_lugar(lugarName);
                evento.setNombre_actividad("futbol");
                String fechaHoraString = "2023-11-01 15:30:00";
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    // Parsea la cadena en un objeto Date
                    Date fechaHoraDate = formato.parse(fechaHoraString);

                    // Crea un Timestamp a partir del objeto Date
                    Timestamp timestamp = new Timestamp(fechaHoraDate);

                    evento.setFecha(timestamp);

                    Log.d("msg-test", evento.getNombre() + " " + evento.getNombre_lugar() + " .");

                    db.collection("eventos")
                            .document(cod_al)
                            .set(evento)
                            .addOnSuccessListener(unused -> {
                                Intent intent = new Intent(getContext(), ListaActividadesDelactvActivity.class);
                                intent.putExtra("Evento creado.", true);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                            });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
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