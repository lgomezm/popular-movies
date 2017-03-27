package com.nano.movies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nano.movies.R;
import com.nano.movies.activity.MainFragment;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Luis Gomez on 1/3/2017.
 */

public class MovieListAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private final Context context;
    private final MainFragment fragment;
    private final List<Movie> movies;
    private int totalPages;
    private int maxPageReached;

    public MovieListAdapter(Context context, MainFragment fragment, List<Movie> movies) {
        super(context, -1, movies);
        this.context = context;
        this.fragment = fragment;
        this.movies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (null != convertView) {
            imageView = (ImageView) convertView;
        } else {
            imageView = new ImageView(getContext());
        }
        String imageUrl = context.getString(R.string.images_base_url) + movies.get(position).getImagePath();
        Picasso.with(context).load(imageUrl).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (movies.size() - 1 == position && maxPageReached < totalPages) {
            Log.d(LOG_TAG, "Fetching more movies!");
            fragment.restartLoader();
        }
        return imageView;
    }

    public void updateMovies(MoviesResult moviesResult) {
        if (null != moviesResult && null != moviesResult.getMovies()) {
            totalPages = moviesResult.getTotalPages();
            maxPageReached = moviesResult.getCurrentPage();
            for (Movie movie : moviesResult.getMovies()) {
                add(movie);
            }
        }
    }

    public Context getContext() {
        return context;
    }
}
