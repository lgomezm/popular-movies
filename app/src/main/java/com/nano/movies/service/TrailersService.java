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
import com.nano.movies.data.MoviesContract;
import com.nano.movies.model.MovieVideo;
import com.nano.movies.model.VideosResult;
import com.nano.movies.rest.MovieApiClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Luis Gomez on 3/13/2017.
 */
public class TrailersService extends IntentService {

    private final String LOG_TAG = TrailersService.class.getSimpleName();

    public TrailersService() {
        super(TrailersService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        int movieId = intent.getIntExtra(context.getString(R.string.movie_id_extra_key), MovieVideo.NO_MOVIE_ID);
        if (movieId != MovieVideo.NO_MOVIE_ID) {
            Cursor cursor = getContentResolver().query(MoviesContract.TrailerEntry.CONTENT_URI,
                    new String[] { MoviesContract.TrailerEntry._ID },
                    MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[] { String.valueOf(movieId) },
                    null);
            if (!cursor.moveToFirst()) {
                downloadTrailers(context, movieId);
            } else {
                Log.d(LOG_TAG, "Trailers have already been downloaded for this movie");
            }
        } else {
            Log.d(LOG_TAG, "No movie id provided. Service stopping...");
        }
    }

    private void downloadTrailers(Context context, int movieId) {
        String baseUrl = context.getString(R.string.movies_base_url);
        String apiKey = context.getString(R.string.movies_api_key);

        Gson gson = new GsonBuilder()
                .setDateFormat(context.getString(R.string.dates_rest_api_format)).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieApiClient service = retrofit.create(MovieApiClient.class);
        Call<VideosResult> call = service.getVideoList(movieId, apiKey);
        try {
            Response<VideosResult> response = call.execute();
            if (response.isSuccessful()) {
                List<MovieVideo> videos = response.body().getResults();
                ContentValues[] values = new ContentValues[videos.size()];
                for (int i = 0; i < videos.size(); i++) {
                    MovieVideo video = videos.get(i);
                    ContentValues cv = new ContentValues();
                    cv.put(MoviesContract.TrailerEntry.COLUMN_VIDEO_ID, video.getVideoId());
                    cv.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                    cv.put(MoviesContract.TrailerEntry.COLUMN_VIDEO_NAME, video.getName());
                    cv.put(MoviesContract.TrailerEntry.COLUMN_SIZE, video.getSize());
                    values[i] = cv;
                }
                getContentResolver().bulkInsert(MoviesContract.TrailerEntry.CONTENT_URI, values);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting trailers", e);
        }
    }
}
