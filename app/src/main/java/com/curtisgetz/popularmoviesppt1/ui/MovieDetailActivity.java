package com.curtisgetz.popularmoviesppt1.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideo;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideoListAdapter;
import com.curtisgetz.popularmoviesppt1.utils.CheckForFavoriteLoader;
import com.curtisgetz.popularmoviesppt1.utils.DbBitmapUtil;
import com.curtisgetz.popularmoviesppt1.utils.FetchReviewsLoader;
import com.curtisgetz.popularmoviesppt1.utils.FetchVideoListLoader;
import com.curtisgetz.popularmoviesppt1.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.curtisgetz.popularmoviesppt1.utils.DbBitmapUtil.getBytes;


public class MovieDetailActivity extends AppCompatActivity
        implements MovieVideoListAdapter.VideoItemClickListener,
        LoaderManager.LoaderCallbacks<List<MovieVideo>>{

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
    private final static int VIDEO_LOADER_ID = 6;
    private final static int REVIEW_LOADER_ID = 5;
    private final static int IS_FAV_LOADER_ID = 4;

    private Movie mMovie;
    private boolean mIsSW600, mIsFavorite;
    private List<MovieVideo> mVideoList;

    private LoaderManager.LoaderCallbacks<List<MovieReview>> mReviewCallback;
    private LoaderManager.LoaderCallbacks<Cursor> mIsFavoriteCallback;
    private int mMovieId;
    private List<MovieReview> mReviewList;
    private Context mContext;
    private byte[] mImageBytes;


    @BindView(R.id.video_recyclerview) RecyclerView mVideoRecyclerView;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTV;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    @BindView(R.id.tv_user_rating) TextView mRating;
    @BindView(R.id.iv_backdrop) ImageView mBGImageView;
    @BindView(R.id.iv_poster) ImageView mPosterImageView;
    @BindView(R.id.review_button)Button mReviewButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        mContext = MovieDetailActivity.this;

        //Callbacks for Loaders
        mReviewCallback = new ReviewCallback();
        mIsFavoriteCallback = new CheckForFavoriteCallback();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVideoRecyclerView.setLayoutManager(layoutManager);

        //set true if smallest width is >= 600
        mIsSW600 = isSmallestWidth600();


        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.detail_save_key))){
            Intent intent = getIntent();
            if(intent != null) {
                mMovie = intent.getParcelableExtra(getString(R.string.movie_to_pass));
                updateUI();
            }else{
                Toast.makeText(this, R.string.detail_intent_error, Toast.LENGTH_SHORT).show();
                finish();
            }

        }else{
           mMovie = savedInstanceState.getParcelable(getString(R.string.detail_save_key));
           updateUI();
        }
        //load videos/trailers
        loadVideos();
        //check if this movie is in the favorites database
        checkIfFavorite();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.detail_save_key), mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMovie = savedInstanceState.getParcelable(getString(R.string.detail_save_key));
        super.onRestoreInstanceState(savedInstanceState);
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

        //get bitmap from Picassso. Convert to byte array to save in DB if user saves.
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mImageBytes =  DbBitmapUtil.getBytes(bitmap);
                mMovie.setmImageBytes(DbBitmapUtil.getBytes(bitmap));
                mPosterImageView.setImageBitmap(bitmap);
                Log.v(TAG, "Bitmap Loaded");
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mMovie.getFullPosterUrl(mIsSW600))
                .placeholder(R.drawable.posterloadingplaceholder185)
                .error(R.drawable.posterplaceholder185)
                .into(target);
