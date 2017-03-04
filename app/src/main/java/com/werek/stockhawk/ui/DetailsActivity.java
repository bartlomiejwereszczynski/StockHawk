package com.werek.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.werek.stockhawk.BuildConfig;
import com.werek.stockhawk.R;
import com.werek.stockhawk.data.Contract;
import com.werek.stockhawk.data.PrefUtils;
import com.werek.stockhawk.util.StockUtil;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = DetailsActivity.class.getName();
    public static final String STOCK_SYMBOL = BuildConfig.APPLICATION_ID + ".stockSymbol";

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.change)
    TextView change;

    @BindView(R.id.chart)
    LineChart chart;

    @BindBool(R.bool.rtl)
    boolean useRtl;

    String symbolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(STOCK_SYMBOL)) {
            symbolName = intent.getStringExtra(STOCK_SYMBOL);
        }

        if (symbolName == null) {
            throw new InvalidParameterException("missing stock symbol");
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    void loadData(Cursor cursor) {
        symbol.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        setTitle(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        price.setText(StockUtil.dollarFormat(cursor.getFloat(Contract.Quote.POSITION_PRICE)));


        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String changeDollars = StockUtil.dollarFormatWithPlus(rawAbsoluteChange);
        String percentage = StockUtil.percentageFormat(percentageChange / 100);

        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            change.setText(changeDollars);
        } else {
            change.setText(percentage);
        }

        loadHistory(cursor.getString(Contract.Quote.POSITION_HISTORY));
    }

    void loadHistory(String history) {
        String[] datedStocks;
        List<Entry> entries = new ArrayList<>();

        // reverse order
        List<String> rev = Arrays.asList(history.split("\n"));

        Collections.reverse(rev);
        datedStocks = rev.toArray(new String[]{});

        for (String datedStock : datedStocks) {
            String[] stockData = datedStock.split(",");

            // 0 - time, 1 - price
            entries.add(new Entry(Float.parseFloat(stockData[0].trim()), Float.parseFloat(stockData[1].trim())));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.label_stock_prices));
        chart.setData(new LineData(dataSet));
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Long date = (long) value;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date);
                return (new SimpleDateFormat(getString(R.string.util_date_format), Locale.getDefault())).format(cal.getTime());
            }
        });
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(symbolName),
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        loadData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
