package com.example.gheggie.virs;

import java.io.Serializable;

public class Stream implements Serializable {

    private String address;
    private String userIcon;

    Stream(){}

    Stream(String url, String pic) {
        address = url;
        userIcon = pic;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getAddress() {
        return address;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
