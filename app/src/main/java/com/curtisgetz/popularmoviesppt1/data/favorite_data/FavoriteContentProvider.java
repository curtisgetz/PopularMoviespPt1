package com.curtisgetz.popularmoviesppt1.data.favorite_data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import static com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID;
import static com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract.FavoriteEntry.TABLE_NAME;

public class FavoriteContentProvider extends ContentProvider {


    private final static String TAG = FavoriteContentProvider.class.getSimpleName();
    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    //FOR URI MATCHER
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        // initialize with no matches by passing NO_MATCH
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteContract.CONTENT_AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteContract.CONTENT_AUTHORITY,
                FavoriteContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    //member variable for a FavoriteDBHelper.  Initialized in onCreate()
    private FavoriteDbHelper mFavoriteDbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteDbHelper = new FavoriteDbHelper(context);
        return true;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        // URI matching code
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case FAVORITES:
                //insert new values into database
                long id = db.insertWithOnConflict(TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if( id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);

                }else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri - " + uri);
        }

        //notify the resolver if the uri has been changed, and return newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        //return constructed uri (points to newly inserted row
        return returnUri;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // get access to rightable db
        final SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();

        //URI match code
        int match = sUriMatcher.match(uri);
        //variable for Cursor to return
        Cursor returnCursor;

        switch(match){
            case FAVORITES:
                returnCursor = db.query(TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                //Selection is the movie ID col.
                String [] mSelectionArgs = new String[] {id};

                Log.v(TAG, "FAVORITES WITH ID - "   + mSelectionArgs[0]);

                returnCursor = db.query(TABLE_NAME,
                        projection,
                        COLUMN_MOVIE_ID + "=?",
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            //default exception
            default:
                throw new UnsupportedOperationException("Unknown URI - " + uri);

        }
        //set notification URL on the Cursor and return it
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    //implement delete to delete a single row

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //get access to writable DB
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        //Uri matching code
        int match = sUriMatcher.match(uri);
        // keep track of number of deletes favorites
        int favoritesDeleted; //defaults to 0

        String whereClause = COLUMN_MOVIE_ID + "=?";
        //code to delete single row
        switch (match) {

            case FAVORITE_WITH_ID:
                //get favorite ID from the URI path
                String id = uri.getPathSegments().get(1);
                //use selection and selectionArgs to filter for this ID
                favoritesDeleted = db.delete(TABLE_NAME,whereClause, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri - " + uri);
        }


        //notify resolver of change and return the number of items deleted
        if(favoritesDeleted != 0) {
            //favorite deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return number of favorites deleted
        return favoritesDeleted;


    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not available ");

    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not available ");
    }
}

