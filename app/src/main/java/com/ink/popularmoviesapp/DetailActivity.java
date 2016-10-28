package com.ink.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        if (savedInstanceState == null) {
            //setting values(Uri) in argument of fragment
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            DetailFragment df = new DetailFragment();
            if (null != df) {
                df.setArguments(arguments);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail2, df)
                    .commit();
        }
    }
}
