package com.example.teleassociation.adminGeneral;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teleassociation.R;
import com.example.teleassociation.Usuario.FirstFragment;
import com.example.teleassociation.adapter.ListaActividadesGeneralAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaActividadesGeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaActividadesGeneralFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView nameUser;
    usuario usuario = new usuario();
    private List<eventoListarUsuario> eventLista = new ArrayList<>();
    private RecyclerView recyclerView;

    public ListaActividadesGeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaActividadesGeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaActividadesGeneralFragment newInstance(String param1, String param2) {
        ListaActividadesGeneralFragment fragment = new ListaActividadesGeneralFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_lista_actividades_general, container, false);
        db = FirebaseFirestore.getInstance();

        obtenerDatosUsuario(usuario -> {
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            nameUser = rootView.findViewById(R.id.nameUser);
            nameUser.setText(usuario.getNombre());
        });


        recyclerView = rootView.findViewById(R.id.listRecyclerEventoAdmin);

        db.collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();
                        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String fechaSt = date.toString();
                                String url_imagen = (String) document.get("url_imagen");
                                String[] partes = fechaSt.split(" ");
                                //String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaEsp.format(date));
                                String fecha = formatoFechaEsp.format(date);
                                String hora = partes[3];
                                eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos,nombre_actividad,url_imagen);
                                eventLista.add(eventos);
                                Log.d("msg-test", " | nombre: " + nombre + " | fecha: " + fecha + " | hora: " + hora);
                            }
                        }

                        ListaActividadesGeneralAdapter listaActividadesGeneralAdapter = new ListaActividadesGeneralAdapter();
                        listaActividadesGeneralAdapter.setEventList(eventLista);
                        listaActividadesGeneralAdapter.setContext(getContext());

                        // Inicializa el RecyclerView y el adaptador
                        recyclerView.setAdapter(listaActividadesGeneralAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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

        ImageView btnMoney = rootView.findViewById(R.id.btnMoney);
        btnMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), donacionesAdmin.class);
                startActivity(intent);

            }
        });


        return rootView;
    }

    private void obtenerDatosUsuario(FirstFragment.FirestoreCallback callback) {
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