/*
        Picasso.get().load(mMovie.getFullPosterUrl(mIsSW600))
                .placeholder(R.drawable.posterloadingplaceholder185)
                .error(R.drawable.posterplaceholder185)
                .into(mPosterImageView);*/

        Picasso.get().load(mMovie.getFullBGImageUrl())
                .placeholder(R.drawable.backdropplaceholder)
                .error(R.drawable.backdropplaceholder)
                .into(mBGImageView);
    }



    // Get user click on trailers/videos
    @Override
    public void onMovieItemClick(int clickedMovieIndex) {
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


    @NonNull
    @Override
    public Loader<List<MovieVideo>> onCreateLoader(int id, Bundle args) {
        return new FetchVideoListLoader(this, mMovie);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieVideo>> loader, List<MovieVideo> data) {

           if(data == null || data.size() == 0) {
                //for future use
           }else {
               mVideoList = data;
               mMovie.setmMovieVideoList(data);
               MovieVideoListAdapter adapter = new MovieVideoListAdapter(this, data);
               mVideoRecyclerView.setAdapter(adapter);
           }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieVideo>> loader) {

    }


    public void loadVideos(){
        // if online, load videos/trailers
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


    /*
     *The following method is taken from stackoverflow
     *       https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * via link in project implementation guide
     *       https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.g9v5r052l2am
     */
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }


    public void addToFavorites(){
        //insert new favorite with ContentResolver
        //create empty ContentValues object
        ContentValues contentValues = new ContentValues();
        //put movie into ContentValues
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, mMovie.getmTitle());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, mMovie.getmSynopsis());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, mMovie.getmReleaseDate());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, mMovie.getmVoteAverage());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, mMovie.getmPosterUrl());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_BG_URL, mMovie.getmBGImage());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getmId());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_IMAGE_DATA, mMovie.getmImageBytes());


        //insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(MovieDetailActivity.this, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();

        }
    }




    public void removeFromFavorites(){
        //build Uri for deleting single movie
        Uri uri = FavoriteContract.FavoriteEntry.buildFavUriWithID(mMovieId);
        // delete record and get returned int to confirm an item was deleted
        int deleted = getContentResolver().delete(uri, null, null);
        //if anything was deleted, display confirmation
        if(deleted > 0){
            Toast.makeText(MovieDetailActivity.this,
                    getString(R.string.removed_from_favorites),
                    Toast.LENGTH_SHORT).show();
        }

    }



    public void getReviews(View view){
        //start reviews Loader
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<MovieReview>> reviewLoader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if(reviewLoader == null){
            loaderManager.initLoader(REVIEW_LOADER_ID, null, mReviewCallback).forceLoad();
        }else {
            loaderManager.restartLoader(REVIEW_LOADER_ID, null, mReviewCallback).forceLoad();
        }
    }


    public void showReviews(){
            // Show reviews in fragment
            MovieReviewFragment movieReviewFragment = new MovieReviewFragment();
            movieReviewFragment.setmReviewList(mReviewList);
            movieReviewFragment.show(getSupportFragmentManager(), "MovieReviewFragment");
    }


//Inflate menu for adding movie to favorites
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteItem = menu.findItem(R.id.fav_menu_option);
        //change title depending on whether movie is already a favorite or not
        if(mIsFavorite){
            favoriteItem.setTitle(getString(R.string.remove_from_favorites));
        }else {
            favoriteItem.setTitle(getString(R.string.add_to_favorites));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        switch (clickedItem){
            case R.id.fav_menu_option:
                toggleFavorite(item);
                invalidateOptionsMenu();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleFavorite(MenuItem menuItem){
        if(mIsFavorite){
            //remove movie from favorites and set mIsFavorite to false
            removeFromFavorites();
            mIsFavorite = false;
        }else {
            //add movie to favorites and set mIsFavorite to true
            addToFavorites();
            mIsFavorite = true;
        }
    }


    private void checkIfFavorite(){
        //start Loader to check DB for favorite
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> favCheckLoader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if(favCheckLoader == null){
            loaderManager.initLoader(IS_FAV_LOADER_ID, null, mIsFavoriteCallback);
        }else {
            loaderManager.restartLoader(IS_FAV_LOADER_ID, null, mIsFavoriteCallback);
        }
    }


    //LoaderCallback class for review Loader
    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<MovieReview>>{

        @NonNull
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
            return new FetchReviewsLoader(mContext, mMovieId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
            if(data.size() > 0) {
                mReviewList = data;
                showReviews();
            }else{
                Toast.makeText(MovieDetailActivity.this, getString(R.string.no_reviews), Toast.LENGTH_SHORT).show();

            }
        }



        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

        }
    }


    //callback class for Loader to check if Movie is in DB
    private class CheckForFavoriteCallback implements LoaderManager.LoaderCallbacks<Cursor>{


        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CheckForFavoriteLoader(MovieDetailActivity.this, mMovieId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            //if movie is not currently in the DB, data.moveToFirst will return false, if match it will return true
            mIsFavorite = data.moveToFirst();
            //update menu
            invalidateOptionsMenu();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    }




}
