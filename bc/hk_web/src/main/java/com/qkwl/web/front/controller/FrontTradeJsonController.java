package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.coin.*;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.EntrustOrderDTO;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserPriceclock;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.comm.AutoCache;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FrontTradeJsonController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontTradeJsonController.class);

    @Autowired
    private IEntrustServer entrustServer;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private AutoCache autoCache;
    @Autowired
    private IUserService userService;

    /**
     * 买单
     */
    @ResponseBody
    @RequestMapping(value = "/trade/cny_buy")
    public ReturnResult tradeCoinBuy(
            @RequestParam(required = false, defaultValue = "0") Integer limited, // 是否按照市场价买入
            @RequestParam(value = "symbol") int tradeId, // 币种
            @RequestParam BigDecimal tradeAmount, // 数量
            @RequestParam BigDecimal tradePrice, // 单价
            @RequestParam(required = false, defaultValue = "0") String tradePwd) {
        if (tradeId == 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10001"));
        }
        if (tradeAmount == null || tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("trade.com.1000"));
        }
        if (tradePrice == null || tradePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("trade.com.1001"));
        }
        FUser user = super.getCurrentUserInfoByToken();
        if (user == null) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10001"));
        }

        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception ex) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
        }
        try {
            long start = System.currentTimeMillis();
            EntrustOrderDTO entrustDTO = new EntrustOrderDTO(MatchTypeEnum.LIMITE, user.getFid(), tradeId
                    , EntrustSourceEnum.WEB, tradePrice, tradeAmount, tradePwd, ip);
            Result result = entrustServer.createBuyEntrust(entrustDTO);
            System.out.println("买单消耗："+(System.currentTimeMillis() - start)+" 毫秒");
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(super.GetR18nMsg("trade.create.order." + result.getCode()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("tradeCoinBuy is param error, {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(super.GetR18nMsg("trade.create.order." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), super.GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            logger.error("tradeCoinBuy err ", e);
        }
        return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
    }

    /**
     * 卖单
     */
    @ResponseBody
    @RequestMapping(value = "/trade/cny_sell")
    public ReturnResult tradeCoinSell(
            @RequestParam(required = false, defaultValue = "0") Integer limited, // 是否按照市场价买入
            @RequestParam(value = "symbol") int tradeId, // 币种
            @RequestParam BigDecimal tradeAmount, // 数量
            @RequestParam BigDecimal tradePrice, // 单价
            @RequestParam(required = false, defaultValue = "0") String tradePwd) throws Exception {
        if (tradeId == 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10001"));
        }
        if (tradeAmount == null || tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("trade.com.1000"));
        }
        if (tradePrice == null || tradePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(super.GetR18nMsg("trade.com.1001"));
        }
        FUser user = super.getCurrentUserInfoByToken();
        if (user == null) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10001"));
        }
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception ex) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
        }
        try {
            long start = System.currentTimeMillis();
            EntrustOrderDTO entrustDTO = new EntrustOrderDTO(MatchTypeEnum.LIMITE, user.getFid(), tradeId
                    , EntrustSourceEnum.WEB, tradePrice, tradeAmount, tradePwd, ip);
            Result result = entrustServer.createSellEntrust(entrustDTO);
            System.out.println("卖单消耗："+(System.currentTimeMillis() - start)+" 毫秒");
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(super.GetR18nMsg("trade.create.order." + result.getCode()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("tradeCoinBuy is param error, {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(super.GetR18nMsg("trade.create.order." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), super.GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            logger.error("tradeCoinSell err ", e);
        }
        return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
    }

    /**
     * 撤单
     */
    @ResponseBody
    @RequestMapping(value = "/trade/cny_cancel")
    public ReturnResult cancelEntrust(@RequestParam(required = true) BigInteger id) {
        FUser fuser = super.getCurrentUserInfoByToken();
        try {
            long start = System.currentTimeMillis();
            Result result = entrustServer.cancelEntrust(fuser.getFid(), id);
            System.out.println("撤销订单消耗："+(System.currentTimeMillis() - start)+" 毫秒");
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(super.GetR18nMsg("trade.cancel.order." + result.getCode()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("tradeCoinBuy is param error, {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(super.GetR18nMsg("trade.cancel.order." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(super.GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            logger.error("cancelEntrust is error ", e);
        }
        return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
    }

    /**
     * 委单交易明细
     */
    @ResponseBody
    @RequestMapping(value = "/trade/cny_entrustLog")
    public String entrustLog(
            @RequestParam(required = true) BigInteger id
    ) throws Exception {
        FUser fUser = super.getCurrentUserInfoByToken();
        JSONObject jsonObject = new JSONObject();
        FEntrustHistory fentrust = entrustServer.getEntrustHistory(fUser.getFid(), id);
        if (fentrust == null) {
            jsonObject.put("result", false);
            return jsonObject.toString();
        }
        List<FEntrustLog> fentrustlogs = entrustServer.getEntrustLog(fentrust.getFentrustid());
        if (fentrustlogs == null || fentrustlogs.size() == 0) {
            jsonObject.put("result", false);
            return jsonObject.toString();
        }
        jsonObject.put("result", true);
        jsonObject.put("title", GetR18nMsg("com.trade.error.10016", fentrust.getFid()));
        StringBuffer content = new StringBuffer();
        content.append("<table class='table text-center'> <tbody> " + "<tr class='text-title'> " + "<th>"
                + GetR18nMsg("market.market.dealtime") + "</th> " + "<th>"
                + GetR18nMsg("trade.entrustype") + "</th> " + "<th>"
                + GetR18nMsg("trade.entrusprice") + "</th> " + "<th>"
                + GetR18nMsg("trade.enterprice") + "</th> " + "<th>"
                + GetR18nMsg("trade.enternum") + "</th> " + "<th>"
                + GetR18nMsg("trade.enteramount") + "</th> " + "</tr>");
        if (fentrustlogs.size() == 0) {
            content.append("<tr><td align='center' colspan='6'>"
                    + GetR18nMsg("com.trade.error.10016", fentrust.getFid()) + "</td></tr>");
        }
        SystemTradeType tradeType = redisHelper.getTradeType(fentrustlogs.get(0).getFtradeid(), WebConstant.BCAgentId);
        if (tradeType != null) {
            String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
            String[] digits = digit.split("#");
            Integer cnyDigit = Integer.valueOf(digits[0]);
            Integer coinDigit = Integer.valueOf(digits[1]);
            SystemCoinType buyCoinType = autoCache.getSystemCoinType(tradeType.getBuyCoinId());
            SystemCoinType sellCoinType = autoCache.getSystemCoinType(tradeType.getSellCoinId());
            for (FEntrustLog fentrustlog : fentrustlogs) {
                content.append("<tr style='text-align: center;'> " + "<td>"
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fentrustlog.getFcreatetime()) + "</td> "
                        + "<td class='"
                        + (fentrust.getFtype() == EntrustTypeEnum.BUY.getCode() ? "text-success" : "text-danger") + "'>"
                        + GetR18nMsg("trade.enum.entrusttype" + fentrust.getFtype()) + "</td>"
                        + "<td>" + buyCoinType.getSymbol() + Utils.number4String(fentrust.getFprize(), cnyDigit) + "</td>" + "<td>"
                        + buyCoinType.getSymbol() + Utils.number4String(fentrustlog.getFprize(), cnyDigit) + "</td>" + "<td>"
                        + sellCoinType.getSymbol() + Utils.number4String(fentrustlog.getFcount(), coinDigit) + "</td>" + "<td>"
                        + buyCoinType.getSymbol() + Utils.number4String(fentrustlog.getFamount(), cnyDigit) + "</td>" + "</tr>");
            }
        }
        content.append("</tbody> </table>");
        jsonObject.put("content", content.toString());
        return jsonObject.toString();
    }

    /**
     * 交易页面
     */
    @ResponseBody
    @RequestMapping("/trade/cny_coin_json")
    public ReturnResult coin(
            @RequestParam(value = "tradeId", required = false, defaultValue = "0") Integer tradeid,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer tradeType,
            @RequestParam(value = "limit", required = false, defaultValue = "0") Integer limit) throws Exception {
        limit = limit < 0 ? 0 : limit;
        limit = limit > 1 ? 1 : limit;
        // 交易判断
        JSONObject jsonObject = new JSONObject();
        SystemTradeType systemTradeType = redisHelper.getTradeType(tradeid, WebConstant.BCAgentId);
        if (systemTradeType == null || systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())
                || !systemTradeType.getIsShare() || !systemTradeType.getType().equals(tradeType)) {
            systemTradeType = redisHelper.getTradeTypeFirst(tradeType, WebConstant.BCAgentId);
            return ReturnResult.FAILUER("");
        }
        // 小数位处理(默认价格2位，数量4位)
        String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
        String[] digits = digit.split("#");
        jsonObject.put("cnyDigit", Integer.valueOf(digits[0]));
        jsonObject.put("coinDigit", Integer.valueOf(digits[1]));

        // 用户登录
        FUser fuser = getCurrentUserInfoByToken();
        if (fuser != null) {
            // 是否需要输入交易密码
            fuser = userService.selectUserById(fuser.getFid());
            // 价格闹钟
            FUserPriceclock clock = new FUserPriceclock();
            clock.setFuid(fuser.getFid());
            clock.setFtradeid(tradeid);
            FUserPriceclock priceclock = userService.selectPriceClockByClock(clock);

            jsonObject.put("needTradePasswd", redisHelper.getNeedTradePassword(fuser.getFid()));
            jsonObject.put("priceclock", priceclock);
            jsonObject.put("tradePassword", fuser.getFtradepassword() == null);
            jsonObject.put("login", true);
        } else {
            jsonObject.put("login", false);
        }
        // 交易分类
        Map<Integer, Object> typeMap = new LinkedHashMap<>();
        for (SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()) {
            typeMap.put(typeEnum.getCode(), typeEnum.getValue());
        }
        jsonObject.put("typeMap", typeMap);
        jsonObject.put("type", tradeType);

        // 当前种类币种
        Map<Integer, SystemCoinTypeVO> coinMap = new LinkedHashMap<>();
        List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeSort(tradeType, WebConstant.BCAgentId);
        for (SystemTradeType SystemTradeType : tradeTypeSort) {
            coinMap.put(SystemTradeType.getId(), ModelMapperUtils
                    .mapper(autoCache.getSystemCoinType(SystemTradeType.getSellCoinId()), SystemCoinTypeVO.class));
        }
        SystemCoinInfo coinInfo = redisHelper.getCoinInfo(systemTradeType.getSellCoinId(), getLanEnum().getCode()+"");
        jsonObject.put("coinInfo", coinInfo);
        jsonObject.put("coinMap", coinMap);
        jsonObject.put("systemTradeType", ModelMapperUtils.mapper(redisHelper.getTradeTypeList(WebConstant.BCAgentId), SystemTradeTypeVO.class));
        jsonObject.put("tradeid", tradeid);
        jsonObject.put("limit", limit);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 委单记录
     */
    @ResponseBody
    @RequestMapping("/trade/cny_entrust_json")
    public ReturnResult entrust(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "0") Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage
    ) throws Exception {
        // 用户
        FUser fuser = getCurrentUserInfoByToken();
        // 虚拟币
        SystemTradeType tradeType = redisHelper.getTradeType(symbol, WebConstant.BCAgentId);
        if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !tradeType.getIsShare()) {
            tradeType = redisHelper.getTradeTypeFirst(SystemTradeTypeEnum.USDT.getCode(), WebConstant.BCAgentId);
            return ReturnResult.FAILUER("");
        }
        // 小数位处理(默认价格2位，数量4位)
        String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
        String[] digits = digit.split("#");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cnyDigit", Integer.valueOf(digits[0]));
        jsonObject.put("coinDigit", Integer.valueOf(digits[1]));

        List<Integer> StateEnums = new ArrayList<>();
        if (status == 0) {
            // 正在委托
            FEntrust entrust = new FEntrust();
            entrust.setFuid(fuser.getFid());
            entrust.setFtradeid(symbol);
            entrust.setFagentid(fuser.getFagentid());
            StateEnums.add(EntrustStateEnum.Going.getCode());
            StateEnums.add(EntrustStateEnum.PartDeal.getCode());
            Pagination<FEntrust> paginParam = new Pagination<FEntrust>(currentPage, 20, "/index.php?s=exc&c=tradeController&m=entrust&symbol=" + symbol + "&status=" + status + "&");
            try {
                paginParam = entrustServer.listEntrust(paginParam, entrust, StateEnums);
                jsonObject.put("pagin", paginParam.getPagin());
                jsonObject.put("fentrusts", paginParam.getData());
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        } else {
            // 委托完成
            FEntrustHistory entrust = new FEntrustHistory();
            entrust.setFuid(fuser.getFid());
            entrust.setFtradeid(symbol);
            entrust.setFagentid(fuser.getFagentid());
            StateEnums.add(EntrustStateEnum.AllDeal.getCode());
            StateEnums.add(EntrustStateEnum.Cancel.getCode());
            StateEnums.add(EntrustStateEnum.WAITCancel.getCode());
            Pagination<FEntrustHistory> paginParam = new Pagination<FEntrustHistory>(currentPage, 20, "/index.php?s=exc&c=tradeController&m=entrust&symbol=" + symbol + "&status=" + status + "&");
            try {
                paginParam.setOrderField("fcreatetime");
                paginParam = entrustServer.listEntrustHistory(paginParam, entrust, StateEnums);
                jsonObject.put("pagin", paginParam.getPagin());
                jsonObject.put("fentrusts", paginParam.getData());
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("status", ModelMapperUtils.mapper(tradeType, SystemTradeTypeVO.class));
        jsonObject.put("tradeType", tradeType.getType());
        jsonObject.put("tradeTypeList", ModelMapperUtils.mapper(redisHelper.getTradeTypeList(WebConstant.BCAgentId), SystemTradeTypeVO.class));
        jsonObject.put("symbol", symbol);
        return ReturnResult.SUCCESS(jsonObject);
    }
}
