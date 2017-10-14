package com.example.gheggie.virs;

import java.io.Serializable;

class Poem implements Serializable {

    private final String title;
    private final String poem;
    private final String poet;
    private int snapCount;

    Poem(String _title, String _poem,String _poet, int _snaps) {
        title = _title;
        poem = _poem;
        poet = _poet;
        snapCount = _snaps;
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

    public int getSnapCount() {
        return snapCount;
    }
}
