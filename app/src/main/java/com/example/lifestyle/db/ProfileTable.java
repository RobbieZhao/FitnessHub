package com.example.lifestyle.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_table")

public class ProfileTable {
    @PrimaryKey(autoGenerate = true)
    public int uID;

    @ColumnInfo(name = "userName")
    public String userName;

    @ColumnInfo(name = "profiledata")
    public String profileJson;

    public ProfileTable(@NonNull String userName, @NonNull String profileJson){
        this.userName = userName;
        this.profileJson = profileJson;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }



//    @PrimaryKey(autoGenerate = true)
//    public int uID;
//
//    @ColumnInfo(name = "userName")
//    public String userName;
//
//    @ColumnInfo(name = "age")
//    public int age;
//
//    @ColumnInfo(name = "country")
//    public String country;
//
//    @ColumnInfo(name = "city")
//    public String city;
//
//    @ColumnInfo(name = "foot")
//    public int foot;
//
//    @ColumnInfo(name = "inch")
//    public int inch;
//
//    @ColumnInfo(name = "weight")
//    public int weight;
//
//
//    public ProfileTable(@NonNull int uID, @NonNull String userName, String country, String city, int foot, int inch, int weight){
//        this.uID = uID;
//        this.userName = userName;
//        this.country = country;
//        this.city = city;
//        this.foot = foot;
//        this.inch = inch;
//        this.weight = weight;
//    }
//
//    public void setuID(int uID){ this.uID = uID; }
//
//    public int getuID() {
//        return uID;
//    }
//
//    public void setUserName(String userName) { this.userName = userName; }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setCountry(String country) { this.country = country; }
//
//    public String getCountry() { return country; }
//
//    public void setCity(String city) { this.city = city; }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setFoot(int foot) { this.foot = foot; }
//
//    public int getFoot(){
//        return foot;
//    }
//
//    public void setInch(int inch) { this.inch = inch; }
//
//    public int getInch(){
//        return inch;
//    }
//
//    public void setWeight(int weight) { this.weight = weight; }


}
