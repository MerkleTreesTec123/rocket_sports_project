package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;


/**
 * Market Detail 24小时成交量数据
 *
 *
 */
public class MarketDetailRequest implements IRequest{

    public String symbol;

    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> map = new HashMap<>();
        map.put("symbol",symbol);
        return map;
    }
}
