package com.menposun.calambawonders;

public class ReviewSetter {

    String r_nickname, r_msg, r_profile_pic ,r_rating, r_id, r_date;

    private ReviewSetter (){
        
    }


    public ReviewSetter(String r_nickname, String r_rating, String r_msg, String r_profile_pic, String r_id,  String r_date){
        this.r_nickname = r_nickname;
        this.r_profile_pic = r_profile_pic;
        this.r_rating = r_rating;
        this.r_msg = r_msg;
        this.r_id = r_id;
        this.r_date = r_date;
    }
    public String getR_date() {
        return r_date;
    }

    public void setR_date(String r_date) {
        this.r_date = r_date;
    }

    public String getR_nickname() {
        return r_nickname;
    }

    public void setR_nickname(String r_nickname) {
        this.r_id = r_nickname;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getR_msg() {
        return r_msg;
    }

    public void setR_msg(String r_msg) {
        this.r_msg = r_msg;
    }

    public String getR_profile_pic() {
        return r_profile_pic;
    }

    public void setR_profile_pic(String r_profile_pic) {
        this.r_profile_pic = r_profile_pic;
    }

    public String getR_rating() {
        return r_rating;
    }

    public void setR_rating(String r_rating) {
        this.r_rating = r_rating;
    }

}
