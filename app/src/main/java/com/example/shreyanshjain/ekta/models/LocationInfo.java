package com.example.shreyanshjain.ekta.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationInfo {

    Double latitude;
    Double longitude;
    String time;
    String date;

    public LocationInfo(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
