package com.udacity.stockhawk.util;

import android.database.Cursor;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StockUtil {
    public static StockItem fromCursor(Cursor cursor) {
        StockItem stock = new StockItem();
        stock.symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
        stock.price = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        stock.percentageChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
        stock.absoluteChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        return stock;
    }

    public static String dollarFormat(float price) {
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        return dollarFormat.format(price);
    }

    public static String dollarFormatWithPlus(float price) {
        DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        return dollarFormatWithPlus.format(price);
    }

    public static String percentageFormat(float percent) {
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        return percentageFormat.format(percent);
    }
}
