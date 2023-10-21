package com.example.teleassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.teleassociation.services.EventService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventoDetalleDelactvActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detalle_delactv);

        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();

        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button boton2 = findViewById(R.id.verActividades);
        Button boton3 = findViewById(R.id.verParticipantes);
        Button boton4 = findViewById(R.id.donar);
        Button boton5 = findViewById(R.id.editarEvento);

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, ListaActividadesDelactvActivity.class);
                startActivity(intent);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, ParticipantesDelactv.class);
                startActivity(intent);
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, DonacionDelactvActivity.class);
                startActivity(intent);
            }
        });

        boton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventoDetalleDelactvActivity.this, EditarEventoDelactvActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.40:3000") // Reemplaza con tu base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            EventService apiService = retrofit.create(EventService.class);

            Call<List<Lugar>> call = apiService.obtenerListaDeLugares();
            call.enqueue(new Callback<List<Lugar>>() {
                @Override
                public void onResponse(Call<List<Lugar>> call, Response<List<Lugar>> response) {
                    if (response.isSuccessful()) {
                        List<Lugar> lugares = response.body();

                        // Recorre la lista de lugares y agrega marcadores al mapa
                        for (Lugar lugar : lugares) {
                            LatLng ubicacion = new LatLng(lugar.getLatitud(), lugar.getLongitud());

                            // Personaliza el marcador según el nombre del lugar
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(ubicacion)
                                    .title(lugar.getNombre());

                        /*    switch (lugar.getNombre()) {
                                case "Bati":
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    break;
                                case "Polideportivo PUCP":
                                   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    break;
                                case "Cancha de Minas":
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    break;
                                case "Digimundo":
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                    break;
                                case "Estacionamiento de Letras":
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    break;
                                default:
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                    break;
                            }

                            mMap.addMarker(markerOptions);*/
                            // Supongamos que las coordenadas de la PUCP son -12.073106 (latitud) y -77.051369 (longitud)
                            LatLng coordenadasPUCP = new LatLng(-12.073106, -77.051369);

                            mMap.addMarker(new MarkerOptions().position(coordenadasPUCP).title("PUCP"));

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadasPUCP, 15));

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Lugar>> call, Throwable t) {
                    // Maneja el caso de fallo de la solicitud
                }
            });

    }


}