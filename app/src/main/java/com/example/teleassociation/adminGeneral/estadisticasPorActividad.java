package com.example.teleassociation.adminGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.github.mikephil.charting.charts.PieChart;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class estadisticasPorActividad extends AppCompatActivity {
    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();
    private PieChart pieChart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_por_actividad);
        // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        pieChart = findViewById(R.id.piechart);

        // Recibe el nombre de la actividad de la intención
        String nombreActividad = getIntent().getStringExtra("actividadNombre");

        if (nombreActividad != null) {
            // Realiza la consulta de eventos
            consultarEventos(nombreActividad);
        }


        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    private void consultarEventos(String nombreActividad) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("eventos")
                .whereEqualTo("nombre_actividad", nombreActividad)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Procesa los documentos de eventos aquí
                        List<String> eventos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombreEvento = document.getString("nombre");
                            eventos.add(nombreEvento);
                        }

                        // Después de obtener la lista de eventos, consulta los participantes
                        consultarParticipantes(eventos);
                    } else {
                        // Maneja errores en la consulta de eventos
                        Log.e("Estadisticas", "Error al consultar eventos", task.getException());
                    }
                });
    }

    private void consultarParticipantes(List<String> eventos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (String evento : eventos) {
            Task<QuerySnapshot> task = db.collection("participantes")
                    .whereEqualTo("evento", evento)
                    .get();

            tasks.add(task);
        }

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(objects -> {
                    int totalBarras = 0;
                    int totalJugadores = 0;

                    for (Object obj : objects) {
                        if (obj instanceof QuerySnapshot) {
                            QuerySnapshot snapshot = (QuerySnapshot) obj;

                            for (QueryDocumentSnapshot document : snapshot) {
                                String asignacion = document.getString("asignacion");
                                // Cuenta las asignaciones "barra" o "jugador"
                                if (asignacion != null) {
                                    if ("barra".equals(asignacion)) {
                                        totalBarras++;
                                        Log.d("msg-test", "TOTAL BARRAS: "+totalBarras);


                                    } else if ("jugador".equals(asignacion)) {
                                        totalJugadores++;
                                        Log.d("msg-test", "TOTAL JUGADORES: "+totalJugadores);

                                    }
                                }
                            }
                        }
                    }

                    // Después de procesar todos los participantes de un evento, actualiza la gráfica
                    setupPieChart(totalBarras, totalJugadores);
                })
                .addOnFailureListener(e -> {
                    // Maneja errores en la consulta de participantes
                    Log.e("Estadisticas", "Error al consultar participantes", e);
                });
    }


    private void setupPieChart(int barra, int jugador) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(barra, "Barra"));
        pieEntries.add(new PieEntry(jugador, "Jugador"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Relación Barra - Jugador");
        dataSet.setColors(new int[]{R.color.colorEstudiantes, R.color.colorEgresados}, this);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Actualizar el título
    }










    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId()==R.id.firstFragment){
                loadFragment(adminGeneralInicioFragment);
                return true;
            }
            if(item.getItemId()==R.id.secondFragment){
                loadFragment(listaActividadesGeneralFragment);
                return true;
            }
            if(item.getItemId()==R.id.thirdFragment){
                loadFragment(crearActividadFragment);
                return true;
            }
            if(item.getItemId()==R.id.fourFragment){
                loadFragment(personasGeneralFragment);
                return true;
            }
            if(item.getItemId()==R.id.sixtFragment){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    };




    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        transaction.commit();

    }



}