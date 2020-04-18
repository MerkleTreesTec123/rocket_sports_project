package com.qkwl.common.Excel;

import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;

import java.math.BigDecimal;

/**
 * 个人支付宝数据实体
 * Created by ZKF on 2017/6/16.
 */
public class PrivateAlipay {

    // 流水号
    private String serialNo;
    // 时间
    private String timestamp;
    // uid
    private Integer uid;
    // 金额
    private BigDecimal amount;
    // 来源
    private String source;
    // 状态
    private String status;

    // 订单数据库创建的时间
    private String createtime_s;

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

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getCreatetime_s() {
        return createtime_s;
    }

    public void setCreatetime_s(String createtime_s) {
        this.createtime_s = createtime_s;
    }
    @Override
    public String toString() {
        return "PrivateAlipay{" +
                "serialNo='" + serialNo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", uid=" + uid +
                ", amount=" + amount +
                ", source=" + source +
                ", status=" + status +
                ", operation=" + operation +
                ", createtime_s=" + createtime_s +
                '}';
    }
}
