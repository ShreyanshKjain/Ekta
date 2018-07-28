package com.example.shreyanshjain.ekta.models;

import com.google.gson.annotations.SerializedName;

public class NotificationList {
    @SerializedName("location")
    LocationInfo locationInfo;

    @SerializedName("from")
    String from;

    @SerializedName("token_id")
    String token;

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public String getFrom() {
        return from;
    }

    public String getToken() {
        return token;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
