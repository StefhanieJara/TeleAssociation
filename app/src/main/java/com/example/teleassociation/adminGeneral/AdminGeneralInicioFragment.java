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
import com.example.teleassociation.adapter.AdminGeneralInicioAdapter;
import com.example.teleassociation.adapter.EventAdapter;
import com.example.teleassociation.adapter.ListaActividadesGeneralAdapter;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.eventoListarUsuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminGeneralInicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminGeneralInicioFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<actividad> actividadLista = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminGeneralInicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminGeneralInicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminGeneralInicioFragment newInstance(String param1, String param2) {
        AdminGeneralInicioFragment fragment = new AdminGeneralInicioFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_admin_general_inicio, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.listRecyclerActividadAdmin);

        db.collection("actividad")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot actividadCollection = task.getResult();

                        if(actividadLista.isEmpty()){
                            for (QueryDocumentSnapshot document : actividadCollection) {
                                String id = document.getId();
                                String nombre = (String) document.get("nombre");
                                String delegado = (String) document.get("delegado");
                                String descripcion = (String) document.get("descripcion");
                                String url_imagen = (String) document.get("url_imagen");
                                actividad actividad = new actividad();
                                actividad.setId(id);
                                actividad.setNombre(nombre);
                                actividad.setDelegado(delegado);
                                actividad.setDescripcion(descripcion);
                                actividad.setUrl_imagen(url_imagen);
                                actividadLista.add(actividad);
                                Log.d("msg-test", " | nombre: " + actividad.getNombre() + " | delegado: " + actividad.getDelegado() + " | descripcion: " + descripcion + " | url_imagen: " + url_imagen);
                            }
                        }

                        AdminGeneralInicioAdapter adminGeneralInicioAdapter = new AdminGeneralInicioAdapter();
                        adminGeneralInicioAdapter.setactividadLista(actividadLista);
                        adminGeneralInicioAdapter.setContext(getContext());

                        recyclerView.setAdapter(adminGeneralInicioAdapter);
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