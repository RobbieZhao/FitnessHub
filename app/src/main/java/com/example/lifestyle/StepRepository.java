package com.example.lifestyle;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.data.StepData;
import com.example.lifestyle.db.StepDao;
import com.example.lifestyle.db.StepRoomDatabase;
import com.example.lifestyle.db.StepTable;
import com.example.lifestyle.utils.StepUtils;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StepRepository {
    private final MutableLiveData<ArrayList<StepData>> jsonData = new MutableLiveData();
    private StepDao mStepDao;
    private String mStepData;

    StepRepository(Application application){
        StepRoomDatabase db = StepRoomDatabase.getDatabase(application);
        mStepDao = db.stepDao();
    }

    public MutableLiveData<ArrayList<StepData>> getData() {
        return jsonData;
    }

    public void storeStepData(String stepData) throws JSONException {
        mStepData = stepData;

        StepData step_data = StepUtils.getStepData(stepData);
        step_data.getStart();


        StepTable stepTable = new StepTable(
                step_data.getStart(),
                step_data.getEnd(),
                step_data.getStep());
        new StepRepository.insertAsyncTask(mStepDao).execute(stepTable);
    }

    public void fetchStepData() {
        new StepRepository.fetchAsyncTask(mStepDao, this).execute();
    }

    private static class insertAsyncTask extends AsyncTask<StepTable,Void,Void> {
        private StepDao mAsyncTaskDao;

        insertAsyncTask(StepDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(StepTable... stepTables) {
            mAsyncTaskDao.insert(stepTables[0]);
            return null;
        }
    }

    private static class fetchAsyncTask extends AsyncTask<Void, Void, List<StepTable>> {
        private StepDao mAsyncTaskDao;
        private WeakReference<StepRepository> mRepoWReference;

        fetchAsyncTask(StepDao dao, StepRepository repo) {
            mAsyncTaskDao = dao;
            mRepoWReference = new WeakReference(repo);
        }

        @Override
        protected List<StepTable> doInBackground(Void... voids) {
            List<StepTable> stepTable = mAsyncTaskDao.getData();

            return stepTable;
        }

        @Override
        protected void onPostExecute(List<StepTable> stepTables) {
            try {
                ArrayList<StepData> stepDataList = new ArrayList<>();

                for (StepTable stepTable : stepTables) {
                    StepData stepData = new StepData();

                    stepData.setStart(stepTable.getStart());
                    stepData.setEnd(stepTable.getEnd());
                    stepData.setStep(stepTable.getStep());

                    stepDataList.add(stepData);
                }

                StepRepository localWRvar = mRepoWReference.get();
                localWRvar.jsonData.setValue(stepDataList);
            } catch (Exception e) {

            }
        }
    }
}
