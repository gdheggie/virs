package com.example.gheggie.virs;

import java.io.Serializable;
import java.util.ArrayList;

class Poet implements Serializable {

    private String username;
    private String userId;
    private String userIcon;
    private ArrayList<String> poems;
    private ArrayList<String> snappedPoems;

    Poet(){}

    Poet(String user, String id, String icon, ArrayList<String> _poems, ArrayList<String> _snappedPoems) {
        username = user;
        userId = id;
        userIcon = icon;
        poems = _poems;
        snappedPoems = _snappedPoems;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public ArrayList<String> getPoems() {
        return poems;
    }

    public void setPoems(ArrayList<String> poems) {
        this.poems = poems;
    }

    public ArrayList<String> getSnappedPoems() {
        return snappedPoems;
    }

    public void setSnappedPoems(ArrayList<String> snappedPoems) {
        this.snappedPoems = snappedPoems;
    }
}
