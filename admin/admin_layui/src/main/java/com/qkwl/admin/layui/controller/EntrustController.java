package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.admin.layui.base.WebBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.util.Constant;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.rpc.admin.IAdminEntrustServer;
import com.qkwl.common.framework.redis.RedisHelper;

@Controller
public class EntrustController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(EntrustController.class);

    @Autowired
    private IAdminEntrustServer adminEntrustServer;
    @Autowired
    private RedisHelper redisHelper;
    // 每页显示多少条数据
    private int numPerPage = Constant.adminPageSize;

    /**
     * 委单当前记录
     * @return
     * @throws Exception
     */
    @RequestMapping("entrust/entrustList")
    public ModelAndView entrustList(
            @RequestParam(value="pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value="keywords", required=false) String keyWord,
            @RequestParam(value="tradeId", defaultValue="0") Integer tradeId,
            @RequestParam(value="orderField", defaultValue="fcreatetime") String orderField,
            @RequestParam(value="orderDirection", defaultValue="desc") String orderDirection,
            @RequestParam(value="status", defaultValue="0") Integer fstatus,
            @RequestParam(value="entype", defaultValue="-1") Integer fentype,
            @RequestParam(value="price", required=false) BigDecimal fprice
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("entrust/entrustList");
        // 查询条件定义
        Pagination<FEntrust> pageParam = new Pagination<FEntrust>(currentPage, numPerPage);
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        FEntrust filterParam = new FEntrust();
        // 关键字
        if (!StringUtils.isEmpty(keyWord)) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 委单虚拟币
        if (tradeId > 0) {
            filterParam.setFtradeid(tradeId);
        }
        modelAndView.addObject("tradeId", tradeId);
        // 委单状态
        if (fstatus > 0) {
            filterParam.setFstatus(fstatus);
        }
        modelAndView.addObject("status", fstatus);
        // 委单类型
        if (fentype > -1) {
            filterParam.setFtype(fentype);
        }
        modelAndView.addObject("entype", fentype);
        // 订单价格
        if (fprice != null) {
            filterParam.setFprize(fprice);
            modelAndView.addObject("price", fprice);
        }

        // 页面参数-交易类型
        List<SystemTradeType> tradeType = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        Map<Integer, String> tradeMap = new HashMap<Integer, String>();
        for (SystemTradeType trade : tradeType) {
            tradeMap.put(trade.getId(), trade.getSellName());
        }
        tradeMap.put(0, "全部");
        modelAndView.addObject("tradeMap", tradeMap);

        // 页面参数-虚拟币类型
        Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
        typeMap.put(0, "全部");
        modelAndView.addObject("typeMap", typeMap);

        // 页面参数-委单状态
        Map<Integer, String> statusMap = new HashMap<Integer, String>();
        statusMap.put(EntrustStateEnum.Going.getCode(), EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.Going.getCode()));
        statusMap.put(EntrustStateEnum.PartDeal.getCode(), EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.PartDeal.getCode()));
        statusMap.put(EntrustStateEnum.WAITCancel.getCode(), EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.WAITCancel.getCode()));
        statusMap.put(0, "全部");
        modelAndView.addObject("statusMap", statusMap);
        // 页面参数-委单类型
        Map<Integer, String> entypeMap = new HashMap<Integer, String>();
        entypeMap.put(-1, "全部");
        entypeMap.put(EntrustTypeEnum.BUY.getCode(), EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.BUY.getCode()));
        entypeMap.put(EntrustTypeEnum.SELL.getCode(), EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.SELL.getCode()));
        modelAndView.addObject("entypeMap", entypeMap);
        // 查询
        Pagination<FEntrust> pagination = adminEntrustServer.selectFEntrustList(pageParam, filterParam);
        modelAndView.addObject("entrustList", pagination);
        return modelAndView;
    }

    /**
     * 委单历史记录
     * @return
     * @throws Exception
     */
    @RequestMapping("entrust/entrustHistoryList")
    public ModelAndView entrustHistoryList(
            @RequestParam(value="pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value="keywords", required=false) String keyWord,
            @RequestParam(value="tradeId", defaultValue="0") Integer tradeId,
            @RequestParam(value="orderField", defaultValue="fcreatetime") String orderField,
            @RequestParam(value="orderDirection", defaultValue="desc") String orderDirection,
            @RequestParam(value="status", defaultValue="0") Integer fstatus,
            @RequestParam(value="entype", defaultValue="-1") Integer fentype,
            @RequestParam(value="price", required=false) BigDecimal fprice) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("entrust/entrustHistoryList");
        // 查询条件定义
        Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, numPerPage);
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        FEntrustHistory filterParam = new FEntrustHistory();
        // 关键字
        if (!StringUtils.isEmpty(keyWord)) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 委单虚拟币
        if (tradeId > 0) {
            filterParam.setFtradeid(tradeId);
        }
        modelAndView.addObject("tradeId", tradeId);
        // 委单状态
        if (fstatus > 0) {
            filterParam.setFstatus(fstatus);
        }
        modelAndView.addObject("status", fstatus);
        // 委单类型
        if (fentype > -1) {
            filterParam.setFtype(fentype);
        }
        modelAndView.addObject("entype", fentype);
        // 订单价格
        if (fprice != null) {
            filterParam.setFprize(fprice);
            modelAndView.addObject("price", fprice);
        }

        // 页面参数-交易类型
        List<SystemTradeType> tradeType = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        Map<Integer, String> tradeMap = new HashMap<Integer, String>();
        for (SystemTradeType trade : tradeType) {
            tradeMap.put(trade.getId(), trade.getSellName());
        }
        tradeMap.put(0, "全部");
        modelAndView.addObject("tradeMap", tradeMap);

        // 页面参数-虚拟币类型
        Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
        typeMap.put(0, "全部");
        modelAndView.addObject("typeMap", typeMap);
        // 页面参数-委单状态
        Map<Integer, String> statusMap = new HashMap<Integer, String>();
        statusMap.put(EntrustStateEnum.AllDeal.getCode(), EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.AllDeal.getCode()));
        statusMap.put(EntrustStateEnum.Cancel.getCode(), EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.Cancel.getCode()));
        statusMap.put(0, "全部");
        modelAndView.addObject("statusMap", statusMap);
        // 页面参数-委单类型
        Map<Integer, String> entypeMap = new HashMap<Integer, String>();
        entypeMap.put(-1, "全部");
        entypeMap.put(EntrustTypeEnum.BUY.getCode(), EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.BUY.getCode()));
        entypeMap.put(EntrustTypeEnum.SELL.getCode(), EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.SELL.getCode()));
        modelAndView.addObject("entypeMap", entypeMap);

        if(StringUtils.isEmpty(keyWord) && tradeId == 0 && fprice == null && fentype == -1 && fstatus== 0){
            return modelAndView;
        }

        // 查询
        Pagination<FEntrustHistory> pagination = adminEntrustServer.selectFEntrustHistoryList(pageParam, filterParam);
        modelAndView.addObject("entrustList", pagination);
        return modelAndView;
    }

    /**
     * 撤单
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("entrust/cancelEntrust")
    @ResponseBody
    public ReturnResult cancelEntrust(
            @RequestParam(value = "uid", required = true) Integer fid) throws Exception {
        FEntrust fentrust = adminEntrustServer.selectFEntrust(fid);
        if (fentrust != null && (fentrust.getFstatus().equals(EntrustStateEnum.Going.getCode()) || fentrust.getFstatus().equals(EntrustStateEnum.PartDeal.getCode()))) {
            boolean flag = false;
            try {
                flag = adminEntrustServer.updateCancelEntrust(fentrust.getFuid(), fentrust.getFid());
            } catch (Exception e) {
                e.printStackTrace();
                return ReturnResult.FAILUER("委单撤销失败");
            }
            if (flag) {
                return ReturnResult.SUCCESS("委单撤销成功");
            } else {
                return ReturnResult.FAILUER("委单错误，委单数据不存在或委单是杠杆单或委单状态不是未成交、部分成交");
            }
        } else {
            return ReturnResult.FAILUER("委单错误，委单数据不存在或委单是杠杆单或委单状态不是未成交、部分成交");
        }
    }

    /**
     * 批量撤单
     */
    @RequestMapping("entrust/batchCancelEntrust")
    @ResponseBody
    public ReturnResult batchCancelEntrust(
            @RequestParam(value = "ids", required = true) String ids){
        String[] idString = ids.split(",");
        Integer errCount = 0;
        for (String id : idString) {
            FEntrust fentrust = adminEntrustServer.selectFEntrust(Integer.valueOf(id));
            if (fentrust != null && (fentrust.getFstatus().equals(EntrustStateEnum.Going.getCode())
                    || fentrust.getFstatus().equals(EntrustStateEnum.PartDeal.getCode()))) {
                try {
                    if (!adminEntrustServer.updateCancelEntrust(fentrust.getFuid(), fentrust.getFid()))
                        errCount++;
                } catch (Exception e) {
                    logger.error("批量撤销当前委单异常，委单id:"+id, e);
                    errCount++;
                }
            }else{
                errCount++;
            }
        }
        return ReturnResult.SUCCESS((errCount > 0 ? ("部分撤销成功，失败" + errCount + "条!") : "批量撤销成功!"));
    }

    /**
     * 导出委单当前记录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("entrust/EntrustExport")
    @ResponseBody
    public ReturnResult EntrustExport() throws Exception {
        HttpServletResponse response = sessionContextUtils.getContextResponse();
        ModelAndView modelAndView = new ModelAndView();
        response.setContentType("Application/excel");
        response.addHeader("Content-Disposition", "attachment;filename=EntrustList.xls");
        XlsExport e = new XlsExport();
        int rowIndex = 0;
        // header
        e.createRow(rowIndex++);
        for (ExportFiled filed : ExportFiled.values()) {
            e.setCell(filed.ordinal(), filed.toString());
        }
        Collection<FEntrust> fentrustList = GetFentrustList();
        for (FEntrust fentrust : fentrustList) {
            e.createRow(rowIndex++);
            for (ExportFiled filed : ExportFiled.values()) {
                switch (filed) {
                    case 序号:
                        e.setCell(filed.ordinal(), rowIndex - 1);
                        break;
                    case 登录名:
                        e.setCell(filed.ordinal(), fentrust.getFloginname());
                        break;
                    case 会员昵称:
                        e.setCell(filed.ordinal(), fentrust.getFnickname());
                        break;
                    case 会员真实姓名:
                        e.setCell(filed.ordinal(), fentrust.getFrealname());
                        break;
                    case 虚拟币类型:
                        e.setCell(filed.ordinal(), fentrust.getFcoinname());
                        break;
                    case 交易类型:
                        e.setCell(filed.ordinal(), fentrust.getFtype_s());
                        break;
                    case 状态:
                        e.setCell(filed.ordinal(), fentrust.getFstatus_s());
                        break;
                    case 单价:
                        e.setCell(filed.ordinal(), fentrust.getFprize().doubleValue());
                        break;
                    case 数量:
                        e.setCell(filed.ordinal(), fentrust.getFcount().doubleValue());
                        break;
                    case 未成交数量:
                        e.setCell(filed.ordinal(), fentrust.getFleftcount().doubleValue());
                        break;
                    case 已成交数量:
                        e.setCell(filed.ordinal(), MathUtils.sub(fentrust.getFcount(), fentrust.getFleftcount()).doubleValue());
                        break;
                    case 总金额:
                        e.setCell(filed.ordinal(), fentrust.getFamount().doubleValue());
                        break;
                    case 成交总金额:
                        e.setCell(filed.ordinal(), fentrust.getFsuccessamount().doubleValue());
                        break;
                    case 修改时间:
                        e.setCell(filed.ordinal(), fentrust.getFlastupdattime());
                        break;
                    case 创建时间:
                        e.setCell(filed.ordinal(), fentrust.getFcreatetime());
                        break;
                    default:
                        break;
                }
            }
        }
        e.exportXls(response);
        return ReturnResult.SUCCESS("导出成功！");
    }

    /**
     * 导出委单当前记录列表数据查询
     *
     * @return
     */
    private Collection<FEntrust> GetFentrustList() {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 当前页
        int currentPage = 1;
        // 搜索关键字
        String keyWord = request.getParameter("keywords");
        String orderField = request.getParameter("orderField");
        String orderDirection = request.getParameter("orderDirection");
        String fstatus = request.getParameter("status");
        String tradeId = request.getParameter("tradeId");
        String fentype = request.getParameter("entype");
        String fprice = request.getParameter("price");
        String logDate = request.getParameter("logDate");
        String endDate = request.getParameter("endDate");
        if (request.getParameter("pageNum") != null) {
            currentPage = Integer.parseInt(request.getParameter("pageNum"));
        }
        // 查询条件定义
        Pagination<FEntrust> pageParam = new Pagination<FEntrust>(currentPage, numPerPage);
        FEntrust filterParam = new FEntrust();
        // 关键字
        if (keyWord != null && keyWord.trim().length() > 0) {
            pageParam.setKeyword(keyWord);
        }
        // 委单虚拟币
        if (!StringUtils.isEmpty(tradeId)) {
            int type = Integer.parseInt(tradeId);
            if (type != 0) {
                filterParam.setFtradeid(type);
            }
        }
        // 委单状态
        if (fstatus != null && fstatus.trim().length() > 0) {
            int status = Integer.parseInt(fstatus);
            if (status != 0) {
                filterParam.setFstatus(status);
            }
        }
        // 委单类型
        if (fentype != null && fentype.trim().length() > 0) {
            int entype = Integer.parseInt(fentype);
            if (entype != -1) {
                filterParam.setFtype(entype);
            }
        }
        // 订单价格
        if (fprice != null && fprice.trim().length() > 0) {
            try {
                BigDecimal price = new BigDecimal(fprice);
                filterParam.setFprize(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 创建时间
        if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
            pageParam.setBegindate(logDate);
        }
        if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
            pageParam.setEnddate(endDate);
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
        // 查询
        Pagination<FEntrust> pagination = adminEntrustServer.selectFEntrustList(pageParam, filterParam);
        return pagination.getData();
    }

    /**
     * 导出委单历史记录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("entrust/EntrustHistoryExport")
    @ResponseBody
    public ReturnResult EntrustHistoryExport(
            @RequestParam(value="type",required=true) int type) throws Exception {
        HttpServletResponse response = sessionContextUtils.getContextResponse();
        response.setContentType("Application/excel");
        response.addHeader("Content-Disposition", "attachment;filename=EntrustHistoryList.xls");
        XlsExport e = new XlsExport();
        int rowIndex = 0;
        // header
        e.createRow(rowIndex++);
        for (ExportFiled filed : ExportFiled.values()) {
            e.setCell(filed.ordinal(), filed.toString());
        }
        Collection<FEntrustHistory> fentrustList = null;
        if(type == 0){
            fentrustList = GetFEntrustHistoryList();
        }else{
            fentrustList = GetFEntrustHistoryListNoPage();
        }
        for (FEntrustHistory fentrust : fentrustList) {
            e.createRow(rowIndex++);
            for (ExportFiled filed : ExportFiled.values()) {
                switch (filed) {
                    case 序号:
                        e.setCell(filed.ordinal(), rowIndex - 1);
                        break;
                    case 登录名:
                        e.setCell(filed.ordinal(), fentrust.getFloginname());
                        break;
                    case 会员昵称:
                        e.setCell(filed.ordinal(), fentrust.getFnickname());
                        break;
                    case 会员真实姓名:
                        e.setCell(filed.ordinal(), fentrust.getFrealname());
                        break;
                    case 虚拟币类型:
                        e.setCell(filed.ordinal(), fentrust.getFcoinname());
                        break;
                    case 交易类型:
                        e.setCell(filed.ordinal(), fentrust.getFtype_s());
                        break;
                    case 状态:
                        e.setCell(filed.ordinal(), fentrust.getFstatus_s());
                        break;
                    case 单价:
                        e.setCell(filed.ordinal(), fentrust.getFprize().doubleValue());
                        break;
                    case 数量:
                        e.setCell(filed.ordinal(), fentrust.getFcount().doubleValue());
                        break;
                    case 未成交数量:
                        e.setCell(filed.ordinal(), fentrust.getFleftcount().doubleValue());
                        break;
                    case 已成交数量:
                        e.setCell(filed.ordinal(), MathUtils.sub(fentrust.getFcount(), fentrust.getFleftcount()).doubleValue());
                        break;
                    case 总金额:
                        e.setCell(filed.ordinal(), fentrust.getFamount().doubleValue());
                        break;
                    case 成交总金额:
                        e.setCell(filed.ordinal(), fentrust.getFsuccessamount().doubleValue());
                        break;
                    case 修改时间:
                        e.setCell(filed.ordinal(), fentrust.getFlastupdattime());
                        break;
                    case 创建时间:
                        e.setCell(filed.ordinal(), fentrust.getFcreatetime());
                        break;
                    default:
                        break;
                }
            }
        }
        e.exportXls(response);
        return ReturnResult.SUCCESS("导出成功");
    }

    /**
     * 导出委单当前记录列表数据查询
     *
     * @return
     */
    private Collection<FEntrustHistory> GetFEntrustHistoryList() {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 当前页
        int currentPage = 1;
        // 搜索关键字
        String keyWord = request.getParameter("keywords");
        String orderField = request.getParameter("orderField");
        String orderDirection = request.getParameter("orderDirection");
        String fstatus = request.getParameter("status");
        String tradeId = request.getParameter("tradeId");
        String fentype = request.getParameter("entype");
        String fprice = request.getParameter("price");
        String logDate = request.getParameter("logDate");
        String endDate = request.getParameter("endDate");
        if (request.getParameter("pageNum") != null) {
            currentPage = Integer.parseInt(request.getParameter("pageNum"));
        }
        // 查询条件定义
        Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, 100000);
        FEntrustHistory filterParam = new FEntrustHistory();
        // 关键字
        if (keyWord != null && keyWord.trim().length() > 0) {
            pageParam.setKeyword(keyWord);
        }
        // 委单虚拟币
        if (!StringUtils.isEmpty(tradeId)) {
            int type = Integer.parseInt(tradeId);
            if (type != 0) {
                filterParam.setFtradeid(type);
            }
        }
        // 委单状态
        if (fstatus != null && fstatus.trim().length() > 0) {
            int status = Integer.parseInt(fstatus);
            if (status != 0) {
                filterParam.setFstatus(status);
            }
        }
        // 委单类型
        if (fentype != null && fentype.trim().length() > 0) {
            int entype = Integer.parseInt(fentype);
            if (entype != -1) {
                filterParam.setFtype(entype);
            }
        }
        // 订单价格
        if (fprice != null && fprice.trim().length() > 0) {
            try {
                BigDecimal price = new BigDecimal(fprice);
                filterParam.setFprize(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 起始时间
        if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
            pageParam.setBegindate(logDate);
        }
        // 起始时间
        if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
            pageParam.setEnddate(endDate);
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
        // 查询
        Pagination<FEntrustHistory> pagination = adminEntrustServer.selectFEntrustHistoryList(pageParam, filterParam);
        return pagination.getData();
    }


    /**
     * 导出委单当前记录列表数据查询
     *
     * @return
     */
    private Collection<FEntrustHistory> GetFEntrustHistoryListNoPage() {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 当前页
        int currentPage = 1;
        // 搜索关键字
        String keyWord = request.getParameter("keywords");
        String orderField = request.getParameter("orderField");
        String orderDirection = request.getParameter("orderDirection");
        String fstatus = request.getParameter("status");
        String tradeId = request.getParameter("tradeId");
        String fentype = request.getParameter("entype");
        String fprice = request.getParameter("price");
        String logDate = request.getParameter("logDate");
        String endDate = request.getParameter("endDate");
        // 查询条件定义
        Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, 100000);
        FEntrustHistory filterParam = new FEntrustHistory();
        // 关键字
        if (keyWord != null && keyWord.trim().length() > 0) {
            pageParam.setKeyword(keyWord);
        }
        // 委单虚拟币
        if (!StringUtils.isEmpty(tradeId)) {
            int type = Integer.parseInt(tradeId);
            if (type != 0) {
                filterParam.setFtradeid(type);
            }
        }
        // 委单状态
        if (fstatus != null && fstatus.trim().length() > 0) {
            int status = Integer.parseInt(fstatus);
            if (status != 0) {
                filterParam.setFstatus(status);
            }
        }
        // 委单类型
        if (fentype != null && fentype.trim().length() > 0) {
            int entype = Integer.parseInt(fentype);
            if (entype != -1) {
                filterParam.setFtype(entype);
            }
        }
        // 订单价格
        if (fprice != null && fprice.trim().length() > 0) {
            try {
                BigDecimal price = new BigDecimal(fprice);
                filterParam.setFprize(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 起始时间
        if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
            pageParam.setBegindate(logDate);
        }
        if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
            pageParam.setEnddate(endDate);
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
        // 查询
        List<FEntrustHistory> pagination = adminEntrustServer.selectFEntrustHistoryListNoPage(pageParam, filterParam);
        return pagination;
    }

    // 导出列名
    private static enum ExportFiled {
        序号, 登录名, 会员昵称, 会员真实姓名, 虚拟币类型, 交易类型, 状态, 单价, 数量, 未成交数量, 已成交数量, 总金额, 成交总金额, 修改时间, 创建时间;
    }
}
