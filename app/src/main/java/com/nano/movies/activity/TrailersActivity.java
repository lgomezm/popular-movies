package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.nano.movies.R;
import com.nano.movies.model.MovieVideo;
import com.nano.movies.service.TrailersService;

/**
 * Created by Luis Gomez on 3/13/2017.
 */
public class TrailersActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.trailers_container, new TrailersFragment())
                    .commit();
        }
        String movieIdKey = getString(R.string.movie_id_extra_key);
        int movieId = getIntent().getIntExtra(movieIdKey, MovieVideo.NO_MOVIE_ID);
        if (movieId != MovieVideo.NO_MOVIE_ID) {
            Intent mServiceIntent = new Intent(this, TrailersService.class);
            mServiceIntent.putExtra(movieIdKey, movieId);
            startService(mServiceIntent);
        }
    }
}
