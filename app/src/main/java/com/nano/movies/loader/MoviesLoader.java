package com.nano.movies.loader;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nano.movies.R;
import com.nano.movies.model.MoviesResult;
import com.nano.movies.rest.MovieApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Luis Gomez on 3/2/2017.
 */

public class MoviesLoader extends AsyncTaskLoader<MoviesResult> {

    private final String LOG_TAG = MoviesLoader.class.getSimpleName();

    private int pageNumber;

    public MoviesLoader(Context context, int pageNumber) {
        super(context);
        this.pageNumber = pageNumber;
    }

    @Override
    public MoviesResult loadInBackground() {
        Context context = getContext();
        String baseUrl = context.getString(R.string.movies_base_url);
        String apiKey = context.getString(R.string.movies_api_key);
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_movies_by_key),
                context.getString(R.string.pref_movies_by_popular));

        Gson gson = new GsonBuilder()
                .setDateFormat(context.getString(R.string.dates_rest_api_format)).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MoviesResult result = null;
        MovieApiClient service = retrofit.create(MovieApiClient.class);
        Call<MoviesResult> call = service.getMovies(sortOrder, pageNumber, apiKey);
        try {
            Response<MoviesResult> response = call.execute();
            if (response.isSuccessful()) {
                result = response.body();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting movies", e);
        }
        return result;
    }
}
