package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.teleassociation.databinding.ActivityMainBinding;
import com.example.teleassociation.databinding.ActivityRegistrarseBinding;
import com.example.teleassociation.dto.usuario;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {

    FirebaseFirestore db;
    ActivityRegistrarseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegistrarseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        String[] opciones = {"Egresado", "Estudiante"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCondicion.setAdapter(adapter);

        binding.registrarse.setOnClickListener(v -> {
            String nombre = binding.editTextNombre.getEditableText().toString();
            String codigo = binding.editTextCodigo.getEditableText().toString();
            String correo = binding.editTextCorreo.getEditableText().toString();
            String condicion = binding.spinnerCondicion.getSelectedItem().toString();
            String contrasena = binding.editTextContraseA.getEditableText().toString();
            String confirmaContra = binding.editTextConfirmaContra.getEditableText().toString();
            String validado = "no";
            String rol = "participante";

            if (nombre.isEmpty()) {
                showError("El campo 'Nombre' no puede estar vacío.");
            } else if (!isValidCodigo(codigo)) {
                showError("Código no válido");
            } else if (!isValidEmail(correo)) {
                showError("Correo electrónico no válido");
            }  else if (!isValidPassword(contrasena)) {
                showError("Contraseña no válida. Debe tener al menos 8 caracteres y al menos un carácter especial.");
            } else if (!contrasena.equals(confirmaContra)) {
                showError("Las contraseñas no coinciden");
            } else {
                // Agregar  lógica de registro
                // ...
                usuario usuario = new usuario();
                usuario.setRol(condicion);
                usuario.setContrasenha(contrasena);
                usuario.setCorreo(correo);
                usuario.setNombre(nombre);
                usuario.setValidado(validado);
                usuario.setRol(rol);

                db.collection("usuarios")
                        .document(codigo)
                        .set(usuario)
                        .addOnSuccessListener(unused -> {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("registroExitoso", true); // Agregar una marca de registro exitoso al intent
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar ", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidCodigo(String codigo) {
        return codigo.length() == 8 && Pattern.matches("[0-9]+", codigo);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && Pattern.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*", password);
    }
}