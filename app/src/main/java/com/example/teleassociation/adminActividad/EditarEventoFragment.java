package com.example.teleassociation.adminActividad;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import com.example.teleassociation.adminGeneral.EditarActividadAdmin;
import com.example.teleassociation.adminGeneral.inicioAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarEventoFragment extends Fragment {

    private FirebaseFirestore db;
    private AutoCompleteTextView delegadoEdit;
    private ArrayAdapter<String> adapterItems;
    private TextInputLayout tituloActividadEdit;
    private TextInputLayout titulodescripcionEdit;

    public static EditarEventoFragment newInstance(String nombreEvento) {
        EditarEventoFragment fragment = new EditarEventoFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_evento, container, false);

        db = FirebaseFirestore.getInstance();

        Button buttonEditarActividad = view.findViewById(R.id.button12);
        TextInputEditText titulo = view.findViewById(R.id.titulo);
        TextInputEditText descripcion = view.findViewById(R.id.descrEvent);
        TextInputEditText fecha = view.findViewById(R.id.fechaDelEvento);
        TextInputEditText lugar = view.findViewById(R.id.lugarDelEvento);
        String eventoParticipante = getArguments().getString("nombreEvento"); // Asegúrate de que sea "nombreEvento" y no "nombre_evento"

        buttonEditarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tituloEvento = titulo.getText().toString();
                String descripcionEvento = descripcion.getText().toString();
                String fechaEvento = fecha.getText().toString();
                String lugarEvento = lugar.getText().toString();

                actualizarInformacionFirebase(eventoParticipante, tituloEvento, descripcionEvento, fechaEvento, lugarEvento);
            }
        });

        return view;
    }

    private void actualizarInformacionFirebase(String nombre, String titulo, String descripcion, String fecha, String lugar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("eventos")
                .whereEqualTo("nombre", nombre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference eventoRef = document.getReference();
                                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date fechaHoraDate = formato.parse(fecha);
                                    Timestamp timestamp = new Timestamp(fechaHoraDate);

                                    eventoRef
                                            .update("nombre", titulo,
                                                    "descripcion", descripcion,
                                                    "fecha", timestamp,
                                                    "lugar", lugar)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}