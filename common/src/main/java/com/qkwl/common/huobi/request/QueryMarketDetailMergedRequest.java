package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

public class QueryMarketDetailMergedRequest implements IRequest {

    public String symbol;

    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> map = new HashMap<>();
        map.put("symbol",symbol);
        return map;
    }

}
