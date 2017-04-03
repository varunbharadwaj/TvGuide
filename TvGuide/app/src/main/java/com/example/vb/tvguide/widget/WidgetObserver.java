package com.example.vb.tvguide.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.vb.tvguide.R;

/**
 * Created by vb on 3/2/2017.
 */

public class WidgetObserver extends ContentObserver {

    AppWidgetManager appWidgetManager;
    ComponentName componentName;

    public WidgetObserver(Handler handler, AppWidgetManager appWidgetManager, ComponentName componentName) {
        super(handler);
        this.appWidgetManager = appWidgetManager;
        this.componentName = componentName;
    }

    @Override
    public void onChange(boolean selfChange) {
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(componentName), R.id.widget_list);
    }

}
