package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易历史
 *
 */
public class HistoryTradeRequest implements IRequest {

    public String symbol;
    public String size;

    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> map = new HashMap<>();
        map.put("symbol",symbol);
        map.put("size",size);
        return map;
    }

}
