package com.curtisgetz.popularmoviesppt1.utils;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;


import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;

import java.util.List;

public class FetchReviewsLoader extends AsyncTaskLoader<List<MovieReview>>{


    private static final String TAG = FetchReviewsLoader.class.getSimpleName();
    private int mMovieID;

    public FetchReviewsLoader(Context context, int movieID) {
        super(context);
        this.mMovieID = movieID;
    }

    @Override
    protected void onStartLoading() {
        Log.v(TAG, "ON START LOADING");
        super.onStartLoading();
        forceLoad();
    }


    @Override
    public List<MovieReview> loadInBackground() {
        Log.v(TAG, "LOAD IN BACKGROUND");
        return NetworkUtils.getReviewList(mMovieID);
    }

    @Override
    public void deliverResult(List<MovieReview> data) {
        super.deliverResult(data);
    }
}
