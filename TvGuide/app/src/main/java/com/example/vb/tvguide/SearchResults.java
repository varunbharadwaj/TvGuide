package com.example.vb.tvguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SearchResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_search_results);
    }
}
