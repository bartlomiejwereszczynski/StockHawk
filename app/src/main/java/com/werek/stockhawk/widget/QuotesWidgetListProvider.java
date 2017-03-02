package com.werek.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.werek.stockhawk.R;
import com.werek.stockhawk.data.Contract;
import com.werek.stockhawk.data.PrefUtils;
import com.werek.stockhawk.data.StockItem;
import com.werek.stockhawk.util.StockUtil;

import java.util.ArrayList;

import timber.log.Timber;

public class QuotesWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<StockItem> mList = new ArrayList<>();
    Context mContext;

    public QuotesWidgetListProvider(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Called when your factory is first constructed. The same factory may be shared across
     * multiple RemoteViewAdapters depending on the intent passed.
     */
    @Override
    public void onCreate() {

    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
     * RemoteViewsFactory to respond to data changes by updating any internal references.
     * <p>
     * Note: expensive tasks can be safely performed synchronously within this method. In the
     * interim, the old data will be displayed within the widget.
     *
     * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
     */
    @Override
    public void onDataSetChanged() {
        Timber.d("loading refreshed list of stocks");
        Cursor cursor = mContext.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL
        );
        if (!cursor.moveToFirst()) {
            Timber.d("moveToFirst failed");
            cursor.close();
            return;
        }

        mList.clear();
        do {
            StockItem stock = StockUtil.fromCursor(cursor);
            mList.add(stock);
        } while (cursor.moveToNext());

        cursor.close();
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is
     * unbound.
     */
    @Override
    public void onDestroy() {

    }

    /**
     * See
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * Note: expensive tasks can be safely performed synchronously within this method, and a
     * loading view will be displayed in the interim. See {@link #getLoadingView()}.
     *
     * @param position The position of the item within the Factory's data set of the item whose
     *                 view we want.
     * @return A RemoteViews object corresponding to the data at the specified position.
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("getting view at position " + position);
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_quotes_list_item);
        StockItem stock = mList.get(position);
        Timber.d("used stock item: " + stock);
        remoteView.setTextViewText(R.id.symbol, stock.symbol);
        remoteView.setTextViewText(R.id.price, StockUtil.dollarFormat(stock.price));
        remoteView.setInt(
                R.id.change,
                "setBackgroundResource",
                stock.absoluteChange > 0 ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red
        );
        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            remoteView.setTextViewText(R.id.change, StockUtil.dollarFormatWithPlus(stock.absoluteChange));
        } else {
            remoteView.setTextViewText(R.id.change, StockUtil.percentageFormat(stock.percentageChange / 100));
        }
        Timber.d("returned remote view", remoteView);
        return remoteView;
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that
     * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
     * view will be used.
     *
     * @return The RemoteViews representing the desired loading view.
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * @return The number of types of Views that will be returned by this factory.
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * @param position The position of the item within the data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @return True if the same id always refers to the same object.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
