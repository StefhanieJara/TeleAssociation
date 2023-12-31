package com.example.teleassociation.Usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.MisEventAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.teleassociation.Usuario.SecondFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass. s
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView nameUser;
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    ArrayList<String> eventosParticipa = new ArrayList<>();
    private RecyclerView recyclerView;
    private Spinner spinner;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
        eventLista.clear();
        eventosParticipa.clear();
        View rootView = inflater.inflate(R.layout.fragment_second, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listRecyclerView);

        obtenerDatosUsuario(usuario -> {
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            Log.d("msg-test", "El ID del usuario fuera del collection es: " + usuario.getId());

            // Ahora puedes utilizar el nombre del usuario como lo necesites, por ejemplo:
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(usuario.getNombre());

            Log.d("msg-test", " inicio");
            db.collection("participantes")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot eventosCollection = task.getResult();
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String asignacion = (String) document.get("asignacion");
                                String codigo = (String) document.get("codigo");
                                String evento = (String) document.get("evento");
                                if (usuario.getId().equals(codigo)) {
                                    Log.d("msg-test", " | evento: " + evento);
                                    eventosParticipa.add(evento);}}

                            // Itera a través de eventos de la colección "eventos"
                            db.collection("eventos")
                                    .orderBy("fecha", Query.Direction.ASCENDING)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            QuerySnapshot eventosCollection2 = task2.getResult();
                                            SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                                            SimpleDateFormat formatoFechaNuevo = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
                                            if(eventLista.isEmpty()){
                                                for (QueryDocumentSnapshot document2 : eventosCollection2) {
                                                    String eventoId = document2.getId();
                                                    String estado = (String) document2.get("estado");
                                                    String nombre = (String) document2.get("nombre");
                                                    String nombre_actividad = (String) document2.get("nombre_actividad");
                                                    Date date = document2.getDate("fecha");
                                                    String apoyos = (String) document2.get("apoyos");
                                                    String url_imagen = (String) document2.get("url_imagen");
                                                    String fechaSt = date.toString();
                                                    String[] partes = fechaSt.split(" ");
                                                    //String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                                    Log.d("msg-test1","el antiguo formato de fecha es :"+date);
                                                    Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaNuevo.format(date));
                                                    String fecha = formatoFechaNuevo.format(date);
                                                    String hora = partes[3];
                                                    String fecha_hora = fecha+" "+hora;

                                                    //Log.d("msg-test", " | nombre de eventos: " + nombre);

                                                    // Verifica si el nombre del evento está en eventosParticipa
                                                    if (eventosParticipa.contains(nombre)) {
                                                        if(estado.equals("proceso")){
                                                            eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos, nombre_actividad,url_imagen);
                                                            eventos.setId(eventoId);
                                                            eventLista.add(eventos);
                                                            Log.d("msg-test", " | nombre: " + nombre + "| actividad: "+ nombre_actividad + " | fecha: " + fecha + " | hora: " + hora);
                                                        }
                                                    }
                                                }
                                            }
                                            MisEventAdapter misEventAdapter = new MisEventAdapter();
                                            misEventAdapter.setEventList(eventLista);
                                            misEventAdapter.setContext(getContext());

                                            recyclerView.setAdapter(misEventAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


                                        }
                                    });

                        }
                    });

            String[] opciones = {"Proceso", "Finalizado"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner = rootView.findViewById(R.id.spinnerCondicion);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedOption = opciones[position];

                    if ("Proceso".equals(selectedOption)) {
                        Proceso(usuario.getId());
                    } else {
                        Finalizado(usuario.getId());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Método necesario pero no utilizado en este caso
                }
            });

        });

        ImageView campanaImageView = rootView.findViewById(R.id.imageView2);
        campanaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCampanaClick(v);
            }
        });



        return rootView;
    }

    private void obtenerDatosUsuario(SecondFragment.FirestoreCallback callback) {
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

    private void Proceso(String codigoUsuario) {
        eventLista.clear();
        eventosParticipa.clear();
        db.collection("participantes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        for (QueryDocumentSnapshot document : eventosCollection) {
                            String asignacion = (String) document.get("asignacion");
                            String codigo = (String) document.get("codigo");
                            String evento = (String) document.get("evento");
                            if (codigoUsuario.equals(codigo)) {
                                Log.d("msg-test", " | evento: " + evento);
                                eventosParticipa.add(evento);}}

                        // Itera a través de eventos de la colección "eventos"
                        db.collection("eventos")
                                .orderBy("fecha", Query.Direction.ASCENDING)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        QuerySnapshot eventosCollection2 = task2.getResult();
                                        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                                        SimpleDateFormat formatoFechaNuevo = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
                                        if(eventLista.isEmpty()){
                                            for (QueryDocumentSnapshot document2 : eventosCollection2) {
                                                String eventoId = document2.getId();
                                                String estado = (String) document2.get("estado");
                                                String nombre = (String) document2.get("nombre");
                                                String nombre_actividad = (String) document2.get("nombre_actividad");
                                                Date date = document2.getDate("fecha");
                                                String apoyos = (String) document2.get("apoyos");
                                                String url_imagen = (String) document2.get("url_imagen");
                                                String fechaSt = date.toString();
                                                String[] partes = fechaSt.split(" ");
                                                //String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                                Log.d("msg-test1","el antiguo formato de fecha es :"+date);
                                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaNuevo.format(date));
                                                String fecha = formatoFechaNuevo.format(date);
                                                String hora = partes[3];
                                                String fecha_hora = fecha+" "+hora;

                                                //Log.d("msg-test", " | nombre de eventos: " + nombre);

                                                // Verifica si el nombre del evento está en eventosParticipa
                                                if (eventosParticipa.contains(nombre)) {
                                                    if(estado.equals("proceso")){
                                                        eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos, nombre_actividad,url_imagen);
                                                        eventos.setId(eventoId);
                                                        eventLista.add(eventos);
                                                        Log.d("msg-test", " | nombre: " + nombre + "| actividad: "+ nombre_actividad + " | fecha: " + fecha + " | hora: " + hora);
                                                    }
                                                }
                                            }
                                        }
                                        MisEventAdapter misEventAdapter = new MisEventAdapter();
                                        misEventAdapter.setEventList(eventLista);
                                        misEventAdapter.setContext(getContext());

                                        recyclerView.setAdapter(misEventAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


                                    }
                                });

                    }
                });
    }

    private void Finalizado(String codigoUsuario) {
        eventLista.clear();
        eventosParticipa.clear();
        db.collection("participantes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        for (QueryDocumentSnapshot document : eventosCollection) {
                            String asignacion = (String) document.get("asignacion");
                            String codigo = (String) document.get("codigo");
                            String evento = (String) document.get("evento");
                            if (codigoUsuario.equals(codigo)) {
                                Log.d("msg-test", " | evento: " + evento);
                                eventosParticipa.add(evento);}}

                        // Itera a través de eventos de la colección "eventos"
                        db.collection("eventos")
                                .orderBy("fecha", Query.Direction.ASCENDING)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        QuerySnapshot eventosCollection2 = task2.getResult();
                                        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                                        SimpleDateFormat formatoFechaNuevo = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
                                        if(eventLista.isEmpty()){
                                            for (QueryDocumentSnapshot document2 : eventosCollection2) {
                                                String eventoId = document2.getId();
                                                String estado = (String) document2.get("estado");
                                                String nombre = (String) document2.get("nombre");
                                                String nombre_actividad = (String) document2.get("nombre_actividad");
                                                Date date = document2.getDate("fecha");
                                                String apoyos = (String) document2.get("apoyos");
                                                String url_imagen = (String) document2.get("url_imagen");
                                                String fechaSt = date.toString();
                                                String[] partes = fechaSt.split(" ");
                                                //String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                                Log.d("msg-test1","el antiguo formato de fecha es :"+date);
                                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaNuevo.format(date));
                                                String fecha = formatoFechaNuevo.format(date);
                                                String hora = partes[3];
                                                String fecha_hora = fecha+" "+hora;

                                                //Log.d("msg-test", " | nombre de eventos: " + nombre);

                                                // Verifica si el nombre del evento está en eventosParticipa
                                                if (eventosParticipa.contains(nombre)) {
                                                    if(estado.equals("finalizado")){
                                                        eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos, nombre_actividad,url_imagen);
                                                        eventos.setId(eventoId);
                                                        eventLista.add(eventos);
                                                        Log.d("msg-test", " | nombre: " + nombre + "| actividad: "+ nombre_actividad + " | fecha: " + fecha + " | hora: " + hora);
                                                    }
                                                }
                                            }
                                        }
                                        MisEventAdapter misEventAdapter = new MisEventAdapter();
                                        misEventAdapter.setEventList(eventLista);
                                        misEventAdapter.setContext(getContext());

                                        recyclerView.setAdapter(misEventAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


                                    }
                                });

                    }
                });
    }
    public void onCampanaClick(View view) {
        // Abre la NotificationActivity al hacer clic en la campana
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        startActivity(intent);
    }

}