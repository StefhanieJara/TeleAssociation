package com.example.teleassociation.Usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.ChatActivity;
import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.adminActividad.ChatFragment;
import com.example.teleassociation.adminActividad.EventoDetalleAdminActvidadFragment;
import com.example.teleassociation.databinding.ActivityEventoDetalleAlumnoBinding;
import com.example.teleassociation.dto.usuario;
import com.example.teleassociation.dto.usuarioSesion;
import com.example.teleassociation.fotosEvento;
import com.example.teleassociation.subirFotoEventAlum;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class eventoDetalleAlumno extends AppCompatActivity implements OnMapReadyCallback {
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FirebaseFirestore db;

    private String fechaEvento;
    private String nombreEvento;
    private String nombreEventoParticipante;
    private MapView mapView;
    private GoogleMap mMap;
    private double latitud;
    private double longitud;
    LatLng destinoLatLng;
    private String idEvento;

    private String urlImagenEvento;  // Declarar la variable
    private Date date;
    private String apoyosEvento;
    private String descripcion;
    ActivityEventoDetalleAlumnoBinding binding;
    FirebaseAuth mAuth;
    TextView nameUser;
    CardView cardView2;
    CardView cardView4;
    Button nuevaFoto;
    String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventoDetalleAlumnoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Obtén la referencia al MapView desde el layout
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        // Asegúrate de llamar a getMapAsync para establecer el callback
        mapView.getMapAsync(this);
        // Verificar si el fragmento ya está en el contenedor (por ejemplo, en caso de cambios de configuración)
        // Manejador de clics para el botón "Chatear"

        Intent intent = getIntent();
        String eventoId = intent.getStringExtra("eventoId");
        idEvento = eventoId;
        Button btnChatear = findViewById(R.id.chatear2);

        Log.d("msg-test", "Llegó el codigo: " + eventoId);

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        db = FirebaseFirestore.getInstance();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        obtenerDatosUsuario(usuario -> {
            Log.d("msg-test", "El nombre del usuario fuera del collection es: " + usuario.getNombre());
            nameUser = findViewById(R.id.nameUser);
            nameUser.setText(usuario.getNombre());
            nombreUsuario = usuario.getNombre();
            db.collection("eventos")
                    .document(eventoId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            // Obtén los datos del documento
                            nombreEvento = documentSnapshot.getString("nombre");
                            date = documentSnapshot.getDate("fecha");
                            apoyosEvento = (String) documentSnapshot.get("apoyos");
                            descripcion = (String) documentSnapshot.get("descripcion");
                            // Actualiza la vista con la información obtenida
                            updateUIWithEventData(documentSnapshot);

                            db.collection("participantes")
                                    .whereEqualTo("evento", nombreEvento)
                                    .whereEqualTo("nombre", nombreUsuario)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String nombreParticipante = document.getString("nombre");
                                                String eventoParticipante = document.getString("evento");

                                                Log.d("msg-test", "Nombre del participante: " + nombreParticipante);
                                                Log.d("msg-test", "Evento del participante: " + eventoParticipante);

                                                // Aquí puedes realizar otras acciones con la información obtenida
                                            }
                                            nuevaFoto = findViewById(R.id.nuevaFoto);
                                            if (!task.getResult().isEmpty()) {
                                                nuevaFoto.setVisibility(View.VISIBLE);
                                                btnChatear.setVisibility(View.VISIBLE);
                                                nuevaFoto.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent=new Intent(eventoDetalleAlumno.this, subirFotoEventAlum.class);
                                                        intent.putExtra("eventoId", eventoId);  // Pasa el ID del evento a la nueva actividad
                                                        startActivity(intent);
                                                    }
                                                });
                                                btnChatear.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(eventoDetalleAlumno.this, ChatActivity.class);
                                                        intent.putExtra("nombreEvento", nombreEvento);
                                                        intent.putExtra("idDocumento", idEvento);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                //nuevaFoto.setVisibility(View.GONE);
                                                //btnChatear.setVisibility(View.GONE);
                                                nuevaFoto.setVisibility(View.INVISIBLE);
                                                btnChatear.setVisibility(View.INVISIBLE);
                                            }
                                        } else {
                                            Log.e("msg-test", "Error al obtener documentos: " + task.getException());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja posibles errores aquí
                    });

        });
        Button btnVerFotos = findViewById(R.id.verFotos);
        btnVerFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Crea un Intent para iniciar FotosEventoActivity
                Intent intent = new Intent(eventoDetalleAlumno.this, fotosEvento.class);

                // Pasa el ID del evento al Intent
                intent.putExtra("eventoId", eventoId);

                // Inicia la actividad
                startActivity(intent);
            }
        });




    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.firstFragment) {
                TextView nombreEvento = findViewById(R.id.nombreEvento);
                cardView2 = findViewById(R.id.cardView2);
                cardView4 = findViewById(R.id.cardView4);
                Button chatear2 = findViewById(R.id.chatear2);
                Button nuevaFoto = findViewById(R.id.nuevaFoto);
                Button verFotos = findViewById(R.id.verFotos);
                MapView MapView = findViewById(R.id.mapView);

                nombreEvento.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                cardView4.setVisibility(View.INVISIBLE);
                chatear2.setVisibility(View.INVISIBLE);
                nuevaFoto.setVisibility(View.INVISIBLE);
                verFotos.setVisibility(View.INVISIBLE);
                MapView.setVisibility(View.INVISIBLE);

                loadFragment(firstFragment);
                return true;
            }
            if (item.getItemId() == R.id.secondFragment) {
                TextView nombreEvento = findViewById(R.id.nombreEvento);
                cardView2 = findViewById(R.id.cardView2);
                cardView4 = findViewById(R.id.cardView4);
                Button chatear2 = findViewById(R.id.chatear2);
                Button nuevaFoto = findViewById(R.id.nuevaFoto);
                Button verFotos = findViewById(R.id.verFotos);
                MapView MapView = findViewById(R.id.mapView);

                nombreEvento.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                cardView4.setVisibility(View.INVISIBLE);
                chatear2.setVisibility(View.INVISIBLE);
                nuevaFoto.setVisibility(View.INVISIBLE);
                verFotos.setVisibility(View.INVISIBLE);
                MapView.setVisibility(View.INVISIBLE);

                loadFragment(secondFragment);
                return true;
            }
            if (item.getItemId() == R.id.thirdFragment) {
                TextView nombreEvento = findViewById(R.id.nombreEvento);
                cardView2 = findViewById(R.id.cardView2);
                cardView4 = findViewById(R.id.cardView4);
                Button chatear2 = findViewById(R.id.chatear2);
                Button nuevaFoto = findViewById(R.id.nuevaFoto);
                Button verFotos = findViewById(R.id.verFotos);
                MapView MapView = findViewById(R.id.mapView);

                nombreEvento.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                cardView4.setVisibility(View.INVISIBLE);
                chatear2.setVisibility(View.INVISIBLE);
                nuevaFoto.setVisibility(View.INVISIBLE);
                verFotos.setVisibility(View.INVISIBLE);
                MapView.setVisibility(View.INVISIBLE);

                loadFragment(thirdFragment);
                return true;
            }
            if (item.getItemId() == R.id.fourFragment) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    };


    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }

    private void updateUIWithEventData(DocumentSnapshot documentSnapshot) {
        // Actualiza los elementos de la vista con los valores de fechaEvento y apoyosEvento
        TextView nombreTexView = findViewById(R.id.nombreEvento);
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView horaTextView = findViewById(R.id.hora);
        TextView apoyosTextView = findViewById(R.id.cantApoyos);
        TextView descripcionTextView = findViewById(R.id.decripcionEvento);
        ImageView imageViewEvento = findViewById(R.id.imagenView);  // Asegúrate de tener este ID en tu XML
        urlImagenEvento = documentSnapshot.getString("url_imagen");




        nombreTexView.setText(nombreEvento);
        String fechaSt = date.toString();
        String[] partesFechaHora = fechaSt.split(" ");
        SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
        String fecha = formatoFechaEsp.format(date);
        String horaMinutos = partesFechaHora[3].substring(0, 5);
        if (partesFechaHora.length >= 2) {
            fechaTextView.setText("Fecha: " + fecha);
            horaTextView.setText("Hora: " + horaMinutos);
        } else {
            fechaTextView.setText("Fecha: No disponible");
            horaTextView.setText("Hora: No disponible");
        }
        apoyosTextView.setText("Apoyos: " + apoyosEvento);
        descripcionTextView.setText(descripcion);

        if (urlImagenEvento != null && !urlImagenEvento.isEmpty()) {
            Glide.with(this)
                    .load(urlImagenEvento)
                    .into(imageViewEvento);
        }

    }


    private void obtenerDatosUsuario(eventoDetalleAlumno.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuario usuario = new usuario();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();

            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            QuerySnapshot usuariosCollection = task2.getResult();
                            for (QueryDocumentSnapshot document : usuariosCollection) {
                                String codigo = document.getId();
                                String correo = (String) document.get("correo");
                                String nombre = (String) document.get("nombre");

                                if (correo.equals(email)) {
                                    usuarioSesion.setId(codigo);
                                    usuarioSesion.setNombre(nombre);
                                    usuarioSesion.setCorreo(correo);
                                    // Llamada al método de la interfaz con el nombre del usuario
                                    callback.onCallback(usuarioSesion);
                                    return;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Maneja la excepción que ocurra al intentar obtener los documentos
                        Log.e("msg-test", "Excepción al obtener documentos de la colección usuarios: ", e);
                    });
        }
    }

    private void solicitarPermisosDeUbicacion() {
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Si no tienes permisos, solicítalos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            } else {
                // Si ya tienes permisos, puedes habilitar la capa de "mi ubicación" y obtener la ubicación actual
                mMap.setMyLocationEnabled(true);
                obtenerUbicacionActual(idEvento);
            }
        } else {
            Log.e("Mapa", "El mapa es nulo");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, habilita la capa de "mi ubicación" y obtén la ubicación actual
                mMap.setMyLocationEnabled(true);
                obtenerUbicacionActual(idEvento);
            } else {
                // Permiso denegado, maneja la situación según tus requerimientos
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        solicitarPermisosDeUbicacion();
        // Llama a la función para solicitar permisos y obtener la ubicación actual
    }


    private void obtenerUbicacionActual(String eventoId) {
        // Realizar la consulta para encontrar el evento por su ID
        db.collection("eventos")
                .document(eventoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Documento encontrado, obtén el GeoPoint de ubicación
                            GeoPoint ubicacion = documentSnapshot.getGeoPoint("ubicacion");

                            if (ubicacion != null) {
                                // Resto del código para procesar la ubicación
                                latitud = ubicacion.getLatitude();
                                longitud = ubicacion.getLongitude();
                                destinoLatLng = new LatLng(latitud, longitud);

                                // Ahora puedes usar latitud y longitud según sea necesario
                                Log.d("Coordenadas", "Latitud: " + latitud + ", Longitud: " + longitud);

                                // Verificar permisos de ubicación
                                if (ActivityCompat.checkSelfPermission(this,
                                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // Si no tienes permisos, solicítalos
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                            1);
                                } else {
                                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                                    if (locationManager != null) {
                                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if (lastKnownLocation != null) {
                                            LatLng miUbicacion = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                            Log.d("msg-test", String.valueOf(miUbicacion));

                                            // Agregar un marcador en la ubicación actual del usuario
                                            mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Mi Ubicación"));

                                            // Mover la cámara al marcador de la ubicación actual
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f));

                                            obtenerYMostrarRuta(miUbicacion, destinoLatLng);
                                        }
                                    }
                                }
                            } else {
                                // Manejar el caso en que la ubicación sea nula
                                Log.d("Coordenadas", "La ubicación es nula para el evento con ID: " + eventoId);
                            }
                        } else {
                            // Manejar el caso en que el documento no existe o es nulo
                            if (documentSnapshot == null) {
                                Log.d("Evento", "El documento es nulo para el evento con ID: " + eventoId);
                            } else {
                                Log.d("Evento", "No se encontró el evento con ID: " + eventoId);
                            }
                        }
                    } else {
                        // Manejar errores en la tarea
                        Log.e("Error", "Error al realizar la consulta: " + task.getException().getMessage());
                    }
                });
    }



    private void obtenerYMostrarRuta(LatLng origen, LatLng destino) {
        String url = obtenerURLDirecciones(origen, destino);
        new eventoDetalleAlumno.ObtenerRuta().execute(url);
    }

    private String obtenerURLDirecciones(LatLng origen, LatLng destino) {
        String apiKey = "AIzaSyAUQXpnbBf2qrLbTViHWD3rcXsRMSod-KQ";  // Reemplaza con tu clave de API
        String str_origen = "origin=" + origen.latitude + "," + origen.longitude;
        String str_destino = "destination=" + destino.latitude + "," + destino.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String params = str_origen + "&" + str_destino + "&" + sensor + "&" + mode + "&key=" + apiKey;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
    }

    private class ObtenerRuta extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = descargarUrl(url[0]);
            } catch (Exception e) {
                // Manejar la excepción
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            eventoDetalleAlumno.ParserRuta parserTask = new eventoDetalleAlumno.ParserRuta();
            parserTask.execute(result);
        }
    }

    private String descargarUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
        } finally {
            urlConnection.disconnect();
        }

        return data;
    }

    private class ParserRuta extends AsyncTask<String, Integer, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<LatLng> path = new ArrayList<>();

            try {
                jObject = new JSONObject(jsonData[0]);
                eventoDetalleAlumno.DirectionsJSONParser parser = new eventoDetalleAlumno.DirectionsJSONParser();

                // Se empieza a parsear la dirección y se obtiene la lista de puntos de la ruta
                path = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return path;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(result);
            lineOptions.width(5);
            lineOptions.color(Color.BLUE);

            mMap.addPolyline(lineOptions);
        }
    }

    private class DirectionsJSONParser {
        List<LatLng> parse(JSONObject jObject) {
            List<LatLng> puntos = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {
                jRoutes = jObject.getJSONArray("routes");

                // Loop para todos los elementos de la ruta
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                    // Loop para todos los elementos de las piernas
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        // Loop para todos los elementos de los pasos
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = PolyUtil.decode(polyline);

                            // Loop para todos los puntos de la ruta
                            for (int l = 0; l < list.size(); l++) {
                                double lat = list.get(l).latitude;
                                double lng = list.get(l).longitude;
                                LatLng position = new LatLng(lat, lng);

                                puntos.add(position);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return puntos;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    public interface FirestoreCallback {
        void onCallback(usuarioSesion usuario);
    }



}