package com.nano.movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Luis Gomez on 3/13/2017.
 */

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int REVIEW = 100;
    static final int TRAILER = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEW);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILER);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REVIEW:
                return MoviesContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MoviesContract.TrailerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case REVIEW:
                tableName = MoviesContract.ReviewEntry.TABLE_NAME;
                break;
            case TRAILER:
                tableName = MoviesContract.TrailerEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Cursor retCursor = mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REVIEW: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MoviesContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case REVIEW:
                tableName = MoviesContract.ReviewEntry.TABLE_NAME;
                break;
            case TRAILER:
                tableName = MoviesContract.TrailerEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if ( null == selection ) selection = "1";
        int rowsDeleted = db.delete(tableName, selection, selectionArgs);
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case REVIEW:
                tableName = MoviesContract.ReviewEntry.TABLE_NAME;
                break;
            case TRAILER:
                tableName = MoviesContract.TrailerEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        int rowsUpdated = db.update(tableName, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case REVIEW:
                tableName = MoviesContract.ReviewEntry.TABLE_NAME;
                break;
            case TRAILER:
                tableName = MoviesContract.TrailerEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
