package com.nano.movies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.movies.R;
import com.nano.movies.activity.TrailersFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis Gomez on 3/13/2017.
 */

public class TrailerListAdapter extends CursorAdapter {

    public TrailerListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    static class ViewHolder {
        @BindView(R.id.list_item_video_thumbnail)
        ImageView videoThumbnail;
        @BindView(R.id.list_item_video_name)
        TextView videoNameTextView;
        @BindView(R.id.list_item_video_size)
        TextView videoSizeTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.videoNameTextView.setText(cursor.getString(TrailersFragment.COL_VIDEO_NAME));
        viewHolder.videoSizeTextView.setText(cursor.getString(TrailersFragment.COL_VIDEO_SIZE));

        String videoKey = cursor.getString(TrailersFragment.COL_VIDEO_ID);
        String imageUrl = String.format(context.getString(R.string.youtube_thumb_url), videoKey);
        Picasso.with(context).load(imageUrl).into(viewHolder.videoThumbnail);
    }
}
