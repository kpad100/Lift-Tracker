package com.example.lifttracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifttracker.EntityClass.DataModel;
import com.example.lifttracker.R;

import java.util.List;

public class WorkoutDataAdapter extends RecyclerView.Adapter<WorkoutDataAdapter.ViewHolder>{
    Context context;
    List<DataModel> list;

    public WorkoutDataAdapter(Context context, List<DataModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workouts_cards_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.workoutNameText.setText(list.get(position).getWorkoutName());
        holder.exerciseText.setText(list.get(position).getExercise());
        holder.weightText.setText(list.get(position).getWeight() + "");
        holder.repsText.setText(list.get(position).getReps() + "");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView workoutNameText, exerciseText, weightText, repsText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutNameText = itemView.findViewById(R.id.workoutNameText);
            exerciseText = itemView.findViewById(R.id.exerciseText);
            weightText = itemView.findViewById(R.id.weightText);
            repsText = itemView.findViewById(R.id.repsText);
        }
    }
}
