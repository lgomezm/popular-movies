package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nano.movies.R;
import com.nano.movies.adapter.MovieListAdapter;
import com.nano.movies.loader.MoviesLoader;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;

import java.util.ArrayList;

/**
 * Created by Luis on 1/2/17.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<MoviesResult> {

    public static final int MOVIES_LOADER = 0;

    private MovieListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MovieListAdapter(getActivity(), this, new ArrayList<Movie>());

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Bundle args = new Bundle();
        args.putInt("page", 1);
        getLoaderManager().initLoader(MOVIES_LOADER, args, this).forceLoad();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
    }

    @Override
    public Loader<MoviesResult> onCreateLoader(int i, Bundle bundle) {
        int pageNumber = bundle.getInt("page");
        return new MoviesLoader(getActivity(), pageNumber);
    }

    @Override
    public void onLoadFinished(Loader<MoviesResult> loader, MoviesResult moviesResult) {
        adapter.updateMovies(moviesResult);
    }

    @Override
    public void onLoaderReset(Loader<MoviesResult> loader) {
        adapter.clear();
    }
}
