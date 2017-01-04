package com.nano.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

/**
 * Created by Luis on 1/2/17.
 */

public class MainFragment extends Fragment {

    private MoviesListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MoviesListAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridMovies = (GridView) rootView.findViewById(R.id.grid_movies);
        gridMovies.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask weatherTask = new FetchMoviesTask();
        weatherTask.execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(Void... objects) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            try {
                String moviesJsonStr = null;
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri builtUri = Uri.parse("https://api.themoviedb.org/3/movie/popular?").buildUpon()
                        .appendQueryParameter("page", "1")
                        .appendQueryParameter("api_key", getString(R.string.movies_api_key)).build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
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
                return getMoviesFromJson(moviesJsonStr);
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

        private Movie[] getMoviesFromJson(String moviesJsonStr) throws JSONException {
            if (null != moviesJsonStr) {
                JSONObject moviesJson = new JSONObject(moviesJsonStr);
                JSONArray moviesArray = moviesJson.getJSONArray("results");
                Movie[] movies = new Movie[moviesArray.length()];
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieJson = moviesArray.getJSONObject(i);

                    Movie movie = new Movie();
                    movie.setTitle(movieJson.getString("original_title"));
                    movie.setImageUrl(movieJson.getString("poster_path"));

                    movies[i] = movie;
                }
                return movies;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (null != movies) {
                adapter.clear();
                for (Movie movie : movies) {
                    adapter.add(movie);
                }
            }
        }
    }
}
