package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

public class TradeDetailRequest implements IRequest {

    /**
     * 交易对
     *
     * btcusdt, bccbtc, rcneth ...
     */
    public String symbol;

    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> map = new HashMap<>();
        map.put("symbol",symbol);
        return map;
    }
}
