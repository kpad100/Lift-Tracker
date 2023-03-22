package com.example.lifttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lifttracker.Adapter.WorkoutAdapter;
import com.example.lifttracker.DB.DatabaseClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    TextView detailsText;
    LinearLayout cardLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.workouts);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tutorial:
                    startActivity(new Intent(getApplicationContext(),tutorial.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.about:
                    startActivity(new Intent(getApplicationContext(),about.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.workouts:
                    return true;
            }
            return false;
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),NewWorkoutActivity.class));
                overridePendingTransition(0,0);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new WorkoutAdapter(getApplicationContext(), DatabaseClass.getDatabase(getApplicationContext()).getDao().getAllWorkouts()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //detailsText = findViewById(R.id.details);
        //cardLayout = findViewById(R.id.cardLayout);
//        cardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    /*public void expand(View view) {
        int visibility = (detailsText.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(30);
        TransitionManager.beginDelayedTransition(cardLayout, autoTransition);
        detailsText.setVisibility(visibility);
    }*/
}