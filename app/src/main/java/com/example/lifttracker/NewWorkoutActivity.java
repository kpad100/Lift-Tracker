package com.example.lifttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.lifttracker.Adapter.WorkoutAdapter;
import com.example.lifttracker.Adapter.WorkoutSetAdapter;
import com.example.lifttracker.DB.DatabaseClass;
import com.example.lifttracker.EntityClass.Workout;
import com.example.lifttracker.EntityClass.WorkoutSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewWorkoutActivity extends AppCompatActivity {

    LinearLayout parentLayout;
    EditText workoutNameText;//, exerciseText, weightText, repsText;
    Button endWorkout, addVideo;

    List<Integer> workoutSetList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);
        parentLayout = findViewById(R.id.newWorkoutParentLayout);
        addNewView();

        String date_n = new SimpleDateFormat("MM/dd/YY", Locale.getDefault()).format(new Date());

        workoutNameText = findViewById(R.id.workoutNameText);
        workoutNameText.setText("Workout " + date_n);
        endWorkout = findViewById(R.id.endWorkoutButton);

        FloatingActionButton addSetButton = findViewById(R.id.addSetButton);
        addSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewView();
            }
        });

        endWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(0,0);
            }
        });

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
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    private void addNewView() {
        View row_workout_set_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_workout_set, null);
        parentLayout.addView(row_workout_set_view);
    }

    private void saveData() {
        int counter = DatabaseClass.getDatabase(getApplicationContext()).getDao().getTotalNumWorkoutSets();
        String workoutNameTxt = workoutNameText.getText().toString().trim();
        int childCount = parentLayout.getChildCount();
        View v;

        for(int i = 0; i < childCount; i++) {
            v = parentLayout.getChildAt(i);
            Spinner exerciseSpinner = v.findViewById(R.id.exercise_spinner);
            EditText weightEditText = v.findViewById(R.id.weightEditText);
            EditText repsEditText = v.findViewById(R.id.repsEditText);
            String exerciseTxt = exerciseSpinner.getSelectedItem().toString().trim();
            int weightTxt = Integer.parseInt(weightEditText.getText().toString().trim());
            int repsTxt = Integer.parseInt(repsEditText.getText().toString().trim());

            counter++;
            WorkoutSet set = new WorkoutSet(exerciseTxt, weightTxt, repsTxt);
            DatabaseClass.getDatabase(getApplicationContext()).getDao().insertOrUpdateWorkoutSet(set);
            workoutSetList.add(counter);
        }

        Workout workout = new Workout(workoutNameTxt, workoutSetList);
        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertOrUpdateWorkout(workout);

        WorkoutAdapter workoutAdapter = new WorkoutAdapter(getApplicationContext(), DatabaseClass.getDatabase(getApplicationContext()).getDao().getAllWorkouts());
        workoutAdapter.notifyDataSetChanged();

        //WorkoutSetAdapter workoutSetAdapter = new WorkoutSetAdapter(getApplicationContext(), DatabaseClass.getDatabase(getApplicationContext()).getDao().getAll)


        /*workoutNameText.setText("");
        exerciseEditText.setText("");
        weightText.setText("");
        repsText.setText("");*/
        Toast.makeText(this, "Data Successfully Saved", Toast.LENGTH_SHORT).show();
    }
}