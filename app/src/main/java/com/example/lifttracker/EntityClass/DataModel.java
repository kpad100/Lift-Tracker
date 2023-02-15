package com.example.lifttracker.EntityClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workoutData")
public class DataModel {

    @PrimaryKey(autoGenerate = true)
    private int key;

    @ColumnInfo(name = "workoutName")
    private String workoutName;

    @ColumnInfo(name = "exercise")
    private String exercise;

    @ColumnInfo(name = "weight")
    private int weight;

    @ColumnInfo(name = "reps")
    private int reps;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    /*
    @ColumnInfo(name = "videoPath")
    private String videoPath;

    // whatever metrics we decide on (velocity, acceleration, force, etc.)
     */
}
