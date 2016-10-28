package com.ink.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created & Developed by Manu Sharma on 10/26/2016.
 */

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //this are the integer value which is matched with relative URIs
    static final int MOVIE = 100;
    static final int MOVIE_WITH_CATEGORY = 101;
    static final int CATEGORY = 102;

    private MovieDBHelper mOpenHelper;

    /*selection strings for various cases*/

    //if selecting a movie with category the selection string should be
    //movie.category_id = ?
    private static final String sMovieWithCategory = MovieContract.CategoryEntry.TABLE_NAME
            + "." + MovieContract.CategoryEntry.COLUMN_NAME + " =?";

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();

        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.CategoryEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_CATEGORY_KEY +
                        " = " + MovieContract.CategoryEntry.TABLE_NAME +
                        "." + MovieContract.CategoryEntry._ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor resultCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                resultCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY:
                resultCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_CATEGORY:
                resultCursor = getMovieWithCategory(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;
    }

    private Cursor getMovieWithCategory(Uri uri, String[] projection, String sortOrder) {
        String category = uri.getPathSegments().get(1);

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithCategory,
                new String[]{category},
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //First extract the match integer from the uri parameter.
        final int match = sUriMatcher.match(uri);

        //on basis of match integer return the respective type of result.
        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case CATEGORY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_CATEGORY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //get a instance of writable database.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //create a uri variable to return the row.
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case MOVIE:
                //insert the values.
                long movieInsertedId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

                //validate if value was inserted or operation was failed.
                if (movieInsertedId > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(movieInsertedId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CATEGORY:
                //insert the values.
                long categoryInsertedId = db.insert(MovieContract.CategoryEntry.TABLE_NAME, null, contentValues);

                //validate if value was inserted or operation was failed.
                if (categoryInsertedId > 0)
                    returnUri = MovieContract.CategoryEntry.buildMovieUri(categoryInsertedId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //notify this uri if used somewhere.
        getContext().getContentResolver().notifyChange(uri, null);

        //return the row uri for recently inserted row.
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_CATEGORY, CATEGORY);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_CATEGORY);
        // 3) Return the new matcher!
        return uriMatcher;
    }
}
