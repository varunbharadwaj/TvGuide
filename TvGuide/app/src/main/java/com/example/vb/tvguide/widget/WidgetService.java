package com.example.vb.tvguide.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // return remote view factory
        return new WidgetDataProvider(this.getApplicationContext(), intent);
    }
}
