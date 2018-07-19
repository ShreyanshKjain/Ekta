package com.example.shreyanshjain.ekta.models;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    @SerializedName("title")
        String title;
    @SerializedName("body")
        String body;
    @SerializedName("image")
        String image;
    @SerializedName("timestamp")
        String timeStamp;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
