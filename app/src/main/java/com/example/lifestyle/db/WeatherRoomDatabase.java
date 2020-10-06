package com.example.lifestyle.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(entities = {WeatherTable.class}, version = 1, exportSchema = false)

public abstract class WeatherRoomDatabase extends RoomDatabase {
    private static volatile WeatherRoomDatabase mInstance;
    public abstract WeatherDao weatherDao();

    public static synchronized WeatherRoomDatabase getDatabase(final Context context){
        if(mInstance==null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    WeatherRoomDatabase.class, "weather.db").addCallback(sRoomDatabaseCallback).build();
        }
        return mInstance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsync(mInstance).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {
        private final WeatherDao mDao;

        PopulateDbAsync(WeatherRoomDatabase db){
            mDao = db.weatherDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAll();
            WeatherTable weatherTable = new WeatherTable("dummy_loc","dummy_data");
            mDao.insert(weatherTable);
            return null;
        }
    }
}