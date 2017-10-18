package com.example.gheggie.virs;

import android.content.Context;
import android.widget.ImageButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class VirsUtils {

    private static final String FILE_NAME = "poet.txt";

    static final String NEW_POEM = "virs.heggie.greg.NEW-POEM-INTENT";

    static String USERNAME;

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

    static Poet loadPoet(Context context) {
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

    static void snapChange(ImageButton imgButton) {
        if(imgButton.getTag().equals(R.drawable.snap)){
            imgButton.setBackgroundResource(R.drawable.snapped);
            imgButton.setTag(R.drawable.snapped);
        } else {
            imgButton.setBackgroundResource(R.drawable.snap);
            imgButton.setTag(R.drawable.snap);
        }
    }

    static void shareChange(ImageButton imgButton) {
        if (imgButton.getTag().equals(R.drawable.twittershare)) {
            imgButton.setBackgroundResource(R.drawable.twittershared);
            imgButton.setTag(R.drawable.twittershared);
        } else {
            imgButton.setBackgroundResource(R.drawable.twittershare);
            imgButton.setTag(R.drawable.twittershare);
        }
    }
}
