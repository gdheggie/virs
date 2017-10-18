package com.example.gheggie.virs;

import android.net.Uri;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

class Poet implements Serializable {

    private final String username;
    private final String userId;
    //private final Uri userIcon;
    private ArrayList<String> poems;
    private ArrayList<String> snappedPoems;

    Poet(String user, String id, ArrayList<String> _poems, ArrayList<String> _snappedPoems) {
        username = user;
        userId = id;
        poems = _poems;
        snappedPoems = _snappedPoems;
    }

//    Poet(String user, String id, Uri icon, ArrayList<String> _poems, ArrayList<String> _snappedPoems) {
//        username = user;
//        userId = id;
//        userIcon = icon;
//        poems = _poems;
//        snappedPoems = _snappedPoems;
//    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

//    public Uri getUserIcon() {
//        return userIcon;
//    }

    public ArrayList<String> getPoems() {
        return poems;
    }

    public ArrayList<String> getSnappedPoems() {
        return snappedPoems;
    }
}
