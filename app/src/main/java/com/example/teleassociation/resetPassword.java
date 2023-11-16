package com.example.teleassociation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.teleassociation.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button butonRecuperar;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        butonRecuperar = findViewById(R.id.buttonRecuperar);
        mAuth = FirebaseAuth.getInstance();


        butonRecuperar.setOnClickListener(v -> {
            email = findViewById(R.id.emailEditText);
            String emailStr = email.getText().toString().trim();

            Log.d("msg-test", "El correo enviado es: "+emailStr);

            mAuth.sendPasswordResetEmail(emailStr)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg-test", "Email sent.");
                                Intent intent = new Intent(resetPassword.this, MainActivity.class);
                                intent.putExtra("resetPassword", true);
                                startActivity(intent);
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar la excepción
                            Log.e("msg-test", "Exception en reset password: " + e.getMessage());
                            Toast.makeText(resetPassword.this, "Ha ocurrido un error al reestablecer la contraseña. Ingrese un correo correcto.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });;
        });
    }
}