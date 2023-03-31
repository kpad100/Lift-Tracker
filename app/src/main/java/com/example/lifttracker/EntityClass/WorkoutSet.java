package com.example.lifttracker.EntityClass;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.lifttracker.DB.Converters;
import com.example.lifttracker.DB.UriConverters;

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


    @ColumnInfo(name = "videoPath")
    @TypeConverters({UriConverters.class})
    private Uri videoPath;

    // whatever metrics we decide on (velocity, acceleration, force, etc.)

    public WorkoutSet(String exercise, int weight, int reps, Uri videoPath) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
        this.videoPath = videoPath;
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

    public Uri getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(Uri videoPath) {
        this.videoPath = videoPath;
    }
}
