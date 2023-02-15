package com.example.lifttracker.DaoClass;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lifttracker.EntityClass.DataModel;

import java.util.List;

@Dao
public interface DaoClass {

    @Insert
    void insertAllData(DataModel model);

    // Select All Data
    @Query("select * from workoutData")
    List<DataModel> getAllData();
}
