package com.example.gheggie.virs;

import java.io.Serializable;

class Venue implements Serializable{

    private String venueTitle;
    private String venueLocation;
    private String venueVenueName;
    private String venueStartTime;
    private String venueLogo;

    Venue(String name, String title, String location, String startTime, String logo){
        venueVenueName = name;
        venueTitle = title;
        venueLocation = location;
        venueStartTime = startTime;
        venueLogo = logo;
    }

    public String getVenueName() {
        return venueVenueName;
    }

    public String getVenueTitle() {
        return venueTitle;
    }

    public String getVenueLocation() {
        return venueLocation;
    }

    public String getVenueTime(){
        return venueStartTime;
    }

    public String getVenueLogo() {
        return venueLogo;
    }
}
