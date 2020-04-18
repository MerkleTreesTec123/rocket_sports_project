package com.qkwl.common.dto.capital;

import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.common.BaseParamDTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 法币币充提订单
 */
public class BankOperationOrderDTO extends BaseParamDTO implements Serializable{

    private final static long serialVersionUID =123123123453L;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 币种ID
     */
    private Integer coinId;
    /**
     * 订单类型
     */
    private CapitalOperationInOutTypeEnum operationInOutType;
    /**
     * 充值类型
     */
    private Integer operationType;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 用户银行卡ID
     */
    private Integer userBankId;
    /**
     * 订单来源
     */
    private DataSourceEnum dataSource;
    /**
     * 用户VIP等级
     */
    private Integer userLevel;

    /**
     * 是否发送风控
     */
    private Boolean isRisk;
    /**
     * 登录账号
     */
    private String loginName;
    /**
     * 币种名称
     */
    private String coinName;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 充值类型
     */
    private Integer rechargeType;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public CapitalOperationInOutTypeEnum getOperationInOutType() {
        return operationInOutType;
    }

    public void setOperationInOutType(CapitalOperationInOutTypeEnum operationInOutType) {
        this.operationInOutType = operationInOutType;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getUserBankId() {
        return userBankId;
    }

    public void setUserBankId(Integer userBankId) {
        this.userBankId = userBankId;
    }

    public DataSourceEnum getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEnum dataSource) {
        this.dataSource = dataSource;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Boolean getRisk() {
        return isRisk;
    }

    public void setRisk(Boolean risk) {
        isRisk = risk;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    @Override
    public String toString() {
        return "BankOperationOrderDTO{" +
                "userId=" + userId +
                ", coinId=" + coinId +
                ", operationInOutType=" + operationInOutType.getValue() +
                ", operationType=" + operationType +
                ", amount=" + amount +
                ", ip='" + getIp() + '\'' +
                ", userBankId=" + userBankId +
                ", dataSource=" + dataSource.getValue() +
                ", userLevel=" + userLevel +
                ", isRisk=" + isRisk +
                ", loginName='" + loginName + '\'' +
                ", coinName='" + coinName + '\'' +
                '}';
    }
}
