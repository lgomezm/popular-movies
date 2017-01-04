package com.nano.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Luis on 1/2/17.
 */

public class MainFragment extends Fragment {

    private MovieListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MovieListAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridMovies = (GridView) rootView.findViewById(R.id.grid_movies);
        gridMovies.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();
    }

    private void fetchMovies() {
        FetchMoviesTask weatherTask = new FetchMoviesTask(adapter,
                getString(R.string.movies_api_key));
        weatherTask.execute(new Integer(1));
    }
}
