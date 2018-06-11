package com.curtisgetz.popularmoviesppt1.utils;

import android.util.Log;

import com.curtisgetz.popularmoviesppt1.R;
import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    //JSON KEYS for main movie data
    private static final String RESULTS_ARRAY_KEY = "results";
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String MOVIE_ID_KEY = "id";
    private static final String POSTER_URL_KEY = "poster_path";
    private static final String SYNOPSIS_KEY = "overview";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String BACKDROP_URL_KEY = "backdrop_path";

    //JSON KEYS for movie detail
    private static final String VIDEO_ID_KEY = "id";
    private static final String VIDEO_KEY_KEY = "key";
    private static final String VIDEO_SITE_KEY = "site";
    private static final String VIDEO_TYPE_KEY = "type";
    private static final String VIDEO_NAME_KEY = "name";
    private static final String VIDEO_LANG_KEY = "iso_639_1";

    //JSON Keys for movie reviews
    private static final String REVIEW_AUTHOR_KEY = "author";
    private static final String REVIEW_CONTENT_KEY = "content";
    private static final String REVIEW_ID_KEY = "id";
    private static final String REVIEW_URL_KEY = "url";

    //figure out best way to move to strings.xml
    private static final String FALLBACK_STRING = "Unknown";


    public static List<Movie> getMainMovieList(String json)  {
        //create new list to store new Movie objects
        List<Movie> listOfMovieObjects = new ArrayList<>();
        try {
            //Get main JSON object
            JSONObject queryJsonObject = new JSONObject(json);
            //Get results JSON object
            JSONArray resultsJsonArray = queryJsonObject.getJSONArray(RESULTS_ARRAY_KEY);

            //Variables for Movie object
            String title, releaseDate, posterUrl, synopsis, bgImageUrl;
            double voteAverage;
            int movieId;

            //iterate through objects inside results array and assign values
            for(int i = 0; i < resultsJsonArray.length(); i++){
                JSONObject singleMovieObject = resultsJsonArray.getJSONObject(i);
                title = singleMovieObject.optString(TITLE_KEY, FALLBACK_STRING);
                releaseDate = singleMovieObject.optString(RELEASE_DATE_KEY, FALLBACK_STRING);
                posterUrl = singleMovieObject.optString(POSTER_URL_KEY, null);
                synopsis = singleMovieObject.optString(SYNOPSIS_KEY,FALLBACK_STRING);
                voteAverage = singleMovieObject.optDouble(VOTE_AVERAGE_KEY, 0.0);
                movieId = singleMovieObject.optInt(MOVIE_ID_KEY, 0);
                bgImageUrl = singleMovieObject.optString(BACKDROP_URL_KEY, FALLBACK_STRING);
                //Add new movie object to ArrayList
                listOfMovieObjects.add(new Movie(movieId, voteAverage, title, releaseDate,
                        posterUrl, synopsis, bgImageUrl));
            }

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return listOfMovieObjects;
    }



    public static List<MovieVideo> getMovieTrailerList(String json){
        List<MovieVideo> movieVideoList = new ArrayList<>();
        //variables for MovieVideo object
        String videoID, videoName, videoLanguage, videoKey, videoSite, videoType;

        try {
            JSONObject queryJsonObject = new JSONObject(json);
            JSONArray resultsJsonArray = queryJsonObject.getJSONArray(RESULTS_ARRAY_KEY);

            for(int i = 0; i < resultsJsonArray.length(); i++){
               JSONObject videoObject = resultsJsonArray.getJSONObject(i);
               videoID = videoObject.optString(VIDEO_ID_KEY, FALLBACK_STRING);
               videoName = videoObject.optString(VIDEO_NAME_KEY, FALLBACK_STRING);
               videoLanguage = videoObject.optString(VIDEO_LANG_KEY, FALLBACK_STRING);
               videoKey = videoObject.optString(VIDEO_KEY_KEY, FALLBACK_STRING);
               videoSite = videoObject.optString(VIDEO_SITE_KEY, FALLBACK_STRING);
               videoType = videoObject.optString(VIDEO_TYPE_KEY, FALLBACK_STRING);
               //add new MovieVideo object to ArrayList
               // Log.v(TAG, videoName );
                movieVideoList.add(new MovieVideo(videoID, videoName, videoLanguage, videoKey,
                        videoSite, videoType));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return movieVideoList;

    }


    public static List<MovieReview> getMovieReviewList(String json) {
        List<MovieReview> reviewList = new ArrayList<>();
        String author, id, url, content;


        try {
            JSONObject resultJsonObject = new JSONObject(json);
            JSONArray resultsJsonArray = resultJsonObject.getJSONArray(RESULTS_ARRAY_KEY);

            for(int i = 0; i < resultsJsonArray.length(); i++){
                JSONObject reviewObject = resultsJsonArray.getJSONObject(i);
                author = reviewObject.optString(REVIEW_AUTHOR_KEY, FALLBACK_STRING);
                id = reviewObject.optString(REVIEW_ID_KEY, FALLBACK_STRING);
                url = reviewObject.optString(REVIEW_URL_KEY, FALLBACK_STRING);
                content = reviewObject.optString(REVIEW_CONTENT_KEY, FALLBACK_STRING);
                // add new MovieReview to ArrayList
                Log.v(TAG, author + " : " + String.valueOf(i));
                reviewList.add(new MovieReview(id, author, content, url));
            }

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }


        return reviewList;
    }



}
