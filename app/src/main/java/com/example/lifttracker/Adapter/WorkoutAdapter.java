package com.example.lifttracker.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifttracker.DB.DatabaseClass;
import com.example.lifttracker.EntityClass.Workout;
import com.example.lifttracker.EntityClass.WorkoutSet;
import com.example.lifttracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder>{
    Context context;
    private List<Workout> workoutList;
    private OnItemClickListener onItemClickListener;

    public WorkoutAdapter(Context context, List<Workout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
        Collections.reverse(workoutList);
        //this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workouts_cards_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutNameText.setText(workout.getWorkoutName());
        List<WorkoutSet> workoutSetList = new ArrayList<>();
        List<Integer> workoutSetIdList = workout.getWorkoutSetList();
        for(int i = 0; i < workoutSetIdList.size(); i++)
            workoutSetList.add(DatabaseClass.getDatabase(context).getDao().getWorkoutSetById(workoutSetIdList.get(i)));

        holder.workoutSetRecyclerView.setAdapter(new WorkoutSetAdapter(workoutSetList, context));
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView workoutNameText;
        private RecyclerView workoutSetRecyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutNameText = itemView.findViewById(R.id.workoutNameText);

            workoutSetRecyclerView = itemView.findViewById(R.id.workout_set_recycler_view);
            workoutSetRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
