package com.ink.popularmoviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created & Developed by Manu Sharma on 10/25/2016.
 */
/*This is the class contains all the constants and URIs for content provider and database tables in DB.
* This is used as a contract to all the database and providers calls.*/
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device. It can be termed as url of the webservices.
    public static final String CONTENT_AUTHORITY = "com.ink.popularmoviesapp.provider";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.ink.popularmoviesapp/movie/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_CATEGORY = "category";

    /*creating entry table for category*/
    public static final class CategoryEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        //below are the content types which will tell system weather to return a single row
        //or multiple rows.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "category";

        //column to store title of the movie, type string
        public static final String COLUMN_NAME = "category_name";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*Creating entry table for Movie table*/
    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        //below are the content types which will tell system weather to return a single row
        //or multiple rows.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_CATEGORY_KEY = "category_id";

        //column to store title of the movie, type string
        public static final String COLUMN_NAME = "movie_name";

        //column to store string description of the movie.
        public static final String COLUMN_DESCRIPTION = "movie_description";

        //column to store string description of the movie.
        public static final String COLUMN_POSTER_URL = "movie_poster_url";

        //column to store string description of the movie.
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";

        //column to store string description of the movie.
        public static final String COLUMN_RATING = "movie_rating";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithCategory(String category){
            return CONTENT_URI.buildUpon().appendPath(category).build();
        }
    }
}
