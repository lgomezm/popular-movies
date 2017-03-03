package com.nano.movies.loader;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.nano.movies.R;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Context context = getContext();
        String baseUrl = context.getString(R.string.movies_base_url);
        String apiKey = context.getString(R.string.movies_api_key);
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_movies_by_key),
                context.getString(R.string.pref_movies_by_popular));
        try {
            String moviesJsonStr = null;
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendPath(sortOrder)
                    .appendQueryParameter("page", String.valueOf(pageNumber))
                    .appendQueryParameter("api_key", apiKey).build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (null != inputStream) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                if (buffer.length() > 0) {
                    moviesJsonStr = buffer.toString();
                }
            }
            MoviesResult result = getMoviesResultFromJson(moviesJsonStr);
            result.setCurrentPage(pageNumber);
            return result;
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private MoviesResult getMoviesResultFromJson(String moviesJsonStr) throws JSONException {
        if (null != moviesJsonStr) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJson = moviesArray.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(movieJson.getString("original_title"));
                movie.setOverview(movieJson.getString("overview"));
                movie.setVoteAverage((float) movieJson.getDouble("vote_average"));
                movie.setImagePath(movieJson.getString("poster_path"));
                movie.setReleaseDate(getReleaseDateFromJson(movieJson, format));

                movies.add(movie);
            }
            MoviesResult result = new MoviesResult();
            result.setMovies(movies);
            result.setTotalPages(moviesJson.getInt("total_pages"));
            return result;
        } else {
            return null;
        }
    }

    private Date getReleaseDateFromJson(JSONObject movieJson, DateFormat format)
            throws JSONException {
        String releaseDateStr = movieJson.getString("release_date");
        Date releaseDate = null;
        try {
            releaseDate = format.parse(releaseDateStr);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing release date: " + releaseDateStr, e);
        }
        return releaseDate;
    }
}
