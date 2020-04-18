package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.Enum.charts.CapitalDataTypeEnum;
import com.qkwl.common.Enum.charts.CycleEnum;
import com.qkwl.common.Enum.charts.TradeDataTypeEnum;
import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.report.ReportCapital;
import com.qkwl.common.dto.report.ReportTrade;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminReportService;
import com.qkwl.common.util.DateCollector;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 图表统计
 * Created by ZKF on 2017/7/25.
 */
@Controller
public class ChartsController extends WebBaseController{

    @Autowired
    private IAdminReportService adminReportService;
    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping("/admin/tradeCharts")
    public ModelAndView tradeCharts(
            @RequestParam(value = "tradeId", required = false, defaultValue = "0") Integer tradeId,
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "dataId", required = false, defaultValue = "0") Integer dataId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("charts/tradeCharts");

        Map<Integer, String> dataTypeMap = new LinkedHashMap<>();
        dataTypeMap.put(0, "请选择");
        for(TradeDataTypeEnum e : TradeDataTypeEnum.values()){
            dataTypeMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("dataTypeMap", dataTypeMap);

        Map<Integer, String> tradeTypeMap = new LinkedHashMap<>();
        tradeTypeMap.put(0, "请选择");
        List<SystemTradeType> tradeList = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        for(SystemTradeType trade:tradeList){
            tradeTypeMap.put(trade.getId(), trade.getBuyName()+"/"+trade.getSellName());
        }
        modelAndView.addObject("tradeTypeMap", tradeTypeMap);

        Map<Integer, String> cycleMap = new LinkedHashMap<>();
        cycleMap.put(0, "请选择");
        for(CycleEnum e:CycleEnum.values()){
            cycleMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("cycleMap", cycleMap);

        modelAndView.addObject("tradeId", tradeId);
        modelAndView.addObject("dataId", dataId);
        modelAndView.addObject("cycleId", cycleId);
        modelAndView.addObject("day", day);

        if(tradeId == 0 || cycleId == 0 || dataId == 0){
            return modelAndView;
        }

        if(dataId.equals(TradeDataTypeEnum.FEE.getCode())){
            SystemTradeType trade = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
            modelAndView.addObject("buyName", TradeDataTypeEnum.getValueByCode(dataId)+"-"+trade.getSellName());
            modelAndView.addObject("sellName", TradeDataTypeEnum.getValueByCode(dataId)+"-"+trade.getBuyName());
        }else{
            modelAndView.addObject("buyName", TradeDataTypeEnum.getValueByCode(dataId));
            modelAndView.addObject("sellName", TradeDataTypeEnum.getValueByCode(dataId));
        }

        JSONArray array = null;
        if(cycleId.equals(CycleEnum.DAY.getCode())){
            array = getHourArray(tradeId, dataId, day);
        }else if(cycleId.equals(CycleEnum.WEEK.getCode())){
            array = getDayArray(2, tradeId, dataId, day);
        }else if(cycleId.equals(CycleEnum.MONTH.getCode())){
            array = getDayArray(3, tradeId, dataId, day);
        }

        modelAndView.addObject("data", array);
        return modelAndView;
    }

    @RequestMapping("/charts/tradeReport")
    @ResponseBody
    public ReturnResult tradeReport(
            @RequestParam(value = "tradeId", required = false, defaultValue = "0") Integer tradeId,
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "dataId", required = false, defaultValue = "0") Integer dataId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        if(tradeId == 0 || cycleId == 0 || dataId == 0 || StringUtils.isEmpty(day)){
            return ReturnResult.FAILUER("请选择您需要的统计！");
        }

        SystemTradeType trade = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);

        String[] xaxis = getX(cycleId, day);
        JSONObject obj = new JSONObject();
        obj.put("trade", trade.getBuyName()+"/"+trade.getSellName());
        obj.put("xaxis", xaxis);
        JSONArray array = null;
        if(cycleId.equals(CycleEnum.DAY.getCode())){
            array = getHourReport(tradeId, dataId, day);
        }else if(cycleId.equals(CycleEnum.WEEK.getCode())){
            array = getDayReport(2, tradeId, dataId, day);
        }else if(cycleId.equals(CycleEnum.MONTH.getCode())){
            array = getDayReport(3, tradeId, dataId, day);
        }
        obj.put("line", array);

