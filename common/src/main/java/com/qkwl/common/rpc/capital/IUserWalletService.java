package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.dto.wallet.UserCoinWallet;

import java.util.List;

/**
 * 用户钱包接口
 */
public interface IUserWalletService {
    /**
     * 获取用户虚拟币钱包
     *
     * @param userId 用户ID
     * @param coinId 虚拟币ID
     * @return 虚拟币钱包实体对象
     */
    UserCoinWallet getUserCoinWallet(Integer userId, Integer coinId);

    /**
     * 获取用户虚拟币钱包列表
     *
     * @param userId 用户ID
     * @return 虚拟币钱包实体对象列表
     */
    List<UserCoinWallet> listUserCoinWallet(Integer userId);

    List<CurrencyRate> listCurrencyRate();

    CurrencyRate getCurrencyRate(String currency);
}
