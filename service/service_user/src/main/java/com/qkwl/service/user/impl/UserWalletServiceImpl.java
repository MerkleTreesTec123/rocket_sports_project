package com.qkwl.service.user.impl;

import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.service.common.mapper.CurrencyRateMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户钱包接口
 */
@Service("userWalletService")
public class UserWalletServiceImpl implements IUserWalletService {
    @Autowired
    private UserCoinWalletMapper coinWalletMapper;

    @Autowired
    private CurrencyRateMapper currencyRateMapper;

    @Override
    public UserCoinWallet getUserCoinWallet(Integer userId, Integer coinId) {
        return coinWalletMapper.selectByUidAndCoin(userId, coinId);
    }

    @Override
    public List<UserCoinWallet> listUserCoinWallet(Integer userId) {
        // 这里 USDT 为 3 UFO 为 1
        List<UserCoinWallet>  userCoinWallets = coinWalletMapper.selectByUid(userId);
        UserCoinWallet ufoTotal =  coinWalletMapper.selectIeoBuyLog(userId, Integer.valueOf(21));
        UserCoinWallet ufoInviteTotal =  coinWalletMapper.selectIeoInviteReward(userId, Integer.valueOf(21));
        UserCoinWallet usdtInviteTotal = coinWalletMapper.selectIeoInviteReward(userId, Integer.valueOf(3));
        for (UserCoinWallet wallet : userCoinWallets) {
                if (wallet.getCoinId().intValue() == 21 && ufoTotal != null) {
                    wallet.setFrozen(wallet.getFrozen().add(ufoTotal.getTotal()));
                }

                if (wallet.getCoinId().intValue() == 21 && ufoInviteTotal != null) {
                    wallet.setFrozen(wallet.getFrozen().add(ufoInviteTotal.getTotal()));
                }

                if (wallet.getCoinId().intValue() == 3 && usdtInviteTotal != null) {
                    wallet.setFrozen(wallet.getFrozen().add(usdtInviteTotal.getTotal()));
                }
                    
        }

        return userCoinWallets;
     }
         

    @Override
    public List<CurrencyRate> listCurrencyRate() {
        return currencyRateMapper.getAllCurrencyRate();
    }

    @Override
    public CurrencyRate getCurrencyRate(String currency) {
        return currencyRateMapper.get(currency);
    }
}
