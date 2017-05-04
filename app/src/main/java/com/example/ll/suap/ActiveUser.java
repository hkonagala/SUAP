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
    public String name;
    public String phone;
    public String makeModel;
    public String year;
    public String color;
    public String permit;

    //specific to active user
    UserType myType;
    Timestamp time;


    public ActiveUser(UserInformation me, UserType type) {
        time.setTime(System.currentTimeMillis());
        name=me.name;
        phone=me.phone;
        makeModel=me.makeModel;
        year=me.year;
        color=me.color;
        permit=me.permit;
        myType=type;
    }
}
