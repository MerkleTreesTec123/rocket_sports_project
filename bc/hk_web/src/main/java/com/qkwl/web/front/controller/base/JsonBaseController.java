package com.qkwl.web.front.controller.base;

import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.web.utils.WebConstant;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JsonBaseController extends RedisBaseControll {

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 获取多语言
     *
     * @param key 键值
     * @return
     */
    public String GetR18nMsg(String key) {
        return LuangeHelper.GetR18nMsg(sessionContextUtils.getContextRequest(), key);
    }

    /**
     * 获取多语言
     *
     * @param key  键值
     * @param args 参数
     * @return
     */
    public String GetR18nMsg(String key, Object... args) {
        return LuangeHelper.GetR18nMsg(sessionContextUtils.getContextRequest(), key, args);
    }

    /**
     * 获取语言枚举
     *
     * @return
     */
    public LocaleEnum getLanEnum() {
        String localeStr = LuangeHelper.getLan(sessionContextUtils.getContextRequest());
        for (LocaleEnum locale : LocaleEnum.values()) {
            if (locale.getName().equals(localeStr)) {
                return locale;
            }
        }
        return null;
    }

    public String getIpAddr() {
        return Utils.getIpAddr(sessionContextUtils.getContextRequest());
    }

    public String getLan() {
        return LuangeHelper.getLan(sessionContextUtils.getContextRequest());
    }

    /**
     * 净资产
     */
    public BigDecimal getNetAssets(List<UserCoinWallet> coinWallets) {
        Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeIdWithUsdt(WebConstant.BCAgentId);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets, price;
        Integer tradeId;
        for (UserCoinWallet coinWallet : coinWallets) {
            // 人民币 或者是 usdt
            if (coinWallet.getCoinId().equals(SystemTradeTypeEnum.USDT.getCoinId())) {
                assets = MathUtils.add(coinWallet.getTotal(), MathUtils.add(coinWallet.getFrozen(),coinWallet.getLockTotal()));
                totalAssets = MathUtils.add(totalAssets, assets);
                continue;
            }
            // 当前币种没有在 USDT 交易区，尝试获取其他交易区对应价格转换 USDT
            tradeId = trades.get(coinWallet.getCoinId());
            if (tradeId == null) {
                SystemTradeTypeEnum[] values = SystemTradeTypeEnum.values();
                for (SystemTradeTypeEnum value : values) {
                    if (!SystemTradeTypeEnum.USDT.getSymbol().equals(value.getSymbol())) {
                        Map<Integer, Integer> coinIdToTradeIdWithTradeType = redisHelper.getCoinIdToTradeIdWithTradeType(value.getCode(), WebConstant.BCAgentId);
                        Integer otherTradeId = coinIdToTradeIdWithTradeType.get(coinWallet.getCoinId());
                        if (otherTradeId == null) {
                            continue;
                        }
                        BigDecimal lastPrice = redisHelper.getLastPrice(otherTradeId);
                        BigDecimal add = MathUtils.add(coinWallet.getTotal(), MathUtils.add(coinWallet.getFrozen(),coinWallet.getLockTotal()));
                        //当前交易区的总额
                        BigDecimal mul = MathUtils.mul(add, lastPrice);
                        //交易id
                        Integer integer = trades.get(value.getCoinId());
                        BigDecimal lastPrice1 = redisHelper.getLastPrice(integer);
                        totalAssets = MathUtils.add(totalAssets,MathUtils.mul(lastPrice1,mul));
                    }
                }
                continue;
            }
            price = redisHelper.getLastPrice(tradeId);
            assets = MathUtils.add(coinWallet.getTotal(), MathUtils.add(coinWallet.getFrozen(),coinWallet.getLockTotal()));
            assets = MathUtils.mul(assets, price);
            totalAssets = MathUtils.add(totalAssets, assets);
        }
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_DIV_SCALE);
    }

    /**
     * 总资产
     */
    public BigDecimal getTotalAssets(List<UserCoinWallet> coinWallets) {
        Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeIdWithUsdt(WebConstant.BCAgentId);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets, price;
        Integer tradeId;
        for (UserCoinWallet coinWallet : coinWallets) {
            // 人民币
            if (coinWallet.getCoinId().equals(33)) {
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                assets = MathUtils.add(assets, coinWallet.getBorrow());
                totalAssets = MathUtils.add(totalAssets, assets);
                continue;
            }
            // 虚拟币
            tradeId = trades.get(coinWallet.getCoinId());
            if (tradeId == null) {
                continue;
            }
            price = redisHelper.getLastPrice(tradeId);
            assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
            assets = MathUtils.add(assets, coinWallet.getBorrow());
            assets = MathUtils.mul(assets, price);
            totalAssets = MathUtils.add(totalAssets, assets);
        }
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }

    protected boolean isOpenAuth() {
        String isOpenAuth = redisHelper.getSystemArgs("isOpenAuth");
        if (org.springframework.util.StringUtils.isEmpty(isOpenAuth) || !"1".equals(isOpenAuth)) {
            return false;
        }
        return true;
    }


}
