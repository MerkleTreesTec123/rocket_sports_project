package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

public class QueryKLineRequest implements IRequest {

    /**
     * 交易对
     *
     * btcusdt, bccbtc, rcneth
     */
    public String symbol;

    /**
     * K线类型
     *
     * 1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
     */
    public String period ;

    /**
     * 获取数量 默认 150
     *
     * [1,2000]
     */
    public String size;


    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> map = new HashMap<>();
        map.put("symbol",symbol);
        map.put("period",period);
        map.put("size",size);
        return map;
    }
}
