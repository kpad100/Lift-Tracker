package com.example.lifttracker.EntityClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "workout_table")
public class Workout {

    @PrimaryKey(autoGenerate = true)
    private int workoutId;
    @ColumnInfo(name = "workout_name")
    private String workoutName;

    @ColumnInfo(name = "workoutSet_list")
    private List<Integer> workoutSetList;

    public Workout(String workoutName, List<Integer> workoutSetList) {
        this.workoutName = workoutName;
        this.workoutSetList = workoutSetList;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public List<Integer> getWorkoutSetList() {
        return workoutSetList;
    }

    public void setSetList(List<Integer> workoutSetList) {
        this.workoutSetList = workoutSetList;
    }
}

