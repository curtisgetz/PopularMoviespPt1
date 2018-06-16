package com.curtisgetz.popularmoviesppt1.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideo;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideoListAdapter;
import com.curtisgetz.popularmoviesppt1.utils.FetchReviewsLoader;
import com.curtisgetz.popularmoviesppt1.utils.FetchVideoListLoader;
import com.curtisgetz.popularmoviesppt1.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailActivity extends AppCompatActivity
        implements MovieVideoListAdapter.VideoItemClickListener,
        LoaderManager.LoaderCallbacks<List<MovieVideo>>{

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
    private final static int VIDEO_LOADER_ID = 6;
    private final static int REVIEW_LOADER_ID = 5;


    //    private String mMovieTitle;
    private Movie mMovie;
    private boolean mIsSW600;
    private List<MovieVideo> mVideoList;
    private FragmentManager testFragmentManager;
    private LoaderManager.LoaderCallbacks<List<MovieReview>> mReviewCallback;
    private int mMovieId;
    private List<MovieReview> mReviewList;
    private Context mContext;
   // private MovieVideoListAdapter mAdapter;

    @BindView(R.id.video_recyclerview) RecyclerView mVideoRecyclerView;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTV;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    @BindView(R.id.tv_user_rating) TextView mRating;
    @BindView(R.id.iv_backdrop) ImageView mBGImageView;
    @BindView(R.id.iv_poster) ImageView mPosterImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        mContext = MovieDetailActivity.this;

        mReviewCallback = new ReviewCallback();
        testFragmentManager = getSupportFragmentManager();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVideoRecyclerView.setLayoutManager(layoutManager);

        // TODO  check into synopsis textview scrolling later
        // mSynopsis.setMovementMethod(new ScrollingMovementMethod());

        //set true if smallest width is >= 600
        mIsSW600 = isSmallestWidth600();
        Log.v(TAG, String.valueOf(mIsSW600));
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

        //TODO check if I should move this inside if-else above.  SHould be able to save video list
        // in savedInstanceState
        loadVideos();



    }





    private boolean isSmallestWidth600(){
        // check if smallest width of screen is at least 600
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthInDIP = Math.round(dm.widthPixels / dm.density);
        return (widthInDIP > 600);
    }

    private void updateUI(){
        mMovieId = mMovie.getmId();
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
    public void onMovieItemClick(int clickedMovieIndex) {
        //TODO testing remove Toast when finished
        Toast.makeText(this,  mVideoList.get(clickedMovieIndex).getmName(), Toast.LENGTH_SHORT).show();


        String trailerKey = mVideoList.get(clickedMovieIndex).getmVideoKey();
        //pass trailerKey of selected video to buildVideoIntentUri method
        Uri videoUri = NetworkUtils.buildVideoIntentUri(trailerKey);
        //create Intent to watch trailer. Try YouTube app first
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, videoUri);

        if(videoIntent.resolveActivity(getPackageManager()) != null){
            startActivity(videoIntent);
        } else {
            videoErrorToast();
        }

    }

    public void videoErrorToast(){
        Toast.makeText(this, getString(R.string.video_error), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.detail_save_key), mMovie);
        super.onSaveInstanceState(outState);
    }


    @NonNull
    @Override
    public Loader<List<MovieVideo>> onCreateLoader(int id, Bundle args) {
        return new FetchVideoListLoader(this, mMovie);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieVideo>> loader, List<MovieVideo> data) {

           if(data == null || data.size() == 0) {
               Toast.makeText(this,
                       "No trailers available for this movie.", Toast.LENGTH_SHORT).show();
           }else {
               mVideoList = data;
               mMovie.setmMovieVideoList(data);
               MovieVideoListAdapter adapter = new MovieVideoListAdapter(this, data);
               mVideoRecyclerView.setAdapter(adapter);

               //TODO removed member variables, added local variables
               //mAdapter = new MovieVideoListAdapter(this, data);
               //mVideoRecyclerView.setAdapter(mAdapter);

           }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieVideo>> loader) {

    }


    public void loadVideos(){

        if(!isOnline()){
            noNetworkToast();
        }else {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<MovieVideo>> videoLoader = loaderManager.getLoader(VIDEO_LOADER_ID);
            if (videoLoader == null) {
                loaderManager.initLoader(VIDEO_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(VIDEO_LOADER_ID, null, this);
            }
        }

    }

    private void noNetworkToast(){
        Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
    }


    //TODO fix crashes with no network. Some should resolve when saving to savedInstanceState.
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }



    //TODO  remove, only for testing
    public void testButton(View view){

        Log.v(TAG, "TEST BUTTON");
        //TODO reuse cached data
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<MovieReview>> reviewLoader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if(reviewLoader == null){
            Log.v(TAG, "LOADER NULL");
            loaderManager.initLoader(REVIEW_LOADER_ID, null, mReviewCallback).forceLoad();
        }else {
            Log.v(TAG, "LOADER NOT NULL");
            loaderManager.restartLoader(REVIEW_LOADER_ID, null, mReviewCallback).forceLoad();
        }
       /* Bundle bundle = new Bundle();

        MovieReviewFragment movieReviewFragment = new MovieReviewFragment();
        movieReviewFragment.setmMovieId(mMovie.getmId());

        movieReviewFragment.show(getSupportFragmentManager(), "MovieReviewFragment");
*/
        //Movie movieToPass = mMainMovieList.get(clickedPosterIndex);
        // create intent for MovieDetailActivity
       // Intent intent = new Intent(getApplicationContext(), MovieReviewsActivity.class);
       // intent.putExtra(getString(R.string.movie_id_to_pass), mMovie.getmId());
        //put movie as extra
       // if(intent.resolveActivity(getPackageManager()) != null) {
       //     startActivity(intent);
        //}
    }



    public void showReviews(){

        Log.v(TAG, "SHOW REVIEWS");

        Bundle bundle = new Bundle();

        MovieReviewFragment movieReviewFragment = new MovieReviewFragment();
        //movieReviewFragment.setmMovieId(mMovieId);
        movieReviewFragment.setmReviewList(mReviewList);
       // Log.v(TAG, String.valueOf(movieReviewFragment.getmMovieId()));
        movieReviewFragment.show(getSupportFragmentManager(), "MovieReviewFragment");

    }

    public void initReviewLoader(){
        mReviewCallback = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @NonNull
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
                Log.v(TAG, "POn create loader!");
                return new FetchReviewsLoader(mContext, mMovieId);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
                Log.v(TAG, "LOADER FINISHED");
                mReviewList = data;
                showReviews();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

            }
        };
    }


    public void showNoReviewsToast() {
        Toast.makeText(this, "No reviews for this movie", Toast.LENGTH_SHORT).show();
    }




    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<MovieReview>>{

        @NonNull
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.v(TAG, "POn create loader!");
            return new FetchReviewsLoader(mContext, mMovieId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
            Log.v(TAG, "LOADER FINISHED");
            if(data.size() > 0) {
                mReviewList = data;
                showReviews();
            }else {
                showNoReviewsToast();
            }
        }



        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

        }
    }
}
