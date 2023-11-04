package com.example.teleassociation.Usuario;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.databinding.ActivityInicioUsuarioBinding;
import com.example.teleassociation.dto.pagos;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
    FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_third, container, false);
        db = FirebaseFirestore.getInstance();

        Button button9 = rootView.findViewById(R.id.button9);
        TextInputEditText donativo = rootView.findViewById(R.id.donativo);
        button9.setOnClickListener(view -> {
            String donativoStr = donativo.getText().toString();

            try {
                // Intenta convertir donativoStr a un número
                double monto = Double.parseDouble(donativoStr);

                // Si la conversión tiene éxito, procede a crear y guardar el objeto 'pagos'
                pagos pagos = new pagos();
                pagos.setCodigo_usuario("20190000");
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
                            Intent intent = new Intent(getContext(), inicio_usuario.class);
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