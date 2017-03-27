package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.nano.movies.R;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MovieVideo;
import com.nano.movies.service.ReviewsService;
import com.nano.movies.service.TrailersService;

/**
 * Created by Luis Gomez on 3/15/2017.
 */

public class ReviewsActivity extends ActionBarActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reviews_container, new ReviewsFragment())
                    .commit();
        }
        String movieKey = getString(R.string.movie_extra_key);
        movie = getIntent().getParcelableExtra(movieKey);
        if (movie != null) {
            Intent mServiceIntent = new Intent(this, ReviewsService.class);
            String movieIdKey = getString(R.string.movie_id_extra_key);
            mServiceIntent.putExtra(movieIdKey, movie.getId());
            startService(mServiceIntent);
        }
    }
}
