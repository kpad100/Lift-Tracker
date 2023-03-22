package com.example.lifttracker.EntityClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_set_table")
public class WorkoutSet {

    @PrimaryKey(autoGenerate = true)
    private int setId;

    @ColumnInfo(name = "exercise")
    private String exercise;

    @ColumnInfo(name = "weight")
    private int weight;

    @ColumnInfo(name = "reps")
    private int reps;

    /*
    @ColumnInfo(name = "videoPath")
    private String videoPath;

    // whatever metrics we decide on (velocity, acceleration, force, etc.)
     */

    public WorkoutSet(String exercise, int weight, int reps) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
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
}
