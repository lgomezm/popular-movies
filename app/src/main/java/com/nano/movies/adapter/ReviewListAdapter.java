package com.nano.movies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.nano.movies.R;
import com.nano.movies.activity.ReviewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Luis Gomez on 3/27/2017.
 */
public class ReviewListAdapter extends CursorAdapter {

    public ReviewListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    static class ViewHolder {
        @BindView(R.id.reviewAuthor)
        TextView authorView;
        @BindView(R.id.reviewContents)
        TextView contentsView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String authorText = String.format(context.getString(R.string.review_actor),
                cursor.getString(ReviewsFragment.COL_REVIEW_AUTHOR));

        viewHolder.authorView.setText(authorText);
        viewHolder.contentsView.setText(cursor.getString(ReviewsFragment.COL_REVIEW_CONTENT));
    }
}
