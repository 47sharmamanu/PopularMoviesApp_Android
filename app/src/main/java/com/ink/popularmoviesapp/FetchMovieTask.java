package com.ink.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ink.popularmoviesapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created & Developed by Manu Sharma on 10/2/2016.
 */

public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
    public static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    Context mContext;

    FetchMovieTask(Context context) {
        mContext = context;
    }

    @Override
    protected Movie[] doInBackground(String... param) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        final String CATEGORY = Utility.getPreferedFilterForMovie(mContext);


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            final String FORECAST_BASE_URL =
                    "    http://api.themoviedb.org/3/movie/";
            final String APP_KEY = "api_key";

            String convertedUrl = FORECAST_BASE_URL + CATEGORY + "?";

            Uri builtUri = Uri.parse(convertedUrl).buildUpon()
                    .appendQueryParameter(APP_KEY, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            long requestUploaded = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                requestUploaded += line.getBytes().length;
                Log.e(LOG_TAG, requestUploaded + "");
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            Log.v(LOG_TAG, movieJsonStr);
            return getWeatherDataFromJson(CATEGORY, movieJsonStr);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private Movie[] getWeatherDataFromJson(String category, String movieJsonStr) throws JSONException {

        //items to be extracted
        final String LIST = "results";
        final String POSTER_PATH = "poster_path";
        final String DESCRIPTION = "overview";
        final String TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(LIST);

        //adding category into category table and get id for it.
        long categoryId = addCategoryInDatabase(category);

        //parsing json for movies
        int resultLength = movieArray.length();
        Movie[] movies = new Movie[resultLength];
        for (int i = 0; i < resultLength; i++) {


            // Get the JSON object representing the day
            JSONObject movieObject = movieArray.getJSONObject(i);

            String title = movieObject.getString(TITLE);
            double rating = movieObject.getDouble(RATING);
            String description = movieObject.getString(DESCRIPTION);
            String realeaseDate = movieObject.getString(RELEASE_DATE);
            String posterPath = "http://image.tmdb.org/t/p/w185/" + movieObject.getString(POSTER_PATH);

//            Log.d(LOG_TAG,mTitle+" "+posterPath+" "+mRating+" "+mDescription+" "+realeaseDate);
            addMovieInDatabase(categoryId,title, description, posterPath, realeaseDate, String.valueOf(rating));
            movies[i] = new Movie(title, description, posterPath, realeaseDate, rating);

        }
        return movies;
    }

    long addMovieInDatabase(long categoryId, String title, String description, String posterUrl, String releaseDate, String rating) {
        final long movieId;

        //First check if movie with the same name already exist in the Database.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_NAME + " = ?",
                new String[]{title},
                null
        );

        if (null != movieCursor && movieCursor.moveToFirst()) {
            //movie with same mTitle already exist in the DB so return the id of same.
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
        } else {
            //movie with this mTitle doesn't exist and we are going to insert it as record.
            //First create the contentValues object with values, which we want to store in db.
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_CATEGORY_KEY, categoryId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_NAME, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, description);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, posterUrl);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

            //Finally insert the data and store the returned URI.
            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            //Log if record inserted correctly.
            Log.d(LOG_TAG, "Record inserted at: " + insertedUri);

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }
        return movieId;
    }

    long addCategoryInDatabase(String category) {
        final long categoryId;

        //first check if category already exists
        Cursor categoryCursor = mContext.getContentResolver().query(
                MovieContract.CategoryEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.CategoryEntry.COLUMN_NAME + " = ?",
                new String[]{category},
                null
        );

        if(null!=categoryCursor&&categoryCursor.moveToFirst()){
            int index = categoryCursor.getColumnIndex(MovieContract.CategoryEntry._ID);
            categoryId = categoryCursor.getLong(index);
        }else {
            //movie with this mTitle doesn't exist and we are going to insert it as record.
            //First create the contentValues object with values, which we want to store in db.
            ContentValues categoryValues = new ContentValues();

            categoryValues.put(MovieContract.CategoryEntry.COLUMN_NAME, category);

            //Finally insert the data and store the returned URI.
            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.CategoryEntry.CONTENT_URI,
                    categoryValues
            );

            //Log if record inserted correctly.
            Log.d(LOG_TAG, "Record inserted at: " + insertedUri);

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            categoryId = ContentUris.parseId(insertedUri);
        }
        return categoryId;
    }
}
