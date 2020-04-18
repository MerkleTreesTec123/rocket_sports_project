package com.qkwl.common.huobi.response;

/**
 * K线
 */
public class KLine {

    /**
     * id : 1514184300
     * open : 13286.71
     * close : 13271.0
     * low : 13252.69
     * high : 13300.0
     * amount : 1.6909681053649925
     * vol : 22453.646962
     * count : 28
     */

    private int id;
    private double open;
    private double close;
    private double low;
    private double high;
    private double amount;
    private double vol;
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
