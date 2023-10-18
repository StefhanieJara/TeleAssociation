package com.example.teleassociation;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
    String channelId = "channelDefaultPri";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannel();

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



}