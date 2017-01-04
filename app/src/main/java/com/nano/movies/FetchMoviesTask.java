package com.nano.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luis Gomez on 1/3/2017.
 */

public class FetchMoviesTask extends AsyncTask<Integer, Void, MoviesResult> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MovieListAdapter adapter;
    private String apiKey;

    public FetchMoviesTask(MovieListAdapter adapter, String apiKey) {
        this.adapter = adapter;
        this.apiKey = apiKey;
    }

    @Override
    protected MoviesResult doInBackground(Integer... objects) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            int pageNumber = objects[0];
            String moviesJsonStr = null;
            Uri builtUri = Uri.parse("https://api.themoviedb.org/3/movie/popular?").buildUpon()
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
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJson = moviesArray.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(movieJson.getString("original_title"));
                movie.setImageUrl(movieJson.getString("poster_path"));

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

    @Override
    protected void onPostExecute(MoviesResult moviesResult) {
        adapter.updateMovies(moviesResult);
    }
}