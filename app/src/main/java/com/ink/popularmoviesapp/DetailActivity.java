package com.ink.popularmoviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

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