        return ReturnResult.SUCCESS(obj);
    }

    @RequestMapping("/admin/capitalCharts")
    public ModelAndView capitalCharts(
            @RequestParam(value = "coinId", required = false, defaultValue = "-1") Integer coinId,
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "dataId", required = false, defaultValue = "0") Integer dataId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("charts/capitalCharts");

        Map<Integer, String> dataTypeMap = new LinkedHashMap<>();
        dataTypeMap.put(0, "请选择");
        for(CapitalDataTypeEnum e : CapitalDataTypeEnum.values()){
            dataTypeMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("dataTypeMap", dataTypeMap);

        Map<Integer, String> coinMap = new LinkedHashMap<>();
        coinMap.put(-1, "请选择");
        List<SystemCoinType> coinList = redisHelper.getCoinTypeListAll();
        for(SystemCoinType coin:coinList){
            coinMap.put(coin.getId(), coin.getName());
        }
        modelAndView.addObject("coinMap", coinMap);

        Map<Integer, String> cycleMap = new LinkedHashMap<>();
        cycleMap.put(0, "请选择");
        for(CycleEnum e:CycleEnum.values()){
            cycleMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("cycleMap", cycleMap);

        modelAndView.addObject("coinId", coinId);
        modelAndView.addObject("dataId", dataId);
        modelAndView.addObject("cycleId", cycleId);
        modelAndView.addObject("day", day);

        if(coinId < 0 || cycleId == 0){
            return modelAndView;
        }

        SystemCoinType coin = redisHelper.getCoinType(coinId);
        modelAndView.addObject("coinName", coin.getName());

        JSONArray array = getCapitalArray(cycleId, coinId, dataId, day);
        modelAndView.addObject("data", array);
        return modelAndView;
    }

    @RequestMapping("/charts/capitalReport")
    @ResponseBody
    public ReturnResult capitalReport(
            @RequestParam(value = "coinId", required = false, defaultValue = "-1") Integer coinId,
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "dataId", required = false, defaultValue = "0") Integer dataId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        if(coinId < 0 || cycleId == 0 || StringUtils.isEmpty(day)){
            return ReturnResult.FAILUER("请选择您需要的统计！");
        }

        SystemCoinType coin = redisHelper.getCoinType(coinId);

        String[] xaxis = getX(cycleId, day);
        JSONObject obj = new JSONObject();
        obj.put("coin", coin.getName());
        obj.put("xaxis", xaxis);
        JSONArray array = null;
        if(cycleId.equals(CycleEnum.DAY.getCode())){
            array = getCapitalReport(1, coinId, dataId, day);
        }else if(cycleId.equals(CycleEnum.WEEK.getCode())){
            array = getCapitalReport(2, coinId, dataId, day);
        }else if(cycleId.equals(CycleEnum.MONTH.getCode())){
            array = getCapitalReport(3, coinId, dataId, day);
        }
        obj.put("line", array);

        return ReturnResult.SUCCESS(obj);
    }

    @RequestMapping("/admin/incomeCharts")
    public ModelAndView IncomeCharts(
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("charts/incomeCharts");

        Map<Integer, String> dataTypeMap = new LinkedHashMap<>();
        dataTypeMap.put(0, "请选择");
        for(CapitalDataTypeEnum e : CapitalDataTypeEnum.values()){
            dataTypeMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("dataTypeMap", dataTypeMap);


        Map<Integer, String> cycleMap = new LinkedHashMap<>();
        cycleMap.put(0, "请选择");
        for(CycleEnum e:CycleEnum.values()){
            cycleMap.put(e.getCode(), e.getValue());
        }
        modelAndView.addObject("cycleMap", cycleMap);

        modelAndView.addObject("cycleId", cycleId);
        modelAndView.addObject("day", day);

        if(cycleId == 0 || StringUtils.isEmpty(day)){
            return modelAndView;
        }

        String[] xaxis = getX(cycleId, day);
        modelAndView.addObject("xaxis", xaxis);

        List<SystemCoinType> coinList = redisHelper.getCoinTypeListAll();
        JSONArray array = new JSONArray();
        for(SystemCoinType coin : coinList){
            JSONObject coinObj = getIncomeArray(cycleId, coin, day);
            array.add(coinObj);
        }

        modelAndView.addObject("data", array);
        return modelAndView;
    }

    @RequestMapping("/charts/incomeReport")
    @ResponseBody
    public ReturnResult incomeReport(
            @RequestParam(value = "cycleId", required = false, defaultValue = "0") Integer cycleId,
            @RequestParam(value = "day", required = false, defaultValue = "") String day
    ){
        if(cycleId == 0 || StringUtils.isEmpty(day)){
            return ReturnResult.FAILUER("请选择您需要的统计！");
        }

        String[] xaxis = getX(cycleId, day);
        JSONObject obj = new JSONObject();
        obj.put("xaxis", xaxis);

        List<SystemCoinType> coinTypeList = redisHelper.getCoinTypeListAll();
        if(coinTypeList != null){
            List<String> legendList = new LinkedList<>();
            JSONArray array = new JSONArray();
            for(SystemCoinType coin : coinTypeList){
                JSONObject coinReport = getIncomeReport(cycleId, coin, day);
                coinReport.put("name", coin.getName());
                coinReport.put("type", "line");
                array.add(coinReport);
                legendList.add(coin.getName());
            }
            obj.put("line", array);
            obj.put("legend", legendList.toArray());
        }

        return ReturnResult.SUCCESS(obj);
    }


    private JSONArray getDayArray(Integer cycle, Integer tradeId, Integer dataId, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
        }

        param.put("tradeId", tradeId);
        JSONArray array = new JSONArray();
        for(int i = 1; i <= length; i++){
            JSONObject obj = new JSONObject();
            obj.put("time", "第"+i+"天");
            for(EntrustTypeEnum e : EntrustTypeEnum.values()){
                param.put("type", e.getCode());
                String key = e.getCode() == 0 ? "buy":"sell";
                List<ReportTrade> list = adminReportService.selectDayTradeReport(param);
                if(list != null && list.size() > 0){
                    for(ReportTrade trade: list){
                        if(length > 7){
                            trade.setHourIndex(DateCollector.getDayOfMonth(trade.getGmtBegin()));
                        }else{
                            trade.setHourIndex(DateCollector.getDayOfWeek(trade.getGmtBegin()));
                        }
                        if(i == trade.getHourIndex()){
                            if(dataId.equals(TradeDataTypeEnum.VOLUME.getCode())){
                                obj.put(key,trade.getTradeCount());
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.TURNVOLUME.getCode())){
                                obj.put(key,trade.getTradeAmount());
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.FEE.getCode())){
                                obj.put(key,trade.getTradeFee());
                                break;
                            }else{
                                obj.put(key,BigDecimal.ZERO);
                                break;
                            }
                        }else{
                            obj.put(key,BigDecimal.ZERO);
                        }
                    }
                }else{
                    obj.put(key,BigDecimal.ZERO);
                }
            }
            array.add(obj);
        }
        return array;
    }

    private JSONArray getDayReport(Integer cycle, Integer tradeId, Integer dataId, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
        }

        param.put("tradeId", tradeId);
        JSONArray array = new JSONArray();
        for(EntrustTypeEnum e : EntrustTypeEnum.values()){
            List<BigDecimal> data = new LinkedList<>();
            for(int i = 1; i <= length; i++){
                param.put("type", e.getCode());
                List<ReportTrade> list = adminReportService.selectDayTradeReport(param);
                BigDecimal amount = BigDecimal.ZERO;
                if(list != null && list.size() > 0){
                    for(ReportTrade trade: list){
                        if(length > 7){
                            trade.setHourIndex(DateCollector.getDayOfMonth(trade.getGmtBegin()));
                        }else{
                            trade.setHourIndex(DateCollector.getDayOfWeek(trade.getGmtBegin()));
                        }
                        if(i == trade.getHourIndex()){
                            if(dataId.equals(TradeDataTypeEnum.VOLUME.getCode())){
                                amount = trade.getTradeCount();
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.TURNVOLUME.getCode())){
                                amount = trade.getTradeAmount();
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.FEE.getCode())){
                                amount = trade.getTradeFee();
                                break;
                            }else{
                                break;
                            }
                        }
                    }
                }
                data.add(amount);
            }
            JSONObject obj = new JSONObject();
            obj.put("type", e.getCode());
            obj.put("amount", data.toArray());
            array.add(obj);
        }
        return array;
    }

    private JSONArray getHourArray(Integer tradeId, Integer dataId, String day){
        Map<String, Object> param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
        param.put("tradeId", tradeId);
        JSONArray array = new JSONArray();
        for(int i = 0; i <= 23; i++){
            JSONObject obj = new JSONObject();
            obj.put("time", (i+1)+"点");
            for(EntrustTypeEnum e : EntrustTypeEnum.values()){
                param.put("type", e.getCode());
                String key = e.getCode() == 0 ? "buy":"sell";
                List<ReportTrade> list = adminReportService.selectHourTradeReport(param);
                if(list != null && list.size() > 0){
                    for(ReportTrade trade: list){
                        if(i == trade.getHourIndex()){
                            if(dataId.equals(TradeDataTypeEnum.VOLUME.getCode())){
                                obj.put(key,trade.getTradeCount());
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.TURNVOLUME.getCode())){
                                obj.put(key,trade.getTradeAmount());
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.FEE.getCode())){
                                obj.put(key,trade.getTradeFee());
                                break;
                            }else{
                                obj.put(key,BigDecimal.ZERO);
                                break;
                            }
                        }else{
                            obj.put(key,BigDecimal.ZERO);
                        }
                    }
                }else{
                    obj.put(key,BigDecimal.ZERO);
                }
            }
            array.add(obj);
        }
        return array;
    }

    private JSONArray getHourReport(Integer tradeId, Integer dataId, String day){
        Map<String, Object> param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
        param.put("tradeId", tradeId);
        JSONArray array = new JSONArray();
        for(EntrustTypeEnum e:EntrustTypeEnum.values()){
            param.put("type", e.getCode());
            List<BigDecimal> data = new LinkedList<>();
            List<ReportTrade> list = adminReportService.selectHourTradeReport(param);
            for(int i = 0; i <= 23; i++){
                BigDecimal amount = BigDecimal.ZERO;
                if(list != null && list.size() > 0){
                    for(ReportTrade trade: list){
                        if(i == trade.getHourIndex()){
                            if(dataId.equals(TradeDataTypeEnum.VOLUME.getCode())){
                                amount = trade.getTradeCount();
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.TURNVOLUME.getCode())){
                                amount = trade.getTradeAmount();
                                break;
                            }else if(dataId.equals(TradeDataTypeEnum.FEE.getCode())){
                                amount = trade.getTradeFee();
                                break;
                            }else{
                                break;
                            }
                        }
                    }
                }
                data.add(amount);
            }
            JSONObject obj = new JSONObject();
            obj.put("type", e.getCode());
            obj.put("amount", data.toArray());
            array.add(obj);
        }
        return array;
    }

    private JSONArray getCapitalArray(Integer cycle, Integer coinId, Integer dataId, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        String unit = "天";
        if(cycle.equals(CycleEnum.DAY.getCode())){
            param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 24;
            unit = "点";
        }else if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
        }

        param.put("coinId", coinId);
        JSONArray array = new JSONArray();
        for(int i = 1; i <= length; i++){
            JSONObject obj = new JSONObject();
            if(cycle.equals(CycleEnum.DAY.getCode())){
                obj.put("time", i+unit);
            }else{
                obj.put("time", "第"+i+unit);
            }
            for(CapitalOperationInOutTypeEnum e : CapitalOperationInOutTypeEnum.values()){
                param.put("type", e.getCode());
                String key = e.getCode() == 1 ? "recharge":"withdraw";
                List<ReportCapital> list = null;
                if(cycle.equals(CycleEnum.DAY.getCode())){
                    list = adminReportService.selectHourCapitalReport(param);
                }else{
                    list = adminReportService.selectDayCapitalReport(param);
                }
                if(list != null && list.size() > 0){
                    for(ReportCapital capital: list){
                        if(cycle.equals(CycleEnum.WEEK.getCode())){
                            capital.setHourIndex(DateCollector.getDayOfWeek(capital.getGmtBegin()));
                        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
                            capital.setHourIndex(DateCollector.getDayOfMonth(capital.getGmtBegin()));
                        }
                        if(i == capital.getHourIndex()){
                           if(dataId.equals(CapitalDataTypeEnum.VOLUME.getCode())){
                                obj.put(key,capital.getAmount());
                                break;
                            }else if(dataId.equals(CapitalDataTypeEnum.FEE.getCode())){
                                obj.put(key,capital.getFee());
                                break;
                            }else{
                                obj.put(key,BigDecimal.ZERO);
                                break;
                            }
                        }else{
                            obj.put(key,BigDecimal.ZERO);
                        }
                    }
                }else{
                    obj.put(key,BigDecimal.ZERO);
                }
            }
            array.add(obj);
        }
        return array;
    }

    private JSONArray getCapitalReport(Integer cycle, Integer coinId, Integer dataId, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        String unit = "天";
        if(cycle.equals(CycleEnum.DAY.getCode())){
            param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 24;
            unit = "点";
        }else if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
        }

        param.put("coinId", coinId);
        JSONArray array = new JSONArray();
        for(CapitalOperationInOutTypeEnum e : CapitalOperationInOutTypeEnum.values()){
            List<BigDecimal> data = new LinkedList<>();
            for(int i = 1; i <= length; i++){
                param.put("type", e.getCode());
                List<ReportCapital> list = null;
                if(cycle.equals(CycleEnum.DAY.getCode())){
                    list = adminReportService.selectHourCapitalReport(param);
                }else{
                    list = adminReportService.selectDayCapitalReport(param);
                }
                BigDecimal amount = BigDecimal.ZERO;
                if(list != null && list.size() > 0){
                    for(ReportCapital capital: list){
                        if(cycle.equals(CycleEnum.WEEK.getCode())){
                            capital.setHourIndex(DateCollector.getDayOfWeek(capital.getGmtBegin()));
                        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
                            capital.setHourIndex(DateCollector.getDayOfMonth(capital.getGmtBegin()));
                        }
                        if(i == capital.getHourIndex()){
                            if(dataId.equals(CapitalDataTypeEnum.VOLUME.getCode())){
                                amount = capital.getAmount();
                                break;
                            }else if(dataId.equals(CapitalDataTypeEnum.FEE.getCode())){
                                amount = capital.getFee();
                                break;
                            }else{
                                break;
                            }
                        }
                    }
                }
                data.add(amount);
            }
            JSONObject obj = new JSONObject();
            obj.put("type", e.getCode());
            obj.put("amount", data.toArray());
            array.add(obj);
        }
        return array;
    }

    private JSONObject getIncomeArray(Integer cycle, SystemCoinType coin, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        String unit = "天";
        if(cycle.equals(CycleEnum.DAY.getCode())){
            param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 24;
            unit = "点";
            param.put("left", 13);
        }else if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
            param.put("left", 10);
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
            param.put("left", 10);
        }

        param.put("coinId", coin.getId());
        if(coin.getType().equals(SystemCoinTypeEnum.CNY.getCode())){
            param.put("tradeType", EntrustTypeEnum.SELL.getCode());
        }else{
            param.put("tradeType", EntrustTypeEnum.BUY.getCode());
        }

        JSONObject obj = new JSONObject();
        for(Integer i = 1; i <= length; i++){
            obj.put("coin", coin.getName());
            List<ReportCapital> list = adminReportService.selectIncomeReport(param);
            if(list != null && list.size() > 0){
                for(ReportCapital capital: list){
                    if(cycle.equals(CycleEnum.WEEK.getCode())){
                        capital.setHourIndex(DateCollector.getDayOfWeek(capital.getGmtBegin()));
                    }else if(cycle.equals(CycleEnum.MONTH.getCode())){
                        capital.setHourIndex(DateCollector.getDayOfMonth(capital.getGmtBegin()));
                    }
                    if(i == capital.getHourIndex()){
                        obj.put(i.toString(),capital.getFee());
                        break;
                    }else{
                        obj.put(i.toString(),BigDecimal.ZERO);
                    }
                }
            }else{
                obj.put(i.toString(),BigDecimal.ZERO);
            }
        }
        return obj;
    }

    private JSONObject getIncomeReport(Integer cycle, SystemCoinType coin, String day){
        Map<String, Object> param = null;
        Integer length = 1;
        String unit = "天";
        if(cycle.equals(CycleEnum.DAY.getCode())){
            param = DateCollector.getDayInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 24;
            unit = "点";
            param.put("left", 13);
        }else if(cycle.equals(CycleEnum.WEEK.getCode())){
            param = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 7;
            param.put("left", 10);
        }else if(cycle.equals(CycleEnum.MONTH.getCode())){
            param = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            length = 31;
            param.put("left", 10);
        }

        param.put("coinId", coin.getId());
        if(coin.getType().equals(SystemCoinTypeEnum.CNY.getCode())){
            param.put("tradeType", EntrustTypeEnum.SELL.getCode());
        }else{
            param.put("tradeType", EntrustTypeEnum.BUY.getCode());
        }

        List<BigDecimal> data = new LinkedList<>();
        JSONObject obj = new JSONObject();
        for(int i = 1; i <= length; i++){
            BigDecimal amount = BigDecimal.ZERO;
            List<ReportCapital> list = adminReportService.selectIncomeReport(param);
            if(list != null && list.size() > 0){
                for(ReportCapital capital: list){
                    if(cycle.equals(CycleEnum.WEEK.getCode())){
                        capital.setHourIndex(DateCollector.getDayOfWeek(capital.getGmtBegin()));
                    }else if(cycle.equals(CycleEnum.MONTH.getCode())){
                        capital.setHourIndex(DateCollector.getDayOfMonth(capital.getGmtBegin()));
                    }
                    if(i == capital.getHourIndex()){
                        amount = capital.getFee();
                        break;
                    }
                }
            }
            data.add(amount);
        }
        obj.put("data", data.toArray());
        return obj;
    }

    private String[] getX(Integer cycle, String day){
        if(cycle.equals(CycleEnum.DAY.getCode())){
            List<String> strArray = new ArrayList<>();
            for(int i = 1; i <= 24; i++){
                strArray.add(i + "点");
            }
            String[] args = new String[24];
            return strArray.toArray(args);
        }
        if(cycle.equals(CycleEnum.WEEK.getCode())){

            Map<String, Object> map = DateCollector.getWeekInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            String begin = (String) map.get("begin");
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parse(begin, DateUtils.YYYY_MM_DD_HH_MM_SS));

            List<String> strArray = new ArrayList<>();
            for(int i = 0; i < 7; i++){
                if(i > 0){
                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
                }
                strArray.add(DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD));
            }
            String[] args = new String[7];
            return strArray.toArray(args);
        }
        if(cycle.equals(CycleEnum.MONTH.getCode())){

            Map<String, Object> map = DateCollector.getMonthInterval(DateUtils.parse(day, DateUtils.YYYY_MM_DD));
            String begin = (String) map.get("begin");
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parse(begin, DateUtils.YYYY_MM_DD_HH_MM_SS));

            List<String> strArray = new ArrayList<>();
            for(int i = 0; i < 31; i++){
                if(i > 0){
                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
                }
                strArray.add(DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD));
            }
            String[] args = new String[31];
            return strArray.toArray(args);
        }
        return null;
    }

    @RequestMapping("/charts/operationData")
    public ModelAndView operationData(
            @RequestParam(value = "begin", required = false) String begin,
            @RequestParam(value = "end", required = false) String end
    ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/operationData");

        if(StringUtils.isEmpty(begin) && StringUtils.isEmpty(end)){
            return modelAndView;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        JSONObject data = new JSONObject();
        Integer email = adminReportService.selectEmailNum(map);
        Integer sms = adminReportService.selectSmsNum(map);

        data.put("email", email);
        data.put("sms", sms);

        modelAndView.addObject("data", data);
        modelAndView.addObject("begin", begin);
        modelAndView.addObject("end", end);
        return modelAndView;
    }
}
