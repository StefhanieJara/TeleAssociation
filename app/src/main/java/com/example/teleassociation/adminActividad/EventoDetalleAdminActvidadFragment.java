package com.example.teleassociation.adminActividad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuarioSesion;
import com.example.teleassociation.subirFotoEventAlum;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
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

public class EventoDetalleAdminActvidadFragment extends Fragment implements OnMapReadyCallback {

    private FirebaseFirestore db;
    private String nombreEventoParticipante;
    private String nombreEvento;
    private MapView mapView;
    private GoogleMap mMap;
    private double latitud;
    private double longitud;
    LatLng destinoLatLng;
    FirebaseAuth mAuth;
    TextView nameUser;
    String delegadoSesion;

    public static EventoDetalleAdminActvidadFragment newInstance(String nombreEvento) {
        EventoDetalleAdminActvidadFragment fragment = new EventoDetalleAdminActvidadFragment();
        Bundle args = new Bundle();
        args.putString("nombreEvento", nombreEvento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_detalle_admin_actvidad, container, false);


        // Obtén la referencia al MapView desde el layout
        // Obtén la referencia al MapView desde el layout
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Necesario para que el mapa funcione correctamente

        // Asigna el callback del mapa
        mapView.getMapAsync(this);

        // Inicializa el contexto de la API de Google Maps Directions

        // Recuperar el nombre del evento desde los argumentos
       nombreEvento = getArguments().getString("nombreEvento"); // Asegúrate de que sea "nombreEvento" y no "nombre_evento"

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();
        obtenerDatosUsuario(usuarioSesion -> {
            nameUser = view.findViewById(R.id.nameUser);
            nameUser.setText(usuarioSesion.getNombre());
            delegadoSesion=usuarioSesion.getNombre();

            // Obtén una referencia al documento o la colección que necesitas
            // Por ejemplo, si tienes una colección llamada "eventos":
            db.collection("eventos")
                    .whereEqualTo("nombre", nombreEvento)  // Filtra por documentos con el campo "nombre" igual a nombreEvento
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Verifica si se encontraron documentos
                            SimpleDateFormat formatoFechaEsp = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Si hay documentos, obtén el primero (en este caso asumimos que solo hay uno)
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                // Ahora puedes obtener los datos del documento
                                String nombreEvento = documentSnapshot.getString("nombre");

                                Date date = documentSnapshot.getDate("fecha");
                                String fechaSt = date.toString();
                                String[] partes = fechaSt.split(" ");
                                //String fechaEvento = partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3]; // "Mon Oct 30"
                                Log.d("msg-test1","el nuevo formato de fecha es :"+formatoFechaEsp.format(date));
                                String fechaEvento = formatoFechaEsp.format(date);
                                String horaEvento = partes[3];
                                String apoyos = (String) documentSnapshot.get("apoyos");
                                String descripcion = (String) documentSnapshot.get("descripcion");
                                String delegado = (String) documentSnapshot.get("delegado");
                                String urlImagenEvento = documentSnapshot.getString("url_imagen");

                                Log.d("msg-test", " | nombre: " + nombreEvento + " | fecha: " + fechaEvento + " | hora: " + horaEvento + "| delegado: "+delegado);
                                nombreEventoParticipante = nombreEvento;

                                // Ahora puedes actualizar tus TextViews u otros elementos de la vista
                                TextView textViewNombreEvento = view.findViewById(R.id.evento);
                                TextView textViewFecha = view.findViewById(R.id.fecha);
                                TextView textViewHora = view.findViewById(R.id.hora);
                                TextView textViewApoyos = view.findViewById(R.id.apoyos);
                                TextView textViewDescripcion = view.findViewById(R.id.descripcionEvento);
                                ImageView imageViewEvento = view.findViewById(R.id.imagen);  // Asegúrate de tener este ID en tu XML

                                if (urlImagenEvento != null && !urlImagenEvento.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(urlImagenEvento)
                                            .into(imageViewEvento);
                                }


                                textViewNombreEvento.setText(nombreEvento);
                                textViewFecha.setText("Fecha: " + fechaEvento);
                                textViewHora.setText("Hora: " + horaEvento);
                                textViewApoyos.setText("Apoyos: " + apoyos);
                                textViewDescripcion.setText(descripcion);

                                Button editarEvento = view.findViewById(R.id.editarEvento);
                                if(delegadoSesion.equals(delegado)){
                                    editarEvento.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Crear un Intent o Fragment y pasar el nombre del evento como argumento
                                            String nombreEvento = nombreEventoParticipante;

                                            // O si estás iniciando un nuevo Fragment:

                                            EditarEventoFragment fragment = EditarEventoFragment.newInstance(nombreEvento);
                                            getParentFragmentManager().beginTransaction()
                                                    .replace(R.id.frame_container, fragment)
                                                    .addToBackStack(null)
                                                    .commit();

                                        }
                                    });
                                }else{
                                    editarEvento.setVisibility(View.GONE);
                                }


                            } else {
                                Log.d("msg-test", "El documento no existe");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("msg-test", "Error al obtener documento: " + e.getMessage());
                        }
                    });
            Button btnVerParticipantes = view.findViewById(R.id.verParticipantes);
            btnVerParticipantes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent o Fragment y pasar el nombre del evento como argumento
                    String nombreEvento = nombreEventoParticipante;

                    // O si estás iniciando un nuevo Fragment:

                    ListaParticipantesFragment fragment = ListaParticipantesFragment.newInstance(nombreEvento);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, fragment)
                            .addToBackStack(null)
                            .commit();

                }
            });
            /*Button editarEvento = view.findViewById(R.id.editarEvento);
            editarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent o Fragment y pasar el nombre del evento como argumento
                    String nombreEvento = nombreEventoParticipante;

                    // O si estás iniciando un nuevo Fragment:

                    EditarEventoFragment fragment = EditarEventoFragment.newInstance(nombreEvento);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, fragment)
                            .addToBackStack(null)
                            .commit();

                }
            });*/
            Button subirFoto = view.findViewById(R.id.subirFoto);

            subirFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí se ejecuta cuando se hace clic en el botón

                    // Crear el Intent para abrir SubirFoto
                    Intent intent = new Intent(getActivity(), subirFotoEventAlum.class);

                    // Lanzar la nueva actividad
                    startActivity(intent);
                }
            });

        });




        return view;
    }

    private void solicitarPermisosDeUbicacion() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no tienes permisos, solicítalos
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Si ya tienes permisos, puedes habilitar la capa de "mi ubicación" y obtener la ubicación actual
            mMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, habilita la capa de "mi ubicación" y obtén la ubicación actual
                mMap.setMyLocationEnabled(true);
                obtenerUbicacionActual();
            } else {
                // Permiso denegado, maneja la situación según tus requerimientos
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Llama a la función para solicitar permisos y obtener la ubicación actual
        solicitarPermisosDeUbicacion();
    }

    private void obtenerUbicacionActual() {

    // Realizar la consulta para encontrar el evento por su nombre
        db.collection("eventos")
                .whereEqualTo("nombre", nombreEvento)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        Log.d("ala",  nombreEvento);
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Iterar sobre todos los documentos encontrados

                                // Obtén el GeoPoint del campo "ubicacion" (ajusta el nombre del campo según sea necesario)
                                GeoPoint ubicacion = documentSnapshot.getGeoPoint("ubicacion");

                                if (ubicacion != null) {
                                    // Obtén las coordenadas
                                    latitud = ubicacion.getLatitude();
                                    longitud = ubicacion.getLongitude();
                                    destinoLatLng = new LatLng(latitud, longitud);

                                    // Ahora puedes usar latitud y longitud según sea necesario
                                    Log.d("Coordenadas", "Latitud: " + latitud + ", Longitud: " + longitud);

                                    // Verificar permisos de ubicación
                                    if (ActivityCompat.checkSelfPermission(requireContext(),
                                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                            && ActivityCompat.checkSelfPermission(requireContext(),
                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // Si no tienes permisos, solicítalos
                                        ActivityCompat.requestPermissions(requireActivity(),
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                1);
                                    } else {
                                        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
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
                                    Log.d("Coordenadas", "La ubicación es nula para el evento: " + nombreEvento);
                                }
                            }
                        } else {
                            // Manejar el caso en que no se encontraron documentos
                            Log.d("Evento", "No se encontró el evento: " + nombreEvento);
                        }
                    } else {
                        // Manejar errores en la tarea
                        Log.e("Error", "Error al realizar la consulta: " + task.getException().getMessage());
                    }
                });

        // Obtener la dirección y mostrar la ruta desde la ubicación actual hasta un destino (por ejemplo, PUCP)


    }

    private void obtenerYMostrarRuta(LatLng origen, LatLng destino) {
            String url = obtenerURLDirecciones(origen, destino);
            new ObtenerRuta().execute(url);
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
                ParserRuta parserTask = new ParserRuta();
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
                    DirectionsJSONParser parser = new DirectionsJSONParser();

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

    private void obtenerDatosUsuario(EventoDetalleAdminActvidadFragment.FirestoreCallback callback) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usuarioSesion usuarioSesion = new usuarioSesion();

        if (user != null) {
            String email = user.getEmail();

            Log.d("msg-test", "el email es: " + email);

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
                                    Log.d("msg-test", "datos del usuario " + codigo + "  correo " + " nombre");
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

    public interface FirestoreCallback {
        void onCallback(usuarioSesion usuario);
    }

}