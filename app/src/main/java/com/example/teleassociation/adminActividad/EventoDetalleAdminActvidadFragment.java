package com.example.teleassociation.adminActividad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.teleassociation.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class EventoDetalleAdminActvidadFragment extends Fragment {

    private FirebaseFirestore db;
    private String nombreEventoParticipante;

    public static EventoDetalleAdminActvidadFragment newInstance(String nombreEvento) {
        EventoDetalleAdminActvidadFragment fragment = new EventoDetalleAdminActvidadFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_detalle_admin_actvidad, container, false);

        // Recuperar el nombre del evento desde los argumentos
        String nombreEvento = getArguments().getString("nombreEvento"); // Asegúrate de que sea "nombreEvento" y no "nombre_evento"

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Obtén una referencia al documento o la colección que necesitas
        // Por ejemplo, si tienes una colección llamada "eventos":
        db.collection("eventos")
                .whereEqualTo("nombre", nombreEvento)  // Filtra por documentos con el campo "nombre" igual a nombreEvento
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Verifica si se encontraron documentos
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Si hay documentos, obtén el primero (en este caso asumimos que solo hay uno)
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Ahora puedes obtener los datos del documento
                            String nombreEvento = documentSnapshot.getString("nombre");

                            Date date = documentSnapshot.getDate("fecha");
                            String fechaSt = date.toString();
                            String[] partes = fechaSt.split(" ");
                            String fechaEvento = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                            String horaEvento = partes[3];
                            String apoyos = (String) documentSnapshot.get("apoyos");
                            String descripcion = (String) documentSnapshot.get("descripcion");
                            Log.d("msg-test", " | nombre: " + nombreEvento + " | fecha: " + fechaEvento + " | hora: " + horaEvento);
                            nombreEventoParticipante=nombreEvento;

                            // Ahora puedes actualizar tus TextViews u otros elementos de la vista
                            TextView textViewNombreEvento = view.findViewById(R.id.evento);
                            TextView textViewFecha = view.findViewById(R.id.fecha);
                            TextView textViewHora = view.findViewById(R.id.hora);
                            TextView textViewApoyos = view.findViewById(R.id.apoyos);
                            TextView textViewDescripcion = view.findViewById(R.id.descripcionEvento);

                            textViewNombreEvento.setText(nombreEvento);
                            textViewFecha.setText("Fecha: " + fechaEvento);
                            textViewHora.setText("Hora: " + horaEvento);
                            textViewApoyos.setText("Apoyos: " + apoyos);
                            textViewDescripcion.setText(descripcion);

                        } else {
                            Log.d("msg-test", "El documento no existe");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("msg-test", "Error al obtener documento: " + e.getMessage());
                    }
                });
        Button btnVerParticipantes = view.findViewById(R.id.verParticipantes);
        btnVerParticipantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent o Fragment y pasar el nombre del evento como argumento
                String nombreEvento = nombreEventoParticipante;

                // O si estás iniciando un nuevo Fragment:

                ListaParticipantesFragment fragment = ListaParticipantesFragment.newInstance(nombreEvento);
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();

            }
        });
        Button editarEvento = view.findViewById(R.id.editarEvento);
        editarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent o Fragment y pasar el nombre del evento como argumento
                String nombreEvento = nombreEventoParticipante;

                // O si estás iniciando un nuevo Fragment:

                EditarEventoFragment fragment = EditarEventoFragment.newInstance(nombreEvento);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });


        return view;
    }

}