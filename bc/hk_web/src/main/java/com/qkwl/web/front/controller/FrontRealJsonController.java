package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.comm.AutoCache;
import com.qkwl.web.front.comm.AutoMarket;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.run.MarketApi;
import com.qkwl.web.utils.WebConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * 实时数据
 */

@Controller
public class FrontRealJsonController extends JsonBaseController {
    @Autowired
    private AutoMarket autoMarket;
    @Autowired
    private IUserWalletService userWalletService;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IEntrustServer entrustServer;
    @Autowired
    private AutoCache autoCache;
    @Autowired
    private MarketApi marketApi;

    /**
     * 降序-买单价格从高到低
     */
    private Comparator<FEntrustHistory> historyComparator = new Comparator<FEntrustHistory>() {
        public int compare(FEntrustHistory o1, FEntrustHistory o2) {
            return o2.getFcreatetime().compareTo(o1.getFcreatetime());
        }
    };

    /**
     * 对应实时成交数据
     * 
     * 深度+盘口+试试成交+最新行情
     *
     * @param symbol
     * @param buysellcount
     * @param successcount
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/real/market")
    @ResponseBody
    public ReturnResult MarketJson(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "0") Integer buysellcount,
            @RequestParam(required = false, defaultValue = "0") Integer successcount
    ) throws Exception {
        if (symbol == 0 || buysellcount < 0 || successcount < 0) {
            return ReturnResult.FAILUER("");
        }   
        //获取虚拟币
        SystemTradeType tradeType = redisHelper.getTradeType(symbol, WebConstant.BCAgentId);
        if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER("");
        }

        if (marketApi.huobiSymbolMap.containsKey(symbol)){
            JSONObject market = marketApi.getMarket(marketApi.huobiSymbolMap.get(symbol));
            JSONObject ticker = marketApi.getTicker(marketApi.huobiSymbolMap.get(symbol));
            market.put("tradeId",tradeType.getId());
            market.put("block",tradeType.getTradeBlock());
            market.put("p_new", ticker.get("p_new"));
            market.put("p_open", ticker.get("p_open"));
            market.put("low", ticker.get("low"));
            market.put("high", ticker.get("high"));
            market.put("chg", ticker.get("chg"));
            market.put("total", ticker.get("total"));
            market.put("depth", marketApi.getDepth(marketApi.huobiSymbolMap.get(symbol)).getJSONObject("depth"));
            market.remove("buys");
            market.remove("sells");
            market.remove("buy");
            market.remove("sell");
            return ReturnResult.SUCCESS(market);
        }       

        // 条数限制
        if (buysellcount > 100) {
            buysellcount = 100;
        }
        if (successcount > 100) {
            successcount = 100;
        }

        JSONObject jsonObject = new JSONObject();
        // 最新价格
        TickerData tickerData = autoMarket.getTickerData(tradeType.getId());
        if (tickerData == null) {
            jsonObject.put("p_new", 0);
            jsonObject.put("p_open", 0);
        } else {
            jsonObject.put("p_new", tickerData.getLast());
            jsonObject.put("p_open", tickerData.getKai());
        }
        jsonObject.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
        jsonObject.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getBuy());
        jsonObject.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());
        jsonObject.put("high",tickerData.getHigh());
        jsonObject.put("low",tickerData.getLow());
        jsonObject.put("chg",tickerData.getChg());

        // 深度            
        JSONObject depthJsonObj = new JSONObject();
        depthJsonObj.put("asks",autoMarket.getSellDepthJson(tradeType.getId()));
        depthJsonObj.put("bids",autoMarket.getBuyDepthJson(tradeType.getId()));
        depthJsonObj.put("date",System.currentTimeMillis());
        jsonObject.put("depth",depthJsonObj);
        // 最新成交
        jsonObject.put("trades", autoMarket.getSuccessJson(tradeType.getId(), successcount));
        // symbol
        jsonObject.put("symbol", tradeType.getBuySymbol());
        jsonObject.put("sellSymbol", tradeType.getSellShortName());
        jsonObject.put("buySymbol", tradeType.getBuyShortName());
        jsonObject.put("tradeId",tradeType.getId());
        jsonObject.put("block",tradeType.getTradeBlock());
        return ReturnResult.SUCCESS(jsonObject);
    }

    // 买卖盘，最新成交
    @RequestMapping(value = "/real/markets")
    @ResponseBody
    public ReturnResult MarketJsons(
            @RequestParam(required = false, defaultValue = "0") String symbol) throws Exception {
        if ("0".equals(symbol)) {
            return ReturnResult.FAILUER("");
        }   
        String[] symbolArray = symbol.split(",");
        JSONArray jsonArray = new JSONArray();
        for (String tradeId : symbolArray) {
            SystemTradeType tradeType = redisHelper.getTradeType(Integer.parseInt(tradeId), WebConstant.BCAgentId);
            if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                return ReturnResult.FAILUER("");
            }

            if (!marketApi.huobiSymbolMap.containsKey(Integer.parseInt(tradeId))){
                //获取虚拟币
                JSONObject jsonObject = new JSONObject();
                // 最新价格
                TickerData tickerData = autoMarket.getTickerData(tradeType.getId());
                if (tickerData == null) {
                    jsonObject.put("p_new", 0);
                    jsonObject.put("p_open", 0);
                } else {
                    jsonObject.put("p_new", tickerData.getLast());
                    jsonObject.put("p_open", tickerData.getKai());
                }
                jsonObject.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
                jsonObject.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getBuy());
                jsonObject.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());

                jsonObject.put("high",tickerData.getHigh());
                jsonObject.put("low",tickerData.getLow());
                jsonObject.put("chg",tickerData.getChg());

                // symbol
                jsonObject.put("symbol", tradeType.getBuySymbol());
                jsonObject.put("sellSymbol", tradeType.getSellShortName());
                jsonObject.put("buySymbol", tradeType.getBuyShortName());
                jsonObject.put("tradeId",tradeType.getId());
                jsonObject.put("block",tradeType.getTradeBlock());

                jsonArray.add(jsonObject);
            }else {
                JSONObject ticker = marketApi.getTicker(marketApi.huobiSymbolMap.get(Integer.parseInt(tradeId)));
                ticker.put("tradeId",tradeType.getId());
                ticker.put("block",tradeType.getTradeBlock());
                jsonArray.add(ticker);
            }
        }

        return ReturnResult.SUCCESS(jsonArray);
    }

    // 获取首页行情
    @RequestMapping(value = "/real/indexmarket")
    @ResponseBody
    public ReturnResult IndexMarketJson() {
        List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        if (tradeTypes == null) {
            return ReturnResult.FAILUER("");
        }

        JSONArray array = new JSONArray();
        for (SystemTradeType tradeType : tradeTypes) {
            JSONObject jsonitem = new JSONObject();
            TickerData tickerData = autoMarket.getTickerData(tradeType.getId());
            jsonitem.put("tradeId", tradeType.getId());
            jsonitem.put("price", tickerData.getLast() == null ? 0d : tickerData.getLast());
            jsonitem.put("total", tickerData.getVol() == null ? 0d : tickerData.getVol());
            jsonitem.put("rose", tickerData.getChg() == null ? 0d : tickerData.getChg());
            jsonitem.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getBuy());
            jsonitem.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());
            jsonitem.put("buysymbol", tradeType.getBuySymbol());
            jsonitem.put("sellsymbol", tradeType.getSellSymbol());
            String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
            String[] digits = digit.split("#");
            Integer cnyDigit = Integer.valueOf(digits[0]);
            Integer coinDigit = Integer.valueOf(digits[1]);
            jsonitem.put("cnyDigit", cnyDigit);
            jsonitem.put("coinDigit", coinDigit);
            jsonitem.put("treadId", tradeType.getId());
            jsonitem.put("sellname", getLanEnum().getCode().equals(LocaleEnum.EN_US.getCode()) ? tradeType.getSellShortName() : tradeType.getSellShortName() + " " + tradeType.getSellName());
            jsonitem.put("image", tradeType.getSellWebLogo());
            jsonitem.put("type", tradeType.getType());
            jsonitem.put("buyName", tradeType.getBuyName());
            jsonitem.put("sellName", tradeType.getSellName());
            jsonitem.put("buyShortName", tradeType.getBuyShortName().toLowerCase());
            jsonitem.put("sellShortName", tradeType.getSellShortName().toLowerCase());
            jsonitem.put("status", tradeType.getStatus());
            jsonitem.put("block",tradeType.getTradeBlock());
            array.add(jsonitem);
        }
        return ReturnResult.SUCCESS(array);
    }

    //获取用户资产
    @ResponseBody
    @RequestMapping(value = "/real/userassets")
    public ReturnResult UserAssets(
            @RequestParam(required = false, defaultValue = "0") Integer tradeid
    ) throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONObject buyCoin = new JSONObject();
        JSONObject sellCoin = new JSONObject();
        FUser fuser = super.getCurrentUserInfoByToken();
        if (fuser == null) {
            return ReturnResult.FAILUER("请登录！");
        }
        SystemTradeType tradeType = redisHelper.getTradeType(tradeid, WebConstant.BCAgentId);
        if (tradeType == null) {
            return ReturnResult.FAILUER("请登录！");
        }
        UserCoinWallet buyuserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getBuyCoinId());
        UserCoinWallet selluserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getSellCoinId());
        FUserScore userScore = this.userService.selectUserScoreById(fuser.getFid());
        if (buyuserWallet == null || selluserWallet == null || userScore == null) {
            return ReturnResult.FAILUER("请登录！");
        }
        jsonObject.put("score", userScore.getFscore());
        buyCoin.put("id", tradeType.getBuyCoinId());
        buyCoin.put("total", buyuserWallet.getTotal().toPlainString());
        buyCoin.put("frozen", buyuserWallet.getFrozen().toPlainString());
        buyCoin.put("borrow", buyuserWallet.getBorrow().toPlainString());
        jsonObject.put("buyCoin", buyCoin);
        sellCoin.put("id", tradeType.getSellCoinId());
        sellCoin.put("total", selluserWallet.getTotal().toPlainString());
        sellCoin.put("frozen", selluserWallet.getFrozen().toPlainString());
        sellCoin.put("borrow", selluserWallet.getBorrow().toPlainString());
        jsonObject.put("sellCoin", sellCoin);
        return ReturnResult.SUCCESS(jsonObject);
    }

    // 获取币种最新价格
    @ResponseBody
    @RequestMapping(value = "/real/lastprice")
    public String lastPrice(
            @RequestParam(required = false, defaultValue = "0") Integer symbol
    ) throws Exception {
        JSONObject object = new JSONObject();
        // 最新价格
        TickerData tickerData = autoMarket.getTickerData(symbol);
        if (tickerData == null) {
            object.put("price", "0");
        } else {
            object.put("price", tickerData.getLast());
        }
        return object.toString();

    }

    //k线交易页委单和资产
    @ResponseBody
    @RequestMapping(value = "/real/getEntruts")
    public ReturnResult EntrutsJson(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "7") Integer count
    ) throws Exception {
        JSONObject result = new JSONObject();
        FUser fuser = super.getCurrentUserInfoByToken();
        if (fuser == null) {
            return ReturnResult.FAILUER("请登录！");
        }
        SystemTradeType tradeType = redisHelper.getTradeType(symbol, WebConstant.BCAgentId);
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
            UserCoinWallet buyUserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getBuyCoinId());
            UserCoinWallet sellUserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getSellCoinId());
            result.put("totalCny", buyUserWallet.getTotal());
            result.put("totalCoin", sellUserWallet.getTotal());
            List<Integer> stateList = new ArrayList<>();
            // 未成交前7条
            FEntrust curEntrust = new FEntrust();
            curEntrust.setFuid(fuser.getFid());
            curEntrust.setFtradeid(symbol);
            curEntrust.setFagentid(fuser.getFagentid());
            stateList.add(EntrustStateEnum.Going.getCode());
            stateList.add(EntrustStateEnum.PartDeal.getCode());
            stateList.add(EntrustStateEnum.WAITCancel.getCode());
            Pagination<FEntrust> curParam = new Pagination<>(0, count);
            curParam = this.entrustServer.listEntrust(curParam, curEntrust, stateList);
            JSONArray entrutsCur = new JSONArray();
            for (FEntrust fEntrust : curParam.getData()) {
                JSONObject entruts = new JSONObject();
                entruts.put("id", fEntrust.getFid());
                entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
                entruts.put("types", super.GetR18nMsg("trade.enum.entrusttype" + fEntrust.getFtype()));
                entruts.put("source", fEntrust.getFsource_s());
                entruts.put("price", fEntrust.getFprize());
                entruts.put("count", fEntrust.getFcount());

                entruts.put("amount", fEntrust.getFamount());        //总价
                entruts.put("ncount", fEntrust.getFleftcount());  //未成交量

                entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
                entruts.put("last", fEntrust.getFlast());
                entruts.put("successamount", fEntrust.getFsuccessamount());
                entruts.put("fees", fEntrust.getFfees());
                entruts.put("status", super.GetR18nMsg("trade.enum.entruststate" + fEntrust.getFstatus()));
                entruts.put("type", fEntrust.getFtype());
                entruts.put("buysymbol", buyCoinType.getShortName());
                entruts.put("sellsymbol", sellCoinType.getShortName());
                entrutsCur.add(entruts);
            }
            result.put("entrutsCur", entrutsCur);
            // 成交前7条
            stateList.clear();
            stateList.add(EntrustStateEnum.AllDeal.getCode());
            stateList.add(EntrustStateEnum.Cancel.getCode());
            FEntrustHistory hisEntrust = new FEntrustHistory();
            hisEntrust.setFuid(fuser.getFid());
            hisEntrust.setFtradeid(symbol);
            hisEntrust.setFagentid(fuser.getFagentid());
            Pagination<FEntrustHistory> hisParam = new Pagination<>(0, 20);
            hisParam.setOrderField("fid");
            hisParam = this.entrustServer.listEntrustHistory(hisParam, hisEntrust, stateList);
            List<FEntrustHistory> data = (List<FEntrustHistory>)hisParam.getData();
            Collections.sort(data,historyComparator);

            JSONArray entrutsHis = new JSONArray();
            int loopCount = 0;
            for (FEntrustHistory fEntrust : data) {
                if (loopCount >= count) {
                    break;
                }       
                JSONObject entruts = new JSONObject();
                entruts.put("id", fEntrust.getFid());
                entruts.put("time", Utils.dateFormat(new Timestamp(fEntrust.getFcreatetime().getTime())));
                entruts.put("types", super.GetR18nMsg("trade.enum.entrusttype" + fEntrust.getFtype()));
                entruts.put("source", fEntrust.getFsource_s());
                entruts.put("price", fEntrust.getFprize());
                entruts.put("count", fEntrust.getFcount());

                entruts.put("amount", fEntrust.getFamount());        //总价
                entruts.put("ncount", fEntrust.getFleftcount());  //未成交量

                entruts.put("leftcount", MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount()));
                entruts.put("last", fEntrust.getFlast());
                entruts.put("successamount", fEntrust.getFsuccessamount());
                entruts.put("fees", fEntrust.getFfees());
                entruts.put("status", super.GetR18nMsg("trade.enum.entruststate" + fEntrust.getFstatus()));
                entruts.put("type", fEntrust.getFtype());
                entruts.put("buysymbol", buyCoinType.getShortName());
                entruts.put("sellsymbol", sellCoinType.getShortName());
                entrutsHis.add(entruts);
                loopCount ++;
            }
            result.put("entrutsHis", entrutsHis);
            return ReturnResult.SUCCESS("获取成功！", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("数据获取失败！");
        }
    }


    /**
     * 导航登陆后信息
     */
    @ResponseBody
    @RequestMapping(value = "/real/userWallet")
    public ReturnResult userWallet() {
        FUser fuser = getCurrentUserInfoByToken();
        JSONObject result = new JSONObject();
        if (fuser != null) {
            List<UserCoinWallet> userCoinWallets;
            try {
                userCoinWallets = userWalletService.listUserCoinWallet(fuser.getFid());
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

            fuser.setFloginpassword("");
            fuser.setFtradepassword("");
            fuser.setFgoogleurl("");
            fuser.setFgoogleauthenticator("");
            fuser.setFemail("");
            fuser.setIp("");
            fuser.setFidentityno("");
            fuser.setFidentitytype(null);

            result.put("score", fuser.getScore());
            result.put("netassets", getNetAssets(userCoinWallets).toPlainString());
            result.put("totalassets", getTotalAssets(userCoinWallets).toPlainString());

            result.put("wallet", walletformatJson(userCoinWallets));
            result.put("userinfo",fuser);
            return ReturnResult.SUCCESS(result);
        } else {
            return ReturnResult.FAILUER("请登录！");
        }
    }

    private JSONArray walletformatJson(List<UserCoinWallet> userCoinWallets) {
        if (userCoinWallets == null || userCoinWallets.size() == 0) {
            return new JSONArray();
        }
        JSONArray jsonArray = new JSONArray();
        for (UserCoinWallet coinWallet : userCoinWallets) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(coinWallet));
            jsonObject.put("total",coinWallet.getTotal().toPlainString());
            jsonObject.put("frozen",coinWallet.getFrozen().toPlainString());
            jsonObject.put("borrow",coinWallet.getTotal().toPlainString());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 获取用户uid,登录用户名,钱包余额信息
     */
    @ResponseBody
    @RequestMapping(value = "/real/userWallet_json")
    public ReturnResult userWallets() {
        FUser fuser = getCurrentUserInfoByToken();
        JSONObject result = new JSONObject();
        if (fuser != null) {
            List<UserCoinWallet> userCoinWallets;
            try {
                userCoinWallets = userWalletService.listUserCoinWallet(fuser.getFid());
                Iterator iterator = userCoinWallets.iterator();
                while (iterator.hasNext()) {
                    UserCoinWallet wallet = (UserCoinWallet) iterator.next();
                    if (!redisHelper.hasCoinId(wallet.getCoinId())) {
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, "登录已失效，请重新登录!");
                //return ReturnResult.FAILUER("请登录！");
            }

            result.put("userinfo", fuser);
            result.put("wallet", walletformatJson(userCoinWallets));
            return ReturnResult.SUCCESS(result);
        } else {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN, "登录已失效，请重新登录!");
        }
    }

    /**
     * 语言切换
     */
    @ResponseBody
    @RequestMapping(value = "/real/switchlan")
    public ReturnResult switchLan(
            @RequestParam String lan
    ) {
        FSystemLan systemLan = redisHelper.getLanguageType(lan);
        if (systemLan == null) {
            return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10001"));
        } else {
            HttpServletRequest request = sessionContextUtils.getContextRequest();
            HttpServletResponse response = sessionContextUtils.getContextResponse();
            if (LuangeHelper.setLan(request, response, lan)) {

                return ReturnResult.SUCCESS(super.GetR18nMsg("common.succeed.200"));
            }
        }
        return ReturnResult.FAILUER(super.GetR18nMsg("com.public.error.10000"));
    }
}


