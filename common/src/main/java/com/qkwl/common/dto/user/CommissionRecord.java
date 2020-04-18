package com.qkwl.common.dto.user;

import com.qkwl.common.dto.Enum.CommissionRecordStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户佣金返现记录
 */
public class CommissionRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 邀请人ID
     */
    private Integer introuid;

    /**
     * 币种ID
     */
    private Integer coinid;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private Integer status;

    private String  status_s;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 修改时间
     */
    private Date updatetime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 币种名称
     */
    private String coinname;

    public String getStatus_s() {
        return CommissionRecordStatusEnum.getStatusName(status);
    }

    public void setStatus_s(String status_s) {
        this.status_s = status_s;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getIntrouid() {
        return introuid;
    }

    public void setIntrouid(Integer introuid) {
        this.introuid = introuid;
    }

    public Integer getCoinid() {
        return coinid;
    }

    public void setCoinid(Integer coinid) {
        this.coinid = coinid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
