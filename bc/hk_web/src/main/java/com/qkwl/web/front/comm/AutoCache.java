package com.qkwl.web.front.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.web.utils.WebConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qkwl.common.model.KeyValues;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;

import javax.annotation.PostConstruct;

@Component("autoArticle")
public class AutoCache {

    @Autowired
    private RedisHelper redisHelper;

    // 公告
    private static Map<String, List<KeyValues>> articlesMap = new HashMap<String, List<KeyValues>>();
    // 币种信息
    private static Map<Integer, SystemCoinType> systemCoinTypeMap = new HashMap<Integer, SystemCoinType>();
    // 交易信息
    private static Map<Integer, SystemTradeType> systemTradeTypeMap = new HashMap<Integer, SystemTradeType>();

    /**
     * 初始化线程
     */
    @PostConstruct
    public void init() {
        new Thread(new Work()).start();
    }

    /**
     * 线程
     */
    class Work implements Runnable {
        public void run() {
            while (true) {
                initArticles();
                initSystemCoinType();
                initSystemTradeType();
                try {
                    Thread.sleep(60000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化新闻
     */
    private void initArticles() {
        List<FSystemLan> systemLans = redisHelper.getLanguageTypeList();
        if (systemLans == null) {
            return;
        }
        articlesMap.clear();
        for (FSystemLan fSystemLan : systemLans) {
            List<FArticleType> farticletypes = redisHelper.getArticleTypeList(fSystemLan.getFid());
            if (farticletypes == null) {
                continue;
            }
            List<KeyValues> articlesList = new ArrayList<KeyValues>();
            for (FArticleType fArticleType : farticletypes) {
                KeyValues keyValues = new KeyValues();
                List<FArticle> farticles = redisHelper.getArticles(2, fArticleType.getFid(), 1, WebConstant.BCAgentId);
                keyValues.setKey(fArticleType);
                keyValues.setValue(farticles);
                articlesList.add(keyValues);
            }
            articlesMap.put(fSystemLan.getFshortname(), articlesList);
        }

    }

    /**
     * 初始化币种信息
     */
    private void initSystemCoinType() {
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeList();
        if (coinTypes == null) {
            return;
        }
        for (SystemCoinType coinType : coinTypes) {
            systemCoinTypeMap.put(coinType.getId(), coinType);
        }
    }

    /**
     * 初始化交易信息
     */
    private void initSystemTradeType() {
        List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        if (tradeTypes == null) {
            return;
        }
        for (SystemTradeType tradeType : tradeTypes) {
            systemTradeTypeMap.put(tradeType.getId(), tradeType);
        }
    }

    /**
     * 获取新闻
     */
    public List<KeyValues> getArticles(String lanName) {
        return articlesMap.get(lanName);
    }

    /**
     * 获取币种信息
     *
     * @param coinId 币种ID
     * @return
     */
    public SystemCoinType getSystemCoinType(Integer coinId) {
        return systemCoinTypeMap.get(coinId);
    }

    /**
     * 获取交易信息
     *
     * @param tradeId 交易ID
     * @return
     */
    public SystemTradeType getSystemTradeType(Integer tradeId) {
        return systemTradeTypeMap.get(tradeId);
    }
}
