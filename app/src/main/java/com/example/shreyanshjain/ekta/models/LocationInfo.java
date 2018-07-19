package com.example.shreyanshjain.ekta.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationInfo {

    Double latitude;
    Double longitude;
    long timestamp;

    public LocationInfo() {
    }

    public LocationInfo(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        Calendar calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();

//        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
//        date = simpledateformat.format(calendar.getTime());
//        SimpleDateFormat simpleTimeformat = new SimpleDateFormat("HH:mm:ss");
//        time = simpleTimeformat.format(calendar.getTime());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
