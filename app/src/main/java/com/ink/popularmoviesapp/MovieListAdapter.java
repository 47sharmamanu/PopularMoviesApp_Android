package com.ink.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created & Developed by Manu Sharma on 10/2/2016.
 */

public class MovieListAdapter extends CursorAdapter {

    public static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    onItemClickListener onItemClickListener;
    Context mContext;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public MovieListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView posterImage;

        public ViewHolder(View view) {
            posterImage = (ImageView) view.findViewById(R.id.movie_image);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //Read poster url from cursor using index defined in MovieListFragment.
        String posterUrl = cursor.getString(MovieListFragment.INDEX_COLUMN_POSTER_URL);
        Picasso.with(mContext).load(posterUrl).placeholder(R.drawable.loading).into(viewHolder.posterImage);
        //read mTitle from cursor and set it as mDescription to image
        String title = cursor.getString(MovieListFragment.INDEX_COLUMN_NAME);
        viewHolder.posterImage.setContentDescription(title);
    }
}
