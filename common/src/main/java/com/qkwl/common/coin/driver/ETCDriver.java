package com.qkwl.common.coin.driver;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinUtils;
import com.qkwl.common.coin.TxInfo;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ETCDriver implements CoinDriver {

    private CoinUtils coinUtils = null;
    private String passWord = null;
    private Integer coinSort = null;
    private String sendAccount = null;
    private String contractAccount = null;
    private int contractWei = 0;

    public ETCDriver(String ip, String port, String pass, Integer coinSort, String sendAccount, String contractAccount, int contractWei) {
        coinUtils = new CoinUtils(ip, port);
        this.passWord = pass;
        this.coinSort = coinSort;
        this.sendAccount = sendAccount;
        this.contractAccount = contractAccount;
        this.contractWei = contractWei;
    }

    @Override
    public Integer getCoinSort() {
        return this.coinSort;
    }

    @Override
    public Integer getBestHeight() {
        JSONObject resultJson = coinUtils.goETC("eth_blockNumber", "[]");
        String result = resultJson.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        String blockNumberStr = CoinUtils.BigHexToString(resultJson.get("result").toString());
        return Integer.parseInt(blockNumberStr);
    }

    @Override
    public BigDecimal getBalance() {
        if (contractAccount != null && contractAccount.length() > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", sendAccount);
            jsonObject.put("to", contractAccount);
            jsonObject.put("value", "0x0");
            jsonObject.put("gas", "0xea60");
            jsonObject.put("gasPrice", "0x5e93d9401");
            String data = "0x70a08231000000000000000000000000" + StringUtils.difference("0x", sendAccount);
            jsonObject.put("data", data);
            JSONObject resultJson = coinUtils.goETC("eth_call", methodStrJson(jsonObject.toString(), "latest"));
            String result = resultJson.get("result").toString();
            if (result.equals("null")) {
                return null;
            }
            String balance = CoinUtils.ETHBalanceHexToStr(result, contractWei);
            return new BigDecimal(balance);
        } else {
            JSONObject resultJson = coinUtils.goETC("eth_getBalance", methodStr(sendAccount, "latest"));
            System.out.println(resultJson.toString());
            String result = resultJson.get("result").toString();
            if (result.equals("null")) {
                return null;
            }
            String balance = CoinUtils.ETHBalanceHexToStr(result);
            return new BigDecimal(balance);
        }
    }

    @Override
    public String getNewAddress(String uId) {
        JSONObject resultJson = coinUtils.goETC("personal_newAccount", methodStr(passWord));
        String result = resultJson.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        return result;
    }

    @Override
    public String sendToAddress(String to, String amount, String nonce) {
        JSONObject jsonObject = new JSONObject();
        if (contractAccount != null && contractAccount.length() > 0) {
            jsonObject.put("from", sendAccount);
            jsonObject.put("to", contractAccount);
            jsonObject.put("value", "0x0");
            jsonObject.put("gas", "0xea60");
            jsonObject.put("gasPrice", "0x5e93d9401");
            String data = "0xa9059cbb000000000000000000000000" + StringUtils.difference("0x", to) + CoinUtils.CntractToETHBalanceHex(amount, contractWei);
            jsonObject.put("data", data);
        } else {
            jsonObject.put("from", sendAccount);
            jsonObject.put("to", to);
            jsonObject.put("value", CoinUtils.ToETHBalanceHex(amount));
            jsonObject.put("gas", "0x15f90");
            jsonObject.put("gasPrice", "0x4a817c800");
            jsonObject.put("nonce", nonce);
        }

        JSONObject resultJson = coinUtils.goETC("personal_signAndSendTransaction", methodStrJson(jsonObject.toString(), this.passWord));
        String result = resultJson.getString("result");
        if (null == result || result.equals("null") ) {
            System.out.println("ETC sendToAddress error --->"+resultJson.toString());
            return null;
        }
        return result;
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
    public List<TxInfo> listTransactions(int count, int from) {
        return null;
    }

    @Override
    public TxInfo getTransaction(String txId) {
        JSONObject json = coinUtils.goETC("eth_getTransactionByHash", methodStr(txId));
        String result = json.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        JSONObject resultJson = json.getJSONObject("result");
        String blockNumberStr = CoinUtils.BigHexToString(resultJson.get("blockNumber").toString());
        String amount = CoinUtils.ETHBalanceHexToStr(resultJson.get("value").toString());
        TxInfo txinfo = new TxInfo();
        txinfo.setFrom(resultJson.get("from").toString());
        txinfo.setTo(resultJson.get("to").toString());
        txinfo.setBlockNumber(Integer.parseInt(blockNumberStr));
        txinfo.setAmount(new BigDecimal(amount));
        return txinfo;
    }

    @Override
    public String sendToAddress(String to, BigDecimal amountDecimal, String nonce, BigDecimal fee) {
        String amount = amountDecimal.toString();
        JSONObject jsonObject = new JSONObject();
        System.out.println("address = "+to+";fee = " + fee.toString() + ";amount = " + amountDecimal.toString()+" ;nonce = "+nonce) ;
        if (contractAccount != null && contractAccount.length() > 0) {
            jsonObject.put("from", sendAccount);
            jsonObject.put("to", contractAccount);
            jsonObject.put("value", "0x0");
            jsonObject.put("gas", "0xea60");
            jsonObject.put("gasPrice", "0x"+fee.toBigInteger().toString(16));
            String data = "0xa9059cbb000000000000000000000000" + StringUtils.difference("0x", to) + CoinUtils.CntractToETHBalanceHex(amount, contractWei);
            jsonObject.put("data", data);
        } else {
            jsonObject.put("from", sendAccount);
            jsonObject.put("to", to);
            jsonObject.put("value", CoinUtils.ToETHBalanceHex(amount));
            jsonObject.put("gas", "0x15f90");
            jsonObject.put("gasPrice", "0x"+fee.toBigInteger().toString(16));
            jsonObject.put("nonce", nonce);
        }
        //System.out.println(jsonObject.toJSONString());

        JSONObject resultJson = coinUtils.goETC("personal_signAndSendTransaction", methodStrJson(jsonObject.toString(), this.passWord));
        String result = resultJson.getString("result");
        if (null == result || result.equals("null") ) {
            System.out.println("ETC sendToAddress error --->"+resultJson.toString());
            return null;
        }
        return result;
    }

    @Override
    public String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo) {
        return null;
    }

    @Override
    public String getETCSHA3(String str) {
        JSONObject resultJson = coinUtils.goETC("web3_sha3", methodStr(str));
        String result = resultJson.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        return result;
    }

    @Override
    public Integer getTransactionCount() {
        JSONObject resultJson = coinUtils.goETC("eth_getTransactionCount", methodStr(sendAccount, "latest"));
        String result = resultJson.get("result").toString();
        if (result.equals("null")) {
            return null;
        }
        BigInteger hexBalanceTmp = new BigInteger(result.substring(2), 16);
        return hexBalanceTmp.intValue();
    }

    private static String methodStr(String... params) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                strBuffer.append("[\"" + params[i] + "\"");
            } else {
                strBuffer.append(",\"" + params[i] + "\"");
            }
            if (i == params.length - 1) {
                strBuffer.append("]");
            }
        }
        return strBuffer.toString();
    }

    private static String methodStrJson(String... params) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                strBuffer.append("[" + params[i] + "");
            } else {
                strBuffer.append(",\"" + params[i] + "\"");
            }
            if (i == params.length - 1) {
                strBuffer.append("]");
            }
        }
        return strBuffer.toString();
    }
}
