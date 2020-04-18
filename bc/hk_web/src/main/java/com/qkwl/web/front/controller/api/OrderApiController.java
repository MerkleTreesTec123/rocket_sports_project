package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.EntrustSourceEnum;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.MatchTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.EntrustOrderDTO;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.comm.AutoCache;
import com.qkwl.web.front.comm.AutoMarket;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 订单相关接口，包括：下单、撤单、获取订单列表、当前订单、历史订单等。
 */
@Controller
public class OrderApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(OrderApiController.class);

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
     * @param symbol      交易对 btc_usdt
     * @param tradeAmount //数量
     * @param tradePrice  //价格
     * @param type        //类型 buy or sell
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/entrust/place")
    public ReturnResult orderPlace(
            @RequestParam(required = true, defaultValue = "") String symbol,
            @RequestParam(required = true, defaultValue = "") BigDecimal tradeAmount,
            @RequestParam(required = true, defaultValue = "") BigDecimal tradePrice,
            @RequestParam(required = true, defaultValue = "") String type,
            @RequestParam(required = true, defaultValue = "") String tradePwd
    ) throws Exception {
        if (tradeAmount == null || tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER("请使用正确的数量");
        }
        if (tradePrice == null || tradePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER("请使用正确的价格");
        }
        if (!"buy".equals(type) && !"sell".equals(type)) {
            return ReturnResult.FAILUER("交易类型错误");
        }

        if (TextUtils.isEmpty(tradePwd)){
            return ReturnResult.FAILUER("请输入交易密码");
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
        Integer fid = getCurrentUserInfoByApiToken().getFid();

        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception ex) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
        }

        try {
            EntrustOrderDTO entrustDTO = new EntrustOrderDTO(MatchTypeEnum.LIMITE, fid, tradeId
                    , EntrustSourceEnum.APP, tradePrice, tradeAmount, tradePwd, ip);
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
    @RequestMapping("/v1/entrust/cancel")
    public ReturnResult orderCancel(@RequestParam(required = false, defaultValue = "") BigInteger id) throws Exception {
        Integer fid = getCurrentUserInfoByApiToken().getFid();
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
     * @param symbol
     * @param type   0表示全部 1表示当前 2表示历史
     * @param page  分页
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/entrust/list")
    public ReturnResult orderEntrust(
            @RequestParam(required = false, defaultValue = "0") String symbol,
            @RequestParam(required = false, defaultValue = "0") Integer type,
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) throws Exception {
        Integer fid = getCurrentUserInfoByApiToken().getFid();
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
            if (type == 0) {
                result.put("entrutsCur", getCurEntrust(fid, tradeId, page, buyCoinType, sellCoinType));
                // 成交前7条
                result.put("entrutsHis", getHistoryEntrust(fid, tradeId, page, buyCoinType, sellCoinType));
            } else if (type == 1) {
                result.put("entrutsCur", getCurEntrust(fid, tradeId, page, buyCoinType, sellCoinType));
            } else if (type == 2) {
                result.put("entrutsHis", getHistoryEntrust(fid, tradeId, page, buyCoinType, sellCoinType));
            } else {

            }
            return ReturnResult.SUCCESS("获取成功！", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("数据获取失败！");
        }
    }

    public JSONArray getCurEntrust(Integer fuid,
                                   Integer tradeId,
                                   Integer page,
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

        Pagination<FEntrust> curParam = new Pagination<>(page, 20);
        curParam = this.entrustServer.listEntrust(curParam, curEntrust, stateList);
        JSONArray entrustCur = new JSONArray();
        for (FEntrust fEntrust : curParam.getData()) {
            JSONObject entruts = new JSONObject();
            entruts.put("id", fEntrust.getFid());
            entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
            entruts.put("types", fEntrust.getFtype());
            entruts.put("source", fEntrust.getFsource_s());
            entruts.put("price", fEntrust.getFprize());
            entruts.put("count", fEntrust.getFcount());
            entruts.put("leftcount", fEntrust.getFleftcount());
            entruts.put("last", fEntrust.getFlast());
            entruts.put("successamount", fEntrust.getFsuccessamount());
            entruts.put("fees", fEntrust.getFfees());
            entruts.put("status", fEntrust.getFstatus());
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
                                       Integer page,
                                       SystemCoinType buyCoinType,
                                       SystemCoinType sellCoinType) {
        List<Integer> stateList = new ArrayList<>();
        stateList.add(EntrustStateEnum.AllDeal.getCode());
        stateList.add(EntrustStateEnum.Cancel.getCode());
        FEntrustHistory hisEntrust = new FEntrustHistory();
        hisEntrust.setFuid(fuid);
        hisEntrust.setFtradeid(tradeId);
        hisEntrust.setFagentid(0);

        Pagination<FEntrustHistory> hisParam = new Pagination<>(page, 20);
        hisParam.setOrderField("fcreatetime");
        hisParam = this.entrustServer.listEntrustHistory(hisParam, hisEntrust, stateList);
        JSONArray entrutsHis = new JSONArray();
        for (FEntrustHistory fEntrust : hisParam.getData()) {
            JSONObject entruts = new JSONObject();
            entruts.put("id", fEntrust.getFid());
            entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
            entruts.put("types", fEntrust.getFtype());
            entruts.put("source", fEntrust.getFsource_s());
            entruts.put("price", fEntrust.getFprize());
            entruts.put("count", fEntrust.getFcount());
            entruts.put("leftcount", fEntrust.getFleftcount());
            entruts.put("last", fEntrust.getFlast());
            entruts.put("successamount", fEntrust.getFsuccessamount());
            entruts.put("fees", fEntrust.getFfees());
            entruts.put("status",fEntrust.getFstatus());
            entruts.put("type", fEntrust.getFtype());
            entruts.put("buysymbol", buyCoinType.getSymbol());
            entruts.put("sellsymbol", sellCoinType.getSymbol());
            entrutsHis.add(entruts);
        }
        return entrutsHis;
    }


}
