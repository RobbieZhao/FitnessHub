package com.example.lifestyle.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_table")

public class ProfileTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uID")
    public String uID;

    @ColumnInfo(name = "profiledata")
    public String profileJson;

    public ProfileTable(@NonNull String uID, @NonNull String profileJson){
        this.uID = uID;
        this.profileJson = profileJson;
    }

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public String getUID() {
        return uID;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }
}
