package com.qkwl.common.dto.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * 券商实体
 * @author ZKF
 */
public class FRechargeAgencyVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	//排序
    private Integer sort;
    //带来名称
    private String ageName;
    //qq号码
    private String qqNumber;
    //状态
    private Integer status;
    //备注
    private String remark;
    //创建时间
    private Date gmtCreate;
    //修改时间
    private Date gmtModified;
    //乐观锁
    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getAgeName() {
        return ageName;
    }

    public void setAgeName(String ageName) {
        this.ageName = ageName;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}