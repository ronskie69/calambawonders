package com.menposun.calambawonders;

import android.graphics.drawable.Drawable;

public class LocationSetters {

    private double latitude;
    private double longitude;
    private String info;
    private String type;

    public LocationSetters(double latitude, double longitude, String info, String type){
        this.latitude = latitude;
        this.longitude = longitude;
        this.info = info;
        this.type = type;
    }

    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public String getInfo() {
        return this.info;
    }
    public String getType() {
        return this.type;
    }
}
