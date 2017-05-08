package com.example.ll.suap;

/**
 * Created by Harika Konagala on 5/7/2017.
 */

public class Ride {

    public enum ride_status{
        completed,//ride completed
        missed,//ride missed
        ongoing//on going ride
    }

    public String passengerId;
    public String driverId;
    public String rideId;
    public ride_status status;

    public Ride(String passengerId, String driverId, String rideId, Ride.ride_status status) {
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.rideId = rideId;
        this.status = status;
    }

    public Ride() {
    }
}
