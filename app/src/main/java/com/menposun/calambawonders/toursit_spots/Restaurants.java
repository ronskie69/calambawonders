package com.menposun.calambawonders.toursit_spots;

public class Restaurants {
    private String imageUri;
    private String information;
    private String placeTitle;
    private int reviewsCount;
    private String reviews;

    public Restaurants(String imageUri, String information, String placeTitle, int reviewsCount, String reviews){
        this.imageUri = imageUri;
        this.information = information;
        this.placeTitle = placeTitle;
        this.reviewsCount = reviewsCount;
        this.reviews = reviews;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getPlaceTitle() {
        return placeTitle;
    }

    public void setPlaceTitle(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
}
