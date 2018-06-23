package com.curtisgetz.popularmoviesppt1.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.database.Cursor;
import android.util.Log;

import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;

public class CheckForFavoriteLoader extends AsyncTaskLoader<Cursor> {

    private final static String TAG = CheckForFavoriteLoader.class.getSimpleName();

    private Cursor mData;
    private int mId;


    public CheckForFavoriteLoader(@NonNull Context context, int id) {
        super(context);
        mId = id;
    }



    @Override
    protected void onStartLoading() {
        if(mData != null){
            deliverResult(mData);
        }else{
            forceLoad();
        }
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {

        try {

            Uri uri = FavoriteContract.FavoriteEntry.buildFavUriWithID(mId);
            return getContext().getContentResolver().query(uri,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
                    null,
                    null);
        }catch (Exception e){
            Log.e(TAG, "Failed to find favorite");
            e.printStackTrace();
            return null;
        }
    }


}
