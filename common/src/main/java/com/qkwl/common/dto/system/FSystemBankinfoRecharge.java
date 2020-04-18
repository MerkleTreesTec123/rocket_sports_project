package com.qkwl.common.dto.system;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.BankTypeEnum;
import com.qkwl.common.dto.Enum.SystemBankInfoEnum;

/**
 * 充值银行卡配置
 * @author LY
 */
public class FSystemBankinfoRecharge implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 银行名称
	private String fbankname;
	// 平台显示名称
	private String fownername;
	// 银行开户地址
	private String fbankaddress;
	// 银行卡号
	private String fbanknumber;
	// 创建时间
	private Date fcreatetime;
	// 状态
	private Integer fstatus;
	// 状态
	private String fstatus_s;
	// 版本
	private Integer version;
	// 银行类型
	private Integer fbanktype;
	// 银行类型
	private String fbanktype_s;
	// 排序
	private Integer fsortid;
	// 使用场景
	private Integer fusetype;
	// 银行代码
	private String fbankcode;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFbankname() {
		return fbankname;
	}

	public void setFbankname(String fbankname) {
		this.fbankname = fbankname == null ? null : fbankname.trim();
	}

	public String getFownername() {
		return fownername;
	}

	public void setFownername(String fownername) {
		this.fownername = fownername == null ? null : fownername.trim();
	}

	public String getFbankaddress() {
		return fbankaddress;
	}

	public void setFbankaddress(String fbankaddress) {
		this.fbankaddress = fbankaddress == null ? null : fbankaddress.trim();
	}

	public String getFbanknumber() {
		return fbanknumber;
	}

	public void setFbanknumber(String fbanknumber) {
		this.fbanknumber = fbanknumber == null ? null : fbanknumber.trim();
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}
	
	public String getFstatus_s() {
		if(this.getFstatus()==null){
			return "";
		}
		fstatus_s = SystemBankInfoEnum.getEnumString(this.getFstatus());
		return fstatus_s;
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getFbanktype() {
		return fbanktype;
	}

	public void setFbanktype(Integer fbanktype) {
		this.fbanktype = fbanktype;
	}

	public String getFbanktype_s() {
		if(fbanktype_s == null){
			fbanktype_s = BankTypeEnum.getTypeValueByCode(fbanktype);
		}
		return fbanktype_s;
	}

	public void setFbanktype_s(String fbanktype_s) {
		this.fbanktype_s = fbanktype_s;
	}

	public Integer getFsortid() {
		return fsortid;
	}

	public void setFsortid(Integer fsortid) {
		this.fsortid = fsortid;
	}

	public Integer getFusetype() {
		return fusetype;
	}

	public void setFusetype(Integer fusetype) {
		this.fusetype = fusetype;
	}

	public String getFbankcode() {
		return fbankcode;
	}

	public void setFbankcode(String fbankcode) {
		this.fbankcode = fbankcode == null ? null : fbankcode.trim();
	}
}