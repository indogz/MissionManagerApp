package com.example.matteo.app1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Fragments.SchedaInterventoFragment;
import Fragments.WaterLevelForecastFragment;
import Models.SchedaIntervento;


public class ToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        if (findViewById(R.id.fragment_container) != null) {
            SchedaInterventoFragment f1 = new SchedaInterventoFragment();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, f1);
            ft.commit();
        }


    }

}
