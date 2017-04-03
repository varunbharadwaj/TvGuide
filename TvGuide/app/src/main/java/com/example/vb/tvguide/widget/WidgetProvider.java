package com.example.vb.tvguide.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.vb.tvguide.R;
import com.example.vb.tvguide.ShowDetails;
import com.example.vb.tvguide.TVShow;
import com.example.vb.tvguide.data.ShowProvider;

import static com.example.vb.tvguide.MainActivity.showObserver;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    public final static String CLICK_ACTION = "com.example.vb.tvguide.widget.CLICK";
    public final static String TAG = "tvguide";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.v(TAG, "update started");

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = createRemoteViews(context, appWidgetManager, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        Log.v(TAG, "activity started");
        if (intent.getAction().equals(CLICK_ACTION)) {
            TVShow show = intent.getExtras().getParcelable("showobj");
            Intent detailsIntent = new Intent(context, ShowDetails.class);
            Log.v(TAG, show.getName());

            // If set, this activity will become the start of a new task on the history stack
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailsIntent.putExtra("showobj", show);
            context.startActivity(detailsIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        if (showObserver == null) {
            ComponentName componentName = new ComponentName(context, WidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            showObserver = new WidgetObserver(new Handler(), appWidgetManager, componentName);
            context.getContentResolver().registerContentObserver(ShowProvider.CONTENT_URI, true, showObserver);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public RemoteViews createRemoteViews(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
        Intent intent = new Intent(context, WidgetService.class);
        remoteViews.setRemoteAdapter(R.id.widget_list, intent);
        remoteViews.setEmptyView(R.id.widget, R.id.empty_view);

        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
        //remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);

        Intent clickIntentTemplate = new Intent(context, WidgetProvider.class);
        clickIntentTemplate.setAction(CLICK_ACTION);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // for API level greater than 16 , TaskStackBuilder can be used
        PendingIntent clickPendingIntentTemplate = PendingIntent.getBroadcast(context, 0,
                clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

        return remoteViews;
    }

}

