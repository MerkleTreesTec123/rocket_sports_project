package com.qkwl.common.Excel;

import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;

import java.math.BigDecimal;

/**
 * 企业支付宝数据实体
 * Created by ZKF on 2017/6/26.
 */
public class PublicAlipay {

    // 流水号
    private String serialNo;
    // 时间
    private String timestamp;
    // 账号
    private String account;
    // 姓名
    private String name;
    // 金额
    private BigDecimal amount;
    // 状态
    private String status;

    private FWalletCapitalOperationDTO operation;


    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FWalletCapitalOperationDTO getOperation() {
        return operation;
    }

    public void setOperation(FWalletCapitalOperationDTO operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "PublicAlipay{" +
                "serialNo='" + serialNo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
