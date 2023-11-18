package com.example.teleassociation;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.teleassociation.Usuario.inicio_usuario;
import com.example.teleassociation.adapter.PersonasGeneralAdapter;
import com.example.teleassociation.adminActividad.ListaActividadesDelactvActivity;
import com.example.teleassociation.adminGeneral.inicioAdmin;
import com.example.teleassociation.databinding.ActivityMainBinding;
import com.example.teleassociation.dto.usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    HashMap<String,String> credencial= new HashMap<>();
    ActivityMainBinding binding;
    String channelId = "channelDefaultPri";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private List<usuario> usuarioLista = new ArrayList<>();
    usuario usuario = new usuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        createNotificationChannel();

        // Verificar si se ha pasado un mensaje de registro exitoso a través del Intent
        if (getIntent().getBooleanExtra("registroExitoso", false)) {
            Toast.makeText(this, "Registro exitoso, espere su validación.", Toast.LENGTH_SHORT).show();
        }
        if (getIntent().getBooleanExtra("resetPassword", false)) {
            Toast.makeText(this, "Se ha enviado un mensaje de recuperación al correo.", Toast.LENGTH_SHORT).show();
        }

        binding.iniciarSesion.setOnClickListener(v -> {
            String email = binding.email.getEditableText().toString();
            String pass= binding.editTextContrasena.getEditableText().toString();
            Log.d("msg-test", "se recibio los parametros de sesion");
            Log.d("msg-test", email+" "+pass);

            if (isValidEmail(email)) {
                Log.d("msg-test", "email valido");
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("msg-test", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    String email = user.getEmail();
                                    Log.d("msg-test", "El correo es: "+email);

                                    db.collection("usuarios")
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    QuerySnapshot usuariosCollection = task2.getResult();
                                                    Log.d("msg-test", "task2 ha sido valido");
                                                    for (QueryDocumentSnapshot document : usuariosCollection) {
                                                        String codigo = document.getId();
                                                        String comentario = (String) document.get("comentario");
                                                        String condicion = (String) document.get("condicion");
                                                        String pass = (String) document.get("contrasenha");
                                                        String correo = (String) document.get("correo");
                                                        String nombre = (String) document.get("nombre");
                                                        String validacion = (String) document.get("validado");
                                                        String rol = (String) document.get("rol");

                                                        if(correo.equals(email)){
                                                            usuario.setComentario(comentario);
                                                            usuario.setCondicion(condicion);
                                                            usuario.setContrasenha(pass);
                                                            usuario.setCorreo(correo);
                                                            usuario.setId(codigo);
                                                            usuario.setNombre(nombre);
                                                            usuario.setRol(rol);
                                                            usuario.setValidado(validacion);
                                                            Log.d("msg-test", "| codigo: " + usuario.getId() + " | nombre: " + usuario.getNombre() + "| correo: "+ usuario.getCorreo() +" | condicion: " + usuario.getCondicion() + " | validacion: " + usuario.getValidado());
                                                            break;
                                                        }
                                                    }
                                                    if(usuario.getValidado().equals("Si")){
                                                        if(usuario.getRol().equals("Usuario")){
                                                            Log.d("msg-test", "Entra rol usuario");
                                                            Intent intent = new Intent(MainActivity.this, inicio_usuario.class);
                                                            intent.putExtra("usuario", usuario);
                                                            startActivity(intent);
                                                        }else if(usuario.getRol().equals("DelegadoActividad")){
                                                            Intent intent = new Intent(MainActivity.this, ListaActividadesDelactvActivity.class);
                                                            Log.d("msg-test", "Entra rol delegado actividad");
                                                            //intent.putExtra("usuario", usuario);
                                                            startActivity(intent);
                                                        }else if(usuario.getRol().equals("DelegadoGeneral")){
                                                            Log.d("msg-test", "Entra rol delegado general");
                                                            Intent intent = new Intent(MainActivity.this, inicioAdmin.class);
                                                            //intent.putExtra("usuario", usuario);
                                                            startActivity(intent);
                                                        }
                                                    }else{
                                                        Toast.makeText(MainActivity.this, "El usuario no ha sido validado.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Maneja la excepción que ocurra al intentar obtener los documentos
                                                Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                                                Toast.makeText(MainActivity.this, "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("msg-test", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Manejar la excepción
                                Log.e("msg-test", "Exception: " + e.getMessage());
                            }
                        });
            } else {
                Toast.makeText(this, "Correo electrónico incorrecto", Toast.LENGTH_SHORT).show();
            }
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
    public void createNotificationChannel() {
        //android.os.Build.VERSION_CODES.O == 26
        NotificationChannel channel = new NotificationChannel(channelId,
                "Canal notificaciones default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        askPermission();

    }
    public void askPermission(){
        //android.os.Build.VERSION_CODES.TIRAMISU == 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{POST_NOTIFICATIONS},101);
        }
    }
    public void notificarImportanceDefault(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Mi primera notificación")
                .setContentText("Esta es mi primera notificación en Android :D")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }

    }
    // Función para validar el formato del correo electrónico
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private void updateUI(FirebaseUser user) {}

    /*public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // El usuario está autenticado, redirige a la última actividad activa
            // Puedes almacenar información sobre la última actividad en algún lugar (por ejemplo, en Firebase Realtime Database)
            // y luego recuperarla aquí para determinar a qué actividad redirigir.

            // Por ahora, simplemente muestra un mensaje de registro en la consola
            Log.d("MainActivity", "El usuario está autenticado. Redirigiendo...");

            // Aquí deberías agregar lógica para determinar a qué actividad redirigir
            // Puedes almacenar la información sobre la última actividad en Firebase Realtime Database o SharedPreferences
            // y luego recuperarla aquí para determinar a qué actividad redirigir.

            // Ejemplo: redirigir a la actividad de inicio de usuario
            Intent intent = new Intent(MainActivity.this, inicio_usuario.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual para que el usuario no pueda retroceder
        }
    }*/


}