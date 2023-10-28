package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.example.teleassociation.databinding.ActivityMainBinding;
import com.example.teleassociation.databinding.ActivityRegistrarseBinding;

import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {

    ActivityRegistrarseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegistrarseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registrarse.setOnClickListener(v -> {
            String nombre = binding.editTextNombre.getEditableText().toString();
            String codigo = binding.editTextCodigo.getEditableText().toString();
            String correo = binding.editTextCorreo.getEditableText().toString();
            String condicion = binding.editTextCondicion.getEditableText().toString();
            String contrasena = binding.editTextContraseA.getEditableText().toString();
            String confirmaContra = binding.editTextConfirmaContra.getEditableText().toString();

            if (nombre.isEmpty()) {
                showError("El campo 'Nombre' no puede estar vacío.");
            } else if (!isValidCodigo(codigo)) {
                showError("Código no válido");
            } else if (!isValidEmail(correo)) {
                showError("Correo electrónico no válido");
            } else if (!isValidCondicion(condicion)) {
                showError("Condición no válida");
            } else if (!isValidPassword(contrasena)) {
                showError("Contraseña no válida. Debe tener al menos 8 caracteres y al menos un carácter especial.");
            } else if (!contrasena.equals(confirmaContra)) {
                showError("Las contraseñas no coinciden");
            } else {
                // Agregar  lógica de registro
                // ...

                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
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

    private boolean isValidCondicion(String condicion) {
        return condicion.equals("egresado") || condicion.equals("estudiante");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && Pattern.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*", password);
    }
}