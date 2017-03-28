package com.nano.movies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nano.movies.R;
import com.nano.movies.activity.MainFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Luis Gomez on 3/27/2017.
 */
public class FavoriteMoviesCursorAdapter extends CursorAdapter {

    public FavoriteMoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new ImageView(context);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view;
        String imageUrl = context.getString(R.string.images_base_url) + cursor.getString(MainFragment.COL_POSTER_PATH);
        Picasso.with(context).load(imageUrl).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}