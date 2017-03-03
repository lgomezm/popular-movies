package com.nano.movies.adapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.movies.R;
import com.nano.movies.activity.MainFragment;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;
import com.nano.movies.task.FetchMoviesTask;
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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.grid_item_movie, parent, false);
        TextView textView = (TextView) itemView.findViewById(R.id.grid_item_movie_textview);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.grid_item_movie_imageview);
        textView.setText(movies.get(position).getTitle());
        String imageUrl = context.getString(R.string.images_base_url) + movies.get(position).getImagePath();
        Picasso.with(context).load(imageUrl).into(imageView);
        if (movies.size() - 1 == position && maxPageReached < totalPages) {
            Log.d(LOG_TAG, "Fetching more movies!");
            Bundle args = new Bundle();
            args.putInt("page", maxPageReached + 1);
            fragment.getLoaderManager().restartLoader(MainFragment.MOVIES_LOADER, args, fragment).forceLoad();
        }
        return itemView;
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
