package com.qkwl.common.coin.driver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.match.MathUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GXBDriver implements CoinDriver {

    private CoinUtils coinUtils = null;
    private String account = null;
    private String account_pwd = null;
    private String account_id = null;
    private Integer coinSort = null;
    private final static BigDecimal GXB_WEI = new BigDecimal("100000");
    private final static String ASSETID_GXB = "1.3.1";

    public GXBDriver(Integer coinSort, String accessKey, String secretKey, String ip, String port, String ethAccount) {
        coinUtils = new CoinUtils(ip, port);
        this.account = accessKey;
        this.account_pwd = secretKey;
        this.account_id = ethAccount;
        this.coinSort = coinSort;
    }

    @Override
    public Integer getCoinSort() {
        return coinSort;
    }

    @Override
    public Integer getBestHeight() {
        this.walletPassPhrase(0);
        JSONObject resultJson = coinUtils.goGXS("get_dynamic_global_properties","[]");
        if (resultJson == null) {
            return null;
        }
        JSONObject headJson = resultJson.getJSONObject("result");
        return headJson.getInteger("last_irreversible_block_num");
    }

    @Override
    public BigDecimal getBalance() {
        this.walletPassPhrase(0);
        JSONObject resultJson = coinUtils.goGXS("list_account_balances", "[\"" + this.account + "\"]");
        if (resultJson == null) {
            return null;
        }
        JSONArray jsonArray = resultJson.getJSONArray("result");
        for (Object object: jsonArray) {
            JSONObject amount = JSON.parseObject(object.toString());
            if (ASSETID_GXB.equals(amount.getString("asset_id"))) {
                return MathUtils.div(amount.getBigDecimal("amount"), GXB_WEI);
            }
        }
        return null;
    }

    @Override
    public String getNewAddress(String uId) {
        return null;
    }

    @Override
    public boolean walletLock() {
        return true;
    }

    @Override
    public boolean walletPassPhrase(int times) {
        coinUtils.goGXS("unlock", "[\"" + this.account_pwd + "\"]");
        return true;
    }

    @Override
    public boolean setTxFee(BigDecimal fee) {
        return false;
    }

    @Override
    public List<TxInfo> listTransactions(int count, int from) {
        this.walletPassPhrase(0);
        int end = from + count;
        String params = "[\"" + this.account_id + "\"," + from + "," + count + "," + end + "]";
        JSONObject resultJson = coinUtils.goGXS("get_relative_account_history", params);
        String result = resultJson.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        List<TxInfo> txInfos = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(result);
        for (Object object : jsonArray) {
            JSONObject txObject = JSON.parseObject(object.toString());
            if (!StringUtils.isNumeric(txObject.get("memo").toString())) {
                continue;
            }
            TxInfo txInfo = new TxInfo();
            txInfo.setUid(txObject.getInteger("memo"));

            JSONObject op1 = txObject.getJSONObject("op");
            JSONArray op2 = op1.getJSONArray("op");
            for (int i = 1; i < op2.size(); ++i) {
                JSONObject opObject = JSON.parseObject(op2.get(i).toString());
                if (this.account_id == null) {
                    return null;
                }
                if (!this.account_id.equals(opObject.getString("to"))) {
                    continue;
                }
                JSONObject amountObject = opObject.getJSONObject("amount");
                if (!ASSETID_GXB.equals(amountObject.getString("asset_id"))) {
                    continue;
                }
                BigDecimal amount = MathUtils.div(amountObject.getBigDecimal("amount"), GXB_WEI);
                txInfo.setAddress(opObject.getString("from"));
                txInfo.setAmount(amount);
                txInfo.setVout(op1.getInteger("trx_in_block"));
                txInfo.setBlockNumber(op1.getInteger("block_num"));
                txInfo.setConfirmations(0);
                // 获取Txid
                JSONObject blockJson = coinUtils.goGXS("get_block","[\"" + txInfo.getBlockNumber() + "\"]");
                JSONArray txJsons = blockJson.getJSONObject("result").getJSONArray("transaction_ids");
                String txid = txJsons.get(txInfo.getVout()).toString();
                txInfo.setTxid(txid);
                txInfos.add(txInfo);
            }
        }
        //Collections.reverse(txInfos);
        return txInfos;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        return null;
    }

    @Override
    public String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee) {
        return null;
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee,String memo) {
        this.walletPassPhrase(0);
        String params = "[\"" + this.account + "\",\"" + address + "\",\"" + MathUtils.decimalFormat(amount) +
                "\",\"GXS\"" + ",\"" + memo + "\"" + ",true" + "]";
        JSONObject object = coinUtils.goGXS("transfer2", params);
        if (object == null) {
            return null;
        }
        JSONArray result = object.getJSONArray("result");
        if (result == null) {
            System.out.println("GXB sendToAddress error --->"+object.toString());
            return null;
        }
        return result.getString(0);
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        return null;
    }

    @Override
    public String getETCSHA3(String str) {
        return null;
    }

    @Override
    public Integer getTransactionCount() {
        return null;
    }
}
