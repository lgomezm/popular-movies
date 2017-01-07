package com.nano.movies.model;

import java.util.List;

/**
 * Created by Luis Gomez on 1/4/2017.
 */

public class MoviesResult {

    private List<Movie> movies;
    private int totalPages;
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
