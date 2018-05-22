package com.curtisgetz.popularmoviesppt1.utils;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;

import com.curtisgetz.popularmoviesppt1.Movie;

import java.util.List;

public class GetMoviesTask implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private Context mContext;
    private int mSortBy;
    private List<Movie> mMovieList;

    public GetMoviesTask(Context mContext, int sortBy) {
        this.mContext = mContext;
        this.mSortBy = sortBy;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(mContext) {
            @Override
            public List<Movie> loadInBackground() {
                return NetworkUtils.getMainMovies(mSortBy, 2);
            }
        };



    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}
