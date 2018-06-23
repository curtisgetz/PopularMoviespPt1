package com.curtisgetz.popularmoviesppt1.data.favorite_data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {




    public static final String CONTENT_AUTHORITY = "com.curtisgetz.popularmoviesppt1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    //define paths for accessing data

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        // table and column names
        public static final String TABLE_NAME = "favorites";

        //columns
        //use movie_id as primary key
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_BG_URL = "background_url";
        public static final String COLUMN_IMAGE_DATA = "image_data";


        public static Uri buildFavUriWithID(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id))
                    .build();
        }
    }
}
