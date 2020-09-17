package com.example.myapplication;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class Helper {
    public static void setRadioGroupStatus(RadioGroup radioGroup, boolean isSelectable) {
        int numberOfChildren = radioGroup.getChildCount();
        for (int i = 0; i < numberOfChildren; i++) {
            radioGroup.getChildAt(i).setEnabled(isSelectable);
        }
    }

    public static void enableRadioGroup(RadioGroup radioGroup) {
        setRadioGroupStatus(radioGroup, true);
    }

    public static void disableRadioGroup(RadioGroup radioGroup) {
        setRadioGroupStatus(radioGroup, false);
    }

    public static void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    public static void enableEditText(ArrayList<EditText> editTexts) {
        int size = editTexts.size();

        for(int i = 0; i < size; i++) {
            enableEditText(editTexts.get(i));
        }
    }

    public static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    public static void disableEditText(ArrayList<EditText> editTexts) {
        int size = editTexts.size();

        for(int i = 0; i < size; i++) {
            disableEditText(editTexts.get(i));
        }
    }


}
