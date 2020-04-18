package com.qkwl.service.user.tx;

import com.qkwl.common.dto.Enum.FinanceRecordOperationEnum;
import com.qkwl.common.dto.Enum.FinanceRecordStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationOutStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.user.base.UserWalletBase;
import com.qkwl.service.user.dao.FPoolMapper;
import com.qkwl.service.user.dao.FUserVirtualAddressMapper;
import com.qkwl.service.user.dao.FVirtualCapitalOperationMapper;
import com.qkwl.service.user.dao.FinanceRecordMapper;
import com.qkwl.service.user.model.FPoolDO;
import com.qkwl.service.user.model.FUserVirtualAddressDO;
import com.qkwl.service.user.model.FVirtualCapitalOperationDO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 币种相关操作
 */
@Service("coinCapitalServiceTx")
public class CoinCapitalServiceTx extends UserWalletBase {

    private static final Logger logger = LoggerFactory.getLogger(CoinCapitalServiceTx.class);

    @Autowired
    private FVirtualCapitalOperationMapper virtualCapitalOperationMapper;
    @Autowired
    private FUserVirtualAddressMapper userVirtualAddressMapper;
    @Autowired
    private FPoolMapper poolMapper;
    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    
    /**
     * 创建充值订单
     */
    public Boolean createRechargeOrder(FVirtualCapitalOperationDO operation) {
        return virtualCapitalOperationMapper.insert(operation) > 0;
    }

    /**
     * 创建提现订单
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean createWithdrawOrder(FVirtualCapitalOperationDO operation) throws Exception {
        //判断是否内部地址
        FUserVirtualAddressDO fUserVirtualAddressDO = userVirtualAddressMapper.selectByAddress(operation.getFwithdrawaddress().toLowerCase());
        if (fUserVirtualAddressDO == null) {
            logger.error("地址为空");
            throw new BCException("资产变更失败");
        }

        if (fUserVirtualAddressDO.getFuid().equals(operation.getFuid())) {
            logger.error("自己给自己转账，直接返回错误");
            throw new BCException("资产变更失败");
        }


        if (!fUserVirtualAddressDO.getFuid().equals(operation.getFuid())){
            // 1.添加资产到对方钱包
            Integer targetUid = fUserVirtualAddressDO.getFuid();
            if (super.updateUserCoinWallet(targetUid, operation.getFcoinid(), operation.getFamount(), BigDecimal.ZERO) == false) {
                logger.error("内部转账-资产变更失败 {} 转给 {}，包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
                throw new BCException("资产变更失败");
            }

            // 2.添加订单记录
			FVirtualCapitalOperationDO tovirtualcaptualoperation = new FVirtualCapitalOperationDO();
			tovirtualcaptualoperation.setFuid(targetUid);
			tovirtualcaptualoperation.setFcoinid(operation.getFcoinid());
			tovirtualcaptualoperation.setFamount(operation.getFamount());
			tovirtualcaptualoperation.setFfees(BigDecimal.ZERO);
			tovirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
			tovirtualcaptualoperation.setFhasowner(true);
			tovirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);// 收款成功
			tovirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());// 收款
			tovirtualcaptualoperation.setFuniquenumber("[平台互转]" + Utils.UUID());
			tovirtualcaptualoperation.setFrechargeaddress(operation.getFwithdrawaddress());
			tovirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
			tovirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
			tovirtualcaptualoperation.setVersion(0);
            tovirtualcaptualoperation.setFplatform(operation.getFplatform());
			if (virtualCapitalOperationMapper.insert(tovirtualcaptualoperation) <= 0) {
                logger.error("内部转账-添加订单记录失败 {} 转给 {} 包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
				throw new BCException("添加订单失败");
            }
            
            // 3.添加资产变更
            FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
			financeRecordDTO.setAmount(tovirtualcaptualoperation.getFamount());
			financeRecordDTO.setCreateDate(new Date());
			financeRecordDTO.setUpdateDate(new Date());
			financeRecordDTO.setUid(targetUid);
			financeRecordDTO.setOperation(FinanceRecordOperationEnum.In.getCode());
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			financeRecordDTO.setRelationCoinId(tovirtualcaptualoperation.getFcoinid());
			financeRecordDTO.setRelationCoinName("");
			financeRecordDTO.setRelationId(tovirtualcaptualoperation.getFid());
			financeRecordDTO.setTxId(tovirtualcaptualoperation.getFuniquenumber());
			financeRecordDTO.setWithdrawAddress("");
			financeRecordDTO.setRechargeAddress(tovirtualcaptualoperation.getFrechargeaddress());
			financeRecordDTO.setMemo("");
			financeRecordDTO.setFee(tovirtualcaptualoperation.getFfees());
			financeRecordDTO.setWalletOperationDate(new Date());
			if (financeRecordMapper.insert(financeRecordDTO) <= 0) {
                logger.error("内部转账-添加资产记录失败 {} 转给 {} 包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
				throw new BCException("添加提现日志失败");
			}	
            

            // 4.添加转账方订单
            operation.setFuniquenumber("[平台互转]" + Utils.UUID());
            operation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
            if (virtualCapitalOperationMapper.insert(operation) <= 0) {
                logger.error("内部转账-添加转账方订单记录失败 {} 转给 {} 包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
                throw  new BCException("添加失败提现订单失败");
            }
            
            // 5.扣除转账放资产
            if (super.updateUserCoinWallet(operation.getFuid(), operation.getFcoinid(), 
            MathUtils.positive2Negative(MathUtils.add(operation.getFamount(),operation.getFfees())), BigDecimal.ZERO) == false) {
                logger.error("内部转账-扣除资产失败 {} 转给 {} 包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
                throw new Exception("提现资产更改失败");
            }
            
            // 6.添加资产记录
            FinanceRecordDTO financeRecordDTO2 = new FinanceRecordDTO();
            financeRecordDTO2.setAmount(operation.getFamount());
            financeRecordDTO2.setCreateDate(new Date());
            financeRecordDTO2.setUpdateDate(new Date());
            financeRecordDTO2.setUid(operation.getFuid());
            financeRecordDTO2.setOperation(FinanceRecordOperationEnum.Out.getCode());
            financeRecordDTO2.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
            financeRecordDTO2.setRelationCoinId(operation.getFcoinid());
            financeRecordDTO2.setRelationCoinName("");
            financeRecordDTO2.setRelationId(operation.getFid());
            financeRecordDTO2.setTxId(operation.getFuniquenumber());
            financeRecordDTO2.setRechargeAddress("");
            financeRecordDTO2.setWithdrawAddress(operation.getFwithdrawaddress());
            financeRecordDTO2.setMemo(operation.getMemo() == null?"":operation.getMemo());
            financeRecordDTO2.setFee(operation.getFfees());
            financeRecordDTO2.setWalletOperationDate(new Date());
            if (financeRecordMapper.insert(financeRecordDTO2) <= 0) {
                logger.error("内部转账-添加转账方资产记录失败 {} 转给 {} 包含手续费金额 {} 币种 {}",operation.getFuid(),targetUid,operation.getFamount(),operation.getFcoinid());
                throw new Exception("添加提现日志 FinanceRecord 失败");
            }
            return true;
        }

        /***
         * 一下是非内部转账逻辑
         */
        if (virtualCapitalOperationMapper.insert(operation) <= 0) {
            throw  new BCException("添加失败提现订单失败");
        }
        BigDecimal opAmount = MathUtils.add(MathUtils.add(operation.getFamount(), operation.getFfees()), operation.getFbtcfees());
        boolean resultStatus = super.updateUserCoinWallet(operation.getFuid(), operation.getFcoinid(), MathUtils.positive2Negative(opAmount), opAmount);
        if (!resultStatus) {
            throw new Exception("提现资产更改失败");
        }

        FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
        financeRecordDTO.setAmount(operation.getFamount());
        financeRecordDTO.setCreateDate(new Date());
        financeRecordDTO.setUpdateDate(new Date());
        financeRecordDTO.setUid(operation.getFuid());
        financeRecordDTO.setOperation(FinanceRecordOperationEnum.Out.getCode());
        financeRecordDTO.setStatus(FinanceRecordStatusEnum.Processing.getCode());
        financeRecordDTO.setRelationCoinId(operation.getFcoinid());
        financeRecordDTO.setRelationCoinName("");
        financeRecordDTO.setRelationId(operation.getFid());
        financeRecordDTO.setTxId("");
        financeRecordDTO.setRechargeAddress("");
        financeRecordDTO.setWithdrawAddress(operation.getFwithdrawaddress());
        financeRecordDTO.setMemo(operation.getMemo() == null?"":operation.getMemo());
        financeRecordDTO.setFee(operation.getFfees());
        financeRecordDTO.setWalletOperationDate(new Date());
        if (financeRecordMapper.insert(financeRecordDTO) <= 0) {
            throw new Exception("添加提现日志 FinanceRecord 失败");
        }
        return true;
    }

    /**
     * 取消提现订单
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean cancleWithdrawOrder(FVirtualCapitalOperationDO operation) throws Exception {
        if (virtualCapitalOperationMapper.updateByPrimaryKey(operation) <= 0) {
            return false;
        }
        BigDecimal opAmount = MathUtils.add(MathUtils.add(operation.getFamount(), operation.getFfees()), operation.getFbtcfees());
        boolean resultStatus = super.updateUserCoinWallet(operation.getFuid(), operation.getFcoinid(), opAmount, MathUtils.positive2Negative(opAmount));
        if (!resultStatus) {
            throw new Exception();
        }

        Map<String,Object> params = new HashMap<>();
        params.put("operation",FinanceRecordOperationEnum.Out.getCode());
        params.put("relationId",operation.getFid());
        params.put("offset",0);
        params.put("limit",1);
        params.put("orderfield","create_date");
        params.put("orderdirection","desc");
        List<FinanceRecordDTO> financeRecordDTOS = financeRecordMapper.selectByParams(params);
        if (financeRecordDTOS == null || financeRecordDTOS.size() != 1) {
            throw new BCException("提现日志 FinanceRecord 不存在记录");
        }
        FinanceRecordDTO financeRecordDTO = financeRecordDTOS.get(0);
        financeRecordDTO.setStatus(FinanceRecordStatusEnum.Cancel.getCode());
        financeRecordDTO.setUpdateDate(new Date());
        if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
            throw new BCException("更新日志失败");
        }
        return true;
    }

    /**
     * 充值地址
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean createCoinAddress(FPoolDO fpool, FUserVirtualAddressDO virtualAddress) throws Exception {
        // 修改充值地址的使用状态标志位为已使用
        if (poolMapper.updatePoolStatus(fpool) <= 0) {
            throw new Exception();
        }
        if (userVirtualAddressMapper.insert(virtualAddress) <= 0) {
            throw new Exception();
        }
        return true;
    }
}
