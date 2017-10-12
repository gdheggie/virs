package com.example.gheggie.virs;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VirsUtils {

    private static final String FILE_NAME = "gait.txt";

    static void savePoet(Context context, Poet poet) {

        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(poet);
            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static Poet loadGait(Context context) {
        Poet poet = null;

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            poet = (Poet) ois.readObject();
            ois.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return poet;
    }
}
