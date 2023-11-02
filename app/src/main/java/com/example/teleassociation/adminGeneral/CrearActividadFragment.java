package com.example.teleassociation.adminGeneral;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.teleassociation.R;


public class CrearActividadFragment extends Fragment {

    private AutoCompleteTextView autocompleteTextView;
    private ArrayAdapter<String> adapterItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_actividad, container, false);

        String[] items = {"Diego Lavado", "Leonardo Abanto"};

        autocompleteTextView = view.findViewById(R.id.auto_complete_texto);
        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_item, items);
        autocompleteTextView.setAdapter(adapterItems);
        autocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                // Puedes realizar acciones con el nombre seleccionado aquí
                Toast.makeText(requireContext(), "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}