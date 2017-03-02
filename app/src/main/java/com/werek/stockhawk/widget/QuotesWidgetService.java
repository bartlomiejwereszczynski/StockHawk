package com.werek.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class QuotesWidgetService extends RemoteViewsService {
    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     *
     * @param intent
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuotesWidgetListProvider(getApplicationContext());
    }
}
