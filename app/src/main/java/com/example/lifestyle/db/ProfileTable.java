package com.example.lifestyle.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_table")
public class ProfileTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "profiledata")
    private String profileJson;

    public ProfileTable(@NonNull String username, @NonNull String profileJson){
        this.username = username;
        this.profileJson = profileJson;
    }

    public void setUsername(String u){
        this.username = u;
    }

    public void setProfileJson(String p){
        this.profileJson = p;
    }

    public String getUsername(){
        return username;
    }

    public String getProfileJson(){
        return profileJson;
    }
}
