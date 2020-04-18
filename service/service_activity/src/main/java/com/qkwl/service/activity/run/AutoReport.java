package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.report.ReportCapital;
import com.qkwl.common.dto.report.ReportTrade;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.DateCollector;
import com.qkwl.common.util.DateUtils;
import com.qkwl.service.activity.dao.*;
import com.qkwl.service.activity.utils.ActivityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * 报表数据采集定时器
 * Created by ZKF on 2017/7/24.
 */
@Component("autoReport")
public class AutoReport {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;
    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;
    @Autowired
    private FVirtualCapitalOperationMapper virtualCapitalOperationMapper;
    @Autowired
    private ReportTradeMapper reportTradeMapper;
    @Autowired
    private ReportCapitalMapper reportCapitalMapper;


    @Scheduled(cron = "0 0 * * * ? ")
    //@PostConstruct
    public void collection(){
        // 1. 获取时间区间
        Map<String, Object> map = DateCollector.getTimeInterval();
        Date begin = DateUtils.parse(map.get("begin").toString(), DateUtils.YYYY_MM_DD_HH_MM_SS);
        Date end = DateUtils.parse(map.get("end").toString(), DateUtils.YYYY_MM_DD_HH_MM_SS);
        // 2. 查询数据
        // (1) 交易对数据
        List<SystemTradeType> tradeTypeList = redisHelper.getAllTradeTypeList(ActivityConstant.BCAgentId);
        for(SystemTradeType tradeType : tradeTypeList){
            map.put("tradeId", tradeType.getId());
            for(EntrustTypeEnum e : EntrustTypeEnum.values()){
                map.put("type", e.getCode());
                ReportTrade report = entrustHistoryMapper.selectReport(map);
                report.setTradeId(tradeType.getId());
                report.setBuyCoinId(tradeType.getBuyCoinId());
                report.setSellCoinId(tradeType.getSellCoinId());
                report.setGmtBegin(begin);
                report.setGmtEnd(end);
                report.setHourIndex((Integer)map.get("index"));
                report.setType(e.getCode());
                if(report.getTradeAmount().compareTo(BigDecimal.ZERO) > 0
                        || report.getTradeCount().compareTo(BigDecimal.ZERO) > 0
                        || report.getTradeFee().compareTo(BigDecimal.ZERO) > 0){
                    reportTradeMapper.insert(report);
                }
            }
        }
        // (2) 充提数据
        List<SystemCoinType> coinTypeList = redisHelper.getCoinTypeList();
        for(SystemCoinType coin : coinTypeList){
            for(CapitalOperationInOutTypeEnum e : CapitalOperationInOutTypeEnum.values()){
                map.put("type", e.getCode());
                ReportCapital report = null;
                if(coin.getShortName().equals("CNY")){
                    report = walletCapitalOperationMapper.selectReport(map);
                    // 新版充值数据待统计
                    if(report == null){
                        report = new ReportCapital();
                        report.setFee(BigDecimal.ZERO);
                    }
                }else{
                    map.put("coin", coin.getId());
                    report = virtualCapitalOperationMapper.selectReport(map);
                }

                if(report != null){
                    report.setType(e.getCode());
                    report.setCoinId(coin.getId());
                    report.setGmtBegin(begin);
                    report.setGmtEnd(end);
                    report.setHourIndex((Integer)map.get("index"));
                    if(report.getAmount().compareTo(BigDecimal.ZERO) > 0
                            || report.getFee().compareTo(BigDecimal.ZERO) > 0){
                        reportCapitalMapper.insert(report);
                    }
                }
            }
        }
    }
}
