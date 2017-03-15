package com.nano.movies.rest;

import com.nano.movies.model.MovieVideo;
import com.nano.movies.model.MoviesResult;
import com.nano.movies.model.VideosResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Luis Gomez on 3/7/2017.
 */

public interface MovieApiClient {

    @GET("movie/{sortOrder}")
    Call<MoviesResult> getMovies(@Path("sortOrder") String sortOrder,
                                 @Query("page") int page,
                                 @Query("api_key") String apiKey);

    @GET("movie/{movieId}/videos")
    Call<VideosResult> getVideoList(@Path("movieId") int movieId,
                                    @Query("api_key") String apiKey);
}
