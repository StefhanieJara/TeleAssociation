package com.example.teleassociation.adminGeneral;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.teleassociation.EmailSender;
import com.example.teleassociation.MyFirebaseMessagingService;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.actividad;
import com.example.teleassociation.dto.notificacion;
import com.example.teleassociation.dto.usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.Tag;

public class validarParticipanteAdmin extends AppCompatActivity {
    FirebaseFirestore db;
    private AutoCompleteTextView validacion;
    private ArrayAdapter<String> adapterItems;
    TextInputLayout textInputNombre;
    TextInputLayout textInputCorreo;
    FirebaseAuth mAuth;
    TextView nameUser;
    com.example.teleassociation.dto.usuario usuario = new usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_participante_admin);
        final TextInputEditText textRechazo = findViewById(R.id.rechazo);
        final TextInputLayout textHint = findViewById(R.id.motivoHint);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.d("msg-test", "El correo que ingresó es: "+email);

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
                                String token = (String) document.get("token");

                                if(correo.equals(email)){
                                    usuario.setComentario(comentario);
                                    usuario.setCondicion(condicion);
                                    usuario.setContrasenha(pass);
                                    usuario.setCorreo(correo);
                                    usuario.setId(codigo);
                                    usuario.setNombre(nombre);
                                    usuario.setRol(rol);
                                    usuario.setValidado(validacion);
                                    usuario.setToken(token);
                                    Log.d("msg-test", "| codigo: " + usuario.getId() + " | nombre: " + usuario.getNombre() + "| correo: "+ usuario.getCorreo() +" | condicion: " + usuario.getCondicion() + " | validacion: " + usuario.getValidado());
                                    break;
                                }
                            }
                            nameUser = findViewById(R.id.nameUser);
                            Log.d("msg-test", "El nombre del usuario es: "+usuario.getNombre());
                            nameUser.setText(usuario.getNombre());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }


        String[] items = {"Si", "No"};
        validacion = findViewById(R.id.validacion);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        validacion.setAdapter(adapterItems);

        validacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(validarParticipanteAdmin.this, "Seleccionado: " + selectedName, Toast.LENGTH_SHORT).show();

                if ("Si".equals(selectedName)) {
                    textRechazo.setVisibility(View.GONE);
                    textHint.setVisibility(View.GONE);
                } else {
                    textRechazo.setVisibility(View.VISIBLE);
                    textHint.setVisibility(View.VISIBLE);
                }
            }
        });

        // Obtener los valores de usuarioNombre y usuarioCorreo
        String usuarioNombre = getIntent().getStringExtra("usuarioNombre");
        String usuarioCorreo = getIntent().getStringExtra("usuarioCorreo");
        String usuarioCodigo = getIntent().getStringExtra("usuarioCodigo");

        // Encontrar los TextInputLayout correspondientes
        textInputNombre = findViewById(R.id.textInputLayout27);
        textInputCorreo = findViewById(R.id.textInputLayout28);

        // Establecer los hints con los valores de usuarioNombre y usuarioCorreo
        textInputNombre.setHint(usuarioNombre);
        textInputCorreo.setHint(usuarioCorreo);

        Button button16 = findViewById(R.id.button16);

        button16.setOnClickListener(view -> {
            String rechazo = textRechazo.getText().toString().trim();
            String validacionStr = validacion.getText().toString().trim();

            //Log.d("msg-test", "El codigo del usuario es: "+usuarioCodigo);

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            Log.d("msg-test", "task2 ha sido valido");
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");
                                String token = (String) document.get("token");

                                if(correo.equals(usuarioCorreo)){
                                    usuario.setCorreo(correo);
                                    usuario.setId(codigo);
                                    usuario.setToken(token);
                                    break;
                                }
                            }
                            Log.d("msg-test", "El codigo del usuario es: "+usuario.getId());
                            DocumentReference usuarioRef = db.collection("usuarios").document(usuario.getId());
                            if(validacionStr.equals("Si")){
                                usuarioRef
                                        .update("validado", validacionStr)
                                        .addOnSuccessListener(unused -> {
                                            String id = (String) usuarioRef.getId();
                                            Log.d("msg-test", "el ID de del usuario aceptado es es: " + id);
                                            Log.d("msg-test", "Validacion: " + validacionStr);
                                            //EmailSender.sendEmail(usuarioCorreo,"Usuario valido en TeleAssociation","Su usuario ha sido valido para estar dentro de la aplicación.");
                                            Log.d("msg-test", "TOKEN: " + usuario.getToken());
                                            enviarNot(usuario.getToken(), "¡Bienvenido a TeleAssociation! Tu registro ha sido validado.");

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
                                                            String id = (String) usuarioRef.getId();
                                                            Log.d("msg-test", "el ID de del usuario denegado es es: " + id);
                                                            Log.d("msg-test", "Validacion: "+ validacionStr);
                                                            //EmailSender.sendEmail(usuarioCorreo,"Usuario invalido en TeleAssociation",rechazo);
                                                            //enviarNotificacionAlumno(usuario.getToken(), "¡Bienvenido a TeleAssociation! Tu registro ha sido validado.");
                                                            enviarNot(usuario.getToken(),  "Usuario invalido en TeleAssociation. "+rechazo);
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
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });


        });
    }
    private void enviarNot(String token, String mensaje) {
        try {
            JSONObject jsonObject = new JSONObject();


            JSONObject notification = new JSONObject();
            notification.put("title", "TeleAssociation");
            notification.put("body", mensaje);
            notification.put("priority", "high");
            JSONObject dataObj = new JSONObject();


            jsonObject.put("notification", notification);
            jsonObject.put("data",dataObj);
            jsonObject.put("to", token);
            callApi(jsonObject);

            // Crear instancia de FirebaseFirestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Obtener la referencia de la colección "notificaciones"
            CollectionReference notificacionesRef = firestore.collection("notificaciones");

            // Crear una nueva instancia de la clase notificacion con los datos necesarios
            notificacion nuevaNotificacion = new notificacion("TeleAssociation", Timestamp.now(), mensaje, usuario.getId());
            notificacionesRef.add(nuevaNotificacion)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("msg-test", "Notificación almacenada en Firestore con ID: " + documentReference.getId());
                        // Continuar con tu lógica aquí, como enviar la notificación FCM
                    })
                    .addOnFailureListener(e -> {
                        Log.e("msg-test", "Error al almacenar la notificación en Firestore", e);
                        // Manejar el error según sea necesario
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void callApi(JSONObject jsonObject){
        okhttp3.MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        okhttp3.Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAEzmjgrY:APA91bEN69zZ8gYBGdJOEWq8RWoff5Fi9A4eHhYk9q-Q5ITiBEXq66mzC_UvFTQARX53-7dh7aQKPjVIfeC4QWV02_ZAjQzbzAshXRswNoFtxq6gRB3cmH5aekYiM-dt6tHOG1T6gfUx")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });


    }




}