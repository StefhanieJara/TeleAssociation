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

import com.example.teleassociation.R;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.ListaActividadesGeneralAdapter;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        recyclerView = rootView.findViewById(R.id.listRecyclerEventoAdmin);

        db.collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot eventosCollection = task.getResult();

                        if(eventLista.isEmpty()){
                            for (QueryDocumentSnapshot document : eventosCollection) {
                                String nombre = (String) document.get("nombre");
                                String nombre_actividad = (String) document.get("nombre_actividad");
                                Date date = document.getDate("fecha");
                                String apoyos = (String) document.get("apoyos");
                                String fechaSt = date.toString();
                                String[] partes = fechaSt.split(" ");
                                String fecha = partes[0] + " " + partes[1] + " " + partes[2]; // "Mon Oct 30"
                                String hora = partes[3];
                                eventoListarUsuario eventos = new eventoListarUsuario(nombre,fecha,hora,apoyos,nombre_actividad);
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


        return rootView;
    }
}