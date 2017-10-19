package com.example.gheggie.virs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class VirsUtils {

    private static final String FILE_NAME = "poet.txt";

    static final String NEW_POEM = "virs.heggie.greg.NEW-POEM-INTENT";
    static final String FEED_POEM = "virs.heggie.greg.FEED-POEM-INTENT";
    static final String USER_POEM = "virs.heggie.greg.USER-POEM-INTENT";
    static final String SNAPPED_POEM = "virs.heggie.greg.SNAPPED-POEM-INTENT";
    static final String USER_CLICKED = "virs.heggie.greg.USER-CLICKED-INTENT";

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

    static void snapChange(Context context, ImageButton imgButton, TextView txtView) {
        if(imgButton.getTag().equals(R.drawable.snap)){
            imgButton.setImageResource(R.drawable.snapped);
            imgButton.setTag(R.drawable.snapped);
            txtView.setTextColor(ContextCompat.getColor(context, R.color.virsPurple));
        } else {
            imgButton.setImageResource(R.drawable.snap);
            imgButton.setTag(R.drawable.snap);
            txtView.setTextColor(ContextCompat.getColor(context, R.color.blackColor));
        }
    }

    static void shareChange(Context context, ImageButton imgButton, TextView txtView) {
        if (imgButton.getTag().equals(R.drawable.twittershare)) {
            imgButton.setImageResource(R.drawable.twittershared);
            imgButton.setTag(R.drawable.twittershared);
            txtView.setTextColor(ContextCompat.getColor(context, R.color.twitterColor));
        } else {
            imgButton.setImageResource(R.drawable.twittershare);
            imgButton.setTag(R.drawable.twittershare);
            txtView.setTextColor(ContextCompat.getColor(context, R.color.blackColor));
        }
    }
}
