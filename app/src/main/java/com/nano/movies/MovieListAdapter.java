package com.nano.movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Luis Gomez on 1/3/2017.
 */

public class MovieListAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private final static String BASE_URL = "http://image.tmdb.org/t/p/w500";

    private final Context context;
    private final List<Movie> movies;
    private int totalPages;
    private int maxPageReached;

    public MovieListAdapter(Context context, List<Movie> movies) {
        super(context, -1, movies);
        this.context = context;
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
        String imageUrl = BASE_URL + movies.get(position).getImageUrl();
        Picasso.with(context).load(imageUrl).into(imageView);
        if (movies.size() - 1 == position && maxPageReached < totalPages) {
            Log.d(LOG_TAG, "Fetching more movies!");
            FetchMoviesTask weatherTask = new FetchMoviesTask(this,
                    context.getString(R.string.movies_api_key));
            weatherTask.execute(maxPageReached + 1);
        }
        return itemView;
    }

    public void updateMovies(MoviesResult moviesResult) {
        totalPages = moviesResult.getTotalPages();
        maxPageReached = moviesResult.getCurrentPage();
        if (null != moviesResult && null != moviesResult.getMovies()) {
            for (Movie movie : moviesResult.getMovies()) {
                add(movie);
            }
        }
    }
}
