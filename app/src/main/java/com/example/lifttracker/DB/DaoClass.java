package com.example.lifttracker.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.lifttracker.EntityClass.Workout;
import com.example.lifttracker.EntityClass.WorkoutSet;

import java.util.List;

@Dao
public interface DaoClass {
    @Query("SELECT * FROM workout_table")
    List<Workout> getAllWorkouts();

    @Query("SELECT * FROM workout_table WHERE workoutId = :workoutId")
    Workout getWorkoutById(int workoutId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateWorkout(Workout workout);

    @Delete
    void deleteWorkout(Workout workout);

    @Query("SELECT * FROM workout_set_table WHERE setId = :setId")
    WorkoutSet getWorkoutSetById(int setId);

    @Query("SELECT COUNT(*) FROM workout_set_table")
    int getTotalNumWorkoutSets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateWorkoutSet(WorkoutSet workoutSet);

    @Delete
    void deleteWorkoutSet(WorkoutSet workoutSet);
}
