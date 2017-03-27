package com.nano.movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.nano.movies.R;
import com.nano.movies.adapter.MovieListAdapter;
import com.nano.movies.loader.MoviesLoader;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis on 1/2/17.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<MoviesResult> {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String POSITION_KEY = "selected_position";
    private static final String PAGE_KEY = "current_page";
    private static final String MOVIES_KEY = "movies";
    public static final int MOVIES_LOADER = 0;

    @BindView(R.id.grid_movies) GridView gridMovies;

    private MovieListAdapter adapter;
    private int mPosition = GridView.INVALID_POSITION;
    private int mPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MovieListAdapter(getActivity(), this, new ArrayList<Movie>());

        ButterKnife.bind(this, rootView);
        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(getString(R.string.movie_extra_key), movie);
                mPosition = position;
                startActivity(intent);
            }
        });
        gridMovies.setAdapter(adapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(POSITION_KEY)) {
                mPosition = savedInstanceState.getInt(POSITION_KEY);
            }
            if (savedInstanceState.containsKey(PAGE_KEY)) {
                mPage = savedInstanceState.getInt(PAGE_KEY);
            }
            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                MoviesResult result = new MoviesResult();
                result.setMovies(savedInstanceState.<Movie>getParcelableArrayList(MOVIES_KEY));
                result.setCurrentPage(mPage - 1);
                result.setTotalPages(mPage); // bummer
                adapter.updateMovies(result);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, mPosition);
        }
        if (mPage != 1) {
            outState.putInt(PAGE_KEY, mPage);
        }
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < adapter.getCount(); i++) {
            movies.add(adapter.getItem(i));
        }
        outState.putParcelableArrayList("movies", new ArrayList<Parcelable>(movies));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (null == savedInstanceState || !savedInstanceState.containsKey(MOVIES_KEY)) {
            Bundle args = new Bundle();
            args.putInt("page", mPage);
            getLoaderManager().initLoader(MOVIES_LOADER, args, this).forceLoad();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mPosition != ListView.INVALID_POSITION) {
            gridMovies.smoothScrollToPosition(mPosition);
        }
        super.onResume();
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

    public void restartLoader() {
        mPage++;
        Bundle args = new Bundle();
        args.putInt("page", mPage);
        getLoaderManager().restartLoader(MOVIES_LOADER, args, this).forceLoad();
        Log.d(LOG_TAG, "Finished loading page " + mPage);
    }
}
