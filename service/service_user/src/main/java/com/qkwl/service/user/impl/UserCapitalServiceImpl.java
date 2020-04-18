package com.qkwl.service.user.impl;

import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.capital.BankOperationOrderDTO;
import com.qkwl.common.dto.capital.CoinOperationOrderDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.service.common.mapper.UserCommonMapper;
import com.qkwl.service.user.dao.*;
import com.qkwl.service.user.model.FVirtualCapitalOperationDO;
import com.qkwl.service.user.model.FWalletCapitalOperationDO;
import com.qkwl.service.user.service.BankCapitalService;
import com.qkwl.service.user.service.CoinCapitalService;
import com.qkwl.service.user.tx.BankCapitalServiceTx;
import com.qkwl.service.user.tx.CoinCapitalServiceTx;
import com.qkwl.service.user.utils.MQSendUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户资产操作接口
 */
@Service("userCapitalService")
public class UserCapitalServiceImpl implements IUserCapitalService {

    private static final Logger logger = LoggerFactory.getLogger(UserCapitalServiceImpl.class);

    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;
    @Autowired
    private FVirtualCapitalOperationMapper virtualCapitalOperationMapper;
    @Autowired
    private UserCommonMapper userCommonMapper;
    @Autowired
    private BankCapitalService bankCapitalService;
    @Autowired
    private CoinCapitalService coinCapitalService;
    @Autowired
    private CoinCapitalServiceTx coinCapitalServiceTx;
    @Autowired
    private BankCapitalServiceTx bankCapitalServiceTx;
    @Autowired
    private MQSendUtils mqSendUtils;
    @Autowired
    private PreValidationHelper preValidationHelper;
    @Autowired
    private FLogConsoleVirtualRechargeMapper logConsoleVirtualRechargeMapper;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    @Autowired
    FinanceRecordMapper financeRecordMapper;

    @Autowired
    RedisHelper redisHelper;

