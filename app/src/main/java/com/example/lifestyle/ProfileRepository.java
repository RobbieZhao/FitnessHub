package com.example.lifestyle;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.db.ProfileDao;
import com.example.lifestyle.db.ProfileTable;
import com.example.lifestyle.db.ProfileRoomDatabase;
import com.example.lifestyle.utils.JSONProfileUtils;

import java.lang.ref.WeakReference;

public class ProfileRepository {
    private final MutableLiveData<ProfileData> jsonData = new MutableLiveData<ProfileData>();
    private ProfileDao mProfileDao;
    private String mProfileData;

    ProfileRepository(Application application){
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profileDao();
    }

    public MutableLiveData<ProfileData> getData() {
        return jsonData;
    }

    public void storeUserData(String profileData) {
        mProfileData = profileData;

        ProfileTable profileTable = new ProfileTable("1", mProfileData);
        new insertAsyncTask(mProfileDao).execute(profileTable);
    }

    public void fetchUserData() {
        new fetchAsyncTask(mProfileDao, this).execute();
    }

    private static class insertAsyncTask extends AsyncTask<ProfileTable,Void,Void>{
        private ProfileDao mAsyncTaskDao;

        insertAsyncTask(ProfileDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ProfileTable... profileTables) {
            mAsyncTaskDao.insert(profileTables[0]);
            return null;
        }
    }

    private static class fetchAsyncTask extends AsyncTask<Void, Void, ProfileTable> {
        private ProfileDao mAsyncTaskDao;
        private WeakReference<ProfileRepository> mRepoWReference;

        fetchAsyncTask(ProfileDao dao, ProfileRepository repo) {
            mAsyncTaskDao = dao;
            mRepoWReference = new WeakReference(repo);
        }

        @Override
        protected ProfileTable doInBackground(Void... voids) {
            ProfileTable profileTable = mAsyncTaskDao.getData("1");

            return profileTable;
        }

        @Override
        protected void onPostExecute(ProfileTable profileTable) {
            try {
                String json = profileTable.getProfileJson();
                ProfileData profileData = JSONProfileUtils.getProfileData(json);

                ProfileRepository localWRvar = mRepoWReference.get();
                localWRvar.jsonData.setValue(profileData);
            } catch (Exception e) {

            }
        }
    }
}