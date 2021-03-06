package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.nano.movies.R;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MovieVideo;
import com.nano.movies.service.TrailersService;

/**
 * Created by Luis Gomez on 3/13/2017.
 */
public class TrailersActivity extends ActionBarActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.trailers_container, new TrailersFragment())
                    .commit();
        }
        String movieKey = getString(R.string.movie_extra_key);
        movie = getIntent().getParcelableExtra(movieKey);
        if (movie != null) {
            Intent mServiceIntent = new Intent(this, TrailersService.class);
            String movieIdKey = getString(R.string.movie_id_extra_key);
            mServiceIntent.putExtra(movieIdKey, movie.getId());
            startService(mServiceIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(getString(R.string.movie_extra_key), movie);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
