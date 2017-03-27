package com.nano.movies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nano.movies.R;
import com.nano.movies.data.MoviesContract.ReviewEntry;
import com.nano.movies.model.MovieVideo;
import com.nano.movies.model.Review;
import com.nano.movies.model.ReviewsResult;
import com.nano.movies.rest.MovieApiClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Luis Gomez on 3/15/2017.
 */

public class ReviewsService extends IntentService {

    private final String LOG_TAG = ReviewsService.class.getSimpleName();

    public ReviewsService() {
        super(ReviewsService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        int movieId = intent.getIntExtra(context.getString(R.string.movie_id_extra_key), MovieVideo.NO_MOVIE_ID);
        if (movieId != MovieVideo.NO_MOVIE_ID) {
            Cursor cursor = getContentResolver().query(ReviewEntry.CONTENT_URI,
                    new String[]{ ReviewEntry._ID },
                    ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(movieId)},
                    null);
            if (!cursor.moveToFirst()) {
                downloadReviews(context, movieId);
            } else {
                Log.d(LOG_TAG, "Reviews have already been downloaded for this movie");
            }
        } else {
            Log.d(LOG_TAG, "No movie id provided. Service stopping...");
        }
    }

    private void downloadReviews(Context context, int movieId) {
        String baseUrl = context.getString(R.string.movies_base_url);
        String apiKey = context.getString(R.string.movies_api_key);

        Gson gson = new GsonBuilder()
                .setDateFormat(context.getString(R.string.dates_rest_api_format)).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieApiClient service = retrofit.create(MovieApiClient.class);
        Call<ReviewsResult> call = service.getReviews(movieId, apiKey);
        try {
            Response<ReviewsResult> response = call.execute();
            if (response.isSuccessful()) {
                List<Review> reviews = response.body().getReviews();
                ContentValues[] values = new ContentValues[reviews.size()];
                for (int i = 0; i < reviews.size(); i++) {
                    Review review = reviews.get(i);
                    ContentValues cv = new ContentValues();
                    //TODO: set contentValues
                    values[i] = cv;
                }
                getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, values);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting reviews", e);
        }
    }
}