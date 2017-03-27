package com.nano.movies.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nano.movies.R;
import com.nano.movies.adapter.TrailerListAdapter;
import com.nano.movies.data.MoviesContract;
import com.nano.movies.model.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis Gomez on 3/15/2017.
 */
public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.COLUMN_REVIEW_ID,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT
    };

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_REMOTE_ID = 1;
    public static final int COL_REVIEW_AUTHOR = 2;
    public static final int COL_REVIEW_CONTENT = 3;

    public static final int REVIEWS_LOADER = 2;

    private TrailerListAdapter adapter;
    private int movieId;

    @BindView(R.id.listview_reviews)
    ListView reviewsListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new TrailerListAdapter(getActivity(), null, 0);
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        String movieKey = getString(R.string.movie_extra_key);
        if (null != intent && intent.hasExtra(movieKey)) {
            Movie movie = intent.getParcelableExtra(movieKey);
            movieId = movie.getId();
            ButterKnife.bind(this, rootView);
            reviewsListView.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MoviesContract.ReviewEntry.CONTENT_URI,
                REVIEW_COLUMNS,
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ String.valueOf(movieId) },
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
