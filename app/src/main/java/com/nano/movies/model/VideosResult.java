package com.nano.movies.model;


import java.util.List;

/**
 * Created by Luis Gomez on 3/14/2017.
 */

public class VideosResult {

    private int id;
    private List<MovieVideo> results;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public List<MovieVideo> getResults() {
        return results;
    }
    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }
}
