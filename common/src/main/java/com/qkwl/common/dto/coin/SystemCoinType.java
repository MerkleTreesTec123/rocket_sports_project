package com.qkwl.common.dto.coin;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 币种类型表
 */
public class SystemCoinType implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键ID
    private Integer id;
    // 排序
    private Integer sortId;
    // 名称
    private String name;
    // 简称
    private String shortName;
    // 符号
    private String symbol;
    // 类型
    private Integer type;
    // 钱包类型
    private Integer coinType;
    // 状态
    private Integer status;
    // 是否提现
    private Boolean isWithdraw;
    // 是否充值
    private Boolean isRecharge;
    // 风控阀值
    private BigDecimal riskNum;
    // 是否PUSH
    private Boolean isPush;
    // 是否理财
    private Boolean isFinances;
    // 平台ID
    private Integer platformId;
    // 钱包链接IP
    private String ip;
    // 钱包链接端口
    private String port;
    // 钱包链接user
    private String accessKey;
    // 钱包链接pass
    private String secrtKey;
    // ethaccount
    private String ethAccount;
    // 资产ID
    private BigInteger assetId;
    // 网络手续费
    private BigDecimal networkFee;
    // 充值到账确认数
    private Integer confirmations;
    // WEB站LOGO
    private String webLogo;
    // APP站LOGO
    private String appLogo;
    // 创建时间
    private Date gmtCreate;
    // 更新时间
    private Date gmtModified;
    // 版本号
    private Integer version;
    // 合约账户
    private String contractAccount;
    // 合约位号
    private Integer contractWei;
    // 浏览器地址
    private String explorerUrl;

    // 扩展字段
    private String typeName;
    private String statusName;
    private String coinTypeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCoinType() {
        return coinType;
    }

    public void setCoinType(Integer coinType) {
        this.coinType = coinType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsWithdraw() {
        return isWithdraw;
    }

    public void setIsWithdraw(Boolean withdraw) {
        isWithdraw = withdraw;
    }

    public Boolean getIsRecharge() {
        return isRecharge;
    }

    public void setIsRecharge(Boolean recharge) {
        isRecharge = recharge;
    }

    public BigDecimal getRiskNum() {
        return riskNum;
    }

    public void setRiskNum(BigDecimal riskNum) {
        this.riskNum = riskNum;
    }

    public Boolean getIsPush() {
        return isPush;
    }

    public void setIsPush(Boolean push) {
        isPush = push;
    }

    public Boolean getIsFinances() {
        return isFinances;
    }

    public void setIsFinances(Boolean finances) {
        isFinances = finances;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecrtKey() {
        return secrtKey;
    }

    public void setSecrtKey(String secrtKey) {
        this.secrtKey = secrtKey;
    }

    public BigInteger getAssetId() {
        return assetId;
    }

    public void setAssetId(BigInteger assetId) {
        this.assetId = assetId;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public String getWebLogo() {
        return webLogo;
    }

    public void setWebLogo(String webLogo) {
        this.webLogo = webLogo;
    }

    public String getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(String appLogo) {
        this.appLogo = appLogo;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTypeName() {
        return SystemCoinTypeEnum.getValueByCode(this.type);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatusName() {
        return SystemCoinStatusEnum.getValueByCode(this.status);
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCoinTypeName() {
        return SystemCoinSortEnum.getValueByCode(this.coinType);
    }

    public void setCoinTypeName(String coinTypeName) {
        this.coinTypeName = coinTypeName;
    }

    public String getEthAccount() {
        return ethAccount;
    }

    public void setEthAccount(String ethAccount) {
        this.ethAccount = ethAccount;
    }

    public String getContractAccount() {
        return contractAccount;
    }

    public void setContractAccount(String contractAccount) {
        this.contractAccount = contractAccount;
    }

    public Integer getContractWei() {
        return contractWei;
    }

    public void setContractWei(Integer contractWei) {
        this.contractWei = contractWei;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    public void setExplorerUrl(String explorerUrl) {
        this.explorerUrl = explorerUrl;
    }

    @Override
    public String toString() {
        return "SystemCoinType{" +
                "id=" + id +
                ", sortId=" + sortId +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type=" + type +
                ", coinType=" + coinType +
                ", status=" + status +
                ", isWithdraw=" + isWithdraw +
                ", isRecharge=" + isRecharge +
                ", riskNum=" + riskNum +
                ", isPush=" + isPush +
                ", isFinances=" + isFinances +
                ", platformId=" + platformId +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secrtKey='" + secrtKey + '\'' +
                ", ethAccount='" + ethAccount + '\'' +
                ", assetId=" + assetId +
                ", networkFee=" + networkFee +
                ", confirmations=" + confirmations +
                ", webLogo='" + webLogo + '\'' +
                ", appLogo='" + appLogo + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", version=" + version +
                ", contractAccount='" + contractAccount + '\'' +
                ", contractWei=" + contractWei +
                ", explorerUrl='" + explorerUrl + '\'' +
                ", typeName='" + typeName + '\'' +
                ", statusName='" + statusName + '\'' +
                ", coinTypeName='" + coinTypeName + '\'' +
                '}';
    }
}