    @Override
    public Boolean getIsFirstCharge(int userId) {
        Map<String, Object> map = new HashMap<>();

        map.put("fuid", userId);
        map.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
        map.put("fstatus", CapitalOperationInStatus.Come);
        int countCny = walletCapitalOperationMapper.countWalletCapitalOperation(map);
        if(countCny > 0){
            return false;
        }

        map.clear();
        map.put("fuid", userId);
        map.put("ftype", VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
        map.put("fstatus", VirtualCapitalOperationInStatusEnum.SUCCESS);
        int countCoin = virtualCapitalOperationMapper.countVirtualCapitalOperation(map);
        if(countCoin > 0){
            return false;
        }

        return true;
    }

    @Override
    public Result createCoinOperationOrder(CoinOperationOrderDTO order) {
        if (order.getCoinId() == null) {
            return Result.param("coinId is null");
        }
        if (order.getAmount() == null || order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.param("amount is null");
        }
        if (order.getOperationType() == null) {
            return Result.param("operationType is null");
        }
        if (order.getDataSource() == null) {
            return Result.param("dataSource is null");
        }
        if (order.getPlatform() == null) {
            return Result.param("platform is null");
        }
        if (order.getOperationType().getCode().equals(VirtualCapitalOperationTypeEnum.COIN_IN.getCode())) {
            if(order.getBaseCoinId() == null){
                return Result.param("baseCoinId is null");
            }
            return coinCapitalService.createRechargeOrder(order);
        } else if (order.getOperationType().getCode().equals(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode())) {
            if (order.getUserId() == null) {
                return Result.param("userId is null");
            }
            if (order.getNetworkFees() == null) {
                return Result.param("networkFees is null");
            }
            FUser user = userCommonMapper.selectOneById(order.getUserId());
            if(user == null ){
                return Result.param("user is not found");
            }
            Result preResult = preValidationHelper.validateCapital(user, "coin");
            if(!preResult.getSuccess()){
                return preResult;
            }
            return coinCapitalService.createWithdrawOrder(order, user);
        } else {
            return Result.param("operationType is err");
        }
    }

    @Override
    public Result cancleCoinOperationOrder(Integer userId, Integer recordId, String ip) {
        if (userId == null) {
            return Result.param("userId is null");
        }
        if (recordId == null) {
            return Result.param("recordId is null");
        }
        if (ip == null) {
            return Result.param("ip is null");
        }
        FVirtualCapitalOperationDO operation = virtualCapitalOperationMapper.selectByPrimaryKey(recordId);
        if (!operation.getFuid().equals(userId)) {
            return Result.failure(1000, "订单记录不存在");
        }
        if (!operation.getFtype().equals(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode())) {
            return Result.failure(1000, "订单记录不存在");
        }
        if (!operation.getFstatus().equals(VirtualCapitalOperationOutStatusEnum.WaitForOperation)) {
            return Result.failure(1001, "订单记录状态错误");
        }
        operation.setFstatus(VirtualCapitalOperationOutStatusEnum.Cancel);
        operation.setFupdatetime(new Date());
        try {
            Boolean flag = coinCapitalServiceTx.cancleWithdrawOrder(operation);
            if (!flag) {
                return Result.failure("订单撤销失败");
            }
            mqSendUtils.SendUserAction(operation.getFuid(), LogUserActionEnum.COIN_WITHDRAW_CANCEL,
                    operation.getFcoinid(), operation.getFamount(), ip);
        } catch (Exception e) {
            logger.error("cancleCoinOperationOrder err userId:{},recordId:{},Ip:{} ", userId, recordId, ip, e);
        }
        return Result.success("订单撤销成功");
    }

    @Override
    public Pagination<FVirtualCapitalOperationDTO> listVirtualCapitalOperation(
            Pagination<FVirtualCapitalOperationDTO> param, FVirtualCapitalOperationDTO operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", param.getOffset());
        map.put("limit", param.getPageSize());
        map.put("fuid", operation.getFuid());
        map.put("ftype", operation.getFtype());
        map.put("fcoinid", operation.getFcoinid());
        map.put("begindate", param.getBegindate());
        map.put("enddate", param.getEnddate());

        int count = virtualCapitalOperationMapper.countVirtualCapitalOperation(map);
        if (count > 0) {
            List<FVirtualCapitalOperationDO> list = virtualCapitalOperationMapper.getPageVirtualCapitalOperation(map);
            for (FVirtualCapitalOperationDO operation1 : list) {
                operation1.setFamount(MathUtils.toScaleNum(operation1.getFamount(), MathUtils.DEF_COIN_SCALE));
                operation1.setFfees(MathUtils.toScaleNum(operation1.getFfees(), MathUtils.DEF_COIN_SCALE));
            }
            param.setData(PojoConvertUtil.convert(list, FVirtualCapitalOperationDTO.class));
        }
        param.setTotalRows(count);
        param.generate();
        return param;
    }

    /**
     * 创建法币操作订单(充值提现)
     *
     * @param order 订单DTO
     * @return Result
     * 1000 银行卡未找到
     * 1004 币种设置为空
     * 1005 最小提现数量
     * 1006 最大提现数量
     * 1007 每日人民币提现最多
     * 1008 您今日总共还可提现人民币
     * 1009 余额不足
     * 1100： 充值银行未找到 <br/>
     * 1101： 系统银行账号已停用<br/>
     * 1102： 最小充值金额为%d <br/>
     * 1103： 最大充值金额为%d <br/>
     * 1104： 订单创建失败 <br/>
     * 1105： 请先完成实名认证！ <br/>
     */
    @Override
    public Result createBankOperationOrder(BankOperationOrderDTO order) {
        if (order.getCoinId() == null) {
            return Result.param("coinID is null");
        }
        if (order.getUserId() == null) {
            return Result.param("userId is null");
        }
        if (order.getAmount() == null) {
            return Result.param("amount is null");
        }
        if (StringUtils.isEmpty(order.getIp())) {
            return Result.param("ip is null");
        }
        FUser user  = userCommonMapper.selectOneById(order.getUserId());
        if(user == null){
            return Result.param("user is not found");
        }

        //先实名认证
        if(preValidationHelper.validateUserIdentityAuth(user)){
            return Result.failure(1105 ,"请先完成实名认证！");
        }

        order.setLoginName(user.getFloginname());

        if (order.getOperationInOutType().getCode().equals(CapitalOperationInOutTypeEnum.IN.getCode())) {
            return bankCapitalService.createRechargeOrder(order);
        } else if (order.getOperationInOutType().getCode().equals(CapitalOperationInOutTypeEnum.OUT.getCode())) {
            //修改等级
            user.setLevel(userCommonMapper.selectVipLevel(user.getFid()));
            Result result = preValidationHelper.validateCapital(user, "cny");
            if(result.getCode() != 200){
                return result;
            }
            return bankCapitalService.createWithdrawOrder(order, user);
        } else {
            return Result.param("operationType is err");
        }
    }


    /**
     * 取消订单信息
     * @param userId 用户id
     * @param recordId 订单id
     * @param ip    ip地址
     * @return Result
     * 200 : 订单撤销成功
     * 1000: 订单记录不存在！<br/>
     * 1001: 订单撤销失败！<br/>
     */
    @Override
    public Result cancelBankOperationOrder(Integer userId, Integer recordId, String ip) {
        if (userId == null) {
            return Result.param("userId is null");
        }
        if (recordId == null) {
            return Result.param("recordId is null");
        }
        if (ip == null) {
            return Result.param("ip is null");
        }
        FWalletCapitalOperationDO operation = walletCapitalOperationMapper.selectByPrimaryKey(recordId);
        if (!operation.getFuid().equals(userId)) {
            return Result.failure(1000, "订单记录不存在");
        }
        operation.setFupdatetime(new Date());
        if (operation.getFtype().equals(CapitalOperationInOutTypeEnum.IN.getCode())) {
            if(!operation.getFstatus().equals(CapitalOperationInStatus.NoGiven)){
                return Result.failure(1001,"订单状态已改变，请刷新重试");
            }
            operation.setFstatus(CapitalOperationInStatus.Invalidate);
            try {
                if (bankCapitalServiceTx.cancleRechargeOrder(operation)) {
                    mqSendUtils.SendUserAction(operation.getFuid(), LogUserActionEnum.COIN_WITHDRAW_CANCEL, operation.getFcoinid(), operation.getFamount(), ip);
                    return Result.success("订单撤销成功");
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("cancleBankOperationOrder cancleRechargeOrder err userId:{},recordId:{},Ip:{} ", userId, recordId, ip, e);
                }
            }
        } else if (operation.getFtype().equals(CapitalOperationInOutTypeEnum.OUT.getCode())) {
            if(!operation.getFstatus().equals(CapitalOperationOutStatus.WaitForOperation)){
                return Result.failure(1001,"订单状态已改变，请刷新重试");
            }
            operation.setFstatus(CapitalOperationOutStatus.Cancel);
            try {
                if (bankCapitalServiceTx.cancleWithdrawOrder(operation)) {
                    mqSendUtils.SendUserAction(operation.getFuid(), LogUserActionEnum.COIN_WITHDRAW_CANCEL, operation.getFcoinid(), operation.getFamount(), ip);
                    return Result.success("订单撤销成功");
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("cancleBankOperationOrder cancleWithdrawOrder err userId:{},recordId:{},Ip:{} ", userId, recordId, ip, e);
                }
            }
        }
        return Result.failure("订单撤销失败");
    }

    @Override
    public FWalletCapitalOperationDTO getWalletCapitalOperationById(Integer id) {
        FWalletCapitalOperationDO fWalletCapitalOperation = walletCapitalOperationMapper.selectByPrimaryKey(id);
        if(fWalletCapitalOperation == null){
            return  null;
        }
        fWalletCapitalOperation.setFamount(MathUtils.toScaleNum(fWalletCapitalOperation.getFamount(), MathUtils.DEF_CNY_SCALE));
        fWalletCapitalOperation.setFfees(MathUtils.toScaleNum(fWalletCapitalOperation.getFfees(), MathUtils.DEF_CNY_SCALE));
        return PojoConvertUtil.convert(fWalletCapitalOperation,FWalletCapitalOperationDTO.class);
    }

    @Override
    public Pagination<FWalletCapitalOperationDTO> listWalletCapitalOperation(Pagination<FWalletCapitalOperationDTO> param, FWalletCapitalOperationDTO operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", param.getOffset());
        map.put("limit", param.getPageSize());
        map.put("fuid", operation.getFuid());
        map.put("ftype", operation.getFtype());
        map.put("finouttype", operation.getFinouttype());
        map.put("begindate", param.getBegindate());
        map.put("enddate", param.getEnddate());
        int count = walletCapitalOperationMapper.countWalletCapitalOperation(map);
        if (count > 0) {
            List<FWalletCapitalOperationDO> list = walletCapitalOperationMapper.getPageWalletCapitalOperation(map);
            for (FWalletCapitalOperationDO fWalletCapitalOperation : list) {
                fWalletCapitalOperation.setFamount(MathUtils.toScaleNum(fWalletCapitalOperation.getFamount(), MathUtils.DEF_CNY_SCALE));
                fWalletCapitalOperation.setFfees(MathUtils.toScaleNum(fWalletCapitalOperation.getFfees(), MathUtils.DEF_CNY_SCALE));
            }
            param.setData(PojoConvertUtil.convert(list,FWalletCapitalOperationDTO.class));
        }
        param.setTotalRows(count);
        param.generate();
        return param;
    }

    @Override
    public Pagination<FLogConsoleVirtualRecharge> listLogConsoleVirtualRecharge(Pagination<FLogConsoleVirtualRecharge> page,
                                                                                FLogConsoleVirtualRecharge record) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());
        map.put("fuid", record.getFuid());
        map.put("fcoinid", record.getFcoinid());
        map.put("ftype", record.getFtype());
        map.put("fstatus", record.getFstatus());

