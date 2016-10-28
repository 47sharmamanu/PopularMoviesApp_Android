package com.ink.popularmoviesapp;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ink.popularmoviesapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link AppCompatDialogFragment} subclass.
 */
public class DetailFragment extends AppCompatDialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG= DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    private Uri mUri;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_NAME,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING
    };

    static final int INDEX_ID = 0;
    static final int INDEX_COLUMN_NAME = 1;
    static final int INDEX_COLUMN_DESCRIPTION = 2;
    static final int INDEX_COLUMN_POSTER_URL = 3;
    static final int INDEX_COLUMN_RELEASE_DATE = 4;
    static final int INDEX_COLUMN_RATING = 5;

    TextView mTitle;
    TextView mDescription;
    TextView mRating;
    TextView mReleaseYear ;
    ImageView mPoster ;

    Movie movie;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View view = inflater.inflate(R.layout.activity_detail, container, false);
        mTitle = (TextView) view.findViewById(R.id.title);
        mDescription = (TextView) view.findViewById(R.id.description);
        mRating = (TextView) view.findViewById(R.id.rating);
        mReleaseYear = (TextView) view.findViewById(R.id.release_year);
        mPoster = (ImageView) view.findViewById(R.id.poster);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        if (null != mUri) {
            String rowId = mUri.getPathSegments().get(1);
            /*return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );*/
            return new CursorLoader(
                    getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    "movie._id = ?",
                    new String[]{rowId},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(null!=data&&data.moveToFirst()){
            //Read poster url of the movie from cursor
            String posterUrl = data.getString(3);
            Picasso.with(getActivity()).load(posterUrl).into(mPoster);
            //Read mTitle of the movie from cursor
            String title = data.getString(1);
            mTitle.setText(title);
            //Read mDescription of the movie from cursor
            String description = data.getString(2);
            mDescription.setText(description);
            //Read release date of the movie from cursor
            String releaseDate = data.getString(4);
            mReleaseYear.setText(releaseDate.substring(0, 4));
            //Read mRating of the movie from cursor
            String rating = data.getString(5);
            mRating.setText(rating + "/10");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
