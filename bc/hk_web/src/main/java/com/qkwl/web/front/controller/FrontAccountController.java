package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.capital.*;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.model.KeyValues;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;


@Controller
public class FrontAccountController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontAccountController.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private IUserCapitalAccountService userCapitalAccountService;
    @Autowired
    private IUserCapitalService userCapitalService;
    @Autowired
    private IUserWalletService userWalletService;

    /**
     * 增加一条充值记录
     */
    @ResponseBody
    @RequestMapping(value = "/deposit/alipay_manual")
    public ReturnResult alipayManual(
            @RequestParam(value = "symbol", required = true) Integer symbol,
            @RequestParam(value = "amount", required = true) String money,
            @RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "type", required = true) int type,
            @RequestParam(value = "ubank", required = true) int userBankId) throws Exception {
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsRecharge()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("financial.err.1002"));
        }
        BigDecimal moneyDecimal = MathUtils.toScaleNum(new BigDecimal(money), MathUtils.ENTER_CNY_SCALE);

        // 最小充值金额
        if (moneyDecimal.compareTo(WebConstant.MINRECHARGECNY) < 0) {
            return ReturnResult.FAILUER(GetR18nMsg("financial.err.1004", MathUtils.decimalFormat(WebConstant.MINRECHARGECNY)));
        }
        // 最大充值金额
        if (moneyDecimal.compareTo(WebConstant.MAXRECHARGECNY) > 0) {
            return ReturnResult.FAILUER(GetR18nMsg("financial.err.1005", MathUtils.decimalFormat(WebConstant.MAXRECHARGECNY)));
        }
        FUser fuser = getCurrentUserInfoByToken();

        BankOperationOrderDTO bankOperationOrder = new BankOperationOrderDTO();
        bankOperationOrder.setCoinId(coinType.getId());
        bankOperationOrder.setAmount(moneyDecimal);
        bankOperationOrder.setUserBankId(userBankId);
        bankOperationOrder.setUserLevel(fuser.getLevel());
        bankOperationOrder.setUserId(fuser.getFid());
        bankOperationOrder.setOperationInOutType(CapitalOperationInOutTypeEnum.IN);
        bankOperationOrder.setOperationType(CapitalOperationTypeEnum.RMB_IN);
        bankOperationOrder.setPlatform(PlatformEnum.BC);
        bankOperationOrder.setIp(getIpAddr());
        bankOperationOrder.setDataSource(DataSourceEnum.WEB);
        bankOperationOrder.setCoinName(coinType.getName());
        bankOperationOrder.setRisk(true);
        bankOperationOrder.setPhone(phone);
        bankOperationOrder.setRechargeType(type);

        Result result = null;
        try {
            result = userCapitalService.createBankOperationOrder(bankOperationOrder);
        } catch (Exception ex) {
            logger.error("createBankOperationOrder err {} ", ex);
        }
        if (result == null) {
            return ReturnResult.FAILUER(GetR18nMsg("common.error.400"));
        }

        if (result.getCode() > 200 && result.getCode() < 1000) {
            return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode(), result.getData()));
        } else if (!result.getSuccess()) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.cny.operation." + result.getCode(), result.getData()));
        }

        ResultRechargeOrderInfoDTO orderInfo = (ResultRechargeOrderInfoDTO) result.getData();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("money", String.valueOf(orderInfo.getMoney()));
        jsonObject.put("tradeId", orderInfo.getOrderId());
        jsonObject.put("userId", fuser.getFid());
        // bank info
        jsonObject.put("bankName", orderInfo.getBankName());
        jsonObject.put("rechargeBankName", orderInfo.getRechargeBankName());
        jsonObject.put("ownerName", orderInfo.getOwnerName());
        jsonObject.put("bankAddress", orderInfo.getBankAddress());
        jsonObject.put("bankNumber", orderInfo.getBankNumber());
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 人民币提现
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "withdraw/cny_manual")
    public ReturnResult withdrawBankSubmit(String tradePwd,
                                           BigDecimal withdrawBalance,
                                           @RequestParam(required = false, defaultValue = "0") String totpCode,
                                           @RequestParam(required = false, defaultValue = "0") String phoneCode,
                                           Integer withdrawBlank,
                                           Integer symbol) {
        if (withdrawBlank == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1000"));
        }

        if (withdrawBalance == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1001"));
        }

        withdrawBalance = MathUtils.toScaleNum(withdrawBalance, MathUtils.ENTER_CNY_SCALE);
        if (withdrawBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1002"));
        }
        if (StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }

        // 用户
        FUser fuser = getCurrentUserInfoByToken();
        if ("0".equals(phoneCode) && fuser.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if ("0".equals(totpCode) && fuser.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }

        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("financial.err.1002"));
        }

        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (BCException e) {
            e.printStackTrace();
        }

        BankOperationOrderDTO bankOperationOrder = new BankOperationOrderDTO();
        bankOperationOrder.setCoinId(coinType.getId());
        bankOperationOrder.setAmount(withdrawBalance);
        bankOperationOrder.setUserBankId(withdrawBlank);
        bankOperationOrder.setUserLevel(fuser.getLevel());
        bankOperationOrder.setUserId(fuser.getFid());
        bankOperationOrder.setOperationInOutType(CapitalOperationInOutTypeEnum.OUT);
        bankOperationOrder.setOperationType(CapitalOperationTypeEnum.RMB_OUT);
        bankOperationOrder.setPlatform(PlatformEnum.BC);
        bankOperationOrder.setIp(getIpAddr());
        bankOperationOrder.setDataSource(DataSourceEnum.WEB);
        bankOperationOrder.setCoinName(coinType.getName());
        bankOperationOrder.setRisk(true);
        bankOperationOrder.setPhone(fuser.getFtelephone());
        bankOperationOrder.setPhoneCode(phoneCode);
        bankOperationOrder.setPassword(tradePwd);
        bankOperationOrder.setGoogleCode(totpCode);
        try {
            Result result = userCapitalService.createBankOperationOrder(bankOperationOrder);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS("提交成功");
            } else {
                return ReturnResult.FAILUER(result.getCode() + result.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ReturnResult.FAILUER("");
    }

    /**
     * 虚拟币提现
     */
    @ResponseBody
    @RequestMapping(value = "/withdraw/coin_manual")
    public ReturnResult withdrawBtcSubmit(
            @RequestParam Integer withdrawAddr,
            @RequestParam BigDecimal withdrawAmount,
            @RequestParam String tradePwd,
            @RequestParam Integer symbol,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") Integer btcfeesIndex,
            @RequestParam(required = false) String memo) {
        if (withdrawAddr == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1000"));
        }
        if (withdrawAmount == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1001"));
        }
        withdrawAmount = MathUtils.toScaleNum(withdrawAmount, MathUtils.ENTER_COIN_SCALE);
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1002"));
        }
        if (StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }
        // 用户
        FUser user = getCurrentUserInfoByToken();
        if ("0".equals(phoneCode) && user.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if ("0".equals(totpCode) && user.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }
        // 币信息
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw() || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        // BTC网络手续费
        BigDecimal BTCFees = coinType.getNetworkFee();
        if (coinType.getShortName().equals("BTC")) {
            if (btcfeesIndex <= 0 || btcfeesIndex >= Constant.BTC_FEES_MAX) {
                BTCFees = Constant.BTC_FEES[0];
            } else {
                BTCFees = Constant.BTC_FEES[btcfeesIndex];
            }
        }
        // 提现
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception e) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        if (coinType.getShortName().equals("BTK") && MathUtils.toScaleNum(withdrawAmount, MathUtils.INTEGER_SCALE).compareTo(withdrawAmount) != 0) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10119"));
        }
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(withdrawAmount) < 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setUserId(user.getFid());
        order.setCoinId(coinType.getId());
        order.setCoinName(coinType.getName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_OUT);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        order.setAddressBindId(withdrawAddr);
        order.setAmount(withdrawAmount);
        order.setTradePass(tradePwd);
        order.setPhoneCode(phoneCode);
        order.setGoogleCode(totpCode);
        order.setIp(ip);
        order.setRisk(risk);
        order.setNetworkFees(BTCFees);
        order.setMemo(StringUtils.isEmpty(memo) ? null : memo);
        try {
            Result result = userCapitalService.createCoinOperationOrder(order);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("withdrawBtcSubmit {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("withdrawBtcSubmit error");
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }

    /**
     * 虚拟币提现取消
     */
    @ResponseBody
    @RequestMapping(value = "/withdraw/coin_cancel")
    public ReturnResult cancelWithdrawBtc(@RequestParam Integer id) throws Exception {
        FUser user = getCurrentUserInfoByToken();
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        try {
            Result result = userCapitalService.cancleCoinOperationOrder(user.getFid(), id, ip);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.coin.withdraw.cancle." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("cancelWithdrawBtc {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.cancle." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            logger.error("cancelWithdrawBtc error");
            e.printStackTrace();
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }

    /**
     * 获取虚拟币充值地址
     */
    @ResponseBody
    @RequestMapping(value = "/withdraw/coin_address")
    public ReturnResult getVirtualAddress(
            @RequestParam(required = true) int symbol) throws Exception {
        try {
            FUser fuser = getCurrentUserInfoByToken();
            fuser = userService.selectUserById(fuser.getFid());

            SystemCoinType coinType = redisHelper.getCoinType(symbol);
            if (coinType == null || !coinType.getIsRecharge() || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
                return ReturnResult.FAILUER(GetR18nMsg("financial.err.1002"));
            }
            String ip = getIpAddr();
            Result result = userCapitalAccountService.createCoinAddressRecharge(fuser.getFid(), coinType.getId(), ip);
            if (result.getCode() == 1000) {
                return ReturnResult.FAILUER(GetR18nMsg("financial.err.1003"));
            }
            return ReturnResult.SUCCESS(200, result.getData());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
        }
    }

    /**
     * 获取系统充值银行信息
     */
    @ResponseBody
    @RequestMapping(value = "/deposit/getsysbankinfo")
    public ReturnResult getSysBankInfo(
            @RequestParam Integer bankid) {
        JSONObject jsonObject = new JSONObject();
        FUser user = super.getCurrentUserInfoByToken();
        jsonObject.put("userId", user.getFid());
        // bank info
        FSystemBankinfoRecharge bankinfo = redisHelper.getRechargeBankById(bankid);
        if (bankinfo == null) {
            return ReturnResult.FAILUER("");
        }
        jsonObject.put("bankName", bankinfo.getFbankname());
        jsonObject.put("ownerName", bankinfo.getFownername());
        jsonObject.put("bankAddress", bankinfo.getFbankaddress());
        jsonObject.put("bankNumber", bankinfo.getFbanknumber());
        return ReturnResult.SUCCESS(jsonObject);
    }


    /**
     * RMB充值
     */
    @ResponseBody
    @RequestMapping("/deposit/cny_deposit_json")
    public ReturnResult cnyDeposit(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage
    ) {
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsRecharge()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            coinType = redisHelper.getCoinTypeIsRechargeFirst(SystemCoinTypeEnum.CNY.getCode());
            if (coinType == null) {
                return ReturnResult.FAILUER("");
            }
        }
        // 系统银行账号
        Integer rechargeType = 0;
        List<FSystemBankinfoRecharge> systemBankinfoRecharges = redisHelper.getRechargeBank(rechargeType);
        if (systemBankinfoRecharges == null || systemBankinfoRecharges.size() == 0) {
            return ReturnResult.FAILUER("");
        }
        // 用户转账的银行类型
        List<FSystemBankinfoWithdraw> systemBankinfoWithdraws = redisHelper.getWithdrawBankList();
        // 用户
        FUser fuser = getCurrentUserInfoByToken();
        // 用户银行卡
        List<FUserBankinfoDTO> userBankinfo = userCapitalAccountService.listBankInfo(fuser.getFid(), coinType.getId(), rechargeType);
        // 随机数小数
        int randomDecimal = (new Random().nextInt(80) + 11);
        // 充值记录
        Pagination<FWalletCapitalOperationDTO> page = new Pagination<>(currentPage,
                Constant.CapitalRecordPerPage, "/deposit/cny_deposit.html?symbol=" + symbol + "&");
        FWalletCapitalOperationDTO operation = new FWalletCapitalOperationDTO();
        operation.setFuid(fuser.getFid());
        operation.setFinouttype(CapitalOperationInOutTypeEnum.IN.getCode());
        operation.setFcoinid(coinType.getId());
        page = userCapitalService.listWalletCapitalOperation(page, operation);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("minRecharge", WebConstant.MINRECHARGECNY);
        jsonObject.put("maxRecharge", WebConstant.MAXRECHARGECNY);
        jsonObject.put("randomDecimal", randomDecimal);
        jsonObject.put("rechargeType", rechargeType);
        jsonObject.put("page", page);
        jsonObject.put("telephone", fuser.getFtelephone());
        jsonObject.put("realname", fuser.getFrealname());
        jsonObject.put("isTelephone", fuser.getFistelephonebind());
        jsonObject.put("isGoogle", fuser.getFgooglebind());
        jsonObject.put("systemBankinfoWithdraws", systemBankinfoWithdraws);
        jsonObject.put("systemBankinfoRecharges", systemBankinfoRecharges);
        jsonObject.put("userBankinfo", userBankinfo);
        jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
        jsonObject.put("cnyList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.CNY.getCode()), SystemCoinTypeVO.class));
        jsonObject.put("coinList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class));
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 人民币提现
     */
    @ResponseBody
    @RequestMapping("/withdraw/cny_withdraw_json")
    public ReturnResult withdrawCny(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        // 币种
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            coinType = redisHelper.getCoinTypeIsWithdrawFirst(SystemCoinTypeEnum.CNY.getCode());
            if (coinType == null) {
                return ReturnResult.FAILUER("");
            }
        }
        // 用户
        FUser fuser = getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());
        // 体现银行
        List<FUserBankinfoDTO> fbankinfoWithdraw = userCapitalAccountService.listBankInfo(fuser.getFid(), coinType.getId(), 0);
        String currentLocal = LuangeHelper.getLan(sessionContextUtils.getContextRequest());
        for (FUserBankinfoDTO bankinfo : fbankinfoWithdraw) {
            FSystemBankinfoWithdraw item = redisHelper.getWithdrawBankById(bankinfo.getFbanktype());
            if (currentLocal.equals(LocaleEnum.ZH_CN.getName())) {
                bankinfo.setFbanktype_s(item.getFcnname());
            } else if (currentLocal.equals(LocaleEnum.ZH_TW.getName())) {
                bankinfo.setFbanktype_s(item.getFtwname());
            } else if (currentLocal.equals(LocaleEnum.EN_US.getName())) {
                bankinfo.setFbanktype_s(item.getFenname());
            }
        }
        //用户转账的银行类型
        List<FSystemBankinfoWithdraw> bankTypes = redisHelper.getWithdrawBankList();
        // 提现记录
        Pagination<FWalletCapitalOperationDTO> page = new Pagination<FWalletCapitalOperationDTO>(currentPage,
                Constant.CapitalWithdrawPerPage, "/withdraw/cny_withdraw.html?symbol=" + symbol + "&");
        FWalletCapitalOperationDTO operation = new FWalletCapitalOperationDTO();
        operation.setFuid(fuser.getFid());
        operation.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
        page = userCapitalService.listWalletCapitalOperation(page, operation);
        // 钱包
        UserCoinWallet userWallet = userWalletService.getUserCoinWallet(fuser.getFid(), coinType.getId());
        // VIP设置
        SystemCoinSetting withdrawSetting = redisHelper.getCoinSetting(coinType.getId(), fuser.getLevel());
        // 界面数据

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("withdrawSetting", withdrawSetting);
        jsonObject.put("wallet", userWallet);
        jsonObject.put("page", page);
        jsonObject.put("bankTypes", bankTypes);
        jsonObject.put("fbankinfoWithdraw", fbankinfoWithdraw);
        jsonObject.put("fuser", ModelMapperUtils.mapper(fuser, FUserDTO.class));
        jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
        jsonObject.put("cnyList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsWithdrawList(SystemCoinTypeEnum.CNY.getCode()), SystemCoinTypeVO.class));
        jsonObject.put("coinList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsWithdrawList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class));
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 虚拟币充值
     *
     * @param symbol
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/deposit/coin_deposit_json")
    public ReturnResult rechargeBtc(@RequestParam(required = false, defaultValue = "0") Integer symbol) throws Exception {
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsRecharge()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            //coinType = redisHelper.getCoinTypeIsRechargeFirst(SystemCoinTypeEnum.COIN.getCode());
            //if (coinType == null) {
                return ReturnResult.FAILUER("暂停充值");
            //}
        }
        FUser user = getCurrentUserInfoByToken();
        // 充值地址
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        Result result = userCapitalAccountService.createCoinAddressRecharge(user.getFid(), coinType.getId(), ip);
        JSONObject jsonObject = new JSONObject();
        if (result.getSuccess()) {
            jsonObject.put("rechargeAddress", result.getData());
        }
        // 最近十次充值记录
        Pagination<FVirtualCapitalOperationDTO> page = new Pagination<>(1, Constant.CapitalRecordPerPage);
        FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();
        operation.setFuid(user.getFid());
        operation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
        operation.setFcoinid(coinType.getId());
        page = userCapitalService.listVirtualCapitalOperation(page, operation);
        jsonObject.put("page", page);
        jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
        jsonObject.put("cnyList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.CNY.getCode()), SystemCoinTypeVO.class));
        jsonObject.put("coinList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class));
        if (coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())) {
            jsonObject.put("memo",user.getFid());
        }
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 虚拟币提现
     */
    @ResponseBody
    @RequestMapping("/withdraw/coin_withdraw_json")
    public ReturnResult withdrawBtc(
            @RequestParam(required = false, defaultValue = "0") Integer symbol) {
        // 币种查找
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
           return ReturnResult.FAILUER("禁止提现");
        }
        // 用户
        FUser user = getCurrentUserInfoByToken();
        // 钱包
        UserCoinWallet userWallet = userWalletService.getUserCoinWallet(user.getFid(), coinType.getId());
        // 地址
        List<FUserVirtualAddressWithdrawDTO> withdrawAddress = userCapitalAccountService.listCoinAddressWithdraw(user.getFid(), coinType.getId());
        // VIP设置
        SystemCoinSetting withdrawSetting = redisHelper.getCoinSetting(coinType.getId(), user.getLevel());
        // 近10条提现记录
        Pagination<FVirtualCapitalOperationDTO> page = new Pagination<FVirtualCapitalOperationDTO>(1, Constant.webPageSize,
                "/withdraw/coin_withdraw_json.html?symbol" + symbol + "&");
        FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();
        operation.setFuid(user.getFid());
        operation.setFtype(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode());
        operation.setFcoinid(coinType.getId());
        page = userCapitalService.listVirtualCapitalOperation(page, operation);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", page);
        jsonObject.put("fuser", ModelMapperUtils.mapper(user, FUserDTO.class));
        jsonObject.put("userWallet", userWallet);
        jsonObject.put("withdrawAddress", withdrawAddress);
        jsonObject.put("withdrawSetting", withdrawSetting);
        jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
        jsonObject.put("cnyList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.CNY.getCode()), SystemCoinTypeVO.class));
        jsonObject.put("coinList", ModelMapperUtils.mapper(
                redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.COIN.getCode()), SystemCoinTypeVO.class));
        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @RequestMapping("/financial/record_json")
    public ReturnResult record(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false, defaultValue = "1") Integer type,
            @RequestParam(required = false, defaultValue = "") String begindate,
            @RequestParam(required = false, defaultValue = "") String enddate,
            @RequestParam(required = false, defaultValue = "2") Integer datetype) throws Exception {

        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            coinType = redisHelper.getCoinTypeFirst();
            if (coinType == null) {
                return ReturnResult.FAILUER("");
            }
            symbol = coinType.getId();
        }
        if (datetype > 0) {
            if (datetype == 1) {
                begindate = Utils.getCurTimeString(0);
                enddate = Utils.getCurTimeString(0);
            }
            if (datetype == 2) {
                begindate = Utils.getCurTimeString(-7);
                enddate = Utils.getCurTimeString(0);
            }
            if (datetype == 3) {
                begindate = Utils.getCurTimeString(-15);
                enddate = Utils.getCurTimeString(0);
            }
            if (datetype == 4) {
                begindate = Utils.getCurTimeString(-30);
                enddate = Utils.getCurTimeString(0);
            }
        }


        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeList();
        // 过滤器
        List<KeyValues> filters = new ArrayList<KeyValues>();

        String keyFormat = "/financial/record.html";
        for (SystemCoinType systemCoinType : coinTypes) {
            String valueR = GetR18nMsg("financial.account.recordselectr");
            if (!StringUtils.isEmpty(valueR)) {
                KeyValues keyValues = new KeyValues();
                keyValues.setKey(keyFormat + "?symbol=" + systemCoinType.getId() + "&type=1");
                keyValues.setValue(String.format(valueR, systemCoinType.getShortName()));
                filters.add(keyValues);
            }
            String valueW = GetR18nMsg("financial.account.recordselectw");
            if (!StringUtils.isEmpty(valueR)) {
                KeyValues keyValues = new KeyValues();
                keyValues.setKey(keyFormat + "?symbol=" + systemCoinType.getId() + "&type=2");
                keyValues.setValue(String.format(valueW, systemCoinType.getShortName()));
                filters.add(keyValues);
            }

        }
        JSONObject jsonObject = new JSONObject();
        FUser fuser = getCurrentUserInfoByToken();
        if (coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            FWalletCapitalOperationDTO operation = new FWalletCapitalOperationDTO();
            operation.setFuid(fuser.getFid());
            operation.setFinouttype(type);
            operation.setFcoinid(symbol);
            Pagination<FWalletCapitalOperationDTO> page = new Pagination<>(currentPage,
                    1, begindate, enddate + " 23:59:59",
                    "/financial/record.html?symbol=" + symbol + "&type=" + type + "&datetype=" + datetype + "&begindate="
                            + begindate + "&enddate=" + enddate + "&");
            /*Pagination<FWalletCapitalOperationDTO> page = new Pagination<>(currentPage,
                    Constant.CapitalRecordPerPage, begindate, enddate + " 23:59:59",
                    "/financial/record.html?symbol=" + symbol + "&type=" + type + "&datetype=" + datetype + "&begindate="
                            + begindate + "&enddate=" + enddate + "&");*/
            page = userCapitalService.listWalletCapitalOperation(page, operation);
            jsonObject.put("list", page);
            jsonObject.put("recordType", SystemCoinTypeEnum.CNY.getCode());
        } else if (coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();
            operation.setFuid(fuser.getFid());
            operation.setFtype(type);
            operation.setFcoinid(coinType.getId());
            operation.setFcoinid(symbol);
            Pagination<FVirtualCapitalOperationDTO> page = new Pagination<FVirtualCapitalOperationDTO>(currentPage,
                    Constant.CapitalRecordPerPage, begindate, enddate + " 23:59:59",
                    "/financial/record.html?type=" + type + "&symbol=" + symbol + "&datetype=" + datetype + "&begindate="
                            + begindate + "&enddate=" + enddate + "&");
            page = userCapitalService.listVirtualCapitalOperation(page, operation);
            jsonObject.put("list", page);
            jsonObject.put("recordType", SystemCoinTypeEnum.COIN.getCode());
        }
        String select = "";
        if (type == 1) {
            select = GetR18nMsg("financial.account.recordselectr");
        }
        if (type == 2) {
            select = GetR18nMsg("financial.account.recordselectw");
        }
        jsonObject.put("select", String.format(select, coinType.getShortName()));
        jsonObject.put("type", type);
        jsonObject.put("symbol", symbol);
        jsonObject.put("filters", filters);
        jsonObject.put("symbol", symbol);
        jsonObject.put("begindate", begindate);
        jsonObject.put("enddate", enddate);
        jsonObject.put("datetype", datetype);
        jsonObject.put("fvirtualcointype", ModelMapperUtils.mapper(coinTypes, SystemCoinTypeVO.class));
        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @RequestMapping("/financial/commission_json")
    public ReturnResult commission(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeList();

        FUser fuser = getCurrentUserInfoByToken();

        Pagination<CommissionRecord> commissionRecordPagination = userCapitalService
                .listCommissionRecord(new Pagination<CommissionRecord>(currentPage, Constant.CapitalRecordPerPage)
                        , fuser.getFid(), null);

        Collection<CommissionRecord> data = commissionRecordPagination.getData();
        Iterator<CommissionRecord> iterator = data.iterator();

        while (iterator.hasNext()) {
            CommissionRecord next = iterator.next();
            for (SystemCoinType coinType : coinTypes) {
                if (coinType.getId() == next.getCoinid()) {
                    next.setCoinname(coinType.getShortName().toUpperCase());
                    break;
                }
            }
        }


        int count = userService.selectIntroUserCount(fuser.getFid());

        //统计买方币种的收益

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("symbol", 33);
        jsonObject1.put("shortName", "USDT");
        jsonObject1.put("amount", userCapitalService.getCommissionTotalByID(fuser.getFid(), 33).toPlainString());
        jsonArray.add(0, jsonObject1);

        //BTC
        jsonObject1 = new JSONObject();
        jsonObject1.put("symbol", 32);
        jsonObject1.put("shortName", "BTC");
        jsonObject1.put("amount", userCapitalService.getCommissionTotalByID(fuser.getFid(), 32).toPlainString());
        jsonArray.add(1, jsonObject1);

        //ETH
        jsonObject1 = new JSONObject();
        jsonObject1.put("symbol", 31);
        jsonObject1.put("shortName", "ETH");
        jsonObject1.put("amount", userCapitalService.getCommissionTotalByID(fuser.getFid(), 31).toPlainString());
        jsonArray.add(2, jsonObject1);

        jsonObject1 = new JSONObject();
        jsonObject1.put("symbol", 37);
        jsonObject1.put("shortName", "CC");
        jsonObject1.put("amount", userCapitalService.getCommissionTotalByID(fuser.getFid(), 37).toPlainString());
        jsonArray.add(3, jsonObject1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("record", jsonArray);
        jsonObject.put("introCount", count);
        jsonObject.put("list", commissionRecordPagination);
        jsonObject.put("introurl", "/user/intro.html?intro=" + fuser.getFid());
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 操作记录
     */
    @ResponseBody
    @RequestMapping("/financial/operation/record")
    public ReturnResult operationRecord(@RequestParam(required = false, defaultValue = "1") Integer currentPage) {
        FUser fuser = getCurrentUserInfoByToken();
        if (fuser == null){
            return ReturnResult.FAILUER("");
        }

        Pagination<FinanceRecordDTO> pagination = new Pagination<>(currentPage,Constant.webPageSize,"/index.php?s=exc&c=financeController&m=record&");
        pagination.setOrderField("fid");
        pagination.setOrderDirection("desc");
        pagination = userCapitalService.listFinanceRecord(pagination, fuser.getFid(),null,null);
        return ReturnResult.SUCCESS(pagination);
    }
}