        int count = logConsoleVirtualRechargeMapper.countPage(map);
        if(count > 0) {
            List<FLogConsoleVirtualRecharge> list = logConsoleVirtualRechargeMapper.listPage(map);
            page.setData(list);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

    @Override
    public Pagination<CommissionRecord> listCommissionRecord(Pagination<CommissionRecord> page,Integer uid,Integer coinID) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("coinid", coinID);
        map.put("introuid",uid);

        int count = commissionRecordMapper.countPage(map);
        if(count > 0) {
            List<CommissionRecord> list = commissionRecordMapper.listPage(map);
            page.setData(list);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

    @Override
    public BigDecimal getCommissionTotalByID(Integer uid, Integer coinid) {
        if (uid == null || uid.intValue() == 0){
            return BigDecimal.ZERO;
        }
        if (coinid == null || coinid.intValue() == 0){
            return BigDecimal.ZERO;
        }

        Map<String,String> map = new HashMap<>();
        map.put("uid",String.valueOf(uid));
        map.put("coinid",String.valueOf(coinid));
        BigDecimal bigDecimal = commissionRecordMapper.totalCommission(map);
        return bigDecimal == null ?BigDecimal.ZERO:bigDecimal;
    }

    @Override
    public Pagination<FinanceRecordDTO> listFinanceRecord(Pagination<FinanceRecordDTO> page, Integer uid,Integer coinId,Integer op) {
        Map<String,Object> params = new HashMap<>();
        FUser user = userCommonMapper.selectOneById(uid);
        if (user == null){
            return page;
        }
        params.put("uid",uid);
        params.put("coinId",coinId);
        params.put("orderfield","create_date");
        params.put("orderdirection","desc");
        params.put("operation", op);
        params.put("offset",page.getOffset());
        params.put("limit",page.getPageSize());
        Integer countPage = financeRecordMapper.countPage(params);
        if (countPage > 0){
            List<FinanceRecordDTO> list = financeRecordMapper.selectByParams(params);
            for (FinanceRecordDTO record: list) {
                SystemCoinType coinType = redisHelper.getCoinType(record.getRelationCoinId());
                record.setRelationCoinName(coinType == null?"未知":coinType.getShortName().toUpperCase());
            }
            page.setTotalRows(countPage);
            page.setData(list);
            page.generate();
        }
        return page;
    }



}
