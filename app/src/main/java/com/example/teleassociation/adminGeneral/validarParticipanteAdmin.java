package com.example.teleassociation.adminGeneral;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.teleassociation.R;
import com.example.teleassociation.dto.actividad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class validarParticipanteAdmin extends AppCompatActivity {
    FirebaseFirestore db;
    private AutoCompleteTextView validacion;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout textInputNombre;
    TextInputLayout textInputCorreo;
    TextInputEditText textRechazo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_participante_admin);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        db = FirebaseFirestore.getInstance();

        String[] items = {"Si", "No"};
        validacion = findViewById(R.id.validacion);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        validacion.setAdapter(adapterItems);

        validacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(validarParticipanteAdmin.this, "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener los valores de usuarioNombre y usuarioCorreo
        String usuarioNombre = getIntent().getStringExtra("usuarioNombre");
        String usuarioCorreo = getIntent().getStringExtra("usuarioCorreo");

        // Encontrar los TextInputLayout correspondientes
        textInputNombre = findViewById(R.id.textInputLayout27);
        textInputCorreo = findViewById(R.id.textInputLayout28);

        // Establecer los hints con los valores de usuarioNombre y usuarioCorreo
        textInputNombre.setHint(usuarioNombre);
        textInputCorreo.setHint(usuarioCorreo);

        Button button16 = findViewById(R.id.button16);

        button16.setOnClickListener(view -> {
            textRechazo = findViewById(R.id.rechazo);
            String rechazo = textRechazo.getText().toString().trim();
            String usuarioCodigo = getIntent().getStringExtra("usuarioCodigo");
            String validacionStr = validacion.getText().toString().trim();

            DocumentReference usuarioRef = db.collection("usuarios").document(usuarioCodigo);

            if(validacionStr.equals("Si")){
                usuarioRef
                        .update("validado", validacionStr)
                        .addOnSuccessListener(unused -> {
                            Intent intent = new Intent(this, inicioAdmin.class);
                            intent.putExtra("Usuario validado.", true);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                        });
            }else{
                if(rechazo.isEmpty()){
                    Toast.makeText(this, "El campo del motivo de rechazo no puede estar vacio", Toast.LENGTH_SHORT).show();
                }else{
                    usuarioRef
                            .update("validado", validacionStr)
                            .addOnSuccessListener(unused -> {
                                usuarioRef
                                        .update("comentario", rechazo)
                                        .addOnSuccessListener(unused2 -> {
                                            Intent intent = new Intent(this, inicioAdmin.class);
                                            intent.putExtra("Usuario validado.", true);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                            });
                }
            }

        });

    }
}