package com.ink.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created & Developed by Manu Sharma on 10/26/2016.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * @param sqLiteDatabase In this method we will write query to create tables and stored procedures.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "onCreate");
        /*Creating a new query to execute which will create a category table in sqlite database with provided constraints in the query*
        /The query we are building is:
        CREATE TABLE category (
        _id INTEGER PRIMARY KEY AUTOINCREMENT,
        category_name TEXT UNIQUE NOT NULL
         );
         */
        final String CATEGORY_TABLE_CREATION_QUERY = "CREATE TABLE " + MovieContract.CategoryEntry.TABLE_NAME + " (" +
                MovieContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.CategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL " +
                " );";

        /*Creating a new query to execute which will create a movie table in sqlite database with provided constraints in the query*
        /The query we are building is:
        CREATE TABLE movie (
        _id INTEGER PRIMARY KEY AUTOINCREMENT,
        category_id INTEGER UNIQUE NOT NULL,
        movie_name TEXT UNIQUE NOT NULL,
        movie_description TEXT NOT NULL,
        movie_poster_url TEXT NOT NULL,
        movie_release_date TEXT NOT NULL,
        movie_rating TEXT NOT NULL
        FOREIGN KEY (category_id) REFERENCES category(_id)
        );
         */
        final String MOVIE_TABLE_CREATION_QUERY = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_CATEGORY_KEY + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +

                //set up category_id as foreign key
                "FOREIGN KEY (" + MovieContract.MovieEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                MovieContract.CategoryEntry.TABLE_NAME + " (" + MovieContract.CategoryEntry._ID + ")" +
                " );";

        //logging queries
        Log.d(LOG_TAG, CATEGORY_TABLE_CREATION_QUERY);
        Log.d(LOG_TAG, MOVIE_TABLE_CREATION_QUERY);

        //executing the query above
        sqLiteDatabase.execSQL(CATEGORY_TABLE_CREATION_QUERY);
        sqLiteDatabase.execSQL(MOVIE_TABLE_CREATION_QUERY);
        Log.d(LOG_TAG, "tables created.");
    }

    /**
     * @param sqLiteDatabase
     * @param i
     * @param i1             Whenever database version number changes this method gets a callback so logic can be return to destroy
     *                       or update previous schema of the tables in db.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(LOG_TAG, "onUpgrade");
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.CategoryEntry.TABLE_NAME);
        Log.d(LOG_TAG, "tables dropped");
        onCreate(sqLiteDatabase);
    }
}
