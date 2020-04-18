package com.qkwl.web.run;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component("marketApi")
public class MarketApi {

    private final static String HOST = "https://api.huobi.pro";

    /**
     * 火币k线
     */
    public final static String HB_KLINE = HOST+"/market/history/kline?period=%s&size=300&symbol=%s";

    /**
     * 深度图
     */
    public final static String HB_DEPTH = HOST+"/market/depth?symbol=%s&type=step0";

    /**
     * 实时买卖
     */
    public final static String HB_MARKET = HOST+"/market/history/trade?symbol=%s&size=50";

    /**
     * 行情
     */
    public final static String HB_TICKER = HOST+"/market/tickers?size=100";

    public final static Map<Integer, String> huobiPeriodMap;

    public final static Map<Integer, String> huobiSymbolMap;

    public final static Map<String,String> platformSymbolMap;


    /**
     * 行情数据
     */
    private Map<String, JSONObject> tickers = new HashMap<>();

    /**
     * 实时买卖
     */
    private Map<String, JSONObject> markets = new HashMap<>();

    /**
     * 实时深度
     */
    private Map<String, JSONObject> depths = new HashMap<>();

    /**
     * k线数据
     */
    private Map<String, Map<String, String>> kLine = new HashMap<>();

    @Autowired
    private RedisHelper redisHelper;

