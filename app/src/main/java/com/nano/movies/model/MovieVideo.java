package com.nano.movies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Luis Gomez on 3/12/2017.
 */

public class MovieVideo {

    public final static int NO_MOVIE_ID = -1;

    @SerializedName("key")
    private String videoId;
    private String name;
    private int size;

    public String getVideoId() {
        return videoId;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
}
