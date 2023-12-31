package com.example.teleassociation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

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

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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

            // Resetear errores
            resetErrorAndDisable(binding.Nombre);
            resetErrorAndDisable(binding.Codigo);
            resetErrorAndDisable(binding.Correo);
            resetErrorAndDisable(binding.Contrasenha);
            resetErrorAndDisable(binding.ConfirmaContra);

            if (nombre.isEmpty()) {
                binding.Nombre.setError("El campo 'Nombre' no puede estar vacío.");
            } else if (!isValidCodigo(codigo)) {
                binding.Codigo.setError("Código no válido");
            } else if (!isValidEmail(correo) || !correo.contains("pucp.edu.pe") && !correo.contains("pucp.edu")) {
                binding.Correo.setError("Correo electrónico no válido");
            }else if (!isValidPassword(contrasena)) {
                binding.Contrasenha.setError("Contraseña no válida. Debe tener al menos 6 caracteres y al menos un carácter especial.");
            } else if (!contrasena.equals(confirmaContra)) {
                binding.ConfirmaContra.setError("Las contraseñas no coinciden");
            } else {

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

                //registrarUsuario(usuario, correo, contrasena,codigo);
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String token = task.getResult();

                                // Guardar el token del usuario en tu base de datos o utilizarlo según sea necesario.
                                usuario.setToken(token);

                            } else {
                                // Si no se pudo obtener el token, manejar el error
                                Log.w("msg-test", "No se pudo obtener el token del usuario");
                                showError("No se pudo obtener el token del usuario");
                            }
                        });

                db.collection("usuarios")
                        .whereEqualTo("id", codigo)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    // Ya existe un usuario con el mismo código, manejar el error o mostrar un mensaje
                                    Log.d("msg-test", "Ya existe un usuario con el mismo código.");
                                    Toast.makeText(Registrarse.this, "Ya existe un usuario con el mismo código.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Verificar si ya existe un usuario con el mismo correo
                                    db.collection("usuarios")
                                            .whereEqualTo("correo", correo)
                                            .get()
                                            .addOnCompleteListener(task10 -> {
                                                if (task10.isSuccessful()) {
                                                    if (!task10.getResult().isEmpty()) {
                                                        Log.d("msg-test", "Ya existe un usuario con el mismo correo.");
                                                        Toast.makeText(Registrarse.this, "Ya existe un usuario con el mismo correo.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Ambas verificaciones pasaron, proceder con el registro
                                                        registrarUsuario(usuario, correo, contrasena,codigo);
                                                    }
                                                } else {
                                                    // Manejar el error al realizar la consulta de correo
                                                    Log.e("msg-test", "Error al consultar la base de datos", task10.getException());
                                                }
                                            });
                                }
                            } else {
                                // Manejar el error al realizar la consulta de código
                                Log.e("msg-test", "Error al consultar la base de datos", task.getException());
                            }
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
        return password.length() >= 6 && Pattern.matches(".*[!@#$%^&()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*", password);
    }

    private void resetErrorAndDisable(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }


    private void registrarUsuario(usuario usuario, String correo, String contrasena, String codigo) {
        db.collection("usuarios")
                .document(codigo)
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    Log.d("msg-test", "Entre a la colección");

                    mAuth.createUserWithEmailAndPassword(correo, contrasena)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    Log.d("msg-test", "Registro exitoso");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    Intent intent = new Intent(Registrarse.this, MainActivity.class);
                                    intent.putExtra("registroExitoso", true);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.e("msg-test", "Error al registrar el usuario", task.getException());
                                    updateUI(null);
                                    Toast.makeText(Registrarse.this, "Algo pasó al guardar", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Manejar la excepción al intentar autenticar en Firebase
                                Log.e("msg-test", "Excepción al autenticar en Firebase: ", e);
                                Toast.makeText(Registrarse.this, "Error al autenticar el usuario.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Manejar la excepción al intentar guardar en la base de datos
                    Log.e("msg-test", "Excepción al ingresar datos al documento de la colección usuarios: ", e);
                    Toast.makeText(Registrarse.this, "Error al ingresar datos del usuario.", Toast.LENGTH_SHORT).show();
                });
    }
}