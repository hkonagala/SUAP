package com.example.ll.suap;


import java.sql.Timestamp;

/**
 * Created by Ben on 5/3/2017.
 *
 * inteneded to act as a way to interface with the DB for purposes of
 * matching users
 */



public class ActiveUser {
    public enum UserType{
        Driver,
        Rider
    }


    //all from UserInformation
    public String userId;
    public String name;
    public String phone;
    public String makeModel;
    public String year;
    public String color;
    public String permit;

    //specific to active user
    UserType myType;
    Timestamp time;

    public ActiveUser(String userId, String name, String phone, String makeModel, String year, String color, String permit, UserType myType, Timestamp time) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.makeModel = makeModel;
        this.year = year;
        this.color = color;
        this.permit = permit;
        this.myType = myType;
        this.time = time;
    }

    public ActiveUser() {
    }
}
