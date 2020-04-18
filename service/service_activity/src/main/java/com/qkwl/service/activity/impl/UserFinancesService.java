package com.qkwl.service.activity.impl;

import java.util.Date;

import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.activity.dao.FUserFinancesMapper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userFinancesService")
public class UserFinancesService {
//	@Autowired
//	private UserCoinWalletMapper userCoinWalletMapper;
//
	@Autowired
	FUserFinancesMapper userFinancesMapper;

	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public boolean updateUserFinances(FUserFinancesDTO fUserFinances) throws Exception{
//		UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectLock(fUserFinances.getFuid(), fUserFinances.getFcoinid());
//		if(userVirtualWallet == null){
//			return false;
//		}
//		userVirtualWallet.setBorrow(MathUtils.sub(userVirtualWallet.getBorrow(), fUserFinances.getFamount()));
//		userVirtualWallet.setTotal(MathUtils.add(MathUtils.add(userVirtualWallet.getTotal(), fUserFinances.getFamount()),fUserFinances.getFplanamount()));
//		userVirtualWallet.setGmtModified(new Date());
//		if(userCoinWalletMapper.update(userVirtualWallet)<=0){
//			return false;
//		}
//		fUserFinances.setFupdatetime(new Date());
//		fUserFinances.setFstate(UserFinancesStateEnum.SEND.getCode());
//		if(userFinancesMapper.updateByPrimaryKey(fUserFinances)<=0){
//			throw new Exception();
//		}
		return true;
	}
}
