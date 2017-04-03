package com.example.vb.tvguide;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.vb.tvguide.data.ShowProvider;
import com.example.vb.tvguide.widget.WidgetObserver;
import com.example.vb.tvguide.widget.WidgetProvider;

import static com.example.vb.tvguide.MainActivity.showObserver;

public class ShowDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        getSupportActionBar().hide();

        if (showObserver == null) {
            ComponentName componentName = new ComponentName(this, WidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            showObserver = new WidgetObserver(new Handler(), appWidgetManager, componentName);
            this.getContentResolver().registerContentObserver(ShowProvider.CONTENT_URI, true, showObserver);
        }
    }
}
