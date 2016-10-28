package com.ink.popularmoviesapp;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created & Developed by Manu Sharma on 10/2/2016.
 */

public class Movie {

    public Movie() {
    }

    public Movie(String title, int description, String posterURL) {

    }

    public Movie(String title, String description, String posterURL, String releaseDate, Double rating) {
        this.title = title;
        this.description = description;
        this.posterURL = posterURL;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public String title;
    public String description;
    public String posterURL;
    public String releaseDate;
    public Double rating;
//    public Drawable drawable;
}
