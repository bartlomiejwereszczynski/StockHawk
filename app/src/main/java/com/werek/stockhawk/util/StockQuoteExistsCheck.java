package com.werek.stockhawk.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Created by werek on 03.03.2017.
 */

public class StockQuoteExistsCheck extends AsyncTask<String, Void, Boolean> {
    public static final String TAG = StockQuoteExistsCheck.class.getName();

    public interface Result {
        void onResult(boolean exists);
    }

    Result mResult;

    public StockQuoteExistsCheck(Result mResult) {
        this.mResult = mResult;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Stock stock = YahooFinance.get(params[0]);
            Log.d(TAG, "doInBackground: " + stock);
            return stock.getQuote().getPrice() != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mResult.onResult(aBoolean);
    }
}
