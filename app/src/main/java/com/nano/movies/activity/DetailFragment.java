package com.nano.movies.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nano.movies.R;
import com.nano.movies.data.MoviesContract.MovieEntry;
import com.nano.movies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis Gomez on 3/26/2017.
 */

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private Movie movie;

    @BindView(R.id.movie_title_textview)
    TextView titleView;
    @BindView(R.id.movie_overview_textview)
    TextView overviewView;
    @BindView(R.id.movie_imageview)
    ImageView imageView;
    @BindView(R.id.movie_vote_average)
    RatingBar voteAvgBar;
    @BindView(R.id.movie_release_date_textview)
    TextView releaseDateView;
    @BindView(R.id.favorite_button)
    ToggleButton favoriteButton;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        String movieExtraKey = getString(R.string.movie_extra_key);
        if (null != intent && intent.hasExtra(movieExtraKey)) {
            movie = intent.getParcelableExtra(movieExtraKey);

            ButterKnife.bind(this, rootView);
            LayerDrawable stars = (LayerDrawable) voteAvgBar.getProgressDrawable();

            // Set ratingBar colors to yellow/gray.
            stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

            titleView.setText(movie.getTitle());
            overviewView.setText(movie.getOverview());
            Picasso.with(getActivity())
                    .load(getString(R.string.images_base_url)+ movie.getImagePath())
                    .into(imageView);
            if (movie.getVoteAverage() != 0.0f) {
                voteAvgBar.setRating(movie.getVoteAverage() / 2);
            }
            if (null != movie.getReleaseDate()) {
                releaseDateView.setText("Released: " +
                        DateFormat.format(getString(R.string.release_date_display_format),
                                movie.getReleaseDate()));
            }

            favoriteButton.setChecked(movie.isFavorite());
            favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), getResDrawable()));
            favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        markAsFavorite();
                    } else {
                        unmarkAsFavorite();
                    }
                    movie.setFavorite(isChecked);
                    favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), getResDrawable()));
                }
            });

        } else {
            Log.d(LOG_TAG, "No movie extra from intent");
        }
        return rootView;
    }

    private int getResDrawable() {
        int resDrawable;
        if (movie.isFavorite()) {
            resDrawable = R.drawable.favorite;
        } else {
            resDrawable = R.drawable.non_favorite;
        }
        return resDrawable;
    }


    public void markAsFavorite() {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
        cv.put(MovieEntry.COLUMN_VOTE_AVG, movie.getVoteAverage());
        cv.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MovieEntry.COLUMN_POSTER_PATH, movie.getImagePath());
        getContext().getContentResolver().insert(MovieEntry.CONTENT_URI, cv);
    }

    public void unmarkAsFavorite() {
        getContext().getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { String.valueOf(movie.getId()) });
    }
}
