package com.curtisgetz.popularmoviesppt1.data.favorite_data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDbHelper extends SQLiteOpenHelper {

    //name of DB
    private static final String DATABASE_NAME = "favoritesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //create favorites tables
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME +
                " (" + FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RATING + " REAL NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_BG_URL + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    //this method discars old table and calls onCreate to recreate a new one. Only occurs when version
    // is incremented.


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
