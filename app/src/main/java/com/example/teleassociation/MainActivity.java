package com.example.teleassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.teleassociation.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    HashMap<String,String> credencial= new HashMap<>();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        credencial.put("w","12345");
        credencial.put("leo.abanto@gmail.com","12345");
        credencial.put("miguel@gmail.com","12345");

        binding.iniciarSesion.setOnClickListener(v -> {
            String email = binding.email.getEditableText().toString();
            String pass= binding.contrasena.getText().toString();

            for (Map.Entry<String, String> entry : credencial.entrySet()) {
                String storedEmail = entry.getKey();
                String storedPassword = entry.getValue();

                if (storedEmail.equals(email) && storedPassword.equals(pass)) {
                    if ("w".equals(email)) {
                        Intent intent = new Intent(MainActivity.this, inicio_usuario.class);
                        startActivity(intent);
                    } else if ("leo.abanto@gmail.com".equals(email)) {
                        Intent intent = new Intent(MainActivity.this, ListaEventosActivity.class);
                        startActivity(intent);
                    } else if ("miguel@gmail.com".equals(email)) {
                        Intent intent = new Intent(MainActivity.this, inicioAdmin.class);
                        startActivity(intent);
                    }
                    return;
                }
            }
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();

        });
        binding.recuperarPass.setOnClickListener(v -> {
            // Navegar a la actividad de recuperación de contraseña al hacer clic en "Olvidé mi contraseña"
            Intent intent = new Intent(MainActivity.this, resetPassword.class);
            startActivity(intent);
        });

    }
    public void registrarse(View view){
        Intent intent=new Intent(this, Registrarse.class);
        startActivity(intent);
    }

}