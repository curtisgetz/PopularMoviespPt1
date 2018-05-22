package com.curtisgetz.popularmoviesppt1.utils;

import android.net.Uri;

import com.curtisgetz.popularmoviesppt1.Movie;

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
    private static final String MY_API = "";
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
    private static final String POSTER_IMAGE_SIZE = "w185";
    private static final String BG_IMAGE_SIZE = "w780";
    //Search category
    private static final String SEARCH_CATEGORY = "movie";

    private static final String PAGE_NUMBER_KEY = "page";

    //Default to popular search
    private final static String SEARCH_POPULAR = "popular";
    private final static String SEARCH_TOP_RATED = "top_rated";



    public static String getBasePosterUrl(){
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

    private static URL buildMovieGridUrl( String searchType, int pageNumber){

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
