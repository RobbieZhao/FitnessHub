package com.example.lifestyle;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.db.ProfileDao;
import com.example.lifestyle.db.ProfileRoomDatabase;
import com.example.lifestyle.db.ProfileTable;
import com.example.lifestyle.utils.JSONProfileUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class ProfileRepository {
    private final MutableLiveData<ProfileData> jsonData =
            new MutableLiveData<>();
    private String mUsername;
    private String mJsonString;
    private String userJson;
    private ProfileDao mProfileDao;

    ProfileRepository(Application application){
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profileDao();
        loadData();
    }

    public void setUser(String username, String user) {
        mUsername = username;
        userJson = user;
    }

    public MutableLiveData<ProfileData> getData() { return jsonData; }

    private void insert() {
        ProfileTable profileTable = new ProfileTable(mUsername, mJsonString);
        new insertAsyncTask(mProfileDao).execute(profileTable);
    }


    private void loadData() {
        new AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... strings) {
                String user = strings[0];
                return user;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s!=null) {
                    mJsonString = s;
                    insert();
                    try {
                        jsonData.setValue(JSONProfileUtils.getprofiledata(s));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute(userJson);
    }


    private static class insertAsyncTask extends AsyncTask<ProfileTable, Void, Void>{
        private ProfileDao mAsyncTaskDao;

        insertAsyncTask(ProfileDao dao){ mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(ProfileTable... profileTables) {
            mAsyncTaskDao.insert(profileTables[0]);
            return null;
        }
    }

    public static void saveDataToDB(final ProfileData data){
        Log.e("ProfileRepo", "user.name " + data.getUsername() );


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                String userJson = null;
                try {
                    userJson = JSONProfileUtils.storeProfileJSON(data);
                    Log.e("ProfileRepo", "userJson " + userJson );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ProfileTable wde = new ProfileTable(data.getUsername(), userJson);
                Log.e("ProfileRepo", "PT: " + wde.userName );

                ProfileRoomDatabase.profileDao().insert(wde);
                return null;
            }
        }.execute();
    }

}
