package com.nano.movies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nano.movies.R;
import com.nano.movies.adapter.FavoriteMoviesCursorAdapter;
import com.nano.movies.adapter.MovieListAdapter;
import com.nano.movies.data.MoviesContract;
import com.nano.movies.loader.MoviesLoader;
import com.nano.movies.model.Movie;
import com.nano.movies.model.MoviesResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis on 1/2/17.
 */

public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String POSITION_KEY = "selected_position";
    private static final String PAGE_KEY = "current_page";
    private static final String MOVIES_KEY = "movies";
    private static final int GRID_NUM_COLUMNS_PORTRAIT = 3;
    private static final int GRID_NUM_COLUMNS_LANDSCAPE = 4;

    public static final int FAVORITES_LOADER = 0;
    public static final int API_LOADER = 1;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVG,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_REMOTE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_POSTER_PATH = 4;
    public static final int COL_VOTE_AVG = 5;
    public static final int COL_OVERVIEW = 6;

    @BindView(R.id.grid_movies) GridView gridMovies;

    private MovieListAdapter apiAdapter;
    private FavoriteMoviesCursorAdapter favoriteAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private int mPage = 1;
    private boolean mIsFavorites;
    private String previousSortOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Context context = getContext();
        previousSortOrder = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_movies_by_key),
                context.getString(R.string.pref_movies_by_popular));
        mIsFavorites = previousSortOrder.equals(context.getString(R.string.pref_movies_by_favorites));

        apiAdapter = new MovieListAdapter(context, this, new ArrayList<Movie>());
        favoriteAdapter = new FavoriteMoviesCursorAdapter(context, null, 0);
        ListAdapter adapter;
        if (mIsFavorites) {
            adapter = favoriteAdapter;
        } else {
            adapter = apiAdapter;
        }

        ButterKnife.bind(this, rootView);
        int numColumns = GRID_NUM_COLUMNS_PORTRAIT;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numColumns = GRID_NUM_COLUMNS_LANDSCAPE;
        }
        gridMovies.setNumColumns(numColumns);
        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie;
                if (mIsFavorites) {
                    movie = getFavoriteMovie((Cursor) adapterView.getItemAtPosition(position));
                } else {
                    movie = (Movie) adapterView.getItemAtPosition(position);
                }
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
            if (savedInstanceState.containsKey(MOVIES_KEY) && !mIsFavorites) {
                MoviesResult result = new MoviesResult();
                result.setMovies(savedInstanceState.<Movie>getParcelableArrayList(MOVIES_KEY));
                result.setCurrentPage(mPage - 1);
                result.setTotalPages(mPage); // bummer
                apiAdapter.updateMovies(result);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, mPosition);
        }
        if (!mIsFavorites) {
            if (mPage != 1) {
                outState.putInt(PAGE_KEY, mPage);
            }
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < apiAdapter.getCount(); i++) {
                movies.add(apiAdapter.getItem(i));
            }
            outState.putParcelableArrayList("movies", new ArrayList<Parcelable>(movies));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mIsFavorites) {
            getLoaderManager().initLoader(FAVORITES_LOADER, null, new FavoriteMoviesLoaderCallbacks());
        } else if (null == savedInstanceState || !savedInstanceState.containsKey(MOVIES_KEY)) {
            Bundle args = new Bundle();
            args.putInt("page", mPage);
            getLoaderManager().initLoader(API_LOADER, args, new ApiLoaderCallbacks()).forceLoad();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mPosition != ListView.INVALID_POSITION) {
            gridMovies.smoothScrollToPosition(mPosition);
        }
        Context context = getContext();
        String newSortOrder = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_movies_by_key),
                context.getString(R.string.pref_movies_by_popular));
        if (!newSortOrder.equals(previousSortOrder)) {
            favoriteAdapter.swapCursor(null);
            apiAdapter.clear();
            mIsFavorites = newSortOrder.equals(context.getString(R.string.pref_movies_by_favorites));
            if (mIsFavorites) {
                gridMovies.setAdapter(favoriteAdapter);
                getLoaderManager().restartLoader(FAVORITES_LOADER, null, new FavoriteMoviesLoaderCallbacks());
            } else {
                gridMovies.setAdapter(apiAdapter);
                mPage = 1;
                Bundle args = new Bundle();
                args.putInt("page", mPage);
                getLoaderManager().restartLoader(API_LOADER, args, new ApiLoaderCallbacks()).forceLoad();
            }
            previousSortOrder = newSortOrder;
        }
        super.onResume();
    }

    private Movie getFavoriteMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getInt(COL_REMOTE_ID));
        movie.setTitle(cursor.getString(COL_TITLE));
        movie.setReleaseDate(new Date(cursor.getLong(COL_RELEASE_DATE)));
        movie.setVoteAverage(cursor.getFloat(COL_VOTE_AVG));
        movie.setOverview(cursor.getString(COL_OVERVIEW));
        movie.setImagePath(cursor.getString(COL_POSTER_PATH));
        movie.setFavorite(true);
        return movie;
    }

    public void restartLoader() {
        mPage++;
        Bundle args = new Bundle();
        args.putInt("page", mPage);
        getLoaderManager().restartLoader(API_LOADER, args, new ApiLoaderCallbacks()).forceLoad();
        Log.d(LOG_TAG, "Finished loading page " + mPage);
    }

    public class ApiLoaderCallbacks implements LoaderManager.LoaderCallbacks<MoviesResult> {
        @Override
        public Loader<MoviesResult> onCreateLoader(int id, Bundle bundle) {
            int pageNumber = bundle.getInt("page");
            return new MoviesLoader(getActivity(), pageNumber);
        }

        @Override
        public void onLoadFinished(Loader<MoviesResult> loader, MoviesResult moviesResult) {
            apiAdapter.updateMovies(moviesResult);
        }

        @Override
        public void onLoaderReset(Loader<MoviesResult> loader) {
            apiAdapter.clear();
        }
    }

    public class FavoriteMoviesLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            favoriteAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            favoriteAdapter.swapCursor(null);
        }
    }
}
