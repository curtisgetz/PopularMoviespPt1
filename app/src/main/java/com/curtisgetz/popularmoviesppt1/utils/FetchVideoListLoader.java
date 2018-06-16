package com.curtisgetz.popularmoviesppt1.utils;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideo;

import java.util.List;

public class FetchVideoListLoader extends AsyncTaskLoader<List<MovieVideo>> {

    private Movie mMovie;

    public FetchVideoListLoader(Context context, Movie movie) {
        super(context);
        this.mMovie = movie;
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<MovieVideo> loadInBackground() {
        return NetworkUtils.getTrailerList(mMovie);
    }


    @Override
    public void deliverResult(List<MovieVideo> data) {
        super.deliverResult(data);
    }


}
