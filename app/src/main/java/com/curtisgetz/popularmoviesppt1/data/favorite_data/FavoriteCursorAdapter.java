package com.curtisgetz.popularmoviesppt1.data.favorite_data;


import android.content.Context;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.PosterGridAdapter;

import java.util.ArrayList;
import java.util.List;


public class FavoriteCursorAdapter  {
/*


    private static final String TAG = FavoriteCursorAdapter.class.getSimpleName();


// TODO  figure how to pull data from database and use in UI

    private Cursor mCursor;
    private Context mContext;



    public FavoriteCursorAdapter(PosterClickListener clickListener,
                                 RecyclerView recyclerView, boolean isSW600, Cursor cursor) {

        super(clickListener, recyclerView, isSW600);
        mCursor = cursor;
        //mContext = context;
        super.mMovieList = getFavoriteMovieList();
    }


    public List<Movie> testMovieList() {

        List<Movie> testList = new ArrayList<>();
        testList.add(new Movie(351286, 9.9, "title1",
                "2018-1-21", "/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg",
                "Very good", "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg"));

        testList.add(new Movie(383498, 2.9, "title2",
                "2018-1-21", "/to0spRl1CMDvyUbOnbb4fTk3VAd.jpg",
                "Very good", "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg"));

        return testList;
    }


    private List<Movie> getFavoriteMovieList() {

        List<Movie> returnList = new ArrayList<>();

        if(mCursor != null) {
            int rows = mCursor.getCount();
            for (int i = 0; i < rows; i++) {
                Movie movie = getMovieFromCursor(mCursor, (i));
                returnList.add(movie);
            }
        }
        //TODO  get all rows from database


        return returnList;
    }


    private Movie getMovieFromCursor(Cursor cursor, int position) {


        mCursor.moveToPosition(position);
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

        Movie movieToReturn = new Movie(movieId, movieRating, movieTitle, movieReleaseDate,
                posterUrl, movieSynopsis, bgImageUrl);

        return movieToReturn;


    }


 */
/*   @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //indixes for columns
  *//*
*/
/*      int idIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry._ID);
        int movieIdIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);
        int ratingIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RATING);
        int titleIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
        int releaseDateIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        int synopsisIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS);
        int bgImageIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BG_URL);*//*
*/
/*


        int posterUrlIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL);

        mCursor.moveToPosition(position);  //move to correct position

        //get values
     *//*
*/
/*   final int id = mCursor.getInt(idIndex);
        int movieId = mCursor.getInt(movieIdIndex);
        double movieRating = mCursor.getDouble(ratingIndex);
        String movieTitle = mCursor.getString(titleIndex);
        String movieReleaseDate = mCursor.getString(releaseDateIndex);

        String movieSynopsis = mCursor.getString(synopsisIndex);
        String bgImageUrl = mCursor.getString(bgImageIndex);*//*
*/
/*

        String posterUrl = mCursor.getString(posterUrlIndex);

        Picasso.get().load(posterUrl)
                .placeholder(R.drawable.posterloadingplaceholder185)
                .error(R.drawable.posterplaceholder185)
                .into(

        super.onBindViewHolder(holder, position);


    }*//*


    public Cursor swapCursor(Cursor cursor) {

        if (mCursor == cursor) {
            return null; // noting has changed
        }

        Cursor tempCursor = mCursor;
        this.mCursor = cursor;

        //chgeck if valid cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return tempCursor;
    }



*/

}