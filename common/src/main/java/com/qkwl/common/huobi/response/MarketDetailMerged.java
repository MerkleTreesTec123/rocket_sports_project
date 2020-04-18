package com.qkwl.common.huobi.response;

import java.util.List;

/**
 *  获取聚合行情
 */
public class MarketDetailMerged {

    /**
     * amount : 70169.60466814943
     * open : 653.0
     * close : 711.06
     * high : 712.37
     * id : 789938790
     * count : 52380
     * low : 565.0
     * version : 789938790
     * ask : [712.32,2.6336]
     * vol : 4.523306784022518E7
     * bid : [711.07,13.229]
     */

    private double amount;
    private double open;
    private double close;
    private double high;
    private int id;
    private int count;
    private double low;
    private int version;
    private double vol;
    private List<Double> ask;
    private List<Double> bid;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public List<Double> getAsk() {
        return ask;
    }

    public void setAsk(List<Double> ask) {
        this.ask = ask;
    }

    public List<Double> getBid() {
        return bid;
    }

    public void setBid(List<Double> bid) {
        this.bid = bid;
    }
}
