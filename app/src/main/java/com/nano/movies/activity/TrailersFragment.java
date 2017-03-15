package com.nano.movies.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nano.movies.R;
import com.nano.movies.adapter.TrailerListAdapter;
import com.nano.movies.data.MoviesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis Gomez on 3/15/2017.
 */

public class TrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TrailersFragment.class.getSimpleName();
    private static final String[] TRAILERS_COLUMNS = {
            MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.COLUMN_VIDEO_NAME,
            MoviesContract.TrailerEntry.COLUMN_VIDEO_ID,
            MoviesContract.TrailerEntry.COLUMN_SIZE
    };

    public static final int COL_TRAILER_ID = 0;
    public static final int COL_VIDEO_NAME = 1;
    public static final int COL_VIDEO_ID = 2;
    public static final int COL_VIDEO_SIZE = 3;

    public static final int TRAILERS_LOADER = 1;

    private TrailerListAdapter adapter;
    private int movieId;

    @BindView(R.id.listview_trailers)
    ListView trailersListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new TrailerListAdapter(getActivity(), null, 0);
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);
        String movieIdExtraKey = getString(R.string.movie_id_extra_key);
        if (null != intent && intent.hasExtra(movieIdExtraKey)) {
            movieId = intent.getIntExtra(movieIdExtraKey, -1);
            ButterKnife.bind(this, rootView);
            trailersListView.setAdapter(adapter);
            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        String videoKey = cursor.getString(COL_VIDEO_ID);
                        final String videoUrl = String.format(getString(R.string.youtube_video_url), videoKey);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(intent);
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MoviesContract.TrailerEntry.CONTENT_URI,
                TRAILERS_COLUMNS,
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { String.valueOf(movieId) },
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
