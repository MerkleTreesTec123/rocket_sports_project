package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.EntrustOrderDTO;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;


import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.comm.AutoCache;
import com.qkwl.web.front.comm.AutoMarket;
import com.qkwl.web.front.controller.FrontTradeJsonController;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontTradeJsonController.class);

    @Autowired
    private IEntrustServer entrustServer;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private AutoMarket autoMarket;

    @Autowired
    private AutoCache autoCache;

    @Autowired
    private IUserWalletService userWalletService;

    /**
     * 下单
     *
     * @param AccessKeyId 访问key
     * @param symbol      交易对 btc_usdt
     * @param tradeAmount //数量
     * @param tradePrice  //价格
     * @param type        //类型 buy or sell
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/order/place")
    public ReturnResult orderPlace(
            @RequestParam(required = false, defaultValue = "") String AccessKeyId,
            @RequestParam(required = false, defaultValue = "") String symbol,
            @RequestParam(required = false, defaultValue = "") BigDecimal tradeAmount,
            @RequestParam(required = false, defaultValue = "") BigDecimal tradePrice,
            @RequestParam(required = false, defaultValue = "") String type
    ) throws Exception {
        if (TextUtils.isEmpty(AccessKeyId) || TextUtils.isEmpty(symbol)) {
            return ReturnResult.FAILUER("非法请求");
        }
        if (tradeAmount == null || tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER("请使用正确的数量");
        }
        if (tradePrice == null || tradePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER("请使用正确的价格");
        }
        if (!"buy".equals(type) && !"sell".equals(type)) {
            return ReturnResult.FAILUER("交易类型错误");
        }

        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != 2) {
            return ReturnResult.FAILUER("非法请求");
        }

        Integer tradeId = 0;
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0])
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1])) {
                tradeId = tradeType.getId();
            }
        }

        if (tradeId == 0) {
            return ReturnResult.FAILUER("非法请求");
        }

        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());

        FApiAuth apiAuthByKey = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + AccessKeyId);
        if (apiAuthByKey == null || !apiAuthByKey.isValid()) {
            return ReturnResult.FAILUER("用户不存在");
        }
        Integer fid = apiAuthByKey.getFuid();
        try {
            EntrustOrderDTO entrustDTO = new EntrustOrderDTO(MatchTypeEnum.LIMITE, fid, tradeId
                    , EntrustSourceEnum.API, tradePrice, tradeAmount, "", ip);
            Result result;
            if (type.equals("buy")) {
                result = entrustServer.createBuyEntrust(entrustDTO);
            } else {
                result = entrustServer.createSellEntrust(entrustDTO);
            }
            if (result.getSuccess()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", result.getData());
                return ReturnResult.SUCCESS(super.GetR18nMsg("trade.create.order." + result.getCode()), jsonObject);
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
        return ReturnResult.FAILUER("网络超时请重试");
    }

    /**
     * 订单取消
     */
    @ResponseBody
    @RequestMapping("/v1/order/cancel")
    public ReturnResult orderCancel(@RequestParam(required = false, defaultValue = "") String AccessKeyId,
                                    @RequestParam(required = false, defaultValue = "") BigInteger id) throws Exception {
        FApiAuth apiAuthByKey = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + AccessKeyId);
        if (apiAuthByKey == null || !apiAuthByKey.isValid()) {
            return ReturnResult.FAILUER("用户不存在");
        }
        Integer fid = apiAuthByKey.getFuid();
        try {
            Result result = entrustServer.cancelEntrust(fid, id);
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
     * 委单详情
     */
    @ResponseBody
    @RequestMapping("/v1/order/detail")
    public ReturnResult orderDetail(
            @RequestParam(required = false, defaultValue = "") String AccessKeyId,
            @RequestParam(required = false, defaultValue = "") BigInteger id) throws Exception {
        FApiAuth apiAuthByKey = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + AccessKeyId);
        if (apiAuthByKey == null || !apiAuthByKey.isValid()) {
            return ReturnResult.FAILUER("用户不存在");
        }
        Integer fid = apiAuthByKey.getFuid();
        JSONObject jsonObject = new JSONObject();
        FEntrustHistory fentrust = entrustServer.getEntrustHistory(fid, id);
        if (fentrust == null) {
            jsonObject.put("result", false);
            return ReturnResult.SUCCESS(jsonObject);
        }
        List<FEntrustLog> fentrustlogs = entrustServer.getEntrustLog(fentrust.getFentrustid());
        if (fentrustlogs == null || fentrustlogs.size() == 0) {
            jsonObject.put("result", false);
            return ReturnResult.SUCCESS(fentrustlogs);
        }
        jsonObject.put("result", true);
        jsonObject.put("title", GetR18nMsg("com.trade.error.10016", fentrust.getFid()));
        SystemTradeType tradeType = redisHelper.getTradeType(fentrustlogs.get(0).getFtradeid(), WebConstant.BCAgentId);
        if (tradeType != null) {
            String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
            String[] digits = digit.split("#");
            Integer cnyDigit = Integer.valueOf(digits[0]);
            Integer coinDigit = Integer.valueOf(digits[1]);
            SystemCoinType buyCoinType = autoCache.getSystemCoinType(tradeType.getBuyCoinId());
            SystemCoinType sellCoinType = autoCache.getSystemCoinType(tradeType.getSellCoinId());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < fentrustlogs.size(); i++) {
                FEntrustLog fEntrustLog = fentrustlogs.get(i);
                JSONObject object = new JSONObject();
                object.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fEntrustLog.getFcreatetime()));
                object.put("price", Utils.number4String(fentrust.getFprize(), cnyDigit));
                object.put("dealPrice", Utils.number4String(fEntrustLog.getFprize(), cnyDigit));
                object.put("count", Utils.number4String(fEntrustLog.getFcount(), coinDigit));
                object.put("amount", Utils.number4String(fEntrustLog.getFamount(), cnyDigit));
                object.put("type", fentrust.getFtype() == EntrustTypeEnum.BUY.getCode() ? "buy" : "sell");
                jsonArray.add(i, object);
            }
            jsonObject.put("content", jsonArray);
        }
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * @param AccessKeyId
     * @param symbol
     * @param type        0表示全部 1表示当前 2表示历史
     * @param count
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/order/entrust")
    public ReturnResult orderEntrust(
            @RequestParam(required = false, defaultValue = "") String AccessKeyId,
            @RequestParam(required = false, defaultValue = "0") String symbol,
            @RequestParam(required = false, defaultValue = "0") Integer type,
            @RequestParam(required = false, defaultValue = "7") Integer count
    ) throws Exception {
        FApiAuth apiAuthByKey = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + AccessKeyId);
        if (apiAuthByKey == null || !apiAuthByKey.isValid()) {
            return ReturnResult.FAILUER("用户不存在");
        }

        Integer fid = apiAuthByKey.getFuid();
        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != 2) {
            return ReturnResult.FAILUER("非法请求");
        }

        Integer tradeId = 0;
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0])
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1])) {
                tradeId = tradeType.getId();
            }
        }
        SystemTradeType tradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
        if (tradeType == null) {
            return ReturnResult.FAILUER("交易错误！");
        }
        SystemCoinType buyCoinType = autoCache.getSystemCoinType(tradeType.getBuyCoinId());
        SystemCoinType sellCoinType = autoCache.getSystemCoinType(tradeType.getSellCoinId());
        if (buyCoinType == null || sellCoinType == null) {
            return ReturnResult.FAILUER("币种错误！");
        }
        try {
            // 获取用户资产
            JSONObject result = new JSONObject();
            result.put("entrutsCur", getCurEntrust(fid, tradeId, count, buyCoinType, sellCoinType));
//            if (type == 0) {
//
//                // 成交前7条
//                result.put("entrutsHis", getHistoryEntrust(fid, tradeId, count, buyCoinType, sellCoinType));
//            } else if (type == 1) {
//                result.put("entrutsCur", getCurEntrust(fid, tradeId, count, buyCoinType, sellCoinType));
//            } else if (type == 2) {
//                result.put("entrutsHis", getHistoryEntrust(fid, tradeId, count, buyCoinType, sellCoinType));
//            } else {
//
//            }
            return ReturnResult.SUCCESS("获取成功！", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("数据获取失败！");
        }
    }

    public JSONArray getCurEntrust(Integer fuid,
                                   Integer tradeId,
                                   Integer count,
                                   SystemCoinType buyCoinType,
                                   SystemCoinType sellCoinType) {

        FEntrust curEntrust = new FEntrust();
        curEntrust.setFuid(fuid);
        curEntrust.setFtradeid(tradeId);
        curEntrust.setFagentid(0);

        List<Integer> stateList = new ArrayList<>();

        stateList.add(EntrustStateEnum.Going.getCode());
        stateList.add(EntrustStateEnum.PartDeal.getCode());
        stateList.add(EntrustStateEnum.WAITCancel.getCode());

        Pagination<FEntrust> curParam = new Pagination<>(0, count);
        curParam = this.entrustServer.listEntrust(curParam, curEntrust, stateList);
        JSONArray entrustCur = new JSONArray();
        for (FEntrust fEntrust : curParam.getData()) {
            JSONObject entruts = new JSONObject();
            entruts.put("id", fEntrust.getFid());
            entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
            entruts.put("types", super.GetR18nMsg("trade.enum.entrusttype" + fEntrust.getFtype()));
            entruts.put("source", fEntrust.getFsource_s());
            entruts.put("price", fEntrust.getFprize());
            entruts.put("count", fEntrust.getFcount());
            entruts.put("leftcount", fEntrust.getFleftcount());
            entruts.put("last", fEntrust.getFlast());
            entruts.put("successamount", fEntrust.getFsuccessamount());
            entruts.put("fees", fEntrust.getFfees());
            entruts.put("status", super.GetR18nMsg("trade.enum.entruststate" + fEntrust.getFstatus()));
            entruts.put("type", fEntrust.getFtype());
            entruts.put("buysymbol", buyCoinType.getSymbol());
            entruts.put("sellsymbol", sellCoinType.getSymbol());
            entrustCur.add(entruts);
        }
        //result.put("entrutsCur", entrutsCur);
        return entrustCur;
    }

    public JSONArray getHistoryEntrust(Integer fuid,
                                       Integer tradeId,
                                       Integer count,
                                       SystemCoinType buyCoinType,
                                       SystemCoinType sellCoinType) {
        List<Integer> stateList = new ArrayList<>();
        stateList.add(EntrustStateEnum.AllDeal.getCode());
        stateList.add(EntrustStateEnum.Cancel.getCode());
        FEntrustHistory hisEntrust = new FEntrustHistory();
        hisEntrust.setFuid(fuid);
        hisEntrust.setFtradeid(tradeId);
        hisEntrust.setFagentid(0);

        Pagination<FEntrustHistory> hisParam = new Pagination<>(0, count);
        hisParam = this.entrustServer.listEntrustHistory(hisParam, hisEntrust, stateList);
        JSONArray entrutsHis = new JSONArray();
        for (FEntrustHistory fEntrust : hisParam.getData()) {
            JSONObject entruts = new JSONObject();
            entruts.put("id", fEntrust.getFid());
            entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
            entruts.put("types", super.GetR18nMsg("trade.enum.entrusttype" + fEntrust.getFtype()));
            entruts.put("source", fEntrust.getFsource_s());
            entruts.put("price", fEntrust.getFprize());
            entruts.put("count", fEntrust.getFcount());
            entruts.put("leftcount", fEntrust.getFleftcount());
            entruts.put("last", fEntrust.getFlast());
            entruts.put("successamount", fEntrust.getFsuccessamount());
            entruts.put("fees", fEntrust.getFfees());
            entruts.put("status", super.GetR18nMsg("trade.enum.entruststate" + fEntrust.getFstatus()));
            entruts.put("type", fEntrust.getFtype());
            entruts.put("buysymbol", buyCoinType.getSymbol());
            entruts.put("sellsymbol", sellCoinType.getSymbol());
            entrutsHis.add(entruts);
        }
        return entrutsHis;
    }

    /**
     * @param step   周期 单位是秒
     * @param symbol 交易对 btc_gset
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/ticker")
    public ReturnResult ticker(@RequestParam(required = false, defaultValue = "") int step,
                               @RequestParam(required = false, defaultValue = "") String symbol) throws Exception {
        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != 2) {
            return ReturnResult.FAILUER("非法请求");
        }

        Integer tradeId = 0;
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0])
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1])) {
                tradeId = tradeType.getId();
            }
        }
        JSONArray result = autoMarket.getKlineJson(tradeId, step / 60);
        return ReturnResult.SUCCESS(result);
    }

    /**
     * @param step   周期 单位是秒
     * @param symbol 交易对 btc_gset
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/depth")
    public ReturnResult depth(@RequestParam(required = false, defaultValue = "0") int step,
                              @RequestParam(required = false, defaultValue = "0") String symbol) throws Exception {
        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != 2) {
            return ReturnResult.FAILUER("非法请求");
        }

        Integer tradeId = 0;
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0])
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1])) {
                tradeId = tradeType.getId();
            }
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject returnObject = new JSONObject();
        if (tradeId == 0) {
            return ReturnResult.FAILUER("币种ID错误！");
        }
        jsonObject.put("bids", autoMarket.getBuyDepthJson(tradeId));
        jsonObject.put("asks", autoMarket.getSellDepthJson(tradeId));
        jsonObject.put("date", Utils.getTimestamp().getTime() / 1000);
        jsonObject.put("lastPrice", redisHelper.getLastPrice(tradeId));
        returnObject.put("depth", jsonObject);
        if (step > 0) {
            JSONObject periodobject = new JSONObject();
            periodobject.put("marketFrom", symbol);
            periodobject.put("coinVol", symbol);
            periodobject.put("type", step);
            periodobject.put("data", autoMarket.getLastKlineJson(tradeId, step / 60));
            returnObject.put("period", periodobject);
        }
        return ReturnResult.SUCCESS(returnObject);
    }

    @RequestMapping(value = "/v1/trade")
    @ResponseBody
    public ReturnResult trade(
            @RequestParam(required = false, defaultValue = "0") String symbol,
            @RequestParam(required = false, defaultValue = "0") Integer count
    ) throws Exception {
        if (symbol.equals("0") || count < 0) {
            return ReturnResult.FAILUER("");
        }

        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != 2) {
            return ReturnResult.FAILUER("非法请求");
        }

        Integer tradeId = 0;
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0])
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1])) {
                tradeId = tradeType.getId();
            }
        }

        // 条数限制
        if (count > 100) {
            count = 100;
        }
        //获取虚拟币
        SystemTradeType tradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
        if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER("");
        }

        JSONObject jsonObject = new JSONObject();
        // 最新成交
        jsonObject.put("trades", autoMarket.getSuccessJson(tradeType.getId(), count));
        // symbol
        jsonObject.put("sellSymbol", tradeType.getSellShortName());
        jsonObject.put("buySymbol", tradeType.getBuyShortName());
        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @RequestMapping("/v1/currency")
    public ReturnResult currencyList () {
        String currencyRateJson = redisHelper.get("currencyRates");
        List<CurrencyRate> resultList = new ArrayList<>();
        if (TextUtils.isEmpty(currencyRateJson)) {
            List<CurrencyRate> currencyRates = userWalletService.listCurrencyRate();
            if (currencyRates != null && currencyRates.size() != 0) {
                redisHelper.setNoExpire("currencyRates", JSON.toJSONString(currencyRates));
                resultList = currencyRates;
            }
        }else {
            resultList = JSONArray.parseArray(currencyRateJson,CurrencyRate.class);
        }

        return ReturnResult.SUCCESS(resultList);
    }

    /**
     * 获取用户余额
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/balance")
    public ReturnResult balance(@RequestParam(required = false, defaultValue = "") String AccessKeyId,@RequestParam(required = false, defaultValue = "") String base) {
        FApiAuth apiAuthByKey = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + AccessKeyId);
        if (apiAuthByKey == null || !apiAuthByKey.isValid()) {
            return ReturnResult.FAILUER("用户不存在");
        }

        Integer fid = apiAuthByKey.getFuid();
        JSONObject result = new JSONObject();
        List<UserCoinWallet> userCoinWallets;
        try {
            userCoinWallets = userWalletService.listUserCoinWallet(fid);
            Iterator iterator = userCoinWallets.iterator();
            while (iterator.hasNext()) {
                UserCoinWallet wallet = (UserCoinWallet) iterator.next();
                if (!redisHelper.hasCoinId(wallet.getCoinId())) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("请登录！");
        }

        CurrencyRate currencyRateObj;
        String currencyJson = redisHelper.get("currency_" + base);
        if (TextUtils.isEmpty(currencyJson)) {
            currencyRateObj = userWalletService.getCurrencyRate(base);
            if (currencyRateObj != null) {
                redisHelper.setNoExpire("currency_" + base,JSON.toJSONString(currencyRateObj));
            }
        } else {
            currencyRateObj = JSON.parseObject(currencyJson,CurrencyRate.class);
        }

        if (currencyRateObj == null) {
            return ReturnResult.FAILUER("system not support this currency rate");
        }

        result.put("netassets", MathUtils.mul(getNetAssets(userCoinWallets),currencyRateObj.getRate()));
        result.put("totalassets", getTotalAssets(userCoinWallets));
        result.put("wallet", userCoinWallets);
        return ReturnResult.SUCCESS(result);
    }

}
