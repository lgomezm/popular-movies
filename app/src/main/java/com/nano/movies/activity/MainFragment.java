package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nano.movies.R;
import com.nano.movies.adapter.MovieListAdapter;
import com.nano.movies.model.Movie;
import com.nano.movies.task.FetchMoviesTask;

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
        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(getString(R.string.movie_extra_key), movie);
                startActivity(intent);
            }
        });
        gridMovies.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        FetchMoviesTask weatherTask = new FetchMoviesTask(adapter);
        weatherTask.execute(new Integer(1));
    }
}
