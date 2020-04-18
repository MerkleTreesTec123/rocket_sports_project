package com.qkwl.service.admin.run;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.service.common.mapper.CurrencyRateMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component("currencyRate")
public class CurrencyRateApi {

    private final static String URL = "https://api.exchangeratesapi.io/latest?base=USD";

    private Logger logger = LoggerFactory.getLogger(CurrencyRateApi.class);

    @Autowired
    RedisHelper redisHelper;
    @Autowired
    CurrencyRateMapper currencyRateMapper;

    private final static OkHttpClient okHttpClient =
            new OkHttpClient
                    .Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .build();


//    @PostConstruct
//    public void init() {
//        logger.info("初始化获取货币汇率");
//        handleCurrencyRateData( fetchCurrencyRateData());
//        updateRedisCache();
//    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void fetchCurrencyRate() {
        logger.info("定时获取货币汇率开始");
        handleCurrencyRateData( fetchCurrencyRateData());
        logger.info("定时获取货币汇率结束");
    }

    private JSONArray fetchCurrencyRateData (){
        JSONArray result = new JSONArray();
        try {
            Response response = okHttpClient.newCall(new Request.Builder().url(URL).get().build()).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            JSONObject rates = jsonObject.getJSONObject("rates");
            Set<String> keys = rates.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject object  = new JSONObject();
                object.put("currency",key);
                object.put("rate",rates.getBigDecimal(key));
                result.add(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("请求货币兑换报错了");
        }
        return result;
    }

    private void handleCurrencyRateData(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() == 0) {
            logger.info("currency data is empty");
        }

        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String currency = jsonObject.getString("currency");
            BigDecimal rate = jsonObject.getBigDecimal("rate");
            CurrencyRate currencyRate = currencyRateMapper.get(currency);
            if (currencyRate == null) {
                currencyRate = new CurrencyRate();
                currencyRate.setCurrency(currency);
                currencyRate.setRate(rate);
                currencyRate.setUpdateTime(new Date());
                currencyRateMapper.insert(currencyRate);
                continue;
            }
            currencyRate = new CurrencyRate();
            currencyRate.setCurrency(currency);
            currencyRate.setRate(rate);
            currencyRate.setUpdateTime(new Date());
            currencyRateMapper.update(currencyRate);
        }
    }

    private void updateRedisCache() {
        logger.info("删除货币汇率 redis 缓存");
        redisHelper.delete("currency_rate");
        logger.info("删除成功");
    }

}
