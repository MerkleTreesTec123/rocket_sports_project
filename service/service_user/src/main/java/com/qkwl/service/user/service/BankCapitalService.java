package com.qkwl.service.user.service;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.capital.BankOperationOrderDTO;
import com.qkwl.common.dto.capital.ResultRechargeOrderInfoDTO;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.service.common.mapper.UserCommonMapper;
import com.qkwl.service.user.constant.ConfigConsts;
import com.qkwl.service.user.dao.FUserBankinfoMapper;
import com.qkwl.service.user.dao.FWalletCapitalOperationMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import com.qkwl.service.user.model.FUserBankinfoDO;
import com.qkwl.service.user.model.FWalletCapitalOperationDO;
import com.qkwl.service.user.tx.BankCapitalServiceTx;
import com.qkwl.service.user.utils.MQSendUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户资产计算接口实现
 *
 * @author ZKF
 */
@Service
public class BankCapitalService {

    private static final Logger logger = LoggerFactory.getLogger(BankCapitalService.class);

    @Autowired
    private FUserBankinfoMapper userBankinfoMapper;
    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private BankCapitalServiceTx bankCapitalServiceTx;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ValidateHelper validateHelper;
    @Autowired
    private MQSendUtils mqSendUtils;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    @Autowired
    private UserCommonMapper userCommonMapper;

