package com.curtisgetz.popularmoviesppt1.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.MovieVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<MovieVideo>>{

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
//    private String mMovieTitle;
    private Movie mMovie;
    private boolean mIsSW600;

    //@BindView(R.id.tv_movie_title) TextView mMovieTitleTV;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTV;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    @BindView(R.id.tv_user_rating) TextView mRating;
    @BindView(R.id.iv_backdrop) ImageView mBGImageView;
    @BindView(R.id.iv_poster) ImageView mPosterImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //setTitle(mMovieTitle);
        ButterKnife.bind(this);
        mSynopsis.setMovementMethod(new ScrollingMovementMethod());
        mIsSW600 = isSmallestWidth600();

        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.detail_save_key))){
            Intent intent = getIntent();
            if(intent != null) {
                mMovie = intent.getParcelableExtra(getString(R.string.movie_to_pass));
                updateUI();
            }

        }else{
           mMovie = savedInstanceState.getParcelable(getString(R.string.detail_save_key));
           updateUI();
        }

    }


    private boolean isSmallestWidth600(){
        // check if smallest width of screen is at least 600
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthInDIP = Math.round(dm.widthPixels / dm.density);
        return (widthInDIP > 600);
    }

    private void updateUI(){
        setTitle(mMovie.getmTitle());
        mReleaseDateTV.setText(mMovie.getLocalizedDateString());
        mSynopsis.setText(mMovie.getmSynopsis());
        mRating.setText(mMovie.getVoteAverageString());

        Picasso.get().load(mMovie.getFullPosterUrl(mIsSW600))
                .placeholder(R.drawable.posterloadingplaceholder185)
                .error(R.drawable.posterplaceholder185)
                .into(mPosterImageView);

        Picasso.get().load(mMovie.getFullBGImageUrl())
                .placeholder(R.drawable.backdropplaceholder)
                .error(R.drawable.backdropplaceholder)
                .into(mBGImageView);


    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.detail_save_key), mMovie);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<List<MovieVideo>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<MovieVideo>> loader) {

    }
}
