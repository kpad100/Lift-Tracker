package com.example.lifttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    TextView detailsText;
    LinearLayout cardLayout;

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

        detailsText = findViewById(R.id.details);
        cardLayout = findViewById(R.id.cardLayout);
        cardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    public void expand(View view) {
        int visibility = (detailsText.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(30);
        TransitionManager.beginDelayedTransition(cardLayout, autoTransition);
        detailsText.setVisibility(visibility);
    }
}