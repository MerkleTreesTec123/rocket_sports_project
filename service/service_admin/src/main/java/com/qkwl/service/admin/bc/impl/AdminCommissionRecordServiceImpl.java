package com.qkwl.service.admin.bc.impl;

import com.qkwl.common.dto.Enum.CommissionRecordStatusEnum;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminCommissionRecordService;
import com.qkwl.service.admin.bc.dao.CommissionRecordMapper;
import com.qkwl.service.admin.bc.dao.FUserFinancesMapper;
import com.qkwl.service.admin.bc.dao.UserCoinWalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("adminCommissionRecordService")
public class AdminCommissionRecordServiceImpl implements IAdminCommissionRecordService {

    @Autowired
    CommissionRecordMapper commissionRecordMapper;

    @Autowired
    UserCoinWalletMapper userCoinWalletMapper;

    /**
     * 获取所有的记录
     *
     * @param uid
     * @param introuid
     * @return
     */
    @Override
    public List<CommissionRecord> getAll(Integer uid, Integer introuid,Integer status) {
        CommissionRecord commissionRecord = new CommissionRecord();
        commissionRecord.setUid(uid);
        commissionRecord.setIntrouid(introuid);
        commissionRecord.setStatus(status);
        return  commissionRecordMapper.getAll(commissionRecord);
    }

    /**
     * 更新
     * @param
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean update(Integer id,Integer status) throws BCException{
        CommissionRecord commissionRecord = commissionRecordMapper.select(id);
        if (commissionRecord == null || commissionRecord.getStatus() == CommissionRecordStatusEnum.SUCCESS){
            throw new BCException("记录不存在，或则已经发放了");
        }

        if (MathUtils.compareTo(commissionRecord.getAmount(), BigDecimal.ZERO)<0){
            throw new BCException("非法值");
        }

        UserCoinWallet userCoinWallet = userCoinWalletMapper.selectByUidAndCoin(commissionRecord.getIntrouid(), commissionRecord.getCoinid());
        if (userCoinWallet == null){
            throw new BCException("没有找到钱包信息");
        }
        userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(),commissionRecord.getAmount()));
        commissionRecord.setStatus(CommissionRecordStatusEnum.SUCCESS);
        commissionRecord.setUpdatetime(new Date());
        userCoinWalletMapper.updateByPrimaryKey(userCoinWallet);
        return (commissionRecordMapper.update(commissionRecord) > 0);
    }

}
