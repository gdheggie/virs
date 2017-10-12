package com.example.gheggie.virs;


import java.util.ArrayList;

class Poet {

    private final String username;
    private final String userid;
    private final ArrayList<String> poems;

    Poet(String user, String id, ArrayList<String> _poems) {
        username = user;
        userid = id;
        poems = _poems;
    }

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }

    public ArrayList<String> getPoems() {
        return poems;
    }
}
