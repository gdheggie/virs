package com.example.gheggie.virs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;

class VirsUtils {

    static final String NEW_POEM = "virs.heggie.greg.NEW-POEM-INTENT";
    static final String EDIT_POEM = "virs.heggie.greg.EDIT-POEM-INTENT";
    static final String FEED_POEM = "virs.heggie.greg.FEED-POEM-INTENT";
    static final String USER_POEM = "virs.heggie.greg.USER-POEM-INTENT";
    static final String SNAPPED_POEM = "virs.heggie.greg.SNAPPED-POEM-INTENT";
    static final String USER_CLICKED = "virs.heggie.greg.USER-CLICKED-INTENT";
    static final String USER_ID = "virs.heggie.greg.USER-ID";
    static final String EDIT_PROFILE = "virs.heggie.greg.EDIT-PROFILE-INTENT";
    static final String USER_WITHIN = "virs.heggie.greg.WITHIN-MILES-INTENT";
    static final String VENUE_CLICK = "virs.heggie.greg.VENUE-CLICK-INTENT";
    static final String STREAM_URL = "virs.heggie.greg.STREAM-URL-INTENT";

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
}
