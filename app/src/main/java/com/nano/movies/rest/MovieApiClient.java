package com.nano.movies.rest;

import com.nano.movies.model.MoviesResult;

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
}
