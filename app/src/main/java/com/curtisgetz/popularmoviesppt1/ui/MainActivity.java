package com.curtisgetz.popularmoviesppt1.ui;



import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.PosterGridAdapter;
import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteCursorAdapter;
import com.curtisgetz.popularmoviesppt1.utils.FetchMoviesTaskLoader;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        PosterGridAdapter.PosterClickListener, LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SORT_BY_POPULAR = 0;
    private static final int SORT_BY_TOP_RATED = 1;
    private static final int MOVIE_LIST_LOADER_ID = 15;
    private static final int RECYCLERVIEW_GRID_SPACING = 4;
    private static final int DEFAULT_COLUMN_COUNT = 3;

    private PosterGridAdapter mAdapter;
    private FavoriteCursorAdapter mCursorAdapter;
    private ArrayList<Movie> mMainMovieList;
    private int mSortBy;
    private int mPageNumber = 1;
    private Handler mHandler;
    private boolean mIsSW600;
    private LoaderManager.LoaderCallbacks<Cursor> mFavoriteCallback;
    private FavoriteCursorAdapter test;

    @BindView(R.id.main_gridview) RecyclerView mPosterRecyclerView;
    @BindView(R.id.tv_error_loading) TextView mErrorTextView;
    @BindView(R.id.loading_progress) ProgressBar mLoadingProgress;



    //TODO   restore when coming back from DetailActivity.  Currently duplicating movies


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "MAIN On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new Handler();


        int columnCount = calculateColumnCount();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        mPosterRecyclerView.setLayoutManager(gridLayoutManager);
        mPosterRecyclerView.addItemDecoration(new RecyclerViewDivider(RECYCLERVIEW_GRID_SPACING));
        //Initialize loader
        getSupportLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, null, this);
        mMainMovieList = new ArrayList<>();
        //check for savedInstanceState
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.save_key))){
            loadMovies();
        }else{
            mMainMovieList = savedInstanceState.getParcelableArrayList(getString(R.string.save_key));
            updateUI();
        }
        //create new PosterGridAdapter
        mAdapter = new PosterGridAdapter(this, mMainMovieList,  mPosterRecyclerView, mIsSW600);
        //Scroll loading listener
        mAdapter.setOnLoadMoreListener(new PosterGridAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showLoadingProgress();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMovies();
                        mAdapter.setLoaded();
                    }
                }, 2000);

            }
        });

    }


    @Override
    protected void onStart() {
        Log.v(TAG, "On Start : " + String.valueOf(mPageNumber));
        Log.v(TAG, "On Start : " + String.valueOf(mMainMovieList.size()));

        super.onStart();

    }

    private int calculateColumnCount(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthInDIP = Math.round(dm.widthPixels / dm.density);
        if(widthInDIP > 600){
            mIsSW600 = true;
            //return number of columns to use
            return 5;
        }else{
            mIsSW600 = false;
            return DEFAULT_COLUMN_COUNT;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.save_key), mMainMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPosterClick(int clickedPosterIndex) {
       if(mMainMovieList.get(clickedPosterIndex) == null){
           Toast.makeText(MainActivity.this, getString(R.string.loading_error), Toast.LENGTH_SHORT)
                   .show();
           return;
       }
        //get selected movie
        Movie movieToPass = mMainMovieList.get(clickedPosterIndex);
        // create intent for MovieDetailActivity
        Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
        //put movie as extra
        intent.putExtra(getString(R.string.movie_to_pass), movieToPass);
       //Start activity
        startActivity(intent);
    }





    public void hideProgressBar(){
      // mProgressBar.setVisibility(View.INVISIBLE);
       mPosterRecyclerView.setVisibility(View.VISIBLE);
    }
    public void showLoadingProgress(){
        mLoadingProgress.setVisibility(View.VISIBLE);
    }
    public void hideLoadingProgress(){
        mLoadingProgress.setVisibility(View.INVISIBLE);
    }



    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new FetchMoviesTaskLoader(this, mSortBy, mPageNumber);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        //mAdapter.setData(data);

        hideLoadingProgress();
        //if null is returned then display toast
        if(data == null) {
            unableToLoadMoreToast();
        } else {
            //add loaded movies to current list
            mMainMovieList.addAll(data);
            updateUI();
            mPageNumber++;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
       // mAdapter.setData(null);

    }
    private void clearUI(){
        showLoadingProgress();
        mMainMovieList.clear();
        mAdapter.notifyDataSetChanged();
        //reset page number for network calls
        mPageNumber = 1;
    }

    private void noNetworkToast(){
        hideLoadingProgress();
        Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
    }

    private void unableToLoadMoreToast(){
        Toast.makeText(this, getString(R.string.unable_to_load), Toast.LENGTH_SHORT).show();
    }


    public void updateUI(){
        //notify data set changed or set adapter
        if(mPosterRecyclerView.getAdapter() != null){
            mAdapter.notifyDataSetChanged();
            hideProgressBar();
        }else {
            mPosterRecyclerView.setAdapter(mAdapter);
            hideProgressBar();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu for sort options
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO update menu for sorting

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Hide menu options for current sort, show menu option for available sort
        MenuItem popularItem = menu.findItem(R.id.action_sort_popular);
        MenuItem topRatedItem = menu.findItem(R.id.action_sort_top_rated);
        switch (mSortBy){
            case SORT_BY_POPULAR:
                popularItem.setVisible(false);
                topRatedItem.setVisible(true);
                return true;
            case SORT_BY_TOP_RATED:
                popularItem.setVisible(true);
                topRatedItem.setVisible(false);
                return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle sort option selections
        int clickedItem = item.getItemId();
        switch (clickedItem){
            //set sort order and load movies
            case R.id.action_sort_popular:
                clearUI();
                mSortBy = SORT_BY_POPULAR;
                loadMovies();
                //update menu
                invalidateOptionsMenu();
                return true;
            case R.id.action_sort_top_rated:
                clearUI();
                mSortBy = SORT_BY_TOP_RATED;
                loadMovies();
                //update menu
                invalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMovies(){
        Log.v(TAG, "loadMovies() - " + String.valueOf(mPageNumber));
        if(!isOnline()){
            noNetworkToast();
        }else {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LIST_LOADER_ID);
            if (movieLoader == null) {
                loaderManager.initLoader(MOVIE_LIST_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(MOVIE_LIST_LOADER_ID, null, this);
            }
        }
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





}
