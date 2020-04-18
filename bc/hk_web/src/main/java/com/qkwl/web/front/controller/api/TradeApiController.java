package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 订单相关接口，包括：下单、撤单、获取订单列表、当前订单、历史订单等。
 */
@Controller
public class TradeApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(TradeApiController.class);

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private IUserWalletService userWalletService;

    @Autowired
    private IUserService userService;

    /**
     * 交易区
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/v1/market/area")
    public ReturnResult tradeMarket() throws Exception {
        SystemTradeTypeEnum[] values = SystemTradeTypeEnum.values();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.length; i++) {
            SystemTradeTypeEnum trade =  values[i];
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",trade.getCode());
            jsonObject.put("name",trade.getSymbol());
            jsonArray.add(i,jsonObject);
        }
        return ReturnResult.SUCCESS(jsonArray);
    }

    /**
     * 获取交易区的交易对
     *
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/v1/market/list")
    public ReturnResult tradeMarket(
            @RequestParam(value = "symbol", required = false, defaultValue = "0") Integer code
    ) throws Exception {
        //所有交易对
        List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeSort(code, WebConstant.BCAgentId);
        return ReturnResult.SUCCESS(tradeTypeSort);
    }

    //获取用户资产
    @ResponseBody
    @RequestMapping(value = "/v1/market/userassets")
    public ReturnResult UserAssets(
            @RequestParam(required = false, defaultValue = "0") Integer tradeid
    ) throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONObject buyCoin = new JSONObject();
        JSONObject sellCoin = new JSONObject();
        FUser fuser = super.getCurrentUserInfoByApiToken();
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
        buyCoin.put("total", buyuserWallet.getTotal());
        buyCoin.put("frozen", buyuserWallet.getFrozen());
        buyCoin.put("borrow", buyuserWallet.getBorrow());
        jsonObject.put("buyCoin", buyCoin);
        sellCoin.put("id", tradeType.getSellCoinId());
        sellCoin.put("total", selluserWallet.getTotal());
        sellCoin.put("frozen", selluserWallet.getFrozen());
        sellCoin.put("borrow", selluserWallet.getBorrow());
        jsonObject.put("sellCoin", sellCoin);
        return ReturnResult.SUCCESS(jsonObject);
    }


}
