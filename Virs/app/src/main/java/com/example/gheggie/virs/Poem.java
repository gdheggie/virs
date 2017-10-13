package com.example.gheggie.virs;

import java.io.Serializable;

class Poem implements Serializable {

    private final String title;
    private final String poem;
    private int snapCount;

    Poem(String _title, String _poem,int _snaps) {
        title = _title;
        poem = _poem;
        snapCount = _snaps;
    }

    public String getTitle() {
        return title;
    }

    public String getPoem() {
        return poem;
    }

    public int getSnapCount() {
        return snapCount;
    }
}
