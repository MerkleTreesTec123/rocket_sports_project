package com.qkwl.common.huobi.response;

/**
 *
 * 获取 Market Detail 24小时成交量数据
 *
 *
 *
 */
public class MarketDetail {


    /**
     * amount : 4316.4346
     * open : 8090.54
     * close : 7962.62
     * high : 8119.0
     * ts : 1489464451000
     * id : 1489464451
     * count : 9595
     * low : 7875.0
     * vol : 3.449727690576E7
     */


    /**
     *
     *
     *     "tick": {
     "id": 消息id,
     "ts": 24小时统计时间,
     "amount": 24小时成交量,
     "open": 前推24小时成交价,
     "close": 当前成交价,
     "high": 近24小时最高价,
     "low": 近24小时最低价,
     "count": 近24小时累积成交数,
     "vol": 近24小时累积成交额, 即 sum(每一笔成交价 * 该笔的成交量)
     }
     *
     */

    private double amount;
    private double open;
    private double close;
    private double high;
    private long ts;
    private int id;
    private int count;
    private double low;
    private double vol;

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

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
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

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }
}
