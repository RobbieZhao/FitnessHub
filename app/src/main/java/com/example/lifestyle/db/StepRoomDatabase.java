package com.example.lifestyle.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {StepTable.class}, version = 1, exportSchema = false)
public abstract class StepRoomDatabase extends RoomDatabase{

    private static volatile StepRoomDatabase mInstance;
    public abstract StepDao stepDao();

    public static synchronized StepRoomDatabase getDatabase(final Context context){
        if(mInstance==null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    StepRoomDatabase.class, "step.db").build();
        }
        return mInstance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new StepRoomDatabase.PopulateDbAsync(mInstance).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {
        private final StepDao mDao;

        PopulateDbAsync(StepRoomDatabase db){
            mDao = db.stepDao();
        }

        //need to complete
        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAll();

            return null;
        }
    }
}

