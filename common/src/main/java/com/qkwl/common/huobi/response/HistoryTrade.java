package com.qkwl.common.huobi.response;

import java.util.List;

/**
 * 交易历史
 *
 */
public class HistoryTrade {


    /**
     * id : 31459998
     * ts : 1502448920106
     * data : [{"id":17592256642623,"amount":0.04,"price":1997,"direction":"buy","ts":1502448920106}]
     */

    private int id;
    private long ts;
    private List<DataBean> data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 17592256642623
         * amount : 0.04
         * price : 1997
         * direction : buy
         * ts : 1502448920106
         */

        private long id;
        private double amount;
        private int price;
        private String direction;
        private long ts;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }
    }
}

