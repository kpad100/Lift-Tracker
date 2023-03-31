package com.example.lifttracker.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lifttracker.EntityClass.Workout;
import com.example.lifttracker.EntityClass.WorkoutSet;

@Database(entities = {Workout.class, WorkoutSet.class}, version = 4)
@TypeConverters({Converters.class, UriConverters.class})
public abstract class DatabaseClass extends RoomDatabase {

    public abstract DaoClass getDao();
    private static DatabaseClass instance;

   public static DatabaseClass getDatabase(final Context context) {
        if(instance == null) {
            synchronized (DatabaseClass.class) {
                instance = Room.databaseBuilder(context, DatabaseClass.class, "DATABASE").fallbackToDestructiveMigration().allowMainThreadQueries().build();
            }
        }

        return instance;
    }
}
