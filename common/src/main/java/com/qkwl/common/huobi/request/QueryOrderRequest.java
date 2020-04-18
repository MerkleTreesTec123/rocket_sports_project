package com.qkwl.common.huobi.request;

import java.util.HashMap;
import java.util.Map;

public class QueryOrderRequest implements IRequest {

    public String symbol;
    public String types;
    public String startDate;
    public String endDate;
    public String states;
    public String from;
    public String direct;
    public String size;

    @Override
    public Map<String,String> getRequestParams(){
        Map<String,String> params = new HashMap<>();
        params.put("symbol",symbol);
        params.put("types",types==null?"":types);
        params.put("startDate",startDate==null?"":startDate);
        params.put("endDate",endDate==null?"":endDate);
        params.put("states",states==null?"":states);
        params.put("from",from==null?"":from);
        params.put("direct",direct==null?"":direct);
        params.put("size",size==null?"":size);
        return params;
    }

}
