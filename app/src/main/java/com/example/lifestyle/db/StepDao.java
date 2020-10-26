package com.example.lifestyle.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StepTable stepTable);

    @Query("DELETE FROM step_table")
    void deleteAll();

    @Query("SELECT * from step_table")
    List<StepTable> getData();
}
