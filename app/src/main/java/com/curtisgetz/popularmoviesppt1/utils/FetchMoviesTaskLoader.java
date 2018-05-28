package com.curtisgetz.popularmoviesppt1.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.curtisgetz.popularmoviesppt1.Movie;

import java.util.List;


public class FetchMoviesTaskLoader extends AsyncTaskLoader<List<Movie>> {


    private static final String TAG = FetchMoviesTaskLoader.class.getSimpleName();
    private int mSortBy, mPageNumber;

    public FetchMoviesTaskLoader(@NonNull Context context, int sortBy, int pageNumber ) {
        super(context);
        this.mSortBy = sortBy;
        this.mPageNumber = pageNumber;
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    @Override
    public List<Movie> loadInBackground() {
        return NetworkUtils.getMainMovies(mSortBy, mPageNumber);

    }

    @Override
    public void deliverResult(@Nullable List<Movie> data) {
        super.deliverResult(data);
    }
}
