package com.werek.stockhawk.data;

public class StockItem {
    public String symbol;
    public float price;
    public float absoluteChange;
    public float percentageChange;

    @Override
    public String toString() {
        return "StockItem{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", absoluteChange=" + absoluteChange +
                ", percentageChange=" + percentageChange +
                '}';
    }
}
