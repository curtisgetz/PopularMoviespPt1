package com.curtisgetz.popularmoviesppt1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
//    private String mMovieTitle;
    private Movie mMovie;

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


        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.detail_save_key))){
            Intent intent = getIntent();
            if(intent != null) {
                Log.v(TAG, "intent is not null");
                mMovie = intent.getParcelableExtra(getString(R.string.movie_to_pass));
                updateUI();
            }

        }else{
           mMovie = savedInstanceState.getParcelable(getString(R.string.detail_save_key));
           updateUI();
        }



    }


    private void updateUI(){
        setTitle(mMovie.getmTitle());
        mReleaseDateTV.setText(mMovie.getLocalizedDateString());
        mSynopsis.setText(mMovie.getmSynopsis());
        mRating.setText(mMovie.getVoteAverageString());

        Picasso.get().load(mMovie.getFullPosterUrl())
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
}