package com.example.gheggie.virs;

import android.net.Uri;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

class Poet implements Serializable {

    private final String username;
    private final String userId;
    private final Uri userIcon;
    private final ArrayList<Poem> poems;
    private final ArrayList<Poem> snappedPoems;

    Poet(String user, String id, Uri icon, ArrayList<Poem> _poems, ArrayList<Poem> _snappedPoems) {
        username = user;
        userId = id;
        userIcon = icon;
        poems = _poems;
        snappedPoems = _snappedPoems;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public Uri getUserIcon() {
        return userIcon;
    }

    public ArrayList<Poem> getPoems() {
        return poems;
    }

    public ArrayList<Poem> getSnappedPoems() {
        return snappedPoems;
    }
}
