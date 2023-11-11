package com.example.teleassociation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.teleassociation.databinding.ActivityMainBinding;
import com.example.teleassociation.databinding.ActivityRegistrarseBinding;
import com.example.teleassociation.dto.usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ActivityRegistrarseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRegistrarseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
            String validado = "No";
            String rol = "Usuario";
            String cometario = "";

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
                usuario.setCondicion(condicion);
                usuario.setContrasenha(contrasena);
                usuario.setCorreo(correo);
                usuario.setNombre(nombre);
                usuario.setValidado(validado);
                usuario.setRol(rol);
                usuario.setComentario(cometario);
                usuario.setId(codigo);

                Log.d("msg-test", " | nombre: " + nombre + " | rol: " + rol + " | condicion: " + condicion);

                db.collection("usuarios")
                        .document(codigo)
                        .set(usuario)
                        .addOnSuccessListener(unused -> {

                            mAuth.createUserWithEmailAndPassword(correo, contrasena)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                                Intent intent = new Intent(Registrarse.this, MainActivity.class);
                                                intent.putExtra("registroExitoso", true); // Agregar una marca de registro exitoso al intent
                                                startActivity(intent);
                                                finish(); // Finalizar la actividad actual
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                updateUI(null);
                                                Toast.makeText(Registrarse.this, "Algo pasó al guardar ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });




                            /*Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("registroExitoso", true); // Agregar una marca de registro exitoso al intent
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual*/
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo pasó al guardar ", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void updateUI(FirebaseUser user) {

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