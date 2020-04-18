package com.qkwl.common.dto.capital;

import com.qkwl.common.dto.common.BaseParamDTO;

import java.io.Serializable;

/**
 * Created by wangchen on 2017-07-10.
 */
public class UpdateRechargeOrderInfoDTO extends BaseParamDTO implements Serializable {

    private static final long serialVersionUID = 123532123182L;

    //用户id
    private Integer uid;
    //订单ID
    private Integer orderId;
    //开户行ID
    private Integer bank;
    //银行卡号
    private String bankAccount;
    //开户姓名
    private String payee;
    //电话号码
    private String phone;
    //类型
    private Integer type;
    //ip地址
    private String ip;



    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBank() {
        return bank;
    }

    public void setBank(Integer bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
