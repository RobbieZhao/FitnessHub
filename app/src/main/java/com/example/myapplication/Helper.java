package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean saveImage(Bitmap finalBitmap, String directory, String filename) {
        try {
            File file = new File(directory, filename);

            if (file.exists()) file.delete();

            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean storeData(HashMap<String, String> hashMap, String directory, String filename) {
        try {
            File file = new File(directory, filename);

            if (file.exists()) file.delete();

            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(hashMap);
            out.close();
            fileOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HashMap<String, String> readData(String directory, String data_file) {
        try {
            File file = new File(directory, data_file);

            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            HashMap<String, String> data = (HashMap) in.readObject();
            in.close();
            fileIn.close();

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static int calculateTotalInches(int foot, int inch) {
        return foot * 12 + inch;
    }

    public static int getIntegerInput(EditText editText) {
        String str = editText.getText().toString();

        if (str.isEmpty())
            return -1;

        return Integer.parseInt(str);
    }

    public static float getFloatInput(EditText editText) {
        String str = editText.getText().toString();

        if (str.isEmpty()) {
            return -1;
        }

        return Float.parseFloat(str);
    }

    public static float calculateBMI(float weight, int inch) {
        return 703 * weight / inch / inch;
    }

    public static double calculateBMR(float weight, int inch, int age, boolean isMale) {
        if (isMale)
            return 66 + 6.2 * weight + 12.7 * inch - 6.76 * age;
        else
            return 655 + 4.35 * weight + 4.7 * inch - 4.7 * age;
    }
}
