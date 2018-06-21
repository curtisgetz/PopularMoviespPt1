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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.curtisgetz.popularmoviesppt1.data.CustomSpinnerAdapter;
import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.PosterGridAdapter;
import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteCursorAdapter;
import com.curtisgetz.popularmoviesppt1.utils.FetchMoviesTaskLoader;
import com.curtisgetz.popularmoviesppt1.utils.PullFavoritesLoader;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        PosterGridAdapter.PosterClickListener, LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SORT_BY_POPULAR = 0;
    private static final int SORT_BY_TOP_RATED = 1;
    private static final int SORT_BY_FAVORITES = 2;

    private static final int MOVIE_LIST_LOADER_ID = 15;
    private static final int FAVORITES_LOADER_ID = 30;

    private static final int RECYCLERVIEW_GRID_SPACING = 4;
    private static final int DEFAULT_COLUMN_COUNT = 3;

    private PosterGridAdapter mAdapter;
    private FavoriteCursorAdapter mCursorAdapter;
   // private ArrayList<Movie> mMainMovieList = new ArrayList<>();
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
        //callback for Favortites Loader
        mFavoriteCallback = new FavoriteCallback();
        //get number of columns based on SW600
        int columnCount = calculateColumnCount();
        //create LayoutManager and set it to RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        mPosterRecyclerView.setLayoutManager(gridLayoutManager);
        //add divider to RV
        mPosterRecyclerView.addItemDecoration(new RecyclerViewDivider(RECYCLERVIEW_GRID_SPACING));

        //check for savedInstanceState
        checkForSavedInstanceState(savedInstanceState);


        //create new PosterGridAdapter
        mAdapter = new PosterGridAdapter(
                this, mPosterRecyclerView, mIsSW600);
        //Scroll loading listener
    //Temp disabled scroll listener.  Was having problems and wanted to complete the project
    // and concentrate on the course material
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
       // Log.v(TAG, "On Start : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));

        super.onStart();

    }


    @Override
    protected void onStop() {
      //  mMainMovieList.clear();
        Log.v(TAG, "On Stop : " + String.valueOf(mPageNumber));
        //Log.v(TAG, "On Stop : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "On Destroy : " + String.valueOf(mPageNumber));
       // Log.v(TAG, "On Destroy : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "On Resume : " + String.valueOf(mPageNumber));
       // Log.v(TAG, "On Resume : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));
        super.onResume();
    }


    @Override
    protected void onPause() {
        Log.v(TAG, "On Pause : " + String.valueOf(mPageNumber));
       // Log.v(TAG, "On Pause : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.v(TAG, "On Restart : " + String.valueOf(mPageNumber));
        //Log.v(TAG, "On ReStart : " + String.valueOf(mMainMovieList.size()));
        Log.v(TAG, "mSortBy = " + String.valueOf(mSortBy));
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        ArrayList<Movie> movies = new ArrayList<>(mAdapter.getmMovieList());
        outState.putParcelableArrayList(getString(R.string.save_key), movies);
        outState.putInt(getString(R.string.page_number_key), mPageNumber);
        outState.putInt(getString(R.string.sort_by_key), mSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "onRestoreInstanceState");
        //checkForSavedInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
        //updateUI();
    }

    public void checkForSavedInstanceState(Bundle savedInstanceState){
        Log.v(TAG, "checkForSaveInstanceState");
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.save_key))){
            Log.v(TAG, "Check For Save Instance State = IF");
            loadMovies();
        }else{
            Log.v(TAG, "Check For Save Instance State = ELSE");
            //mMainMovieList = savedInstanceState.getParcelableArrayList(getString(R.string.save_key));
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(getString(R.string.save_key));
            mAdapter.setData(movies);
            mPageNumber = savedInstanceState.getInt(getString(R.string.page_number_key));
            mSortBy = savedInstanceState.getInt(getString(R.string.sort_by_key));

        }
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
    public void onPosterClick(int clickedPosterIndex) {
        Log.v(TAG, "onPosterClick");
        List<Movie> movies = mAdapter.getmMovieList();
       if(movies.get(clickedPosterIndex) == null){
           Toast.makeText(MainActivity.this, getString(R.string.loading_error), Toast.LENGTH_SHORT)
                   .show();
           return;
       }
        getSupportLoaderManager().destroyLoader(MOVIE_LIST_LOADER_ID);
        //get selected movie
        Movie movieToPass = movies.get(clickedPosterIndex);
        // create intent for MovieDetailActivity
        Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
        //put movie as extra
        intent.putExtra(getString(R.string.movie_to_pass), movieToPass);
       //Start activity
        startActivity(intent);
    }





    public void hideProgressBar(){
        Log.v(TAG, "hideProgressBar");
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
        Log.v(TAG, "onCreateLoader - List<Movie>");
        return new FetchMoviesTaskLoader(this, mSortBy, mPageNumber);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        //mAdapter.setData(data);
        Log.v(TAG, "onLoadFinished - List<Movie>");
        hideLoadingProgress();
        hideProgressBar();
        //if null is returned then display toast
        if(data == null) {
            unableToLoadMoreToast();
        } else {
            //add loaded movies to current list
          //  mMainMovieList.addAll(data);
            //mAdapter.addData(data);
            updateUI(data);
            mPageNumber++;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
       // mAdapter.setData(null);

    }
    private void clearUI(){
        showLoadingProgress();

       // mMainMovieList.clear();
        mAdapter.clearData();
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


    public void updateUI(List<Movie> movieList){
        //notify data set changed or set adapter
        hideProgressBar();
        hideLoadingProgress();
        if(mPosterRecyclerView.getAdapter() != null){
            mAdapter.addData(movieList);
            mAdapter.notifyDataSetChanged();
        }else {
            mPosterRecyclerView.setAdapter(mAdapter);
            mAdapter.setData(movieList);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflatemenu for sort options

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO update menu for sorting

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Hide menu options for current sort, show menu option for available sort
        // Change menu style later
        MenuItem popularItem = menu.findItem(R.id.action_sort_popular);
        MenuItem topRatedItem = menu.findItem(R.id.action_sort_top_rated);
        MenuItem favoriteItem = menu.findItem(R.id.action_sort_favorites);
        MenuItem titleItem = menu.findItem(R.id.action_current_title);

        switch (mSortBy){
            case SORT_BY_POPULAR:
                favoriteItem.setIcon(R.drawable.ic_film_roll);
                popularItem.setVisible(false);
                topRatedItem.setVisible(true);
                favoriteItem.setVisible(true);
                titleItem.setTitle(getString(R.string.popular_title));
                return true;
            case SORT_BY_TOP_RATED:
                popularItem.setVisible(true);
                topRatedItem.setVisible(false);
                favoriteItem.setVisible(true);
                titleItem.setTitle(getString(R.string.top_rated_title));
                return true;
            case SORT_BY_FAVORITES:
                popularItem.setVisible(true);
                topRatedItem.setVisible(true);
                favoriteItem.setVisible(false);
                titleItem.setTitle(getString(R.string.favorites_title));

                return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle sort option selections
        //old menu code

        clearUI();
        int clickedItem = item.getItemId();

        switch (clickedItem) {
            //set sort order and load movies
            case R.id.action_sort_popular:
                //clearUI();
                mAdapter.setIsNotFavorites();
                mSortBy = SORT_BY_POPULAR;
                loadMovies();
                //update menu
                invalidateOptionsMenu();
                return true;
            case R.id.action_sort_top_rated:
               // clearUI();
                mAdapter.setIsNotFavorites();
                mSortBy = SORT_BY_TOP_RATED;
                loadMovies();
                //update menu
                invalidateOptionsMenu();
                return true;
            case R.id.action_sort_favorites:
                //clearUI();
                mAdapter.setIsFavorties();
                mSortBy = SORT_BY_FAVORITES;
                loadMovies();
                //update menu
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




    public void loadMovies(){
        Log.v(TAG, "loadMovies() - " + String.valueOf(mPageNumber));
        if(!isOnline()){
            noNetworkToast();
        }else {
            LoaderManager loaderManager = getSupportLoaderManager();
            switch (mSortBy){
                case SORT_BY_POPULAR:
                case SORT_BY_TOP_RATED:


                    Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LIST_LOADER_ID);
                    if (movieLoader == null) {
                        Log.v(TAG, "movieLoader is NULL");
                        loaderManager.initLoader(MOVIE_LIST_LOADER_ID, null, this);
                    } else {
                        Log.v(TAG, "movieLoader in NOT NULL");
                        loaderManager.restartLoader(MOVIE_LIST_LOADER_ID, null, this);
                    }
                    break;
                case SORT_BY_FAVORITES:

                    Loader<Cursor> cursorLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);
                    if(cursorLoader == null){
                        loaderManager.initLoader(FAVORITES_LOADER_ID, null, mFavoriteCallback);
                    }else {
                        loaderManager.restartLoader(FAVORITES_LOADER_ID, null, mFavoriteCallback);
                    }
                    break;

                default:
                    break;
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

    private class FavoriteCallback implements LoaderManager.LoaderCallbacks<Cursor>{

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            Log.v(TAG, "Favorites, onCreateLoader");
            return new PullFavoritesLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

            if(data == null) {
                unableToLoadMoreToast();
            } else {
                //get movies from Cursor and assign to mMainMovieList
                // mMainMovieList.clear();
              // mMainMovieList.addAll(getMoviesFromCursor(data));
                List<Movie> movies = getMoviesFromCursor(data);
                updateUI(movies);
               // adapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }



        private List<Movie> getMoviesFromCursor(Cursor cursor){
            List<Movie> moviesList = new ArrayList<>();

            int rows = cursor.getCount();
            for(int i = 0; i < rows; i++) {

                cursor.moveToPosition(i);
                //indixes for columns
//        int idIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry._ID);
                int movieIdIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);
                int ratingIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RATING);
                int titleIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
                int releaseDateIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE);
                int synopsisIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS);
                int bgImageIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BG_URL);
                int posterUrlIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL);

                //get values
//        final int id = cursor.getInt(idIndex);
                int movieId = cursor.getInt(movieIdIndex);
                double movieRating = cursor.getDouble(ratingIndex);
                String movieTitle = cursor.getString(titleIndex);
                String movieReleaseDate = cursor.getString(releaseDateIndex);
                String movieSynopsis = cursor.getString(synopsisIndex);
                String bgImageUrl = cursor.getString(bgImageIndex);
                String posterUrl = cursor.getString(posterUrlIndex);

                moviesList.add(new Movie(movieId, movieRating, movieTitle, movieReleaseDate,
                        posterUrl, movieSynopsis, bgImageUrl));

            }

            return moviesList;

        }

    }



}
