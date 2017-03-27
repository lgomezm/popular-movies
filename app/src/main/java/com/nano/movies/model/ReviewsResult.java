package com.nano.movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Luis Gomez on 3/15/2017.
 */

public class ReviewsResult {

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<Review> reviews;
    @SerializedName("total_results")
    private int totalResults;

    public int getMovieId() {
        return movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    public int getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
