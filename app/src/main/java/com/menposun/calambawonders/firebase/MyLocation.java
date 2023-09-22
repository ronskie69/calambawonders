package com.menposun.calambawonders.firebase;

public class MyLocation {
    private double my_lat;
    private double my_long;

    public void setMy_lat(double my_lat){
        this.my_lat = my_lat;
    }
    public void setMy_long(double my_long){
        this.my_long = my_long;
    }
    public double getMy_lat(){
        return this.my_lat;
    }
    public double getMy_long(){
        return this.my_long;
    }
}