package com.qkwl.service.coin.service;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Utils;
import com.qkwl.service.coin.mapper.*;
import com.qkwl.service.coin.util.JobUtils;
import com.qkwl.service.coin.util.MQSend;
import com.qkwl.service.common.mapper.CommonUserCoinWalletMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("coinService")
@Scope("prototype")
public class CoinService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(CoinService.class);

    @Autowired
    private FVirtualCapitalOperationMapper fVirtualCapitalOperationMapper;
    @Autowired
    private FWalletCapitalOperationMapper fWalletCapitalOperationMapper;
    @Autowired
    private FUserVirtualAddressMapper fUserVirtualAddressMapper;
    @Autowired
    private FUserMapper userMapper;
    @Autowired
    private CommonUserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private JobUtils jobUtils;
    @Autowired
    private ScoreHelper scoreHelper;
    @Autowired
    private ValidateHelper validateHelper;
    @Autowired
    private MQSend mqSend;
    @Autowired
    FinanceRecordMapper financeRecordMapper;

    /**
     * 定时创建充值订单
     * @param coinType 虚拟币
     * @throws Exception 执行异常
     */
    public void updateRecharge(SystemCoinType coinType) {
        int coinid = coinType.getId();

        int begin = 0;
        int step = 100;
        boolean is_continue = true;

        String accesskey = coinType.getAccessKey();
        String secretkey = coinType.getSecrtKey();
        String ip = coinType.getIp();
        String port = coinType.getPort();

        // get CoinDriver
        CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), ip, port)
                .accessKey(accesskey)
                .secretKey(secretkey)
                .assetId(coinType.getAssetId())
                .sendAccount(coinType.getEthAccount())
                .builder()
                .getDriver();

        if (coinDriver == null) {
            return;
        }
        List<TxInfo> txInfos;
        while (is_continue) {
            try {
                txInfos = coinDriver.listTransactions(step, begin);
                begin += step;
                //logger.info("{}:{size:{},begin:{},step:{}}", coinType.getShortName(), txInfos.size(), begin, step);
                if (txInfos == null || txInfos.size() == 0) {
                    is_continue = false;
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("updateRecharge listTransactions error");
                is_continue = false;
                continue;
            }
            for (TxInfo txInfo : txInfos) {
                String txid = txInfo.getTxid().trim();

                Date date = Utils.getCurTimeString("2017-08-06 17:40:00");
                if (txInfo.getTime() != null && txInfo.getTime().before(date)) {
                    continue;
                }

                // BTC类 特殊处理
                if (coinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())) {
                    if ("BTC".equals(coinType.getShortName()) ||
                            "LTC".equals(coinType.getShortName()) ||
                            "BCC".equals(coinType.getShortName())) {
                        txid = txid + "_" + txInfo.getVout();
                    } else {
                        txid = txid + "_" + txInfo.getAddress().trim();
                    }
                } else if (coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())
                        || coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())) {
                    txid = txid + "_" + txInfo.getAddress().trim() + "_" + coinType.getAssetId();
                } else if (coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
                    txid = txid + "_" + txInfo.getAddress() + "_" + txInfo.getVout();
                } 

                // 判断操作记录
                List<FVirtualCapitalOperationDTO> fvirtualcaptualoperations = this.fVirtualCapitalOperationMapper.selectByTxid(txid);
                if (fvirtualcaptualoperations.size() > 0) {
                    continue;
                }

                //logger.info("----> btc 正在循环读取每一条记录 : " + txid);

                FVirtualCapitalOperationDTO fvirtualcaptualoperation = new FVirtualCapitalOperationDTO();

                boolean hasOwner = true;
                String address = txInfo.getAddress().trim();
                Integer baseCoinId = coinid;
                if(coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())){
                    SystemCoinType icsCoinType = jobUtils.getCoinTypeShortName("ICS");
                    if(icsCoinType == null){
                        logger.error("ICS coinType is null");
                        continue;
                    }
                    baseCoinId = icsCoinType.getId();
                }
                if(coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())){
                    SystemCoinType icsCoinType = jobUtils.getCoinTypeShortName("MIC");
                    if(icsCoinType == null){
                        logger.error("MIC coinType is null");
                        continue;
                    }
                    baseCoinId = icsCoinType.getId();
                }

                // 判断用户是否存在
                if (coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
                    FUser fUser = userMapper.selectByPrimaryKey(txInfo.getUid());
                    if (fUser == null) {
                        txInfo.setUid(null);
                    }
                } else {
                    FUserVirtualAddressDTO fvirtualaddresse = this.fUserVirtualAddressMapper.selectByCoinAndAddress(baseCoinId, address);
                    if (fvirtualaddresse == null) {
                        txInfo.setUid(null);
                    } else {
                        txInfo.setUid(fvirtualaddresse.getFuid());
                    }
                }

                if (txInfo.getUid() != null) {
                    fvirtualcaptualoperation.setFuid(txInfo.getUid());
                } else {
                    hasOwner = false;// 没有这个地址，充错进来了？没收！
                }

                fvirtualcaptualoperation.setFamount(txInfo.getAmount());
                fvirtualcaptualoperation.setFfees(BigDecimal.ZERO);
                fvirtualcaptualoperation.setFcoinid(coinid);
                fvirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
                fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
                fvirtualcaptualoperation.setFhasowner(hasOwner);
                fvirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
                fvirtualcaptualoperation.setFblocknumber(0);
                fvirtualcaptualoperation.setFconfirmations(0);
                fvirtualcaptualoperation.setFrechargeaddress(txInfo.getAddress().trim());
                fvirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
                fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
                fvirtualcaptualoperation.setVersion(0);
                fvirtualcaptualoperation.setFsource(DataSourceEnum.WEB.getCode());
                fvirtualcaptualoperation.setFuniquenumber(txid);
                if (txInfo.getBlockNumber() != null) {
                    fvirtualcaptualoperation.setFblocknumber(txInfo.getBlockNumber());
                }
                if (txInfo.getTime() != null) {
                    fvirtualcaptualoperation.setTxTime(txInfo.getTime());
                }
                int result = this.fVirtualCapitalOperationMapper.insert(fvirtualcaptualoperation);
                if (result <= 0) {
                    logger.error("Coin updateRecharge insert failed");
                }
            }
        }
    }

    /**
     * 定时刷新确认数
     * @param coinType 虚拟币
     * @throws Exception 执行异常
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateCoinCome(SystemCoinType coinType) throws Exception {
        int coinid = coinType.getId();

        String accesskey = coinType.getAccessKey();
        String secretkey = coinType.getSecrtKey();
        String ip = coinType.getIp();
        String port = coinType.getPort();

        // get CoinDriver
        CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), ip, port)
                .accessKey(accesskey)
                .secretKey(secretkey)
                .assetId(coinType.getAssetId())
                .sendAccount(coinType.getEthAccount())
                .builder()
                .getDriver();

        if(coinDriver == null){
            return;
        }
        List<FVirtualCapitalOperationDTO> fVirtualCapitalOperations = fVirtualCapitalOperationMapper.seletcGoing(coinid, coinType.getConfirmations());

        // 遍历
        for (FVirtualCapitalOperationDTO fvirtualcaptualoperation : fVirtualCapitalOperations) {
            if (fvirtualcaptualoperation == null) {
                continue;
            }
            // 确认数
            int Confirmations = 0;
            // 充值数量
            //BigDecimal Amount = BigDecimal.ZERO;
            // txid
            String[] txids = fvirtualcaptualoperation.getFuniquenumber().split("_");
            String txid = txids[0];
            if (coinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode()) || coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())) {
                // 获取区块高度并更新
                if (fvirtualcaptualoperation.getFblocknumber() <= 0) {
                    TxInfo etcInfo = coinDriver.getTransaction(txid);
                    if (etcInfo != null) {
                        fvirtualcaptualoperation.setFblocknumber(etcInfo.getBlockNumber());
                        if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
                            logger.error("updatate key failed : " + etcInfo.getBlockNumber());
                            throw new Exception();
                        }
                    }
                    continue;
                }
                // 获取确认数,数量
                int blockNumberCreate = fvirtualcaptualoperation.getFblocknumber();
                Confirmations = coinDriver.getBestHeight() - blockNumberCreate;
                //Amount = fvirtualcaptualoperation.getFamount();
            } else if (coinType.getCoinType().equals(SystemCoinSortEnum.ETP.getCode()) ||
                       coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
                int height =  coinDriver.getBestHeight();
                Confirmations = height - fvirtualcaptualoperation.getFblocknumber();
                //Amount = fvirtualcaptualoperation.getFamount();
            } else if (coinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())
            || coinType.getCoinType().equals(SystemCoinSortEnum.USDT.getCode())) {
                // BTC处理
                TxInfo btcInfo = coinDriver.getTransaction(txid);
                if (btcInfo == null) {
                    continue;
                }
                Confirmations = btcInfo.getConfirmations();
                //Amount = btcInfo.getAmount();
            } else if (coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())
                    || coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())) {
                TxInfo btcInfo = coinDriver.getTransaction(txid);
                if (btcInfo == null) {
                    continue;
                }
                // 资产类型，3小企股转账，5其它资产转账
                if(!btcInfo.getType().equals(3) && !btcInfo.getType().equals(5)){
                    continue;
                }
                Confirmations = btcInfo.getConfirmations();
                //Amount = btcInfo.getAmount();
            }else if (coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())) {
                TxInfo transaction = coinDriver.getTransaction(txid);
                if (transaction != null && txid.equals(transaction.getTxid())) {
                    Confirmations = 1;
                }
            }

            if (Confirmations > 0 && Confirmations > fvirtualcaptualoperation.getFconfirmations()) {
                //logger.info("----> 确认数 : " + Confirmations);
                //fvirtualcaptualoperation.setFamount(Amount);
                fvirtualcaptualoperation.setFconfirmations(Confirmations);
                fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
                // 确认状态
                if (fvirtualcaptualoperation.getFstatus() != VirtualCapitalOperationInStatusEnum.SUCCESS) {
                    if (Confirmations >= coinType.getConfirmations()) {
                        // 更新钱包
                        UserCoinWallet userCoinWallet = userCoinWalletMapper.selectByUidAndCoinId(fvirtualcaptualoperation.getFuid(), fvirtualcaptualoperation.getFcoinid());
                        if (userCoinWallet == null) {
                            continue;
                        }
                        userCoinWallet.setTotal(fvirtualcaptualoperation.getFamount());
                        userCoinWallet.setFrozen(BigDecimal.ZERO);
                        userCoinWallet.setGmtModified(new Date());
                        if (this.userCoinWalletMapper.changeFinance(userCoinWallet) <= 0) {
                            throw new Exception();
                        }
                        boolean isFirstRecharge = isFirstCharge(fvirtualcaptualoperation.getFuid());
                        // 更新订单
                        fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);
                        if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
                            throw new Exception();
                        }
                        BigDecimal last = jobUtils.getLastPrice(fvirtualcaptualoperation.getFcoinid());
                        BigDecimal amount = MathUtils.mul(fvirtualcaptualoperation.getFamount(), last);

                        scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), amount, ScoreTypeEnum.RECHARGE.getCode()
                                , "充值" + coinType.getShortName() + ":" + fvirtualcaptualoperation.getFamount());
                        // 首次充值奖励
                        if(isFirstRecharge) {
                            scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), BigDecimal.ZERO, ScoreTypeEnum.FIRSTCHARGE.getCode(), ScoreTypeEnum.FIRSTCHARGE.getValue().toString());
                        }
                        mqSend.SendUserAction(fvirtualcaptualoperation.getFagentid(), fvirtualcaptualoperation.getFuid(),
                                LogUserActionEnum.COIN_RECHARGE, fvirtualcaptualoperation.getFcoinid(), 0,
                                fvirtualcaptualoperation.getFamount());
                        // 风控短信
                        if (fvirtualcaptualoperation.getFamount().compareTo(coinType.getRiskNum()) >= 0) {
                            String riskphone = jobUtils.getSystemArgs(ArgsConstant.RISKPHONE);
                            String[] riskphones = riskphone.split("#");
                            //这里改成通知用户的短信即可
                            if (riskphones.length > 0) {
                                FUser fuser = userMapper.selectByPrimaryKey(fvirtualcaptualoperation.getFuid());
                                for (String string : riskphones) {
                                    try {
                                        validateHelper.smsRiskManage(fuser.getFloginname(), string, PlatformEnum.BC.getCode(),
                                                BusinessTypeEnum.SMS_RISKMANAGE.getCode(), "充值",
                                                fvirtualcaptualoperation.getFamount(), coinType.getName());
                                    } catch (Exception e) {
                                        logger.error("updateCoinCome riskphones err");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
                        financeRecordDTO.setAmount(fvirtualcaptualoperation.getFamount());
                        financeRecordDTO.setCreateDate(new Date());
                        financeRecordDTO.setUpdateDate(new Date());
                        financeRecordDTO.setUid(fvirtualcaptualoperation.getFuid());
                        financeRecordDTO.setOperation(FinanceRecordOperationEnum.In.getCode());
                        financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
                        financeRecordDTO.setRelationCoinId(fvirtualcaptualoperation.getFcoinid());
                        financeRecordDTO.setRelationCoinName("");
                        financeRecordDTO.setRelationId(fvirtualcaptualoperation.getFid());
                        financeRecordDTO.setTxId(fvirtualcaptualoperation.getFuniquenumber());
                        financeRecordDTO.setRechargeAddress(fvirtualcaptualoperation.getFrechargeaddress());
                        financeRecordDTO.setWithdrawAddress("");
                        financeRecordDTO.setMemo(fvirtualcaptualoperation.getMemo());
                        financeRecordDTO.setFee(BigDecimal.ZERO);
                        financeRecordDTO.setWalletOperationDate(new Date());

                        financeRecordMapper.insert(financeRecordDTO);

                    } else {
                        if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
                            throw new Exception();
                        }
                    }
                } else {
                    if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
                        throw new Exception();
                    }
                }
            }
        }
    }

    /**
     * 首次充值判断
     * @param fuid
     * @return
     */
    public boolean isFirstCharge(int fuid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fuid", fuid);
        map.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
        map.put("fstatus", CapitalOperationInStatus.Come);
        int countCny = fWalletCapitalOperationMapper.countWalletCapitalOperation(map);
        if(countCny > 0){
            return false;
        }

        map.clear();
        map.put("fuid", fuid);
        map.put("ftype", VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
        map.put("fstatus", VirtualCapitalOperationInStatusEnum.SUCCESS);
        int countCoin = fVirtualCapitalOperationMapper.countVirtualCapitalOperation(map);

        return (countCny + countCoin) <= 0;
    }
}
