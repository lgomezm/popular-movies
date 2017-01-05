package com.nano.movies;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String BASE_URL = "http://image.tmdb.org/t/p/w500";

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (null != intent && intent.hasExtra("movie")) {
                Movie movie = (Movie) intent.getSerializableExtra("movie");

                TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_textview);
                TextView overviewView = (TextView) rootView.findViewById(R.id.movie_overview_textview);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_imageview);
                RatingBar voteAvgBar = (RatingBar) rootView.findViewById(R.id.movie_vote_average);
                LayerDrawable stars = (LayerDrawable) voteAvgBar.getProgressDrawable();
                stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

                titleView.setText(movie.getTitle());
                overviewView.setText(movie.getOverview());
                Picasso.with(getActivity()).load(BASE_URL + movie.getImagePath()).into(imageView);
                if (movie.getVoteAverage() != 0.0f) {
                    voteAvgBar.setRating(movie.getVoteAverage() / 2);
                }
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        }
    }
}
