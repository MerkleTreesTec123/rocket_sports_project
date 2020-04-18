package com.qkwl.service.market.service;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.dto.market.DepthData;
import com.qkwl.common.dto.market.EntrustLogData;
import com.qkwl.common.dto.market.FPeriod;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.rpc.market.IMarketService;
import com.qkwl.common.util.Utils;
import com.qkwl.service.market.mapper.FEntrustLogMapper;
import com.qkwl.service.market.mapper.FEntrustMapper;
import com.qkwl.service.market.mapper.FPeriodMapper;
import com.qkwl.service.market.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service("marketService")
public class MarketServiceImpl implements IMarketService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MarketServiceImpl.class);

    @Autowired
    private JobUtils jobUtils;
    @Autowired
    private FPeriodMapper fPeriodMapper;
    @Autowired
    private FEntrustMapper fEntrustMapper;
    @Autowired
    private FEntrustLogMapper fEntrustLogMapper;

    /**
     * 最大存储
     */
    private static final int MAX_LEN = 300;

    /**
     * 最大成交记录
     */
    private static final int SUCCESS_MAX = 60;

    /**
     * K线时间类型
     */
    private final int[] TIME_KIND = {
            1, 3, 5, 15, 30, // 分钟
            60, 2 * 60, 4 * 60, 6 * 60, 12 * 60, // 小时
            24 * 60, 7 * 24 * 60 // 天
    };

    /**
     * 本月开始时间
     */
    private Timestamp startTime = null;

    /**
     * 是否第一次更新行情
     */
    private boolean isFirstInit = true;

    /**
     * 实时数据
     */
    private ConcurrentMap<Integer, TickerData> TickerMap = new ConcurrentHashMap<Integer, TickerData>();
    private ConcurrentMap<Integer, ConcurrentSkipListSet<EntrustLogData>> oneDayLogMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<EntrustLogData>>();
    private ConcurrentMap<Integer, String> TickerJson = new ConcurrentHashMap<Integer, String>();

    /**
     * 三日数据
     */
    private ConcurrentMap<Integer, ConcurrentSkipListSet<FPeriod>> ThreeDayLogMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>>();

    /**
     * 已成交数据
     */
    private ConcurrentMap<Integer, ConcurrentSkipListSet<DepthData>> SuccessMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<DepthData>>();
    private ConcurrentMap<Integer, String> SuccessJson = new ConcurrentHashMap<Integer, String>();


    /**
     * 买卖深度
     */
    private ConcurrentMap<Integer, ConcurrentSkipListSet<DepthData>> BuyDepthMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<DepthData>>();
    private ConcurrentMap<Integer, ConcurrentSkipListSet<DepthData>> SellDepthMap = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<DepthData>>();
    private ConcurrentMap<Integer, String> BuyDepthJson = new ConcurrentHashMap<Integer, String>();
    private ConcurrentMap<Integer, String> SellDepthJson = new ConcurrentHashMap<Integer, String>();

    /**
     * K线数据
     */
    private ConcurrentMap<Integer, ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>>> KlineMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>>>();
    private ConcurrentMap<Integer, ConcurrentHashMap<Integer, String>> KlineJson = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, String>>();

    /**
     * lastKline数据
     */
    private ConcurrentMap<Integer, ConcurrentHashMap<Integer, FPeriod>> lastKlineMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, FPeriod>>();
    private ConcurrentMap<Integer, ConcurrentHashMap<Integer, String>> lastKlineJson = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, String>>();

    private Set skipSet = new HashSet<Integer>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        skipSet.add(1);
        skipSet.add(2);
        skipSet.add(6);
        skipSet.add(47);
        skipSet.add(48);
        skipSet.add(49);
            
        List<SystemTradeType> tradeTypes = jobUtils.getTradeTypeList();
        if(tradeTypes == null ){
            return;
        }
        for (SystemTradeType tradeType : tradeTypes) {
            int tradeId = tradeType.getId();
            // 初始化币种实体
            System.out.println("init marketbean : " + tradeId);
            initMarketBean(tradeId);
            // 初始化币种数据
            if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                continue;
            }
            System.out.println("init marketdata : " + tradeId);
            initMarketData(tradeId);
            System.out.println("init marketdata success : " + tradeId);
        }
    }

    public Set<Integer> getSkipTrade() {
        return this.skipSet;
    }

    /**
     * 重置所有行情数据
     */
    @Override
    public void restMarket() {
        List<SystemTradeType> tradeTypes = jobUtils.getTradeTypeList();
        if(tradeTypes == null ){
            return;
        }
        for (SystemTradeType tradeType : tradeTypes) {
            int tradeId = tradeType.getId();
            // 初始化币种数据
            if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                continue;
            }
            System.out.println("---> init marketdata : " + tradeId);
            initMarketBean(tradeId);
            initMarketData(tradeId);
            System.out.println("---> init marketdata success : " + tradeId);
        }
    }

    /**
     * 初始化行情数据,用于添加币和启动币
     *
     * @param tradeId 交易ID
     * @return boolean
     */
    @Override
    public boolean initMarket(int tradeId) {
        SystemTradeType tradeType = jobUtils.getTradeType(tradeId);
        if (!tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
            return false;
        }
        // 初始化币种数据
        System.out.println("---> init marketdata : " + tradeId);
        initMarketBean(tradeId);
        initMarketData(tradeId);
        System.out.println("---> init marketdata success : " + tradeId);
        return true;
    }

    /**
     * 初始化行情实体,用于添加币和启动币
     *
     * @param tradeId 交易ID
     */
    @Override
    public void initMarketBean(int tradeId) {
        System.out.println("---> 初始化实时行情 : " + tradeId);
        // 初始化实时行情
        TickerData tickerDataTmp = new TickerData();
        TickerMap.put(tradeId, tickerDataTmp);
        ConcurrentSkipListSet<EntrustLogData> oneDayTmp = new ConcurrentSkipListSet<EntrustLogData>(oneDayLogCompara);
        oneDayLogMap.put(tradeId, oneDayTmp);
        System.out.println("---> 初始化三日行情: " + tradeId);
        // 初始化三日行情
        ConcurrentSkipListSet<FPeriod> threeDayTmp = new ConcurrentSkipListSet<FPeriod>(kLineCompara);
        ThreeDayLogMap.put(tradeId, threeDayTmp);
        System.out.println("---> 初始化Kline: " + tradeId);
        // 初始化K线数据
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> klineTmp = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>>();
        ConcurrentHashMap<Integer, String> klineJsonTmp = new ConcurrentHashMap<Integer, String>();
        ConcurrentHashMap<Integer, FPeriod> lastKlineTmp = new ConcurrentHashMap<Integer, FPeriod>();
        ConcurrentHashMap<Integer, String> lastklineJsonTmp = new ConcurrentHashMap<Integer, String>();
        for (int i : TIME_KIND) {
            ConcurrentSkipListSet<FPeriod> periodTmp = new ConcurrentSkipListSet<FPeriod>(kLineCompara);
            FPeriod fPeriod = new FPeriod();
            klineTmp.put(i, periodTmp);
            lastKlineTmp.put(i, fPeriod);
            String stringklineJson = new String();
            String stringlastklineJson = new String();
            klineJsonTmp.put(i, stringklineJson);
            lastklineJsonTmp.put(i, stringlastklineJson);
        }
        KlineMap.put(tradeId, klineTmp);
        lastKlineMap.put(tradeId, lastKlineTmp);
        KlineJson.put(tradeId, klineJsonTmp);
        lastKlineJson.put(tradeId, lastklineJsonTmp);
        System.out.println("---> 初始化Depth: " + tradeId);
        ConcurrentSkipListSet<DepthData> buyDepthTmp = new ConcurrentSkipListSet<DepthData>(prizeComparaDESC);
        BuyDepthMap.put(tradeId, buyDepthTmp);
        ConcurrentSkipListSet<DepthData> sellDepthTmp = new ConcurrentSkipListSet<DepthData>(prizeComparaASC);
        SellDepthMap.put(tradeId, sellDepthTmp);
        System.out.println("---> 初始化已成交: " + tradeId);
        ConcurrentSkipListSet<DepthData> successTmp = new ConcurrentSkipListSet<DepthData>(successComparaDESC);
        SuccessMap.put(tradeId, successTmp);
    }

    /**
     * 初始化数据
     *
     * @param tradeId 交易ID
     */
    private void initMarketData(int tradeId) {
        System.out.println("---> 生成实时行情 : " + tradeId);
        initTicker(tradeId);
        initTickerJson(tradeId);
        System.out.println("---> 生成Kline: " + tradeId);
        // 生成数据
        List<FPeriod> fPeriods = fPeriodMapper.selectforId(tradeId);
        for (FPeriod fPeriod : fPeriods) {
            initKlinedata(tradeId, fPeriod);
        }
        initKlineJson(tradeId);
        initLastKline(tradeId);
        initLastKlineJson(tradeId);
        System.out.println("---> 生成三日行情 : " + tradeId);
        initThreeTicker(tradeId);
        initThreeTickerJson(tradeId);
        System.out.println("---> 生成Depth: " + tradeId);
        initBuyDepth(tradeId);
        initBuyDepthJson(tradeId);
        initSellDepth(tradeId);
        initSellDepthJson(tradeId);
        System.out.println("---> 生成已成交: " + tradeId);
        initSuccess(tradeId);
        initSuccessJson(tradeId);
    }

    /**
     * 初始化实时行情
     *
     * @param tradeId 交易ID
     */
    private void initTicker(int tradeId) {
        // 今日零点
        String nowZero = Utils.dateFormatYYYYMMDD(new Timestamp(Utils.getTimesNowZero()));
        // 昨日零点
        String yesterZero = Utils.dateFormatYYYYMMDD(new Timestamp(Utils.getTimesAdd(-1)));
        // 当前时间往前推24小时
        String now24 = Utils.dateFormat(new Timestamp(Utils.getTimesAdd(-1)));
        // 获取
        List<FEntrustLog> fEntrustLogs = fEntrustLogMapper.selectByDate(tradeId, now24);
        ConcurrentSkipListSet<EntrustLogData> oneDayLogSet = oneDayLogMap.get(tradeId);
        for (FEntrustLog fEntrustLog : fEntrustLogs) {
            oneDayLogSet.add(new EntrustLogData(fEntrustLog.getFid(), fEntrustLog.getFentrusttype()
                    , fEntrustLog.getFprize(), fEntrustLog.getFcount(), fEntrustLog.getFcreatetime()));
        }
        oneDayLogMap.put(tradeId, oneDayLogSet);
        // 获取数据
        SystemTradeType tradeType = jobUtils.getTradeType(tradeId);
        Map<String, Object> data24 = fEntrustLogMapper.select24ByDate(tradeId, now24);
        FEntrustLog fkaiEntrustLog = fEntrustLogMapper.selectKaiByData(tradeId, nowZero, yesterZero);
        FEntrustLog flastEntrustLog = fEntrustLogMapper.selectLastByData(tradeId);
        BigDecimal defaultPrice = tradeType.getOpenPrice() == null ? BigDecimal.ZERO : tradeType.getOpenPrice();
        BigDecimal last = flastEntrustLog != null ? flastEntrustLog.getFprize() : defaultPrice;
        BigDecimal kai = fkaiEntrustLog != null ? fkaiEntrustLog.getFprize() : last;
        // 更新数据
        TickerData tickerData = TickerMap.get(tradeId);
        BigDecimal sum = data24 != null ? new BigDecimal(data24.get("sum").toString()) : BigDecimal.ZERO;
        BigDecimal max = data24 != null ? new BigDecimal(data24.get("max").toString()) : BigDecimal.ZERO;
        BigDecimal min = data24 != null ? new BigDecimal(data24.get("min").toString()) : BigDecimal.ZERO;
        tickerData.setHigh(max.compareTo(BigDecimal.ZERO) == 0 ? last : max);
        tickerData.setLow(min.compareTo(BigDecimal.ZERO) == 0 ? last : min);
        tickerData.setLast(last);
        tickerData.setKai(kai);
        tickerData.setVol(sum);
        BigDecimal chgTmp = MathUtils.div(MathUtils.sub(last, kai), kai);
        tickerData.setChg(MathUtils.mul(chgTmp, new BigDecimal("100")));
        TickerMap.put(tradeId, tickerData);
    }

    /**
     * 生成实时行情Json
     *
     * @param tradeId 交易ID
     */
    private void initTickerJson(int tradeId) {
        // 生成Json
        TickerData tickerData = TickerMap.get(tradeId);
        if (tickerData != null) {
            tickerData.setBuy(MathUtils.toScaleNum(tickerData.getBuy(), MathUtils.DEF_CNY_SCALE));
            tickerData.setChg(MathUtils.toScaleNum(tickerData.getChg(), MathUtils.OTHER_SCALE));
            tickerData.setHigh(MathUtils.toScaleNum(tickerData.getHigh(), MathUtils.DEF_CNY_SCALE));
            tickerData.setKai(MathUtils.toScaleNum(tickerData.getKai(), MathUtils.DEF_CNY_SCALE));
            tickerData.setLast(MathUtils.toScaleNum(tickerData.getLast(), MathUtils.DEF_CNY_SCALE));
            tickerData.setLow(MathUtils.toScaleNum(tickerData.getLow(), MathUtils.DEF_CNY_SCALE));
            tickerData.setSell(MathUtils.toScaleNum(tickerData.getSell(), MathUtils.DEF_CNY_SCALE));
            tickerData.setVol(MathUtils.toScaleNum(tickerData.getVol(), MathUtils.DEF_COIN_SCALE));
        }

        String json = JSON.toJSONString(tickerData);
        TickerJson.put(tradeId, json);
        // 投递Redis
        RedisObject redisObject = new RedisObject();
        redisObject.setExtObject(json);
        jobUtils.setRedisData(RedisConstant.TICKERE_KEY + tradeId, JSON.toJSONString(redisObject));
    }

    /**
     * 初始化三日实时行情
     *
     * @param tradeId 交易ID
     */
    private void initThreeTicker(int tradeId) {
        // 获取三日行情
        ConcurrentSkipListSet<FPeriod> threeDayTmp = ThreeDayLogMap.get(tradeId);
        threeDayTmp.clear();
        // 每小时k线
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> kMap = KlineMap.get(tradeId);
        ConcurrentSkipListSet<FPeriod> fPeriods = kMap.get(60);
        // 加入3天数据
        long time = Utils.getTimestamp().getTime();
        for (FPeriod fPeriod : fPeriods) {
            if (time - fPeriod.getFtime().getTime() <= 3 * 24 * 60 * 60 * 1000L) {
                threeDayTmp.add(new FPeriod(tradeId, fPeriod.getFshou(), fPeriod.getFtime()));
            }
        }
        ThreeDayLogMap.put(tradeId, threeDayTmp);
    }

    /**
     * 生成三日行情Json
     *
     * @param tradeId 交易ID
     */
    private void initThreeTickerJson(int tradeId) {
        ConcurrentSkipListSet<FPeriod> fPeriods = ThreeDayLogMap.get(tradeId);
        StringBuffer stringBuffer = new StringBuffer();
        for (FPeriod fPeriod : fPeriods) {
            if (stringBuffer.length() <= 0) {
                stringBuffer.append("[" + fPeriod.getFtime().getTime() + "," + MathUtils.toScaleNum(fPeriod.getFshou(), MathUtils.DEF_CNY_SCALE) + "]");
            } else {
                stringBuffer.append(",[" + fPeriod.getFtime().getTime() + "," + MathUtils.toScaleNum(fPeriod.getFshou(), MathUtils.DEF_CNY_SCALE) + "]");
            }
        }
        // 投递Redis
        RedisObject redisObject = new RedisObject();
        redisObject.setExtObject("[" + stringBuffer.toString() + "]");
        jobUtils.setRedisData(RedisConstant.THREETICKERE_KEY + tradeId, JSON.toJSONString(redisObject));
    }

    /**
     * 初始化买深度
     *
     * @param tradeId 交易ID
     */
    private void initBuyDepth(int tradeId) {
        List<FEntrust> buyEntrusts = fEntrustMapper.selectGoingByType(tradeId, EntrustTypeEnum.BUY.getCode());
        if (buyEntrusts == null || buyEntrusts.size() <= 0) {
            return;
        }
        // 临时存放集合
        ConcurrentHashMap<BigDecimal, BigDecimal> ConcurrentHashMap = new ConcurrentHashMap<BigDecimal, BigDecimal>();
        for (FEntrust fEntrust : buyEntrusts) {
            if (fEntrust == null) {
                continue;
            }
            BigDecimal key = fEntrust.getFprize();
            BigDecimal value = ConcurrentHashMap.get(key);
            if (value != null) {
                value = MathUtils.add(value, fEntrust.getFleftcount());
            } else {
                value = fEntrust.getFleftcount();
            }
            //数量为0直接跳出
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            ConcurrentHashMap.put(key, value);
        }
        // 存储数据
        ConcurrentSkipListSet<DepthData> buyDepthTmp = BuyDepthMap.get(tradeId);
        for (ConcurrentHashMap.Entry<BigDecimal, BigDecimal> entry : ConcurrentHashMap.entrySet()) {
            DepthData depthData = new DepthData(entry.getKey(), entry.getValue());
            buyDepthTmp.add(depthData);
        }
        BuyDepthMap.put(tradeId, buyDepthTmp);
        //更新买一
        TickerData tickerData = TickerMap.get(tradeId);
        tickerData.setBuy(buyDepthTmp.size() <= 0 ? BigDecimal.ZERO : buyDepthTmp.first().getPrize());
        TickerMap.put(tradeId, tickerData);
    }

    /**
     * 初始化卖深度
     *
     * @param tradeId 交易ID
     */
    private void initSellDepth(int tradeId) {
        List<FEntrust> sellEntrusts = fEntrustMapper.selectGoingByType(tradeId, EntrustTypeEnum.SELL.getCode());
        // 临时存放集合
        ConcurrentHashMap<BigDecimal, BigDecimal> ConcurrentHashMap = new ConcurrentHashMap<BigDecimal, BigDecimal>();
        for (FEntrust fEntrust : sellEntrusts) {
            if (fEntrust == null) {
                continue;
            }
            BigDecimal key = fEntrust.getFprize();
            BigDecimal value = ConcurrentHashMap.get(key);
            if (value != null) {
                value = MathUtils.add(value, fEntrust.getFleftcount());
            } else {
                value = fEntrust.getFleftcount();
            }
            //数量为0直接跳出
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            ConcurrentHashMap.put(key, value);
        }
        // 存储数据
        ConcurrentSkipListSet<DepthData> sellDepthTmp = SellDepthMap.get(tradeId);
        for (ConcurrentHashMap.Entry<BigDecimal, BigDecimal> entry : ConcurrentHashMap.entrySet()) {
            DepthData depthData = new DepthData(entry.getKey(), entry.getValue());
            sellDepthTmp.add(depthData);
        }
        // 添加数据
        SellDepthMap.put(tradeId, sellDepthTmp);
        // 更新买一
        TickerData tickerData = TickerMap.get(tradeId);
        tickerData.setSell(sellDepthTmp.size() <= 0 ? BigDecimal.ZERO : sellDepthTmp.first().getPrize());
        TickerMap.put(tradeId, tickerData);
    }

    /**
     * 初始化买深度Json
     *
     * @param tradeId 交易ID
     */
    private synchronized void initBuyDepthJson(int tradeId) {
        // 初始化买
        ConcurrentSkipListSet<DepthData> buyDepthTmp = BuyDepthMap.get(tradeId);
        StringBuilder buystringBuffer = new StringBuilder();
        for (DepthData depthData : buyDepthTmp) {
            if (buystringBuffer.length() <= 0) {
                buystringBuffer.append("[" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_COIN_SCALE) + "]");
            } else {
                buystringBuffer.append(", [" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_COIN_SCALE) + "]");
            }
        }
        BuyDepthJson.put(tradeId, "[" + buystringBuffer.toString() + "]");
        // 投递Redis
        RedisObject buyRedisObject = new RedisObject();
        buyRedisObject.setExtObject("[" + buystringBuffer.toString() + "]");
        jobUtils.setRedisData(RedisConstant.BUYDEPTH_KEY + tradeId, JSON.toJSONString(buyRedisObject));
    }

    /**
     * 初始化卖深度Json
     *
     * @param tradeId 交易ID
     */
    private synchronized void initSellDepthJson(int tradeId) {
        // 初始化卖
        ConcurrentSkipListSet<DepthData> sellDepthTmp = SellDepthMap.get(tradeId);
        StringBuilder sellstringBuffer = new StringBuilder();
        for (DepthData depthData : sellDepthTmp) {
            if (sellstringBuffer.length() <= 0) {
                sellstringBuffer.append("[" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_COIN_SCALE) + "]");
            } else {
                sellstringBuffer.append(", [" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_COIN_SCALE) + "]");
            }
        }
        SellDepthJson.put(tradeId, "[" + sellstringBuffer.toString() + "]");
        // 投递Redis
        RedisObject sellRedisObject = new RedisObject();
        sellRedisObject.setExtObject("[" + sellstringBuffer.toString() + "]");
        jobUtils.setRedisData(RedisConstant.SELLDEPTH_KEY + tradeId, JSON.toJSONString(sellRedisObject));
    }

    /**
     * 初始化K线数据
     *
     * @param tradeId  交易ID
     * @param fPeriod K线实体
     */
    private void initKlinedata(int tradeId, FPeriod fPeriod) {
        // 初始化时间
        try {
            if (startTime == null) {// 00:01
                startTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(
                        new SimpleDateFormat("yyyy-MM-dd").format(fPeriod.getFtime())).getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SOS! kline start failed");
            return;
        }
        // 取数据
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> klineTotal = KlineMap.get(tradeId);
        // 遍历K线类型
        for (int i : TIME_KIND) {
            // 取数据
            ConcurrentSkipListSet<FPeriod> klineData = klineTotal.get(i);
            // 最后数据
            FPeriod lastData = null;
            if (klineData.size() > 0) {
                lastData = klineData.last();
            }
            // 下个时间
            Timestamp openNew = isOpenNewPeriod(i, fPeriod, lastData);
            // 数据生成
            FPeriod addPeriod;
            if (openNew != null) {
                addPeriod = new FPeriod();
                addPeriod.setFtradeid(tradeId);
                addPeriod.setFtime(openNew);
                addPeriod.setFkai(fPeriod.getFkai());
                addPeriod.setFgao(fPeriod.getFgao());
                addPeriod.setFdi(fPeriod.getFdi());
                addPeriod.setFshou(fPeriod.getFshou());
                addPeriod.setFliang(fPeriod.getFliang());
            } else {
                addPeriod = lastData;
                addPeriod.setFgao(fPeriod.getFgao().compareTo(addPeriod.getFgao()) > 0 ? fPeriod.getFgao() : addPeriod.getFgao());
                addPeriod.setFdi(fPeriod.getFdi().compareTo(addPeriod.getFdi()) > 0 ? addPeriod.getFdi() : fPeriod.getFdi());
                addPeriod.setFshou(fPeriod.getFkai());
                addPeriod.setFliang(MathUtils.add(addPeriod.getFliang(), fPeriod.getFliang()));
            }
            // 添加数据
            klineData.add(addPeriod);
            // 剔除数据
            if (klineData.size() > MAX_LEN) {
                klineData.remove(klineData.first());
            }
        }
        // 添加数据
        KlineMap.put(tradeId, klineTotal);
    }

    /**
     * 初始化KlineJson数据
     */
    private void initKlineJson(Integer tradeId) {
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> kMap = KlineMap.get(tradeId);
        ConcurrentHashMap<Integer, String> kJsonMap = KlineJson.get(tradeId);
        for (int i : TIME_KIND) {
            ConcurrentSkipListSet<FPeriod> fPeriods = kMap.get(i);
            StringBuffer sBuffer = new StringBuffer();
            for (FPeriod fPeriod : fPeriods) {
                if (sBuffer.length() <= 0) {
                    sBuffer.append("[" + (fPeriod.getFtime().getTime()) + "," + MathUtils.toScaleNum(fPeriod.getFkai(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFgao(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFdi(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFshou(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFliang(), MathUtils.DEF_COIN_SCALE) + "]");
                } else {
                    sBuffer.append(",[" + (fPeriod.getFtime().getTime()) + "," + MathUtils.toScaleNum(fPeriod.getFkai(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFgao(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFdi(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFshou(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFliang(), MathUtils.DEF_COIN_SCALE) + "]");
                }
            }
            kJsonMap.put(i, "[" + sBuffer.toString() + "]");
            // 投递Redis
            RedisObject redisObject = new RedisObject();
            redisObject.setExtObject("[" + sBuffer.toString() + "]");
            jobUtils.setRedisData(RedisConstant.KLINE_KEY + tradeId + "_" + i, JSON.toJSONString(redisObject));
        }
        KlineJson.put(tradeId, kJsonMap);
    }

    /**
     * 初始化LastKline
     *
     * @param tradeId 交易ID
     */
    private void initLastKline(int tradeId) {
        // 获取数据
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> klineTotal = KlineMap.get(tradeId);
        ConcurrentHashMap<Integer, FPeriod> lastKlineTotal = lastKlineMap.get(tradeId);
        for (int i : TIME_KIND) {
            ConcurrentSkipListSet<FPeriod> fPeriods = klineTotal.get(i);
            // 修改
            FPeriod fperiod = !fPeriods.isEmpty() ? fPeriods.last() : null;
            if (fperiod != null) {
                lastKlineTotal.put(i, fperiod);
            } else {
                TickerData tickerData = TickerMap.get(tradeId);
                if (tickerData != null) {
                    lastKlineTotal.put(i, new FPeriod(tradeId, tickerData.getKai(), tickerData.getHigh()
                            , tickerData.getLow(), tickerData.getLast(), tickerData.getVol(), Utils.getTimestamp()));
                } else {
                    lastKlineTotal.put(i, new FPeriod(tradeId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                            , BigDecimal.ZERO, BigDecimal.ZERO, Utils.getTimestamp()));
                }
            }
        }
        // 更新
        lastKlineMap.put(tradeId, lastKlineTotal);
    }

    /**
     * 初始化LastKlineJson
     *
     * @param tradeId 交易ID
     */
    private void initLastKlineJson(int tradeId) {
        // 获取数据
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> klineTotal = KlineMap.get(tradeId);
        ConcurrentHashMap<Integer, String> lastJson = lastKlineJson.get(tradeId);
        for (int i : TIME_KIND) {
            ConcurrentSkipListSet<FPeriod> fPeriods = klineTotal.get(i);// 修改
            String lastKlineString;
            FPeriod fperiod = !fPeriods.isEmpty() ? fPeriods.last() : null;
            if (fperiod != null) {
                lastKlineString = "[[" + (fperiod.getFtime().getTime())
                        + "," + MathUtils.toScaleNum(fperiod.getFkai(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(fperiod.getFgao(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(fperiod.getFdi(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(fperiod.getFshou(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(fperiod.getFliang(), MathUtils.DEF_COIN_SCALE) + "]]";
            } else {
                lastKlineString = "[]";
            }
            lastJson.put(i, lastKlineString);
            // 投递Redis
            RedisObject redisObject = new RedisObject();
            redisObject.setExtObject(lastKlineString);
            jobUtils.setRedisData(RedisConstant.LASTKLINE_KEY + tradeId + "_" + i, JSON.toJSONString(redisObject));
        }
        // 更新
        lastKlineJson.put(tradeId, lastJson);
    }

    /**
     * 初始化已成交
     *
     * @param tradeId 交易ID
     */
    private void initSuccess(int tradeId) {
        ConcurrentSkipListSet<DepthData> successTmp = SuccessMap.get(tradeId);
        List<FEntrustLog> fEntrustLogs = fEntrustLogMapper.selectSuccess(tradeId, SUCCESS_MAX);
        for (FEntrustLog fEntrustLog : fEntrustLogs) {
            successTmp.add(new DepthData(fEntrustLog.getFprize(), fEntrustLog.getFcount(), fEntrustLog.getFcreatetime()
                    , fEntrustLog.getFentrusttype(), 0));
        }
        SuccessMap.put(tradeId, successTmp);
    }

    /**
     * 初始化已成交Json
     *
     * @param tradeId 交易ID
     */
    private void initSuccessJson(int tradeId) {
        ConcurrentSkipListSet<DepthData> successTmp = SuccessMap.get(tradeId);
        StringBuffer stringBuffer = new StringBuffer();
        for (DepthData depthData : successTmp) {
            if (stringBuffer.length() <= 0) {
                stringBuffer.append("[" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_CNY_SCALE)
                        + ",\"" + Utils.dateFormatHHMMSS(depthData.getTime())
                        + "\"," + depthData.getType() + "]");
            } else {
                stringBuffer.append(",[" + MathUtils.toScaleNum(depthData.getPrize(), MathUtils.DEF_CNY_SCALE)
                        + "," + MathUtils.toScaleNum(depthData.getCount(), MathUtils.DEF_CNY_SCALE)
                        + ",\"" + Utils.dateFormatHHMMSS(depthData.getTime()) + "\"," + depthData.getType() + "]");
            }
        }
        SuccessJson.put(tradeId, "[" + stringBuffer.toString() + "]");
        // 投递Redis
        RedisObject redisObject = new RedisObject();
        redisObject.setExtObject("[" + stringBuffer.toString() + "]");
        jobUtils.setRedisData(RedisConstant.SUCCESSENTRUST_KEY + tradeId, JSON.toJSONString(redisObject));
    }

    /**
     * 定时更新KlineMap
     */
    public void updateMinuteJob(int tradeId) throws Exception {
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> kMap = KlineMap.get(tradeId);
        if(kMap == null ){
            initMarketBean(tradeId);
            initMarketData(tradeId);
            return;
        }
        ConcurrentHashMap<Integer, FPeriod> kLastMap = lastKlineMap.get(tradeId);
        for (int i : TIME_KIND) {
            ConcurrentSkipListSet<FPeriod> fPeriods = kMap.get(i);
            FPeriod fPeriod = kLastMap.get(i);
            //1分钟数据存库
            if (i == TIME_KIND[0] && !isFirstInit) {
                fPeriodMapper.insert(fPeriod);
            }
            if (isFirstInit) {
                isFirstInit = false;
            }
            FPeriod last = null;
            if (fPeriods.size() > 0) {
                last = fPeriods.last();
            }
            FPeriod newPeriod = new FPeriod();
            newPeriod.setFtime(Utils.getYYYYMMDDHHMM());
            Timestamp newTime = isOpenNewPeriod(i, newPeriod, last);
            if (newTime != null) {
                newPeriod.setFtradeid(fPeriod.getFtradeid());
                newPeriod.setFgao(fPeriod.getFshou());
                newPeriod.setFdi(fPeriod.getFshou());
                newPeriod.setFkai(fPeriod.getFshou());
                newPeriod.setFshou(fPeriod.getFshou());
                newPeriod.setFtime(newTime);
                newPeriod.setFliang(BigDecimal.ZERO);
            } else {
                newPeriod = last;
            }
            fPeriods.add(newPeriod);
            // 剔除数据
            if (fPeriods.size() > MAX_LEN) {
                fPeriods.remove(fPeriods.first());
            }
            // 更新
            kMap.put(i, fPeriods);
            kLastMap.put(i, newPeriod);
        }
        KlineMap.put(tradeId, kMap);
        lastKlineMap.put(tradeId, kLastMap);
        // 生成Json数据
        initLastKlineJson(tradeId);
        // 生成Json数据
        initKlineJson(tradeId);
    }

    /**
     * 定时更新最新行情
     */
    public void updateTickerJob(int tradeId) throws Exception {
        ConcurrentSkipListSet<EntrustLogData> oneDayLogSet = oneDayLogMap.get(tradeId);
        // 计算 : 成交量 &高&低
        long time = Utils.getTimestamp().getTime();
        BigDecimal vol = BigDecimal.ZERO, low = BigDecimal.ZERO, high = BigDecimal.ZERO;
        for (EntrustLogData entrustLogData : oneDayLogSet) {
            // 超时移除
            if (time - entrustLogData.getFcreatetime().getTime() > 24 * 60 * 60 * 1000L) {
                oneDayLogSet.remove(entrustLogData);
            } else {
                // 重新统计
                vol = MathUtils.add(vol, entrustLogData.getFcount());
                BigDecimal prizeTmp = entrustLogData.getFprize();
                low = (low.compareTo(prizeTmp) > 0 || low.compareTo(BigDecimal.ZERO) == 0) ? prizeTmp : low;
                high = high.compareTo(prizeTmp) < 0 ? prizeTmp : high;
            }
        }
        oneDayLogMap.put(tradeId, oneDayLogSet);
        // 更新数据
        TickerData tickerData = TickerMap.get(tradeId);
        tickerData.setVol(vol);
        tickerData.setHigh(high);
        tickerData.setLow(low);
        // 更新
        TickerMap.put(tradeId, tickerData);
        // 生成Json
        initTickerJson(tradeId);
    }

    /**
     * 定时更新开盘价
     */
    public void updateKaiJob(int tradeId) throws Exception {
        // 更新数据
        TickerData tickerData = TickerMap.get(tradeId);
        tickerData.setKai(tickerData.getLast());
        // 更新
        TickerMap.put(tradeId, tickerData);
        // 生成Json
        initTickerJson(tradeId);
    }

    /**
     * 定时更新三日行情
     */
    public void updateThreeTickerJob(int tradeId) throws Exception {
        // 实例化三日行情
        ConcurrentSkipListSet<FPeriod> threeDayTmp = ThreeDayLogMap.get(tradeId);
        threeDayTmp.clear();
        // 每小时k线
        ConcurrentHashMap<Integer, ConcurrentSkipListSet<FPeriod>> kMap = KlineMap.get(tradeId);
        ConcurrentSkipListSet<FPeriod> fPeriods = kMap.get(60);
        // 加入3天数据
        long time = Utils.getTimestamp().getTime();
        for (FPeriod fPeriod : fPeriods) {
            if (time - fPeriod.getFtime().getTime() <= 3 * 24 * 60 * 60 * 1000L) {
                threeDayTmp.add(new FPeriod(tradeId, fPeriod.getFshou(), fPeriod.getFtime()));
            }
        }
        ThreeDayLogMap.put(tradeId, threeDayTmp);
        // 生成Json
        initThreeTickerJson(tradeId);
    }

    /**
     * MQ更新lastKline
     *
     * @param tradeId 交易ID
     * @param prize  价格
     * @param count  数量
     * @throws Exception 执行异常
     */
    public synchronized void updateKlineMQ(int tradeId, BigDecimal prize, BigDecimal count) throws Exception {
        // 取数据
        ConcurrentHashMap<Integer, FPeriod> lastklineTotal = lastKlineMap.get(tradeId);
        ConcurrentHashMap<Integer, String> lastJson = lastKlineJson.get(tradeId);
        for (int i : TIME_KIND) {
            FPeriod fPeriod = lastklineTotal.get(i);
            if (fPeriod == null) {
                fPeriod = new FPeriod(tradeId, prize, prize, prize, prize, count, Utils.getYYYYMMDDHHMM());
                lastklineTotal.put(i, fPeriod);
                continue;
            }
            // 赋值开
            if (fPeriod.getFkai().compareTo(BigDecimal.ZERO) <= 0) {
                fPeriod.setFkai(prize);
            }
            // 比较高
            if (prize.compareTo(fPeriod.getFgao()) > 0 || fPeriod.getFgao().compareTo(BigDecimal.ZERO) <= 0) {
                fPeriod.setFgao(prize);
            }
            // 比较低
            if (prize.compareTo(fPeriod.getFdi()) < 0 || fPeriod.getFdi().compareTo(BigDecimal.ZERO) <= 0) {
                fPeriod.setFdi(prize);
            }
            // 赋值收
            fPeriod.setFshou(prize);
            // 赋值量
            fPeriod.setFliang(MathUtils.add(fPeriod.getFliang(), count));

            // 更新
            lastklineTotal.put(i, fPeriod);
            // Json生成
            String string = "[[" + (fPeriod.getFtime().getTime()) + "," + MathUtils.toScaleNum(fPeriod.getFkai(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFgao(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFdi(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFshou(), MathUtils.DEF_CNY_SCALE) + "," + MathUtils.toScaleNum(fPeriod.getFliang(), MathUtils.DEF_COIN_SCALE) + "]]";
            lastJson.put(i, string);
            // 投递Redis
            RedisObject redisObject = new RedisObject();
            redisObject.setExtObject(string);
            jobUtils.setRedisData(RedisConstant.LASTKLINE_KEY + tradeId + "_" + i, JSON.toJSONString(redisObject));
        }
        lastKlineMap.put(tradeId, lastklineTotal);
        lastKlineJson.put(tradeId, lastJson);
    }

    /**
     * MQ更新Ticker
     *
     * @param tradeId 交易ID
     * @param prize  价格
     * @param count  数量
     * @throws Exception 执行异常
     */
    public synchronized void updateTickerMQ(int tradeId, BigDecimal prize, BigDecimal count) throws Exception {
        // 增加新Data
        ConcurrentSkipListSet<EntrustLogData> oneDayLogSet = oneDayLogMap.get(tradeId);
        oneDayLogSet.add(
                new EntrustLogData(new BigInteger(Utils.randomInteger(6)), 3, prize, count, Utils.getTimestamp()));
        oneDayLogMap.put(tradeId, oneDayLogSet);
        // 更新最新成交价
        TickerData tickerData = TickerMap.get(tradeId);
        tickerData.setLast(prize);
        // 更新涨跌幅
        BigDecimal chgTmp = MathUtils.div(MathUtils.sub(prize, tickerData.getKai()), tickerData.getKai());
        tickerData.setChg(MathUtils.mul(chgTmp, new BigDecimal("100")));
        // 更新
        TickerMap.put(tradeId, tickerData);
        // 生成Json
        initTickerJson(tradeId);
    }

    /**
     * MQ更新SuccessMap
     *
     * @param tradeId    交易ID
     * @param type      类型
     * @param count     数量
     * @param last      成交价
     * @throws Exception 执行异常
     */
    public synchronized void updateSuccessMQ(int tradeId, int type, BigDecimal count, BigDecimal last) throws Exception {
        ConcurrentSkipListSet<DepthData> successTmp = SuccessMap.get(tradeId);
        // 加入买
        if (type == EntrustTypeEnum.BUY.getCode()) {
            successTmp.add(new DepthData(last, count, Utils.getTimestamp(), 0, 0));
        } else {
            successTmp.add(new DepthData(last, count, Utils.getTimestamp(), 1, 1));
        }
        // 删除超出
        if (successTmp.size() > SUCCESS_MAX) {
            successTmp.remove(successTmp.last());
        }
        SuccessMap.put(tradeId, successTmp);
        // 生成Json
        initSuccessJson(tradeId);
    }

    /**
     * 更新买深度
     *
     * @param isAdd  是否增加
     * @param tradeId 交易ID
     * @param prize  价格
     * @param count  数量
     * @throws Exception 执行异常
     */
    public synchronized void updateBuyDepthMapMQ(boolean isAdd, int tradeId, BigDecimal prize, BigDecimal count) throws Exception {
        ConcurrentSkipListSet<DepthData> buyDepth = BuyDepthMap.get(tradeId);
        if (buyDepth == null) {
            buyDepth = new ConcurrentSkipListSet<>(prizeComparaDESC);
        }
        boolean isUpdate = false;
        // 查找修改
        for (DepthData depthData : buyDepth) {
            if (depthData.getPrize().compareTo(prize) == 0) {
                if (isAdd) {
                    depthData.setCount(MathUtils.add(depthData.getCount(), count));
                } else {
                    depthData.setCount(MathUtils.sub(depthData.getCount(), count));
                }
                isUpdate = true;
                break;
            }
        }
        // 查找没有新增
        if (!isUpdate) {
            buyDepth.add(new DepthData(prize, isAdd ? count : MathUtils.positive2Negative(count)));
        }
        BuyDepthMap.put(tradeId, buyDepth);
        initBuyDepthJson(tradeId);
        // 更新实时买1
        TickerData tickerData = TickerMap.get(tradeId);
        if (tickerData == null){
            tickerData = new TickerData();
        }
        if (buyDepth.size() > 0) {
            for (DepthData depthData : buyDepth) {
                if (depthData.getCount().compareTo(BigDecimal.ZERO) > 0) {
                    tickerData.setBuy(depthData.getPrize());
                    break;
                }
            }
        } else {
            tickerData.setBuy(BigDecimal.ZERO);
        }
        TickerMap.put(tradeId, tickerData);
        initTickerJson(tradeId);
    }

    /**
     * 更新卖深度
     *
     * @param isAdd  是否增加
     * @param tradeId 交易ID
     * @param prize  价格
     * @param count  数量
     * @throws Exception 执行异常
     */
    public synchronized void updateSellDepthMapMQ(boolean isAdd, int tradeId, BigDecimal prize, BigDecimal count) throws Exception {
        ConcurrentSkipListSet<DepthData> SellDepth = SellDepthMap.get(tradeId);
        if (SellDepth == null) {
            SellDepth = new ConcurrentSkipListSet<>(prizeComparaASC);
        }
        boolean isUpdate = false;
        // 查找修改
        for (DepthData depthData : SellDepth) {
            if (depthData.getPrize().compareTo(prize) == 0) {
                if (isAdd) {
                    depthData.setCount(MathUtils.add(depthData.getCount(), count));
                } else {
                    depthData.setCount(MathUtils.sub(depthData.getCount(), count));
                }
                isUpdate = true;
                break;
            }
        }
        // 查找没有新增
        if (!isUpdate) {
            SellDepth.add(new DepthData(prize, isAdd ? count : MathUtils.positive2Negative(count)));
        }
        SellDepthMap.put(tradeId, SellDepth);
        initSellDepthJson(tradeId);
        // 更新实时卖1
        TickerData tickerData = TickerMap.get(tradeId);
        if (tickerData == null){
            tickerData = new TickerData();
        }
        if (SellDepth.size() > 0) {
            for (DepthData depthData : SellDepth) {
                if (depthData.getCount().compareTo(BigDecimal.ZERO) > 0) {
                    tickerData.setSell(depthData.getPrize());
                    break;
                }
            }
        } else {
            tickerData.setSell(BigDecimal.ZERO);
        }
        TickerMap.put(tradeId, tickerData);
        initTickerJson(tradeId);
    }

    /**
     * 返回下个阶段的时间戳
     *
     * @param step    间隔
     * @param fperiod K线实体
     * @param last    K线实体
     */
    private Timestamp isOpenNewPeriod(long step, FPeriod fperiod, FPeriod last) {//
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Timestamp ftime = new Timestamp(sdf1.parse(sdf1.format(fperiod.getFtime())).getTime());
            // 委单时间 距离 本月开始时间 间隔
            long minus = (ftime.getTime() - startTime.getTime()) / (60 * 1000L);
            //
            Timestamp nowTime = new Timestamp(startTime.getTime() + minus / step * step * 60 * 1000L);// 新的时间

            if (last == null || (minus % step == 0 && last.getFtime().getTime() != nowTime.getTime()) || (minus % step != 0 && last.getFtime().getTime() != nowTime.getTime())) {
                return new Timestamp(startTime.getTime() + minus / step * step * 60 * 1000L);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("isOpenNewPeriod err");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 升序-深度 ASC
     */
    private Comparator<DepthData> prizeComparaASC = new Comparator<DepthData>() {
        public int compare(DepthData o1, DepthData o2) {
            return o1.getPrize().compareTo(o2.getPrize());
        }
    };

    /**
     * 降序-深度 DESC
     */
    private Comparator<DepthData> prizeComparaDESC = new Comparator<DepthData>() {
        public int compare(DepthData o1, DepthData o2) {
            return o2.getPrize().compareTo(o1.getPrize());
        }
    };

    /**
     * 降序-时间 DESC
     */
    private Comparator<DepthData> successComparaDESC = new Comparator<DepthData>() {
        public int compare(DepthData o1, DepthData o2) {
            int result = o2.getTime().compareTo(o1.getTime());
            if (result == 0) {
                return o2.getUuid().compareTo(o1.getUuid());
            } else {
                return result;
            }
        }
    };

    /**
     * Kline-时间排序
     */
    private Comparator<FPeriod> kLineCompara = new Comparator<FPeriod>() {
        public int compare(FPeriod o1, FPeriod o2) {
            return o1.getFtime().compareTo(o2.getFtime());
        }
    };

    /**
     * EentrustLog-时间排序
     */
    private Comparator<EntrustLogData> oneDayLogCompara = new Comparator<EntrustLogData>() {
        public int compare(EntrustLogData o1, EntrustLogData o2) {
            //return o1.getFcreatetime().compareTo(o2.getFcreatetime());
            return o1.getFid().compareTo(o2.getFid());
        }
    };
}
