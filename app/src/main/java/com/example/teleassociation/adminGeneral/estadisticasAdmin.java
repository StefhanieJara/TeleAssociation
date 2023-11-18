package com.example.teleassociation.adminGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.teleassociation.MainActivity;
import com.example.teleassociation.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class estadisticasAdmin extends AppCompatActivity {

    AdminGeneralInicioFragment adminGeneralInicioFragment = new AdminGeneralInicioFragment();
    ListaActividadesGeneralFragment listaActividadesGeneralFragment = new ListaActividadesGeneralFragment();
    CrearActividadFragment crearActividadFragment = new CrearActividadFragment();

    PersonasGeneralFragment personasGeneralFragment = new PersonasGeneralFragment();

    ArrayList barArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_admin);

        BarChart barChart =findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList,"Barra estadisticas");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        //COLOR BAR DATA SET
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //TEXT COLOR
        barDataSet.setValueTextColor(Color.BLACK);
        //setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);


                // Ocultar barra de título
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
            if(item.getItemId()==R.id.fiveFragment){
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

    private void getData(){

        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(2f, 10));
        barArrayList.add(new BarEntry(3f, 20));
        barArrayList.add(new BarEntry(4f, 30));
        barArrayList.add(new BarEntry(5f, 40));
        barArrayList.add(new BarEntry(6f, 50));

    }
}