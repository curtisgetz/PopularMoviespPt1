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
import com.curtisgetz.popularmoviesppt1.data.favorite_data.FavoriteContract;
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
    private int mSortBy;
    private int mPageNumber = 1;
    private Handler mHandler;
    private boolean mIsSW600;
    private LoaderManager.LoaderCallbacks<Cursor> mFavoriteCallback;
    private ArrayList<Movie> mMainMovieList = new ArrayList<>();


    @BindView(R.id.main_gridview) RecyclerView mPosterRecyclerView;
    @BindView(R.id.tv_error_loading) TextView mErrorTextView;
    @BindView(R.id.loading_progress) ProgressBar mLoadingProgress;


    //TODO   restore when coming back from DetailActivity.  Currently duplicating movies


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
       // mHandler = new Handler();
        //callback for Favorites Loader
        mFavoriteCallback = new FavoriteCallback();
        //get number of columns based on SW600
        int columnCount = calculateColumnCount();
        //create LayoutManager and set it to RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
        mPosterRecyclerView.setLayoutManager(gridLayoutManager);
        //add divider to RV
        mPosterRecyclerView.addItemDecoration(new RecyclerViewDivider(RECYCLERVIEW_GRID_SPACING));

        //create new PosterGridAdapter
        mAdapter = new PosterGridAdapter(
                this, mPosterRecyclerView, mIsSW600, mMainMovieList);
        //check for savedInstanceState
        checkForSavedInstanceState(savedInstanceState);


        mLoadingProgress.setVisibility(View.INVISIBLE);
        //Scroll loading listener
    //Temp disabled scroll listener.  Was having problems and wanted to complete the project
    // and concentrate on the course material
       //setScrollListener();

    }




  /*  public void setScrollListener(){

        if(mAdapter != null && mSortBy != SORT_BY_FAVORITES){
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
    }*/






    @Override
    protected void onRestart() {
        if(mSortBy == SORT_BY_FAVORITES){
            //update favorites when restarting
            clearUI();
            loadMovies();
        }
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Movie> movies = new ArrayList<>(mAdapter.getmMovieList());
        outState.putParcelableArrayList(getString(R.string.save_key), movies);
        outState.putInt(getString(R.string.page_number_key), mPageNumber);
        outState.putInt(getString(R.string.sort_by_key), mSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        checkForSavedInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void checkForSavedInstanceState(Bundle savedInstanceState){
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.save_key))){
            loadMovies();
        }else{
            mMainMovieList = savedInstanceState.getParcelableArrayList(getString(R.string.save_key));
            mAdapter = new PosterGridAdapter(this, mPosterRecyclerView, isOnline(), mMainMovieList);
            mPosterRecyclerView.setAdapter(mAdapter);
            mPageNumber = savedInstanceState.getInt(getString(R.string.page_number_key));
            mSortBy = savedInstanceState.getInt(getString(R.string.sort_by_key));
        }
    }




    private int calculateColumnCount(){
        // use more columns on larger screens
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
        //mLoadingProgress.setVisibility(View.VISIBLE);
    }
    public void hideLoadingProgress(){
        //mLoadingProgress.setVisibility(View.INVISIBLE);
    }


    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new FetchMoviesTaskLoader(this, mSortBy, mPageNumber);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {

        //having problems with loader adding movies when I don't want them.  Using this as a workaround for now.
        //need to figure out what I'm doing wrong
        if(mSortBy == SORT_BY_FAVORITES){
            return;
        }
        hideLoadingProgress();
        hideProgressBar();
        //if null is returned then display toast
        if(data == null) {
            unableToLoadMoreToast();
        } else {
            updateUI(data);
            mPageNumber++;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {


    }
    private void clearUI(){
        showLoadingProgress();
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        //reset page number for network calls (for scroll listener)
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
            //change back to AddData if I reenable the scroll listener
            mAdapter.setData(new ArrayList<>(movieList));
            //mAdapter.notifyDataSetChanged();
        }else {
            mPosterRecyclerView.setAdapter(mAdapter);
            mAdapter.setData(new ArrayList<>(movieList));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflat emenu for sort options
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Hide menu options for current sort, show menu option for available sort
        // Change menu style later
        MenuItem popularItem = menu.findItem(R.id.action_sort_popular);
        MenuItem topRatedItem = menu.findItem(R.id.action_sort_top_rated);
        MenuItem favoriteItem = menu.findItem(R.id.action_sort_favorites);
        MenuItem titleItem = menu.findItem(R.id.action_current_title);
        titleItem.setEnabled(false);
        switch (mSortBy){
            case SORT_BY_POPULAR:
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
        if(!isOnline()){
            noNetworkToast();
        }else {
            LoaderManager loaderManager = getSupportLoaderManager();
            switch (mSortBy){
                case SORT_BY_POPULAR:
                case SORT_BY_TOP_RATED:

                    Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LIST_LOADER_ID);
                    if (movieLoader == null) {
                        loaderManager.initLoader(MOVIE_LIST_LOADER_ID, null, this);
                    } else {
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

    //callback class for favorites Loader
    private class FavoriteCallback implements LoaderManager.LoaderCallbacks<Cursor>{

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new PullFavoritesLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

            if(data == null) {
                unableToLoadMoreToast();
            } else {
                List<Movie> movies = getMoviesFromCursor(data);
                updateUI(movies);
                getSupportLoaderManager().destroyLoader(FAVORITES_LOADER_ID);
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
                //indices for columns
                int movieIdIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);
                int ratingIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RATING);
                int titleIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
                int releaseDateIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE);
                int synopsisIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS);
                int bgImageIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BG_URL);
                int posterUrlIndex = cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL);

                //get values
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
