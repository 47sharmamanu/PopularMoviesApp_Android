package com.ink.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ink.popularmoviesapp.data.MovieContract;

/**
 * Created & Developed by Manu Sharma on 10/1/2016.
 */

public class MovieListFragment extends AppCompatDialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    MovieListAdapter movieListAdapter;

    private static final int MOVIE_LOADER_ID = 0;

    private String mCategory;

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

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {

        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri contentUri);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(Utility.LIFE_CYCLE_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(Utility.LIFE_CYCLE_TAG, "onAttach");
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(Utility.LIFE_CYCLE_TAG, "onCreateView");
        movieListAdapter = new MovieListAdapter(getActivity(), null, 0);
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        //find list view
        GridView mMovieListView = (GridView) view.findViewById(R.id.movie_grid_view);
        //setAdapter to list view
        mMovieListView.setAdapter(movieListAdapter);
        //set onItemClickListener on ListView
        mMovieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferedFilterForMovie(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(INDEX_ID)));
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(Utility.LIFE_CYCLE_TAG, "onActivityCreated");
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(Utility.LIFE_CYCLE_TAG, "onStart");
        super.onStart();
        updateMovieData(getActivity());
        checkForCategoryChange();
    }

    private void checkForCategoryChange() {
        String preferredCategory = Utility.getPreferedFilterForMovie(getActivity());
        if (null != mCategory) {
            if (!mCategory.equals(preferredCategory)) {
                //preference is changed so data has to change.
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            }
        }
        mCategory = preferredCategory;
    }

    void updateMovieData(Context context) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(context);
        fetchMovieTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(Utility.LIFE_CYCLE_TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String category = Utility.getPreferedFilterForMovie(getActivity());

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.buildMovieUriWithCategory(category),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.swapCursor(null);
    }
}
