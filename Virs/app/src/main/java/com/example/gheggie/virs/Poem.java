package com.example.gheggie.virs;

import java.io.Serializable;

class Poem implements Serializable {

    private final String title;
    private final String poem;
    private final String poet;
    private final String date;
    private final String poemId;
    private int snapCount;

    Poem(String _title, String _poem,String _poet, String _date, String _id, int _snaps) {
        title = _title;
        poem = _poem;
        poet = _poet;
        date = _date;
        poemId = _id;
        snapCount = _snaps;
    }

    @Override
    public String toString() {
        return title + " " + date;
    }

    public String getTitle() {
        return title;
    }

    public String getPoem() {
        return poem;
    }

    public String getPoet() {
        return poet;
    }

    public String getDate() {
        return date;
    }

    public String getPoemId() {
        return poemId;
    }

    public void setSnapCount(int snapCount) {
        this.snapCount = snapCount;
    }

    public int getSnapCount() {
        return snapCount;
    }
}
