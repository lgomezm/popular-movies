package com.nano.movies.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.nano.movies.R;
import com.nano.movies.model.Movie;

public class DetailActivity extends ActionBarActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_container, new DetailFragment())
                    .commit();
        }

        movie = getMovieFromIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_trailers) {
            if (null != movie) {
                Intent intent = new Intent(this, TrailersActivity.class);
                intent.putExtra(getString(R.string.movie_extra_key), movie);
                startActivity(intent);
                return true;
            }
        } else if (id == R.id.action_reviews) {
            if (null != movie) {
                Intent intent = new Intent(this, ReviewsActivity.class);
                intent.putExtra(getString(R.string.movie_extra_key), movie);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Movie getMovieFromIntent() {
        Intent intent = getIntent();
        String movieExtraKey = getString(R.string.movie_extra_key);
        if (null != intent && intent.hasExtra(movieExtraKey)) {
            return intent.getParcelableExtra(movieExtraKey);
        }
        return null;
    }
}
