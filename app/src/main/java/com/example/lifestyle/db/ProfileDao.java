package com.example.lifestyle.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.*;
import java.util.List;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileTable profileTable);

    @Query("DELETE FROM profile_table")
    void deleteAll();

    @Query("SELECT * from profile_table ORDER BY username ASC")
    LiveData<List<ProfileTable>> getAll();
}
