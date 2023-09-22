package com.menposun.calambawonders;

public class PopularitySetters {
    private String city_title;
    private int city_img;
    private float city_rating;
    private String city_rating_val;

    public PopularitySetters(String city_title, int city_img, float city_rating, String city_rating_val){
        this.city_title = city_title;
        this.city_img = city_img;
        this.city_rating = city_rating;
        this.city_rating_val =city_rating_val;
    }

    public String getCity_title() {
        return city_title;
    }

    public void setCity_title(String city_title) {
        this.city_title = city_title;
    }

    public int getCity_img() {
        return city_img;
    }

    public void setCity_img(int city_img) {
        this.city_img = city_img;
    }

    public float getCity_rating() {
        return city_rating;
    }

    public void setCity_rating(float city_rating) {
        this.city_rating = city_rating;
    }

    public String getCity_rating_val() {
        return city_rating_val;
    }

    public void setCity_rating_val(String city_rating_val) {
        this.city_rating_val = city_rating_val;
    }
}
