package com.example.localservice;

public class Review {
    private String reviewerId;
    private String providerId;
    private String reviewText;
    private float rating;
    private long timestamp;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String reviewerId, String providerId, String reviewText, float rating, long timestamp) {
        this.reviewerId = reviewerId;
        this.providerId = providerId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

