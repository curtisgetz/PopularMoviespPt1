package com.curtisgetz.popularmoviesppt1.utils;

import com.curtisgetz.popularmoviesppt1.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    //Default to popular search
    private final static String SEARCH_POPULAR = "/popular";
    private final static String SEARCH_TOP_RATED = "/top_rated";
    private String SEARCH_TYPE = SEARCH_POPULAR;

    //JSON KEYS
    private static final String RESULTS_ARRAY_KEY = "results";
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String MOVIE_ID_KEY = "id";
    private static final String POSTER_URL_KEY = "poster_path";
    private static final String SYNOPSIS_KEY = "overview";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String BACKDROP_URL_KEY = "backdrop_path";

    private static final String FALLBACK_STRING = "Unknown";



    public static List<Movie> getMainMovieList(String url)  {
        //create new list to store new Movie objects
        List<Movie> listOfMovieObjects = new ArrayList<>();
        try {
            //Get main JSON object
            JSONObject queryJsonObject = new JSONObject(url);
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


}
