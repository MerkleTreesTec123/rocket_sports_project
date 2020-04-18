package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.coin.SystemTradeType;
import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.daylog.*;
import com.qkwl.common.rpc.admin.IAdminDayLogService;
import com.qkwl.common.rpc.admin.IAdminUserFinances;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.framework.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 报表管理
 *
 * @author ZKF
 */
@Controller
public class ReportController extends WebBaseController {

    @Autowired
    private IAdminDayLogService adminDayLogService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IAdminUserService adminUserService;
    @Autowired
    private IAdminUserFinances adminUserFinances;

    /**
     * 虚拟币日流水
     */
    @RequestMapping("/report/dayCapitalCoinList")
    public ModelAndView dayCapitalCoinList(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "keywords", required = false) String keyWord,
            @RequestParam(value = "ftype", defaultValue = "0") Integer ftype,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "logDate", required = false) String logDate) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/dayCapitalCoinList");
        // 查询条件定义
        Pagination<FDayCapitalCoin> pageParam = new Pagination<FDayCapitalCoin>(currentPage, Constant.adminPageSize);
        FDayCapitalCoin filterParam = new FDayCapitalCoin();
        // 关键字
        if (keyWord != null && keyWord.trim().length() > 0) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 虚拟币
        if (ftype > 0) {
            filterParam.setFcoinid(ftype);
            modelAndView.addObject("ftype", ftype);
        }
        // 创建时间
        if (!StringUtils.isEmpty(logDate)) {
            filterParam.setFcreatetime(DateUtils.parse(logDate, DateUtils.YYYY_MM_DD));
            modelAndView.addObject("logDate", logDate);
        }
        // 排序条件
        if (orderField != null && orderField.trim().length() > 0) {
            pageParam.setOrderField(orderField);
        } else {
            pageParam.setOrderField("fcreatetime");
        }
        if (orderDirection != null && orderDirection.trim().length() > 0) {
            pageParam.setOrderDirection(orderDirection);
        } else {
            pageParam.setOrderDirection("desc");
        }
        // 页面参数
        Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
        typeMap.put(0, "全部");
        modelAndView.addObject("typeMap", typeMap);
        // 查询
        Pagination<FDayCapitalCoin> pagination = adminDayLogService.selectDayCapitalCoinList(pageParam, filterParam);
        modelAndView.addObject("page", pagination);
        return modelAndView;
    }

    /**
     * 人民币日流水
     */
    @RequestMapping("/report/dayCapitalRmbList")
    public ModelAndView dayCapitalRmbList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "logDate", required = false) String logDate,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/dayCapitalRmbList");

        Pagination<FDayCapitalRmb> page = new Pagination<FDayCapitalRmb>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }
        FDayCapitalRmb rmb = new FDayCapitalRmb();
        if (!StringUtils.isEmpty(logDate)) {
            rmb.setFcreatetime(DateUtils.parse(logDate, DateUtils.YYYY_MM_DD));
            modelAndView.addObject("logDate", logDate);
        }
        page = adminDayLogService.selectDayCapitalRmbList(page, rmb);

        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 运营日统计
     */
    @RequestMapping("/report/dayOperatList")
    public ModelAndView dayOperatList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "logDate", required = false) String logDate,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/dayOperatList");

        Pagination<FDayOperat> page = new Pagination<FDayOperat>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }
        FDayOperat operat = new FDayOperat();
        if (!StringUtils.isEmpty(logDate)) {
            try {
                operat.setFcreatetime(new SimpleDateFormat("yyyy-MM-dd").parse(logDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            modelAndView.addObject("logDate", logDate);
        }
        page = adminDayLogService.selectDayOperatList(page, operat);

        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 交易日统计
     */
    @RequestMapping("/report/dayTradeCoinList")
    public ModelAndView dayTradeCoinList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "logDate", required = false) String logDate,
            @RequestParam(value = "ftype", required = false) Integer ftype,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/dayTradeCoinList");

        Pagination<FDayTradeCoin> page = new Pagination<FDayTradeCoin>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }
        FDayTradeCoin coin = new FDayTradeCoin();
        if (ftype != null && ftype != 0) {
            coin.setFcoinid(ftype);
            modelAndView.addObject("ftype", ftype);
        }

        if (!StringUtils.isEmpty(logDate)) {
            try {
                modelAndView.addObject("logDate", logDate);
                coin.setFcreatetime(new SimpleDateFormat("yyyy-MM-dd").parse(logDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        page = adminDayLogService.selectDayTradeCoinList(page, coin);

        // 页面参数
        List<SystemTradeType> list = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        Map<Integer, String> typeMap = new LinkedHashMap<>();
        typeMap.put(0, "全部");
        for(SystemTradeType trade:list){
            SystemCoinType coinType = redisHelper.getCoinType(trade.getSellCoinId());
            typeMap.put(trade.getId(), coinType.getName());
        }
        modelAndView.addObject("typeMap", typeMap);


        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 交易日统计-手动统计
     */
    @ResponseBody
    @RequestMapping("/report/updateDayTrade")
    public ReturnResult updateDayTrade() {
        try {
            adminDayLogService.updateDayTrade();
            return ReturnResult.SUCCESS("统计成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("统计失败");
        }
    }

    /**
     * 资产存量统计
     */
    @RequestMapping("/report/daySumList")
    public ModelAndView daySumList(
            @RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
            @RequestParam(value = "logDate", required = false) String logDate,
            @RequestParam(value = "ftype", required = false) Integer ftype,
            @RequestParam(value = "keywords", required = false) String keywords) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/daySumList");

        Pagination<FDaySum> page = new Pagination<FDaySum>(currentPage, Constant.adminPageSize);
        //排序字段
        if (!StringUtils.isEmpty(orderField)) {
            page.setOrderField(orderField);
        }
        //正序倒序
        if (!StringUtils.isEmpty(orderDirection)) {
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if (!StringUtils.isEmpty(keywords)) {
            page.setKeyword(keywords);
        }
        FDaySum sum = new FDaySum();
        if (ftype != null && ftype > 0) {
            sum.setFcoinid(ftype);
            modelAndView.addObject("ftype", ftype);
        }

        if (!StringUtils.isEmpty(logDate)) {
            try {
                sum.setFcreatetime(new SimpleDateFormat("yyyy-MM-dd").parse(logDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            modelAndView.addObject("logDate", logDate);
        }
        page = adminDayLogService.selectDaySumList(page, sum);

        // 页面参数
        Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
        typeMap.put(-1, "全部");
        modelAndView.addObject("typeMap", typeMap);


        modelAndView.addObject("keywords", keywords);
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    @RequestMapping("/report/userfinancestotle")
    public ModelAndView userFinancesTotle() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/userfinancestotle");

        // 页面参数
        Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
        coinMap.put(0, "全部");
        modelAndView.addObject("coinMap", coinMap);

        List<Map<String,Object>> page = adminUserFinances.selectUserFinancesTotal();

        modelAndView.addObject("page", page);
        return modelAndView;
    }

    /**
     * 资产存量统计-手动统计
     */
    @ResponseBody
    @RequestMapping("/report/updateDaySum")
    public ReturnResult updateDaySum() {
        try {
            adminDayLogService.updateDaySum();
            return ReturnResult.SUCCESS("统计成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("统计失败");
        }
    }

    /**
     * 统计总表
     */
    @RequestMapping("/report/dayCharts")
    public ModelAndView dayCharts() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/dayCharts");
        return modelAndView;
    }


    @RequestMapping("report/chartsrefresh")
    @ResponseBody
    public ReturnResult chartsrefresh(){
        FChartsData charts = new FChartsData();

        int regnum = adminUserService.selectRegisterByDate();

        List<SystemCoinType> fVirtualCoinTypes = redisHelper.getCoinTypeListSystem();
        for (SystemCoinType fVirtualCoinType : fVirtualCoinTypes) {
            Map<String, Object> map = adminDayLogService.selectCoinSum(fVirtualCoinType.getId());
            FCoinCharts coin = new FCoinCharts();
            if(map != null){
                coin.setFree(new BigDecimal(map.get("total").toString()));
                coin.setFrozen(new BigDecimal(map.get("frozen").toString()));
                coin.setTotal(MathUtils.add(coin.getFree(), coin.getFrozen()));
            }else{
                coin.setFree(BigDecimal.ZERO);
                coin.setFrozen(BigDecimal.ZERO);
                coin.setTotal(BigDecimal.ZERO);
            }
            if(fVirtualCoinType.getShortName().equals("BTC")){
                charts.setBtc(coin);
            }else if(fVirtualCoinType.getShortName().equals("LTC")){
                charts.setLtc(coin);
            }else if(fVirtualCoinType.getShortName().equals("ETC")){
                charts.setEtc(coin);
            }
        }

        charts.setRegnum(regnum);

        return ReturnResult.SUCCESS(charts);
    }


}
