package com.example.teleassociation.adminGeneral;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.teleassociation.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarActividadAdmin extends AppCompatActivity {

    FirebaseFirestore db;
    private AutoCompleteTextView delegadoEdit;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout tituloActividadEdit;
    TextInputLayout titulodescripcionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_actividad);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        db = FirebaseFirestore.getInstance();

        Button buttonEditarActividad = findViewById(R.id.buttonEditarActividad);
        TextInputEditText actividadEdit = findViewById(R.id.actividadEdit);
        TextInputEditText descripcionEdit = findViewById(R.id.descripcionEdit);
        TextInputEditText imagenEdit = findViewById(R.id.imagenEdit);

        String[] items = {"Diego Lavado", "Leonardo Abanto", "Miguel Ahumada"};
        String actividadDelegado = getIntent().getStringExtra("actividadDelegado");

        delegadoEdit = findViewById(R.id.delegadoEdit);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        delegadoEdit.setAdapter(adapterItems);

        delegadoEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(EditarActividadAdmin.this, "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener los valores restantes
        String actividadNombre = getIntent().getStringExtra("actividadNombre");
        String actividadDescripcion = getIntent().getStringExtra("actividadDescripcion");
        String actividadImagen = getIntent().getStringExtra("actividadImagen");
        String actividadID = getIntent().getStringExtra("actividadID");

        tituloActividadEdit = findViewById(R.id.tituloActividadEdit);
        titulodescripcionEdit = findViewById(R.id.titulodescripcionEdit);

        tituloActividadEdit.setHint(actividadNombre);
        titulodescripcionEdit.setHint(actividadDescripcion);

        buttonEditarActividad.setOnClickListener(view -> {
            String delegadoStr = delegadoEdit.getText().toString().trim();

            DocumentReference actividadRef = db.collection("actividad").document(actividadID);

            if(delegadoStr.isEmpty()){
                Toast.makeText(this, "Se debe seleccionar un delegado de actividad", Toast.LENGTH_SHORT).show();
            }else{
                actividadRef
                        .update("delegado", delegadoStr)
                        .addOnSuccessListener(unused -> {
                            Intent intent = new Intent(this, inicioAdmin.class);
                            intent.putExtra("Nuevo delegado.", true);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}