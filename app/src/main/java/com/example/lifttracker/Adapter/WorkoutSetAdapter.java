package com.example.lifttracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import com.example.lifttracker.DB.DaoClass;
import com.example.lifttracker.DB.DatabaseClass;
import com.example.lifttracker.EntityClass.WorkoutSet;
import com.example.lifttracker.R;

import java.util.List;

public class WorkoutSetAdapter extends RecyclerView.Adapter<WorkoutSetAdapter.WorkoutSetViewHolder> {

    Context context;
    private List<WorkoutSet> workoutSetList;
    public WorkoutSetAdapter(List<WorkoutSet> workoutSetList, Context context) {
        this.workoutSetList = workoutSetList;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkoutSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_set_item, parent, false);
        return new WorkoutSetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutSetViewHolder holder, int position) {
        WorkoutSet workoutSet = workoutSetList.get(position);
        if (workoutSet != null) {
            holder.exerciseText.setText(String.valueOf(workoutSet.getExercise()));
            holder.weightText.setText(String.valueOf(workoutSet.getWeight()));
            holder.repsText.setText(String.valueOf(workoutSet.getReps()));
        }
    }

    @Override
    public int getItemCount() {
        return workoutSetList.size();
    }

    public class WorkoutSetViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseText, weightText, repsText;

        public WorkoutSetViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseText = itemView.findViewById(R.id.exerciseText);
            weightText = itemView.findViewById(R.id.weightText);
            repsText = itemView.findViewById(R.id.repsText);
        }
    }

}
