package com.menposun.calambawonders;

public class PlanSetters {
    private String place_title, place_type, date_plan;

    private PlanSetters(){
        //required empty private contructors to avoid errors as seen in the logcat
        //i don't know why (jsut newbie)
    }

    public PlanSetters(String place_title, String place_type, String date_plan) {
        this.place_title = place_title;
        this.place_type = place_type;
        this.date_plan = date_plan;
    }

    public String getDate_plan() {
        return date_plan;
    }

    public void setDate_plan(String date_plan) {
        this.date_plan = date_plan;
    }

    public PlanSetters(String place_title, String place_type){
        this.place_title = place_title;
        this.place_type = place_type;
    }

    public String getPlace_title() {
        return place_title;
    }

    public void setPlace_title(String place_title) {
        this.place_title = place_title;
    }

    public String getPlace_type() {
        return place_type;
    }

    public void setPlace_type(String place_type) {
        this.place_type = place_type;
    }
}
