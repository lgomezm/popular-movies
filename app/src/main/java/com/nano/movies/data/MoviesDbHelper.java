package com.nano.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nano.movies.data.MoviesContract.TrailerEntry;

/**
 * Created by Luis Gomez on 3/13/2017.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                TrailerEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
