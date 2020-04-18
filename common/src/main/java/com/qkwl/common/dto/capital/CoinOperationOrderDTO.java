package com.qkwl.common.dto.capital;

import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;

import java.math.BigDecimal;

/**
 * 虚拟币充提订单
 */
public class CoinOperationOrderDTO {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 基础币种ID
     */
    private Integer baseCoinId;
    /**
     * 币种ID
     */
    private Integer coinId;
    /**
     * 类型
     */
    private VirtualCapitalOperationTypeEnum operationType;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 网络手续费
     */
    private BigDecimal networkFees;
    /**
     * 操作IP
     */
    private String ip;
    /**
     * 交易ID
     */
    private String txId;
    /**
     * 地址绑定ID
     */
    private Integer addressBindId;
    /**
     * 地址
     */
    private String address;
    /**
     * 地址
     */
    private String memo;
    /**
     * 订单来源
     */
    private DataSourceEnum dataSource;
    /**
     * 平台
     */
    private PlatformEnum platform;
    /**
     * 是否发送风控
     */
    private Boolean isRisk;
    /**
     * 币种名称
     */
    private String coinName;
    /**
     * 短信验证码
     */
    private String phoneCode;
    /**
     * 谷歌验证码
     */
    private String googleCode;
    /**
     * 交易密码
     */
    private String tradePass;

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

    public VirtualCapitalOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(VirtualCapitalOperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getNetworkFees() {
        return networkFees;
    }

    public void setNetworkFees(BigDecimal networkFees) {
        this.networkFees = networkFees;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Integer getAddressBindId() {
        return addressBindId;
    }

    public void setAddressBindId(Integer addressBindId) {
        this.addressBindId = addressBindId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DataSourceEnum getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEnum dataSource) {
        this.dataSource = dataSource;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    public Boolean getRisk() {
        return isRisk;
    }

    public void setRisk(Boolean risk) {
        isRisk = risk;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public Integer getBaseCoinId() {
        return baseCoinId;
    }

    public void setBaseCoinId(Integer baseCoinId) {
        this.baseCoinId = baseCoinId;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

    public String getTradePass() {
        return tradePass;
    }

    public void setTradePass(String tradePass) {
        this.tradePass = tradePass;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "CoinOperationOrderDTO{" +
                "userId=" + userId +
                ", baseCoinId=" + baseCoinId +
                ", coinId=" + coinId +
                ", operationType=" + operationType.getValue() +
                ", amount=" + amount +
                ", networkFees=" + networkFees +
                ", ip='" + ip + '\'' +
                ", txId='" + txId + '\'' +
                ", addressBindId=" + addressBindId +
                ", address='" + address + '\'' +
                ", dataSource=" + dataSource.getValue() +
                ", platform=" + platform.getValue() +
                ", isRisk=" + isRisk +
                ", coinName='" + coinName + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                ", googleCode='" + googleCode + '\'' +
                ", tradePass='" + tradePass + '\'' +
                '}';
    }
}
