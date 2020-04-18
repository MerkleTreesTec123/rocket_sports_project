package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.capital.CoinOperationOrderDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.web.front.controller.base.JsonBaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
public class ETCController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ETCController.class);

    @Autowired
    private IUserCapitalService userCapitalService;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * etc充值回调
     */
    @ResponseBody
    @RequestMapping("/coin/etc/recharge")
    public String etcRecharge(
            @RequestParam(required = false, defaultValue = "") String txhash,
            @RequestParam(required = false, defaultValue = "") String to,
            @RequestParam(required = false, defaultValue = "") String value,
            @RequestParam(required = false, defaultValue = "") String sign,
            @RequestParam(required = false, defaultValue = "") String symbol) {

        //判断参数
        if ("".equals(txhash) || "".equals(to) || "".equals(value) || "".equals(sign) || "".equals(symbol)) {
            return ReturnMsg(10001, "参数错误");
        }
        SystemCoinType coinType = redisHelper.getCoinTypeShortNameSystem(symbol.toUpperCase());
        if (coinType == null) {
            return ReturnMsg(10001, "服务器错误");
        }
        SystemCoinType baseCoinType = null;
        if (coinType.getShortName().equals("ETC")) {
            baseCoinType = coinType;
        } else if (coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())) {
            baseCoinType = redisHelper.getCoinTypeShortNameSystem("ETC");
        }
        if (baseCoinType == null) {
            return ReturnMsg(10001, "服务器错误");
        }
        //验证签名
        try {
            String signVerify = txhash;
            // get CoinDriver
            CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getIp(), coinType.getPort())
                    .builder()
                    .getDriver();
            if (coinDriver == null) {
                return ReturnMsg(10001, "服务器错误");
            }
            signVerify = coinDriver.getETCSHA3(signVerify);
            if (!signVerify.equals(sign)) {
                return ReturnMsg(10001, "签名错误");
            }
        } catch (Exception e) {
            logger.error("etcRecharge sign error，symbol：{}", symbol);
            e.printStackTrace();
            return ReturnMsg(10001, "签名错误");
        }
        BigDecimal amount = new BigDecimal(value);
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(amount) <= 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setAddress(to);
        order.setTxId(txhash);
        order.setBaseCoinId(baseCoinType.getId());
        order.setCoinId(coinType.getId());
        order.setAmount(amount);
        order.setRisk(risk);
        order.setCoinName(coinType.getShortName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_IN);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        order.setIp("");
        try {
            Result result = userCapitalService.createCoinOperationOrder(order);
            if (!result.getSuccess()) {
                return ReturnMsg(10001, "服务器错误1");
            }
        } catch (Exception e) {
            logger.error("etcRecharge 服务器错误，symbol：{}", symbol);
            e.printStackTrace();
            return ReturnMsg(10001, "服务器错误2");
        }

        return ReturnMsg(10000, "回调成功");
    }

    /**
     * 返回参数封装
     */
    private String ReturnMsg(int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", code);
        jsonObject.put("resultMsg", msg);
        return jsonObject.toString();
    }
}
