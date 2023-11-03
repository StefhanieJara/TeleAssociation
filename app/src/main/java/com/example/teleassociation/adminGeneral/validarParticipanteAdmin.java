package com.example.teleassociation.adminGeneral;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.teleassociation.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import android.view.View;

public class validarParticipanteAdmin extends AppCompatActivity {
    FirebaseFirestore db;
    private AutoCompleteTextView validacion;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout textInputNombre;
    TextInputLayout textInputCorreo;

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

        // Obtener los valores de usuarioNombre y usuarioCorreo
        String usuarioNombre = getIntent().getStringExtra("usuarioNombre");
        String usuarioCorreo = getIntent().getStringExtra("usuarioCorreo");

        // Encontrar los TextInputLayout correspondientes
        textInputNombre = findViewById(R.id.textInputLayout27);
        textInputCorreo = findViewById(R.id.textInputLayout28);

        // Establecer los hints con los valores de usuarioNombre y usuarioCorreo
        textInputNombre.setHint(usuarioNombre);
        textInputCorreo.setHint(usuarioCorreo);

    }
}