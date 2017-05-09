package com.example.ll.suap;


import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

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
    public enum ActiveState {
        online,
        offline
    }

    public enum status{
        hold,//driver in queue
        taken,//driver confirm
        available
    }

    //all from UserInformation
    public String userId;
    public String name;
    public String phone;
    public String makeModel;
    public String year;
    public String color;
    public String permit;
    public double latitude;
    public double longitude;
    public String rideId;

    //specific to active user
    public UserType myType;
    public Long timestamp;
    public ActiveState myState;
    public status status;

    public ActiveUser(String userId, String name, String phone, String makeModel, String year, String color, String permit,
                      double latitude, double longitude, String rideId, UserType myType, Long timestamp,
                      ActiveState myState, ActiveUser.status status) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.makeModel = makeModel;
        this.year = year;
        this.color = color;
        this.permit = permit;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rideId = rideId;
        this.myType = myType;
        this.timestamp = timestamp;
        this.myState = myState;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public UserType getMyType() {
        return myType;
    }

    public void setMyType(UserType myType) {
        this.myType = myType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ActiveState getMyState() {
        return myState;
    }

    public void setMyState(ActiveState myState) {
        this.myState = myState;
    }

    public ActiveUser.status getStatus() {
        return status;
    }

    public void setStatus(ActiveUser.status status) {
        this.status = status;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public ActiveUser() {
    }
}
