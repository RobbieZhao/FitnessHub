package com.example.lifestyle.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lifestyle.R;
import com.example.lifestyle.data.StepData;
import com.example.lifestyle.db.StepDao;

import java.util.ArrayList;
import java.util.List;

public class StepHistoryAdapter extends RecyclerView.Adapter<StepHistoryAdapter.ViewHolder> {

    private ArrayList<StepData> mData;
    private Context mContext;

    // data is passed into the constructor
    StepHistoryAdapter(ArrayList<StepData> data) {
        this.mData = data;
    }

//    public void insert(StepData stepData) {
//        mData.add(stepData);
//    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.stephistory_row,parent,false);
        return new ViewHolder(myView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StepData stepData = mData.get(position);

        holder.mTvStart.setText(Utils.toDate(stepData.getStart()));
        holder.mTvEnd.setText(Utils.toDate(stepData.getEnd()));
        holder.mTvStep.setText(String.valueOf(stepData.getStep()));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected View itemLayout;
        protected TextView mTvStart;
        protected TextView mTvEnd;
        protected TextView mTvStep;

        ViewHolder(View view) {
            super(view);
            itemLayout = view;
            mTvStart = view.findViewById(R.id.tv_start);
            mTvEnd = view.findViewById(R.id.tv_end);
            mTvStep = view.findViewById(R.id.tv_step);
        }
    }
}
