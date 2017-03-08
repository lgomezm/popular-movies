package com.nano.movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Luis Gomez on 1/4/2017.
 */

public class MoviesResult {

    @SerializedName("results")
    private List<Movie> movies;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("page")
    private int currentPage;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
