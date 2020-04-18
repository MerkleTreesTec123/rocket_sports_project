package com.qkwl.common.coin.driver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by janyw on 2017/6/26.
 */
public class ETPDriver implements CoinDriver {

    private static final int DEF_DIV_SCALE = 10;
    private static final BigDecimal WEI = new BigDecimal("100000000");

    private CoinUtils coinUtils = null;
    private String accessKey = null;
    private String secretKey = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String account = null;
    private String accPwd = null;

    public ETPDriver(String accessKey, String secretKey, String ip, String port, String pass, Integer coinSort, String account) {
        coinUtils = new CoinUtils(accessKey, secretKey, ip, port);
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.passWord = pass;
        this.coinSort = coinSort;
        this.account = account;
        this.accPwd = "[\"" + accessKey + "\",\"" + secretKey + "\"]";
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public Integer getBestHeight() {
        JSONObject resultJson = coinUtils.goETP("fetch-height", "[]");
        return resultJson.getInteger("result");
    }

    @Override
    public BigDecimal getBalance() {
        JSONObject resultJson = coinUtils.goETP("getbalance",accPwd);
        return getValue(resultJson.getString("total-unspent"));
    }

    @Override
    public String getNewAddress(String uId) {
        JSONObject resultJson = coinUtils.goETP("getnewaddress",accPwd);
        return resultJson.getString("result");
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
        return false;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
        if (from != 0) {
            return null;
        } else {
            int best = getBestHeight();
            from = best - count;
            count = best;
        }
        JSONObject resultJson = coinUtils.goETP("listtxs", "[\"" + "-e" + "\",\"" + from + ":" + count + "\",\"" +accessKey + "\",\"" + secretKey + "\"]");
        JSONArray txs = resultJson.getJSONArray("transactions");
        if (txs == null || txs.size() == 0) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<>();
        for (int i = 0; i < txs.size(); i++) {
            JSONObject tx = txs.getJSONObject(i);
            if (!tx.getString("direction").equals("receive")) {
                continue;
            }
            JSONObject out = getOut(tx.getJSONArray("outputs"));
            if (out == null) {
                continue;
            }
            String type = out.getJSONObject("attachment").getString("type");
            if (type == null || !type.equals("etp")) {
                continue;
            }
            TxInfo txInfo = new TxInfo();
            txInfo.setTxid(tx.getString("hash"));
            txInfo.setAddress(out.getString("address"));
            txInfo.setBlockNumber(tx.getInteger("height"));
            txInfo.setAmount(getValue(out.getString("etp-value")));
            long time = Long.parseLong(tx.get("timestamp").toString() + "000");
            txInfo.setTime(new Date(time));
            txInfos.add(txInfo);
        }
        Collections.reverse(txInfos);
        return txInfos;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        return null;
    }

    @Override
    public String sendToAddress(String address, BigDecimal account, String comment, BigDecimal fee) {
        return null;
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
        return null;
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        String amountTo = getMul(amount);
        String parms = "[\"" + accessKey + "\",\"" + secretKey + "\",\"" +
                account + "\",\"" + to  + "\",\"" + amountTo + "\"]";
        JSONObject resultJson = coinUtils.goETP("sendfrom", parms);
        return resultJson.getJSONObject("transaction").getString("hash");
    }

    @Override
    public String getETCSHA3(String str) {
        return null;
    }

    private JSONObject getOut(JSONArray outs) {
        for (int i = 0; i < outs.size(); i++) {
            JSONObject out = outs.getJSONObject(i);
            if (!out.getBoolean("own")) {
                continue;
            }
            return out;
        }
        return null;
    }

    private BigDecimal getValue(String value) {
        BigDecimal v = new BigDecimal(value);
        return v.divide(WEI, DEF_DIV_SCALE, BigDecimal.ROUND_DOWN);
    }

    private String getMul(String value) {
        BigDecimal v = new BigDecimal(value);
        return v.multiply(WEI).setScale(DEF_DIV_SCALE, BigDecimal.ROUND_DOWN).toBigInteger().toString();
    }

    @Override
    public Integer getTransactionCount() {
        return null;
    }
}
