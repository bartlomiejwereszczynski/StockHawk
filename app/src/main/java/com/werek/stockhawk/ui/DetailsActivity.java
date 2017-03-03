package com.werek.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.werek.stockhawk.BuildConfig;
import com.werek.stockhawk.R;
import com.werek.stockhawk.data.Contract;
import com.werek.stockhawk.data.PrefUtils;
import com.werek.stockhawk.util.StockUtil;

import java.security.InvalidParameterException;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String STOCK_SYMBOL = BuildConfig.APPLICATION_ID + ".stockSymbol";

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.change)
    TextView change;

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
