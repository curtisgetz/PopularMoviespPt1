package com.curtisgetz.popularmoviesppt1.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReviewAdapter;

import java.util.List;

public class MovieReviewFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<MovieReview>>{

    private final static String TAG = MovieReviewFragment.class.getSimpleName();

    private List<MovieReview> mReviewList;
    private int mMovieId;


    public MovieReviewFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        RecyclerView mReviewRecyclerView =  view.findViewById(R.id.review_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mReviewRecyclerView.setLayoutManager(layoutManager);

        MovieReviewAdapter adapter = new MovieReviewAdapter(mReviewList);
        mReviewRecyclerView.setAdapter(adapter);


        return view;
    }


    @Override
    public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
        return null;
    }


    @Override
    public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<MovieReview>> loader) {

    }

    public void setmReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }


    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
