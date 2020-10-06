package com.example.lifestyle;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.db.ProfileDao;
import com.example.lifestyle.db.ProfileRoomDatabase;
import com.example.lifestyle.db.ProfileTable;
import com.example.lifestyle.utils.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class ProfileRepository {
    private final MutableLiveData<ProfileData> jsonData =
            new MutableLiveData<>();
    private String mUsername;
    private String mJsonString;
    private ProfileDao mProfileDao;
    ProfileRepository(Application application){
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profileDao();
        loadData();
    }
    public void setUsername(String username){
        mUsername = username;

    }
    private void insert() {
        ProfileTable profileTable = new ProfileTable(mUsername, mJsonString);
        new insertAsyncTask(mProfileDao).execute(profileTable);
    }

    public MutableLiveData<ProfileData> getData() { return jsonData; }

    private void loadData() { new fetchProfileAsyncTask(this).execute(mUsername); }

    private static class fetchProfileAsyncTask extends AsyncTask<String, Void, String>{
        private WeakReference<ProfileRepository> mRepoWReference;
        fetchProfileAsyncTask(ProfileRepository repo)
        {
            mRepoWReference = new WeakReference<ProfileRepository>(repo);
        }
        @Override
        protected String doInBackground(String... strings) {
            String location = strings[0];
            URL weatherDataURL = null;
            String retrievedJsonData = null;
            if(location!=null) {
                weatherDataURL = NetworkUtils.buildURLFromString(location);
                try {
                    retrievedJsonData = NetworkUtils.getDataFromURL(weatherDataURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return retrievedJsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            ProfileRepository localPRvar = mRepoWReference.get();
            if(s != null){
                localPRvar.mJsonString = s;

            }
        }
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

}
