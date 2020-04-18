package com.qkwl.common.huobi.response;

import java.util.List;

/**
 * 交易详情
 *
 *
 *
 *      "tick": {
        "id": 消息id,
        "ts": 最新成交时间,
        "data": [
            {
                "id": 成交id,
                "price": 成交价钱,
                "amount": 成交量,
                "direction": 主动成交方向,
                "ts": 成交时间
            }
                ]
            }
 *
 *
 */
public class TradeDetail {

    /**
     * id : 600848670
     * ts : 1489464451000
     * data : [{"id":600848670,"price":7962.62,"amount":0.0122,"direction":"buy","ts":1489464451000}]
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
         * id : 600848670
         * price : 7962.62
         * amount : 0.0122
         * direction : buy
         * ts : 1489464451000
         */

        private int id;
        private double price;
        private double amount;
        private String direction;
        private long ts;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
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
