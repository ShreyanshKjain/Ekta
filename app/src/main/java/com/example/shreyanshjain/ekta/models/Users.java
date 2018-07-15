package com.example.shreyanshjain.ekta.models;

public class Users {

    String user_id;
    LocationInfo location;
    String token_id;
    boolean flag;

    public Users() {
    }

    public Users(String user_id, LocationInfo location, String token_id, boolean flag) {
        this.user_id = user_id;
        this.location = location;
        this.token_id = token_id;
        this.flag = flag;
    }

    public String getUser_id() {
        return user_id;
    }

    public LocationInfo getLocation() {
        return location;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }


}