    public final static OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    static {
        huobiPeriodMap = new HashMap<>();
        huobiPeriodMap.put(60, "1min");
        huobiPeriodMap.put(300, "5min");
        huobiPeriodMap.put(900, "15min");
        huobiPeriodMap.put(1800, "30min");
        huobiPeriodMap.put(3600, "60min");
        huobiPeriodMap.put(3600 * 24, "1day");
        huobiPeriodMap.put(3600 * 24 * 30, "1mon");

        huobiSymbolMap = new HashMap<>();
        huobiSymbolMap.put(1, "btcusdt");
        huobiSymbolMap.put(2, "ethusdt");
        huobiSymbolMap.put(6, "eosusdt");
        huobiSymbolMap.put(47, "aeusdt");
        huobiSymbolMap.put(48, "sntusdt");
        huobiSymbolMap.put(49, "omgusdt");
        



        platformSymbolMap = new HashMap<>();
       
        platformSymbolMap.put("eosusdt","eos_usdt");
        platformSymbolMap.put("ethusdt","eth_usdt");
        platformSymbolMap.put("btcusdt","btc_usdt");
        platformSymbolMap.put("aeusdt","ae_usdt");
        platformSymbolMap.put("omgusdt","omg_usdt");
        platformSymbolMap.put("sntusdt","snt_usdt");
            
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void test() {
        fetchTickers();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fetchDepth();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fetchMarkets();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fetchKline();

    }

    /**
     * 获取行情
     */
    private void fetchTickers() {
        try {
            Response execute = okHttpClient.newCall(new Request.Builder().url(HB_TICKER).build()).execute();
            JSONObject hbJsonObject = JSONObject.parseObject(execute.body().string());
            // if (hbJsonObject != null) {
            //     System.out.println(hbJsonObject.toJSONString());
            // } else {
            //     System.out.println("数据为空");
            // }   
            if ("ok".equals(hbJsonObject.getString("status"))) {
                JSONArray data = hbJsonObject.getJSONArray("data");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject innerJsonObject = data.getJSONObject(i);
                    if (huobiSymbolMap.values().contains(innerJsonObject.getString("symbol"))) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("symbol", "");
                        jsonObject.put("total", innerJsonObject.getBigDecimal("amount").multiply(new BigDecimal("0.3")).setScale(4, BigDecimal.ROUND_DOWN));
                        String symbol = innerJsonObject.getString("symbol");
                        String platformSymbol = platformSymbolMap.get(symbol);
                        jsonObject.put("buySymbol",platformSymbol.split("_")[1].toUpperCase());
                        jsonObject.put("sellSymbol",platformSymbol.split("_")[0].toUpperCase());

                        jsonObject.put("p_open", innerJsonObject.getString("open"));
                        jsonObject.put("p_new", innerJsonObject.getString("close"));
                        jsonObject.put("buy", innerJsonObject.getString("low"));
                        jsonObject.put("sell", innerJsonObject.getString("high"));
                        jsonObject.put("low", innerJsonObject.getString("low"));
                        jsonObject.put("high", innerJsonObject.getString("high"));

                        BigDecimal last = innerJsonObject.getBigDecimal("close");
                        BigDecimal kai = innerJsonObject.getBigDecimal("open");
                        BigDecimal chgTmp = MathUtils.div(MathUtils.sub(last, kai), kai);
                        jsonObject.put("chg", MathUtils.mul(chgTmp, new BigDecimal("100")));
                        tickers.put(innerJsonObject.getString("symbol"), jsonObject);


                            
                        // 生成Json
                        TickerData tickerData = new TickerData();
                        tickerData.setBuy(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("low"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setChg(MathUtils.toScaleNum(MathUtils.mul(chgTmp, new BigDecimal("100")), MathUtils.OTHER_SCALE));
                        tickerData.setHigh(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("high"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setKai(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("open"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setLast(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("close"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setLow(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("low"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setSell(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("high"), MathUtils.DEF_CNY_SCALE));
                        tickerData.setVol(MathUtils.toScaleNum(innerJsonObject.getBigDecimal("amount").multiply(new BigDecimal("0.3")), MathUtils.DEF_COIN_SCALE));
                        RedisObject redisObject = new RedisObject();
                        redisObject.setExtObject(tickerData);
                        redisHelper.setNoExpire(RedisConstant.TICKERE_KEY + getTradeIdBySymbol(symbol),redisObject);
                            
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getTradeIdBySymbol(String symbol) {
        Set<Map.Entry<Integer, String>> entries = huobiSymbolMap.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> next = iterator.next();
            if (next.getValue().equals(symbol)) {
                return next.getKey();
            }
        }
        return -1;
    }

    /**
     * 获取实时买卖数据
     */
    private void fetchMarkets() {
        Collection<String> symbols = huobiSymbolMap.values();
        for (String symbol : symbols) {
            JSONObject jsonObject = new JSONObject();
            // 最新价格
            jsonObject.put("p_new", 0);
            jsonObject.put("p_open", 0);

            jsonObject.put("total", null);
            jsonObject.put("buy", null);
            jsonObject.put("sell", null);
            // 深度
            jsonObject.put("buys", null);
            jsonObject.put("sells", null);

            // symbol
            jsonObject.put("symbol", "");

            String platformSymbol = platformSymbolMap.get(symbol);
            jsonObject.put("buySymbol",platformSymbol.split("_")[1].toUpperCase());
            jsonObject.put("sellSymbol",platformSymbol.split("_")[0].toUpperCase());

            try {
                Response execute = okHttpClient.newCall(new Request.Builder().url(String.format(HB_MARKET, symbol)).build()).execute();
                JSONObject hbJsonObject = JSONObject.parseObject(execute.body().string());
                if ("ok".equals(hbJsonObject.getString("status"))) {
                    JSONArray data = hbJsonObject.getJSONArray("data");
                    int size = data.size();
                    JSONArray trades = new JSONArray();
                    for (int i = 0; i < size; i++) {
                        JSONObject innerJsonObject = data.getJSONObject(i);
                        JSONArray innerData = innerJsonObject.getJSONArray("data");
                        int innerDataSize = innerData.size();
                        for (int j = 0; j < innerDataSize; j++) {
                            JSONObject innerDataObject = innerData.getJSONObject(j);
                            JSONObject tradeObject = new JSONObject();
                            tradeObject.put("amount", innerDataObject.get("amount"));
                            tradeObject.put("price", innerDataObject.getBigDecimal("price"));
                            tradeObject.put("time", new SimpleDateFormat("HH:mm:ss").format(new Date(innerDataObject.getLongValue("ts"))));
                            tradeObject.put("en_type", innerDataObject.getString("direction").equals("sell") ? "ask" : "bid");
                            tradeObject.put("type", innerDataObject.getString("direction").equals("sell") ? "卖出" : "买入");
                            tradeObject.put("id", innerDataObject.getLongValue("id"));

                            trades.add(tradeObject);
                        }
                    }

                    // 最新成交
                    jsonObject.put("trades", trades);
                } else {
                    jsonObject.put("trades", new JSONArray());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            markets.put(symbol, jsonObject);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取深度
     */
    private void fetchDepth() {
        Collection<String> values = huobiSymbolMap.values();
        for (String symbol : values) {
            JSONObject returnObject = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            try {
                Response execute = okHttpClient.newCall(new Request.Builder().url(String.format(HB_DEPTH, symbol)).build()).execute();
                JSONObject hbJsonObject = JSONObject.parseObject(execute.body().string());
                if ("ok".equals(hbJsonObject.getString("status"))) {
                    jsonObject.put("bids", hbJsonObject.getJSONObject("tick").getJSONArray("bids"));
                    jsonObject.put("asks", hbJsonObject.getJSONObject("tick").getJSONArray("asks"));
                } else {
                    jsonObject.put("bids", new JSONArray());
                    jsonObject.put("asks", new JSONArray());
                }
                jsonObject.put("date", System.currentTimeMillis());
                returnObject.put("depth", jsonObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
            depths.put(symbol, returnObject);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取k线
     */
    private void fetchKline() {
        Collection<String> values = huobiSymbolMap.values();
        for (String symbol : values) {
            String result = "[[]]";
            Collection<String> periods = huobiPeriodMap.values();
            for (String period : periods) {
                try {
                    Response execute = okHttpClient.newCall(new Request.Builder().url(String.format(HB_KLINE, period, symbol)).build()).execute();
                    String hbKline = execute.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(hbKline);
                    if ("ok".equals(jsonObject.getString("status"))) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        int size = data.size();
                        JSONArray responseJsonArray = new JSONArray(size);
                        for (int i = size - 1; i >= 0; i--) {
                            //高 开 低 收 量
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(data.getJSONObject(i).getLong("id") * 1000);
                            jsonArray.add(data.getJSONObject(i).getBigDecimal("open"));

                            jsonArray.add(data.getJSONObject(i).getBigDecimal("high"));
                            jsonArray.add(data.getJSONObject(i).getBigDecimal("low"));
                            jsonArray.add(data.getJSONObject(i).getBigDecimal("close"));
                            jsonArray.add(data.getJSONObject(i).getBigDecimal("count").multiply(new BigDecimal("0.3")).setScale(4, BigDecimal.ROUND_DOWN));
                            responseJsonArray.add(jsonArray);
                        }
                        result = responseJsonArray.toJSONString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Map<String, String> periodKline = kLine.get(symbol);
                if (periodKline == null){
                    periodKline = new HashMap<>();
                }
                periodKline.put(period,result);

                kLine.put(symbol,periodKline);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

//    private BigDecimal getUSDTRate(){
//        Map<String, Object> systemArgsList = redisHelper.getSystemArgsList();
//        return new BigDecimal(systemArgsList.get("rechargeUSDTPrice").toString());
//    }

    public JSONObject getTicker(String symbol){
        try {
            return tickers.get(symbol) == null?new JSONObject():tickers.get(symbol);
        }catch (Exception e){
            return new JSONObject();
        }

    }

    public JSONObject getMarket(String symbol){
        try {
            return markets.get(symbol);
        }catch (Exception e){
            return new JSONObject();
        }
    }

    public JSONObject getDepth(String symbol){
        try {
            return depths.get(symbol);
        }catch (Exception e){
            return new JSONObject();
        }

    }

    public String getKline(String symbol,String period){
        try {
            if (kLine.get(symbol) == null){
                return "";
            }
            return kLine.get(symbol).get(period);
        }catch (Exception e){
            return "";
        }
    }


    public JSONArray getLastKline(String symbol, String period) {
        try {
            if (kLine.get(symbol) == null) {
                return new JSONArray();
            }
            JSONArray jsonArray = JSONArray.parseArray(kLine.get(symbol).get(period));
            JSONArray jsonArrayOut = new JSONArray();
            jsonArrayOut.add(jsonArray.getJSONArray(jsonArray.size() - 1));
            return jsonArrayOut;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}
