package com.qkwl.common.dto.finances;

import com.qkwl.common.dto.Enum.FinanceRecordOperationEnum;
import com.qkwl.common.dto.Enum.FinanceRecordStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务记录
 */
public class FinanceRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer fid;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 关联的操作的id
     */
    private Integer relationId;

    /**
     * 关联的币种
     */
    private Integer relationCoinId;

    /**
     * 关联的币种名字
     */
    private String relationCoinName;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 具体操作
     */
    private Integer operation;

    /**
     * 操作描述
     */
    private String operationDesc;

    /***
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /***
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /*********以下字段不用显示*************/

    /**
     * 交易id
     */
    private String txId;

    /**
     * 充值地址
     */
    private String rechargeAddress;

    /**
     * 提现地址
     */
    private String withdrawAddress;

    /**
     * 交易标签
     */
    private String memo;

    /***
     * 钱包处理时间
     */
    private Date walletOperationDate;

    /**
     * 手续费
     */
    private BigDecimal fee;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getRelationCoinId() {
        return relationCoinId;
    }

    public void setRelationCoinId(Integer relationCoinId) {
        this.relationCoinId = relationCoinId;
    }

    public String getRelationCoinName() {
        return relationCoinName;
    }

    public void setRelationCoinName(String relationCoinName) {
        this.relationCoinName = relationCoinName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getOperationDesc() {
        return FinanceRecordOperationEnum.getValueByCode(operation);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return FinanceRecordStatusEnum.getValueByCode(status);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getRechargeAddress() {
        return rechargeAddress;
    }

    public void setRechargeAddress(String rechargeAddress) {
        this.rechargeAddress = rechargeAddress;
    }

    public String getWithdrawAddress() {
        return withdrawAddress;
    }

    public void setWithdrawAddress(String withdrawAddress) {
        this.withdrawAddress = withdrawAddress;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getWalletOperationDate() {
        return walletOperationDate;
    }

    public void setWalletOperationDate(Date walletOperationDate) {
        this.walletOperationDate = walletOperationDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }
}
