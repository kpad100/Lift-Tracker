package com.example.lifttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.lifttracker.Adapter.WorkoutDataAdapter;
import com.example.lifttracker.EntityClass.DataModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewWorkoutActivity extends AppCompatActivity {

    EditText workoutNameText, exerciseText, weightText, repsText;
    Button endWorkout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        String date_n = new SimpleDateFormat("MM/dd/YY", Locale.getDefault()).format(new Date());
        EditText workoutName  = (EditText) findViewById(R.id.workoutNameText);
        workoutName.setText("Workout " + date_n);

        workoutNameText = findViewById(R.id.workoutNameText);
        exerciseText = findViewById(R.id.exerciseText);
        weightText = findViewById(R.id.weightText);
        repsText = findViewById(R.id.repsText);
        endWorkout = findViewById(R.id.endWorkoutButton);

        FloatingActionButton addSetButton = findViewById(R.id.addSetButton);
        addSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper textTableRowContext = new ContextThemeWrapper(getBaseContext(), R.style.TextTableRowStyle);
                ContextThemeWrapper numberTableRowContext = new ContextThemeWrapper(getBaseContext(), R.style.NumberTableRowStyle);
                ContextThemeWrapper tableRowContext = new ContextThemeWrapper(getBaseContext(), R.style.TableRowStyle);
                ContextThemeWrapper addVideoContext = new ContextThemeWrapper(getBaseContext(), R.style.addVideoTableRowStyle);

                TableRow row = new TableRow(tableRowContext);
                //row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                EditText exerciseText = new EditText(textTableRowContext);
                EditText weightText = new EditText(numberTableRowContext);
                EditText repsText = new EditText(numberTableRowContext);
                ImageView addVideoIcon = new ImageView(addVideoContext);

                row.addView(exerciseText);
                row.addView(weightText);
                row.addView(repsText);
                row.addView(addVideoIcon);
                TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
                table.addView(row);
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
    }

    private void saveData() {
        String workoutNameTxt = workoutNameText.getText().toString().trim();
        String exerciseTxt = exerciseText.getText().toString().trim();
        String weightTxt = weightText.getText().toString().trim();
        String repsTxt = repsText.getText().toString().trim();


        DataModel model = new DataModel();
        model.setWorkoutName(workoutNameTxt);
        model.setExercise(exerciseTxt);
        model.setWeight(Integer.parseInt(weightTxt));
        model.setReps(Integer.parseInt(repsTxt));

        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertAllData(model);

        WorkoutDataAdapter adapter = new WorkoutDataAdapter(getApplicationContext(), DatabaseClass.getDatabase(getApplicationContext()).getDao().getAllData());
        adapter.notifyDataSetChanged();


        workoutNameText.setText("");
        exerciseText.setText("");
        weightText.setText("");
        repsText.setText("");
        Toast.makeText(this, "Data Successfully Saved", Toast.LENGTH_SHORT).show();
    }
}