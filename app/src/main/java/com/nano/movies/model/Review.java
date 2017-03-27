package com.nano.movies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Luis Gomez on 3/15/2017.
 */

public class Review {

    @SerializedName("id")
    private String reviewId;
    private String author;
    private String content;

    public String getReviewId() {
        return reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
