package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 返回充值订单创建成功的消息
 */
public class ResultRechargeOrderInfoDTO implements Serializable{
    private final static long serialVersionUID = 123324L;

    //创建成功的订单
    private Integer orderId;
    //充值金额
    private BigDecimal money;
    //银行名称
    private String bankName;
    //开户名称
    private String ownerName;
    //开户行地址
    private String bankAddress;
    //银行卡号
    private String bankNumber;
    //充值银行卡名称
    private String rechargeBankName;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getRechargeBankName() {
        return rechargeBankName;
    }

    public void setRechargeBankName(String rechargeBankName) {
        this.rechargeBankName = rechargeBankName;
    }
}
