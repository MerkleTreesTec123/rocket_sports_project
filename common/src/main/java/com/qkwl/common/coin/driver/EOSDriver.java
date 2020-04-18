package com.qkwl.common.coin.driver;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class EOSDriver implements CoinDriver {

    private static final long serialVersionUID = 1L;

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private String accountName = "ufoufo.e";
    private int contractWei = 0;
    private String account = null;
    private String code = null;

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setCoinSort(Integer coinSort) {
        this.coinSort = coinSort;
    }

    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }

    public String getContractAccount() {
        return contractAccount;
    }

    public void setContractAccount(String contractAccount) {
        this.contractAccount = contractAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getContractWei() {
        return contractWei;
    }

    public void setContractWei(int contractWei) {
        this.contractWei = contractWei;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /***
     *
     * @param accessKey account 代币账户
     * @param secretKey code eosio.token
     * @param ip
     * @param port
     * @param pass
     * @param coinSort
     * @param sendAccount main-account
     * @param contractAccount symbol  代币符号 EOS
     * @param contractWei
     */
    public EOSDriver(String accessKey, String secretKey, String ip, String port, String pass, Integer coinSort,
                     String sendAccount, String contractAccount, int contractWei) {
        coinUtils = new CoinUtils(accessKey, secretKey, ip, port);
        this.account = accessKey;
        this.code = secretKey;
        this.passWord = pass;
        this.coinSort = coinSort;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
        this.contractWei = contractWei;
    }

    @Override
    public BigDecimal getBalance() {
        HashMap<String,String> params = new HashMap<>();
        params.put("account",sendAccount);
        params.put("code",code);
        params.put("symbol",contractAccount);
        JSONObject jsonObject = coinUtils.goEOS(params, "/v1/chain/get_currency_balance");
        if (jsonObject.getIntValue("code") != 200) {
            return null;
        }
        return jsonObject.getJSONObject("data").getBigDecimal("amount");
    }   

    @Override
    public String getNewAddress(String time) {
        return null;
    }

    @Override
    public boolean walletLock() {
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        return true;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        return true;
    }

    @Override
    public List<TxInfo> listTransactions(int to, int from) {
        return null;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        HashMap<String,String> params = new HashMap<>();
        params.put("id",txId);
        JSONObject jsonObject = coinUtils.goEOS(params, "/v1/history/get_transaction");
        if (jsonObject.getIntValue("code") != 200) {
            return null;
        }
        TxInfo txInfo = new TxInfo();
        txInfo.setTxid(txId);
        txInfo.setBlockNumber(jsonObject.getJSONObject("data").getInteger("block_num"));
        return txInfo;
    }

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
        return null;
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
        return null;
    }

    @Override
    public String sendToAddress(String to, String amount, String memo) {
        HashMap<String,String> params = new HashMap<>();
        params.put("account",this.account);
        params.put("actor",this.sendAccount);
        params.put("to",to);
        params.put("amount",amount);
        params.put("memo",memo);
        params.put("symbol",contractAccount);
        params.put("password",this.passWord);
        JSONObject jsonObject = coinUtils.goEOS(params, "/v1/wallet/transfer");
        //System.out.println(jsonObject.toJSONString());
        if (jsonObject.getIntValue("code") != 200) {
            System.out.println(jsonObject.toJSONString());
            return null;
        }
        return jsonObject.getJSONObject("data").getString("transaction_id");
    }

    @Override
    public String getETCSHA3(String str) {
        return null;
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public Integer getBestHeight() {
        return 0;
    }

    @Override
    public Integer getTransactionCount() {
        return null;
    }
}
