package com.curtisgetz.popularmoviesppt1.utils;

import android.net.Uri;
import android.util.Log;

import com.curtisgetz.popularmoviesppt1.data.Movie;
import com.curtisgetz.popularmoviesppt1.data.movie_review.MovieReview;
import com.curtisgetz.popularmoviesppt1.data.movie_video.MovieVideo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //TODO **** Add API Key Here ****
    private static final String MY_API = Config.API_KEY;
    //TODO **** Add API Key Here ****
    /*
    *Sign up to request an API key
    * https://www.themoviedb.org/account/signup
    */


    // JSON URI parts
    private static final String API_KEY = "api_key";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    //Image size options:  "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    //Image Size Options
    private static final String IMAGE_SIZE_W92 = "w92";
    private static final String IMAGE_SIZE_W154 = "w154";
    private static final String IMAGE_SIZE_W185 = "w185";
    private static final String IMAGE_SIZE_W342 = "w342";
    private static final String IMAGE_SIZE_W500 = "w500";
    private static final String IMAGE_SIZE_W780 = "w780";
    private static final String IMAGE_SIZE_ORIGINAL = "original";

    //default poster size to w185
    private static String POSTER_IMAGE_SIZE = "w185";
    //default bg image size to 780
    private static String BG_IMAGE_SIZE = "w780";
    //Search category
    private static final String SEARCH_CATEGORY = "movie";
    private static final String SEARCH_VIDEOS = "videos";

    private static final String SEARCH_REVIEWS = "reviews";

    private static final String PAGE_NUMBER_KEY = "page";

    //Default to popular search
    private final static String SEARCH_POPULAR = "popular";
    private final static String SEARCH_TOP_RATED = "top_rated";

    private final static String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
    private final static String YOUTUBE_QUERY_PARAM = "v";



    public static String getBasePosterUrl(boolean isSW600){
        if(isSW600){
            //use w500 image if device width is greater than 600
            POSTER_IMAGE_SIZE = IMAGE_SIZE_W500;
        }
        return BASE_IMAGE_URL + POSTER_IMAGE_SIZE;
    }

    public static String getBaseBGImageUrl(){
        return BASE_IMAGE_URL + BG_IMAGE_SIZE;
    }


    public static List<Movie> getMainMovies (int sortBy, int pageNumber){
        //Check sort type passed
        String searchType;
        if(sortBy == 1){
            searchType = SEARCH_TOP_RATED;
        }else{
            searchType = SEARCH_POPULAR;
        }

        if(!(pageNumber > 0)){
            pageNumber = 1;
        }
        //Create new List of Movies
        List<Movie> mainMovies;
        try {
            URL url = buildMovieGridUrl(searchType, pageNumber);
            String movieJsonResponse = getJsonResponse(url);
            mainMovies = JsonUtils.getMainMovieList(movieJsonResponse);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return mainMovies;

    }

    public static List<MovieVideo> getTrailerList (Movie movie){

        URL url = buildMovieDetailUrl(movie);
        String jsonResponse = null;
        try {
            jsonResponse = getJsonResponse(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Add list of trailers to Movie object
        return JsonUtils.getMovieTrailerList(jsonResponse);

    }


    public static List<MovieReview> getReviewList(int movieID){
        String movieIdString = String.valueOf(movieID);
        URL url = buildReviewUrl(movieIdString);
        String jsonResponse = null;
        try {
            jsonResponse = getJsonResponse(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        //return List of reviews or null if error
        return JsonUtils.getMovieReviewList(jsonResponse);
    }

    public static Uri buildVideoIntentUri(String trailerKey){

        return Uri.parse(BASE_YOUTUBE_URL)
                .buildUpon().appendQueryParameter(YOUTUBE_QUERY_PARAM, trailerKey)
                .build();

    }


    private static URL buildReviewUrl(String movieID){
        //build url to request reviews of movie by movieID
        Uri buildUri = Uri.parse(BASE_URL)
                .buildUpon().appendPath(SEARCH_CATEGORY)
                .appendPath(movieID)
                .appendPath(SEARCH_REVIEWS)
                .appendQueryParameter(API_KEY, MY_API)
                .build();

        URL url = null;
        try{
            url = new URL(buildUri.toString());
            String a = buildUri.toString();
            Log.v(TAG, a);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        //return the url to use, or null if error
        return url;
    }

    private static URL buildMovieDetailUrl(Movie movie){
        String movieID = String.valueOf(movie.getmId());

        Uri buildUri = Uri.parse(BASE_URL)
                .buildUpon().appendPath(SEARCH_CATEGORY)
                .appendPath(movieID)
                .appendPath(SEARCH_VIDEOS)
                .appendQueryParameter(API_KEY, MY_API)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    private static URL buildMovieGridUrl(String searchType, int pageNumber){

        String page = String.valueOf(pageNumber);
        //build URI, append search cat and search type then api key/value
        Uri buildUri = Uri.parse(BASE_URL)
                .buildUpon().appendPath(SEARCH_CATEGORY)
                .appendPath(searchType)
                .appendQueryParameter(API_KEY, MY_API)
                .appendQueryParameter(PAGE_NUMBER_KEY, page)
                .build();

        // set url to null. If creating the new URL fails then null will be returned.
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }


    private static String getJsonResponse(URL url) throws IOException{
        //create connection, inputstream and scanner
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        InputStream inputStream = urlConnection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        try {
            // try scanning inputstream
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }else {
                return null;
            }

        } finally {
            scanner.close();
            urlConnection.disconnect();
        }

    }



}
