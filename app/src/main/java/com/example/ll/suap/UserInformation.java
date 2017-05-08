package com.example.ll.suap;

public class UserInformation {
    public String userId;
    public String email;
    public String name;
    public String phone;
    public String makeModel;
    public String year;
    public String color;
    public String permit;
    //for matching
    public Long timestamp;
    public Boolean matched;
    public String match;
    public double latitude;
    public double longitude;

    // Removed password from here as Firebase Auth will handle that
    // By retrieving the user id, we can uniquely identify the user in database


    public UserInformation(String userId, String email, String name, String phone, String makeModel, String year, String color, String permit, Long timestamp, Boolean matched, String match, double latitude, double longitude) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.makeModel = makeModel;
        this.year = year;
        this.color = color;
        this.permit = permit;
        this.timestamp = timestamp;
        this.matched = matched;
        this.match = match;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserInformation() {

    }
}
