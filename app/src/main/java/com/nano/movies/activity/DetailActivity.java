package com.nano.movies.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nano.movies.R;
import com.nano.movies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_container, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        @BindView(R.id.movie_title_textview) TextView titleView;
        @BindView(R.id.movie_overview_textview) TextView overviewView;
        @BindView(R.id.movie_imageview) ImageView imageView;
        @BindView(R.id.movie_vote_average) RatingBar voteAvgBar;
        @BindView(R.id.movie_release_date_textview) TextView releaseDateView;

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
                Movie movie = (Movie) intent.getSerializableExtra(movieExtraKey);

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
            } else {
                Log.d(LOG_TAG, "No movie extra from intent");
            }
            return rootView;
        }
    }
}