    /**
     * 充值
     *
     * @param order 订单实体
     * @return Result 返回结果 <br/>
     * 充值： <br/>
     * 200：创建成功 <br/>
     * 1100： 充值银行未找到 <br/>
     * 1101： 系统银行账号已停用<br/>
     * 1102： 最小充值金额为%d <br/>
     * 1103： 最大充值金额为%d <br/>
     * 1104： 订单创建失败 <br/>
     */
    public Result createRechargeOrder(BankOperationOrderDTO order) {
        FUserBankinfoDO userBankinfo = userBankinfoMapper.selectByPrimaryKey(order.getUserBankId());
        if (userBankinfo == null) {
            return Result.failure(1000, "提现银行未找到");
        }
        //系统银行卡信息
        FSystemBankinfoRecharge systemBankInfo = null;
        if (order.getOperationType().equals(CapitalOperationTypeEnum.RMB_IN)) {
            systemBankInfo = getBankInfoRecharge(order.getRechargeType(), userBankinfo.getFname());
            if (systemBankInfo == null || systemBankInfo.getFstatus().equals(BankStatusEnum.Disable.getCode())) {
                return Result.failure(1101, "系统银行账号已停用");
            }
        }

        if (order.getAmount().compareTo(ConfigConsts.RMB_RECHANGE_MIN) < 0) {
            return Result.failure(1102, "最小充值金额为" + MathUtils.decimalFormat(ConfigConsts.RMB_RECHANGE_MIN),
                    MathUtils.decimalFormat(ConfigConsts.RMB_RECHANGE_MIN));
        }
        if (order.getAmount().compareTo(ConfigConsts.RMB_RECHANGE_MAX) > 0) {
            return Result.failure(1103, "最大充值金额为" + MathUtils.decimalFormat(ConfigConsts.RMB_RECHANGE_MAX),
                    MathUtils.decimalFormat(ConfigConsts.RMB_RECHANGE_MAX));
        }


        FWalletCapitalOperationDO operation = new FWalletCapitalOperationDO();
        //operation.setFsysbankid(userBankinfo.getFid());

        operation.setFamount(order.getAmount());
        operation.setFfees(BigDecimal.ZERO);

        // 通用附加
        operation.setFtype(order.getOperationType());
        if (order.getOperationType().equals(CapitalOperationTypeEnum.RMB_IN)) {
            operation.setFremittancetype(CapitalOperationTypeEnum.RMB_IN);
            operation.setFsysbankid(systemBankInfo.getFid());
            operation.setFbank(systemBankInfo.getFbankname());
        } else if (order.getOperationType().equals(CapitalOperationTypeEnum.ALIPAY_INT)) {
            operation.setFremittancetype(CapitalOperationTypeEnum.ALIPAY_INT);
            operation.setFbank(CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.ALIPAY_INT));
        } else if (order.getOperationType().equals(CapitalOperationTypeEnum.WECHAT_INT)) {
            operation.setFremittancetype(CapitalOperationTypeEnum.WECHAT_INT);
            operation.setFbank(CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.WECHAT_INT));
        }

        operation.setFbank(userBankinfo.getFname());
        operation.setFaccount(userBankinfo.getFbanknumber());
        operation.setFpayee(userBankinfo.getFrealname());
        //operation.setFaddress(userBankinfo.getFprov() + " " + userBankinfo.getFcity() + " " + userBankinfo.getFdist());
        operation.setFstatus(CapitalOperationInStatus.WaitForComing);
        operation.setFuid(order.getUserId());
        operation.setFcoinid(order.getCoinId());
        operation.setFinouttype(order.getOperationInOutType().getCode());
        operation.setFsource(order.getDataSource().getCode());
        operation.setFplatform(order.getPlatform().getCode());
        operation.setFcreatetime(new Date());
        operation.setFupdatetime(new Date());
        operation.setFagentid(0);
        operation.setVersion(0);
        operation.setFphone(order.getPhone());
        try {
            if (bankCapitalServiceTx.createRechargeOrder(operation)) {
                mqSendUtils.SendUserAction(operation.getFuid(), LogUserActionEnum.RMB_RECHARGE, order.getCoinId(),
                        operation.getFamount(), order.getIp());
                if (order.getRisk()) {
                    String riskphone = redisHelper.getSystemArgs(ArgsConstant.RISKPHONE);
                    String[] riskphones = riskphone.split("#");
                    if (riskphones.length > 0) {
                        for (String phone : riskphones) {
                            validateHelper.smsRiskManage(order.getLoginName(), phone, order.getPlatform().getCode(),
                                    BusinessTypeEnum.SMS_RISKMANAGE.getCode(), order.getOperationInOutType().getValue(),
                                    order.getAmount(), order.getCoinName());
                        }
                    }
                }
                ResultRechargeOrderInfoDTO resultOrderInfo = new ResultRechargeOrderInfoDTO();
                resultOrderInfo.setOrderId(operation.getFid());
                resultOrderInfo.setMoney(operation.getFamount());
                //银行卡充值
                if (order.getOperationType().equals(CapitalOperationTypeEnum.RMB_IN)) {
                    resultOrderInfo.setBankAddress(systemBankInfo.getFbankaddress());
                    resultOrderInfo.setBankName(systemBankInfo.getFbankname());
                    resultOrderInfo.setBankNumber(systemBankInfo.getFbanknumber());
                    resultOrderInfo.setRechargeBankName(userBankinfo.getFname());
                    resultOrderInfo.setOwnerName(systemBankInfo.getFownername());
                }

                return Result.success("订单创建成功", resultOrderInfo);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("createRechargeOrder err {} ", order.toString(), e);
            }
        }
        return Result.failure(1104, "订单创建失败");
    }

    /**
     * 获取人民币充值收款的银行卡信息（先按照银行名称匹配，如果没成功就取第一条）
     *
     * @param type     充值类型
     * @param bankName 银行名称
     */
    private FSystemBankinfoRecharge getBankInfoRecharge(int type, String bankName) {
        List<FSystemBankinfoRecharge> bankInfoRecharges = redisHelper.getRechargeBank(type);
        if (bankInfoRecharges == null) {
            return null;
        }
        FSystemBankinfoRecharge firstBankInfo = null;
        for (FSystemBankinfoRecharge item : bankInfoRecharges) {
            if (item.getFstatus().equals(BankStatusEnum.Disable.getCode())) {
                continue;
            }
            if (item.getFbankname().equals(bankName)) {
                return item;
            }
            if (firstBankInfo == null) {
                firstBankInfo = item;
            }
        }
        return firstBankInfo;
    }

    /**
     * 提现
     *
     * @param order 订单实体
     * @return Result
     */
    public Result createWithdrawOrder(BankOperationOrderDTO order, FUser user) {

        FUserBankinfoDO userBankinfo = userBankinfoMapper.selectByPrimaryKey(order.getUserBankId());
        if (userBankinfo == null) {
            return Result.failure(1000, "提现银行未找到");
        }
        SystemCoinSetting coinSetting = redisHelper.getCoinSetting(order.getCoinId(), user.getLevel());
        if (coinSetting == null) {
            return Result.failure(1004, "币种设置为空");
        }
        if (coinSetting.getWithdrawMin() != null && coinSetting.getWithdrawMin().compareTo(BigDecimal.ZERO) > 0
                && order.getAmount().compareTo(coinSetting.getWithdrawMin()) < 0) {
            return Result.failure(1005, "最小提现数量为" + MathUtils.decimalFormat(coinSetting.getWithdrawMin()),
                    MathUtils.decimalFormat(coinSetting.getWithdrawMin()));
        }
        if (coinSetting.getWithdrawMax() != null && coinSetting.getWithdrawMax().compareTo(BigDecimal.ZERO) > 0
                && order.getAmount().compareTo(coinSetting.getWithdrawMax()) > 0) {
            return Result.failure(1006, "最大提现数量为" + MathUtils.decimalFormat(coinSetting.getWithdrawMax()),
                    MathUtils.decimalFormat(coinSetting.getWithdrawMin()));
        }
        /*int time = walletCapitalOperationMapper.getWalletWithdrawTimes(order.getUserId());
        if (time >= coinSetting.getWithdrawTimes()) {
            return Result.failure(1007, "每日人民币提现最多" + coinSetting.getWithdrawTimes() + "次，请明天提现",
                    coinSetting.getWithdrawTimes());
        }*/
        BigDecimal dayWithdraw = walletCapitalOperationMapper.getDayWithdrawCny(order.getUserId());
        BigDecimal blaneWithdraw = MathUtils.toScaleNum(MathUtils.sub(dayWithdraw, coinSetting.getWithdrawDayLimit()), MathUtils.ENTER_CNY_SCALE);
        if (coinSetting.getWithdrawDayLimit() != null && coinSetting.getWithdrawDayLimit().compareTo(BigDecimal.ZERO) > 0
                && MathUtils.add(dayWithdraw, order.getAmount()).compareTo(coinSetting.getWithdrawDayLimit()) > 0) {
            return Result.failure(1008, "您今日总共还可提现人民币" + MathUtils.decimalFormat(blaneWithdraw) + "元",
                    MathUtils.decimalFormat(blaneWithdraw));
        }
        UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoin(order.getUserId(), order.getCoinId());
        if (wallet == null || wallet.getTotal().compareTo(order.getAmount()) == -1) {
            return Result.failure(1009, "余额不足");
        }

        // 手机验证码，谷歌验证码，交易密码验证
        Result result = validationCheckHelper.getCapitalCheck(user, order.getPhoneCode(), BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode(),
                order.getGoogleCode(), order.getPassword(), order.getIp(), order.getPlatform().getCode());
        if (result.getCode() != 200) {
            return result;
        }

        FWalletCapitalOperationDO operation = new FWalletCapitalOperationDO();
        operation.setFsysbankid(userBankinfo.getFid());
//        BigDecimal fees = MathUtils.mul(order.getAmount(), coinSetting.getWithdrawFee());
//        BigDecimal amt = MathUtils.toScaleNum(MathUtils.sub(order.getAmount(), fees), MathUtils.ENTER_CNY_SCALE);
//
        operation.setFamount(order.getAmount());
        operation.setFfees(BigDecimal.ZERO);
        operation.setFstatus(CapitalOperationOutStatus.WaitForOperation);
        operation.setFuid(order.getUserId());
        operation.setFcoinid(order.getCoinId());
        operation.setFinouttype(order.getOperationInOutType().getCode());
        operation.setFtype(order.getOperationType());
        operation.setFbank(userBankinfo.getFname());
        operation.setFaccount(userBankinfo.getFbanknumber());
        operation.setFpayee(userBankinfo.getFrealname());
        operation.setFphone(user.getFtelephone());
        operation.setFcreatetime(new Date());
        operation.setFupdatetime(new Date());
        operation.setFaddress(userBankinfo.getFprov() + " " + userBankinfo.getFcity() + " " + (userBankinfo.getFdist() == null ? "" : userBankinfo.getFdist() + " ") + userBankinfo.getFaddress());
        operation.setFsource(order.getDataSource().getCode());
        operation.setFplatform(order.getPlatform().getCode());
        operation.setVersion(0);
        // 查询提现银行的编码
        if(userBankinfo.getFbanktype() != null && userBankinfo.getFbanktype() > 0){
            FSystemBankinfoWithdraw bankinfoWithdraw = redisHelper.getWithdrawBankById(userBankinfo.getFbanktype());
            if(bankinfoWithdraw != null && bankinfoWithdraw.getBankCode() != null){
                operation.setFbankCode(bankinfoWithdraw.getBankCode());
            }
        }

        try {
            if (bankCapitalServiceTx.createWithdrawOrder(operation,coinSetting.getWithdrawFee())) {
                mqSendUtils.SendUserAction(operation.getFuid(), LogUserActionEnum.RMB_WITHDRAW_WAIT, order.getCoinId(),
                        operation.getFamount(), order.getIp());
                if (order.getRisk()) {
                    String riskphone = redisHelper.getSystemArgs(ArgsConstant.RISKPHONE);
                    String[] riskphones = riskphone.split("#");
                    if (riskphones.length > 0) {
                        for (String phone : riskphones) {
                            validateHelper.smsRiskManage(order.getLoginName(), phone, PlatformEnum.BC.getCode(),
                                    BusinessTypeEnum.SMS_RISKMANAGE.getCode(), order.getOperationInOutType().getValue(),
                                    order.getAmount(), order.getCoinName());
                        }
                    }
                }
                return Result.success("订单创建成功");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("createWithdrawOrder err {} ", order.toString(), e);
            }
        }
        return Result.failure("订单创建失败");
    }



}
