package com.curtisgetz.popularmoviesppt1.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideo;
import com.curtisgetz.popularmoviesppt1.utils.NetworkUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Movie implements Parcelable{

    //Viewed webcast and example project on Parcelables and onSaveInstanceState() for refresher
    private final static String TAG = Movie.class.getSimpleName();

    private int mId;
    private double mVoteAverage;
    private String mTitle;
    private String mReleaseDate;
    private String mPosterUrl;
    private String mSynopsis;
    private String mBGImage;
    private List<MovieVideo> mMovieVideoList;
    private boolean mIsFavortie;
    private byte[] mImageBytes;


    public Movie(int id, String posterUrl){
        this.mId = id;
        this.mPosterUrl = posterUrl;
    }


    public Movie(int mId, double mVoteAverage, String mTitle, String mReleaseDate,
                 String mPosterUrl, String mSynopsis, String bgImage) {
        this.mId = mId;
        this.mVoteAverage = mVoteAverage;
        this.mTitle = mTitle;
        this.mReleaseDate = mReleaseDate;
        this.mPosterUrl = mPosterUrl;
        this.mSynopsis = mSynopsis;
        this.mBGImage = bgImage;
        this.mIsFavortie = false;

    }

    private Movie(Parcel parcel){
        mId = parcel.readInt();
        mVoteAverage = parcel.readDouble();
        mTitle = parcel.readString();
        mReleaseDate = parcel.readString();
        mPosterUrl = parcel.readString();
        mSynopsis = parcel.readString();
        mBGImage = parcel.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mTitle);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mPosterUrl);
        parcel.writeString(mSynopsis);
        parcel.writeString(mBGImage);


    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public String getVoteAverageString(){
        return String.valueOf(mVoteAverage);
    }

    public void setmVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getLocalizedDateString() {
        /*
        * Used Stackoverflow for help with this
        * https://stackoverflow.com/questions/12503527/
        * how-do-i-convert-the-date-from-one-format-to-another-date-object-in-another-form
        */
        DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat desiredDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        Date date;
        try {
           date = originalDateFormat.parse(mReleaseDate);
       }catch (ParseException e){
           e.printStackTrace();
           return mReleaseDate;
       }

        return desiredDateFormat.format(date);
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmPosterUrl() {
        return mPosterUrl;
    }

    public String getFullPosterUrl(boolean isSW600){
        //return full URL for poster image
/*        String isString = String.valueOf(isSW600);
        String urlString = String.valueOf(mPosterUrl);
        Log.v(TAG, "is SW600 = " + isString + "  ---  " + urlString) ;*/
        return NetworkUtils.getBasePosterUrl(isSW600) + mPosterUrl;
    }

    public void setmPosterUrl(String mPosterUrl) {
        this.mPosterUrl = mPosterUrl;
    }

    public String getmSynopsis() {
        return mSynopsis;
    }

    public void setmSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public String getmBGImage() {
        return mBGImage;
    }

    public String getFullBGImageUrl(){
        //return full URL for background image URL
        return NetworkUtils.getBaseBGImageUrl() + mBGImage;
    }

    public void setmBGImage(String mBGImage) {
        this.mBGImage = mBGImage;
    }

    public List<MovieVideo> getmMovieVideoList() {
        return mMovieVideoList;
    }

    public void setmMovieVideoList(List<MovieVideo> mMovieVideoList) {
        this.mMovieVideoList = mMovieVideoList;
    }

    public void fetchVideoList(){


    }


    public void setIsFavorite(boolean isFavorite){
        this.mIsFavortie = isFavorite;
    }

    public boolean getIsFavorite(){
        return mIsFavortie;
    }

    public byte[] getmImageBytes() {
        return mImageBytes;
    }

    public void setmImageBytes(byte[] mImageBytes) {
        this.mImageBytes = mImageBytes;
    }
}

