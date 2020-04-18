package com.qkwl.common.dto.log;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;import java.math.BigDecimal;
/**
 * 人民币充值修改记录
 * @author LY
 */
public class FLogModifyCapitalOperation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 帐号
	private String faccount;
	// 修改人ID
	private Integer fadminid;
	// 银行
	private String fbank;
	// 开户姓名
	private String fpayee;
	// 手机号
	private String fphone;
	// 修改前金额
	private BigDecimal famount;
	// 修改后金额
	private BigDecimal fmodifyamount;
	// 修改时间
	private Date fupdatetime;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFaccount() {
		return faccount;
	}

	public void setFaccount(String faccount) {
		this.faccount = faccount;
	}

	public Integer getFadminid() {
		return fadminid;
	}

	public void setFadminid(Integer fadminid) {
		this.fadminid = fadminid;
	}

	public String getFbank() {
		return fbank;
	}

	public void setFbank(String fbank) {
		this.fbank = fbank;
	}

	public String getFpayee() {
		return fpayee;
	}

	public void setFpayee(String fpayee) {
		this.fpayee = fpayee;
	}

	public String getFphone() {
		return fphone;
	}

	public void setFphone(String fphone) {
		this.fphone = fphone;
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public BigDecimal getFmodifyamount() {
		return fmodifyamount;
	}

	public void setFmodifyamount(BigDecimal fmodifyamount) {
		this.fmodifyamount = fmodifyamount;
	}

	public Date getFupdatetime() {
		return fupdatetime;
	}

	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}

}
