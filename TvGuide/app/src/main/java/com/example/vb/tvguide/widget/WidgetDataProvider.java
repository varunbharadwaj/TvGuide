package com.example.vb.tvguide.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.vb.tvguide.R;
import com.example.vb.tvguide.TVShow;
import com.example.vb.tvguide.data.ShowContract;
import com.example.vb.tvguide.data.ShowProvider;

/**
 * Created by vb on 3/2/2017.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Intent intent;
    Context context;
    Cursor cursor;
    TVShow selectedShow;

    public WidgetDataProvider(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {


        // close previous cursor
        if (cursor != null)
            cursor.close();

        // create new cursor
        cursor = context.getContentResolver().query(
                ShowProvider.CONTENT_URI,
                new String[]{ShowContract.ShowEntry.ID, ShowContract.ShowEntry.SEASON, ShowContract.ShowEntry.EPISODE,
                        ShowContract.ShowEntry.NAME, ShowContract.ShowEntry.LANGUAGE, ShowContract.ShowEntry.STATUS, ShowContract.ShowEntry.RUNTIME,
                        ShowContract.ShowEntry.TYPE, ShowContract.ShowEntry.SUMMARY, ShowContract.ShowEntry.CHANNEL, ShowContract.ShowEntry.COUNTRY,
                        ShowContract.ShowEntry.URL, ShowContract.ShowEntry.IMAGE, ShowContract.ShowEntry.DAYS, ShowContract.ShowEntry.TIME},
                null,
                null,
                null);

    }

    @Override
    public void onDestroy() {
        if (cursor != null)
            cursor.close();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        // read data from database
        if (cursor.moveToPosition(position)) {
            int id = cursor.getInt(cursor.getColumnIndex(ShowContract.ShowEntry.ID));
            String season = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.SEASON));
            String episode = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.EPISODE));
            String name = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.NAME));
            String language = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.LANGUAGE));
            String status = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.STATUS));
            String runTime = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.RUNTIME));
            String type = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.TYPE));
            String summary = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.SUMMARY));
            String channel = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.CHANNEL));
            String country = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.COUNTRY));
            String url = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.URL));
            String image = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.IMAGE));
            String days = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.DAYS));
            String time = cursor.getString(cursor.getColumnIndex(ShowContract.ShowEntry.TIME));

            selectedShow = new TVShow()
                    .setId(id)
                    .setShowDetails(season, episode, name, language, status, runTime, type)
                    .setSummary(summary)
                    .setSchedule(time, days)
                    .setNetworkDetails(channel, country)
                    .setUrl(url)
                    .setImage(image);


            // fill list item with details
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            remoteView.setTextViewText(R.id.widgetName, name);
            remoteView.setTextViewText(R.id.widgetChannel, channel);


            // handle list item click
            final Intent fillInIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putParcelable("showobj", selectedShow);
            fillInIntent.putExtras(extras);
            remoteView.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
            return remoteView;
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}