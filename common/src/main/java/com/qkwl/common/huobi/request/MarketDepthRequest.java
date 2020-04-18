package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

public class MarketDepthRequest implements IRequest {

    /**
     * btcusdt, bccbtc, rcneth ...
     */
    public String symbol;

    /**
     * step0, step1, step2, step3, step4, step5（合并深度0-5）；step0时，不合并深度
     */
    public String type;

    @Override
    public Map<String, String> getRequestParams() {
        Map<String,String> params = new HashMap<>();
        params.put("symbol",symbol);
        params.put("type",type);
        return params;
    }

}
