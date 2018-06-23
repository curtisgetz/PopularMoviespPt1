package com.curtisgetz.popularmoviesppt1.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.database.Cursor;
import android.util.Log;

import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;

public class PullFavoritesLoader extends AsyncTaskLoader<Cursor> {
    private static final String TAG = PullFavoritesLoader.class.getSimpleName();


    private Cursor mFavoriteData;
    //unsure proper way to avoid this type of memory leak when using Loaders

    public PullFavoritesLoader(@NonNull Context context) {
        super(context);
        mFavoriteData = null;
    }


    @Override
    protected void onStartLoading() {
        if(mFavoriteData != null) {
            //deliver previously loaded data immediately
            deliverResult(mFavoriteData);
        }else {
            forceLoad();
        }
    }


    @Nullable
    @Override
    public Cursor loadInBackground() {

        //load all favorite data from DB
        try {
            return getContext().getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }catch (Exception e){
            Log.e(TAG, "Failed to load data");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable Cursor data) {
        mFavoriteData = data;
        super.deliverResult(data);
    }








}
