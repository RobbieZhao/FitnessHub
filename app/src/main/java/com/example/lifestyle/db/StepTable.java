package com.example.lifestyle.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "step_table")

public class StepTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "start")
    public long start;

    @ColumnInfo(name = "end")
    public long end;

    @ColumnInfo(name = "step")
    public int step;


    public StepTable(@NonNull long start, @NonNull long end, @NonNull int step){
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
