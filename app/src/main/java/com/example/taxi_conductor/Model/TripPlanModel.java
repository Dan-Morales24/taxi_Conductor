package com.example.taxi_conductor.Model;

public class TripPlanModel {


    private String menuDrawer;
    private String rider,driver;
    private DriverModel driverModel;
    private RiderModel riderModel;
    private String origin,originString;
    private String destination, destinationString;
    private String distancePickup, distanceDestination;
    private String durationPickup,durationDestination;
    private double currentLat, currentLng;
    private boolean inDone, isCancel;

    public TripPlanModel() {
    }


    public String getMenuDrawer() {
        return menuDrawer;
    }

    public void setMenuDrawer(String menuDrawer) {
        this.menuDrawer = menuDrawer;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public DriverModel getDriverModel() {
        return driverModel;
    }

    public void setDriverModel(DriverModel driverModel) {
        this.driverModel = driverModel;
    }

    public RiderModel getRiderModel() {
        return riderModel;
    }

    public void setRiderModel(RiderModel riderModel) {
        this.riderModel = riderModel;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginString() {
        return originString;
    }

    public void setOriginString(String originString) {
        this.originString = originString;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationString() {
        return destinationString;
    }

    public void setDestinationString(String destinationString) {
        this.destinationString = destinationString;
    }

    public String getDistancePickup() {
        return distancePickup;
    }

    public void setDistancePickup(String distancePickup) {
        this.distancePickup = distancePickup;
    }

    public String getDistanceDestination() {
        return distanceDestination;
    }

    public void setDistanceDestination(String distanceDestination) {
        this.distanceDestination = distanceDestination;
    }

    public String getDurationPickup() {
        return durationPickup;
    }

    public void setDurationPickup(String durationPickup) {
        this.durationPickup = durationPickup;
    }

    public String getDurationDestination() {
        return durationDestination;
    }

    public void setDurationDestination(String durationDestination) {
        this.durationDestination = durationDestination;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public boolean isInDone() {
        return inDone;
    }

    public void setInDone(boolean inDone) {
        this.inDone = inDone;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }
}
