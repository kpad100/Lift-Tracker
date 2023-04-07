package com.example.lifttracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lifttracker.Adapter.WorkoutAdapter;
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
    EditText workoutNameText;
    Button endWorkout;

    List<Integer> workoutSetList = new ArrayList<>();

    private Uri videoPath;
    List<Uri> videoPathList = new ArrayList<>();
    private int REQUEST_CODE_RECORD_VIDEO = 27;

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

        /*
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
        */
    }

    private void addNewView() {
        View row_workout_set_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_workout_set, null);
        row_workout_set_view.findViewById(R.id.addVideoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRecordVideoActivity();
            }
        });
        parentLayout.addView(row_workout_set_view);
    }

    private void launchRecordVideoActivity() {
        Intent i = new Intent(this, RecordVideoActivity.class);
        startActivityForResult(i, REQUEST_CODE_RECORD_VIDEO);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECORD_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            videoPathList.add(videoUri);
        }
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
            if(!videoPathList.isEmpty()) {
                if(videoPathList.get(i) != null)
                    videoPath = videoPathList.get(i);
            }

            counter++;
            WorkoutSet set = new WorkoutSet(exerciseTxt, weightTxt, repsTxt, videoPath);
            DatabaseClass.getDatabase(getApplicationContext()).getDao().insertOrUpdateWorkoutSet(set);
            workoutSetList.add(counter);
        }

        Workout workout = new Workout(workoutNameTxt, workoutSetList);
        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertOrUpdateWorkout(workout);

        WorkoutAdapter workoutAdapter = new WorkoutAdapter(getApplicationContext(), DatabaseClass.getDatabase(getApplicationContext()).getDao().getAllWorkouts());
        workoutAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Data Successfully Saved", Toast.LENGTH_SHORT).show();
    }
}