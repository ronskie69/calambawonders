package com.menposun.calambawonders;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class NearestLocation {

    private String title;
    private double distance;
    private String type;
    private LatLng latLng;

    public NearestLocation(String title, String type, LatLng latlng, double distance){
        this.title = title;
        this.type = type;
        this.distance = distance;
        this.latLng = new LatLng(latlng);
    }
    public void setLatLng(LatLng latlng){
        this.latLng = latLng;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }

    public String getInfo(){ return this.type; }
    public String getTitle() {
        return this.title;
    }

    public void setInfo(String type) { this.type = type; }
    public void setTitle(String title) {
        this.title = title;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
