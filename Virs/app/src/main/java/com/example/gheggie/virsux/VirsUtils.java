package com.example.gheggie.virsux;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;

class VirsUtils {

    static final String NEW_POEM = "virsux.heggie.greg.NEW-POEM-INTENT";
    static final String EDIT_POEM = "virsux.heggie.greg.EDIT-POEM-INTENT";
    static final String FEED_POEM = "virsux.heggie.greg.FEED-POEM-INTENT";
    static final String USER_POEM = "virsux.heggie.greg.USER-POEM-INTENT";
    static final String SNAPPED_POEM = "virsux.heggie.greg.SNAPPED-POEM-INTENT";
    static final String USER_CLICKED = "virsux.heggie.greg.USER-CLICKED-INTENT";
    static final String USER_ID = "virsux.heggie.greg.USER-ID";
    static final String EDIT_PROFILE = "virsux.heggie.greg.EDIT-PROFILE-INTENT";
    static final String USER_WITHIN = "virsux.heggie.greg.WITHIN-MILES-INTENT";
    static final String VENUE_CLICK = "virsux.heggie.greg.VENUE-CLICK-INTENT";

    static Poet currentPoet = new Poet();

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
