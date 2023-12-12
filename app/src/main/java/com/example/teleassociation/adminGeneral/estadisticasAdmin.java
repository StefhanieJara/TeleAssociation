package com.example.teleassociation.adminGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.example.teleassociation.dto.usuario;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class estadisticasAdmin extends AppCompatActivity {

    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();

    ArrayList barArrayList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView nameUser;
    usuario usuario = new usuario();
    private PieChart pieChart;
    private TextView textViewTitulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_admin);
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

        BarChart barChart = findViewById(R.id.barchart);

        // Configurar gráfico
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);




        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setGranularity(1f);

        // Obtener datos de Firestore y actualizar el gráfico
        getDataAndDrawChart(barChart);



        pieChart = findViewById(R.id.piechart);

        pieChart = findViewById(R.id.piechart);


        // Realizar la consulta a Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int estudiantes = 0;
                            int egresados = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String condicion = document.getString("condicion");

                                if ("Estudiante".equals(condicion)) {
                                    estudiantes++;
                                } else if ("Egresado".equals(condicion)) {
                                    egresados++;
                                }
                            }

                            // Configurar el gráfico circular con los datos obtenidos
                            setupPieChart(estudiantes, egresados);
                        } else {
                            // Manejar errores en la consulta
                        }
                    }
                });



        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ImageView btnMoney = findViewById(R.id.btnMoney);
        btnMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(estadisticasAdmin.this, donacionesAdmin.class);
                startActivity(intent);

            }
        });
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

    private void getDataAndDrawChart(BarChart barChart) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Realizar consulta a Firestore
        db.collection("pagos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> montoFrequencies = new HashMap<>(); // Mapa para almacenar la frecuencia de cada monto
                        List<BarEntry> barEntries = new ArrayList<>();
                        List<String> xValues = new ArrayList<>(); // Lista para almacenar los montos

                        QuerySnapshot pagosCollection = task.getResult();

                        int index = 0; // Variable para mantener el índice de la entrada

                        for (QueryDocumentSnapshot document : pagosCollection) {
                            // Obtener el monto de cada pago
                            String montoStr = document.getString("monto");

                            if (montoStr != null) {
                                // Incrementar la frecuencia del monto en el mapa
                                montoFrequencies.put(montoStr, montoFrequencies.getOrDefault(montoStr, 0) + 1);

                                // Si no hemos agregado el monto al conjunto, lo agregamos al eje X
                                if (!xValues.contains(montoStr)) {
                                    xValues.add(montoStr);
                                }
                            }
                        }

                        for (String monto : xValues) {
                            // Agregar entrada para el monto y su frecuencia
                            barEntries.add(new BarEntry(index++, montoFrequencies.getOrDefault(monto, 0)));
                        }

                        // Configurar el eje X para mostrar los montos
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
                        xAxis.setGranularity(1f); // Asegura que no haya entradas duplicadas en el eje X

                        // Configurar los datos del gráfico
                        BarDataSet barDataSet = new BarDataSet(barEntries, "Monto Estadísticas");
                        BarData barData = new BarData(barDataSet);
                        barChart.setData(barData);

                        // Establecer colores y otras configuraciones del gráfico
                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(16f);

                        // Actualizar el gráfico
                        barChart.invalidate();
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar la excepción que ocurra al intentar obtener los documentos
                    Log.e("msg-test", "Excepción al obtener documentos de la colección pagos: ", e);
                });
    }

    private void setupPieChart(int estudiantes, int egresados) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(estudiantes, "Estudiantes"));
        pieEntries.add(new PieEntry(egresados, "Egresados"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Relación Estudiantes - Egresados");
        dataSet.setColors(new int[]{R.color.colorEstudiantes, R.color.colorEgresados}, this);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Actualizar el título
    }



}