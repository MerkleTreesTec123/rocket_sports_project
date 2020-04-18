package com.qkwl.service.user.model;

import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.CapitalOperationInStatus;
import com.qkwl.common.dto.Enum.CapitalOperationOutStatus;
import com.qkwl.common.dto.Enum.CapitalOperationTypeEnum;
import com.qkwl.common.dto.Enum.DataSourceEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 人民币充提记录
 * 
 * @author LY
 */
public class FWalletCapitalOperationDO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 主键ID
	private Integer fid;
	// 银行卡ID
	private Integer fsysbankid;
	// 用户ID
	private Integer fuid;
	// 币种id
	private Integer fcoinid;
	// 创建时间
	private Date fcreatetime;
	// 数量
	private BigDecimal famount;
	// 充值或提现 CapitalOperationTypeEnum，汇款or提现
	private Integer finouttype;
	// 充值类型
	private Integer ftype;
	private String ftype_s;
	// 状态
	private Integer fstatus;// CapitalOperationInStatus||CapitalOperationOutStatus
	private String fstatus_s;
	// 汇款类型
	private Integer fremittancetype;
	// 备注
	private String fremark;
	// 充值银行
	private String fbank;
	// 银行帐号
	private String faccount;
	// 开户姓名
	private String fpayee;
	// 银行编号
	private String fbankCode;
	// 手机号
	private String fphone;
	// 更新时间
	private Date fupdatetime;
	// 审核管理员ID
	private Integer fadminid;
	// 手续费
	private BigDecimal ffees;
	// 版本号
	private Integer version;
	// 是否充值
	private Boolean fischarge;
	// 开户地址
	private String faddress;
	// 来源
	private Integer fsource;
	private String fsource_s;
	// 订单来源平台
	private Integer fplatform;
	private String fplatform_s;

	// 流水号
	private String fserialno;

	/********** 扩展字段 ************/

	// 登录名
	private String floginname;
	// 昵称
	private String fnickname;
	// 真实姓名
	private String frealname;
	// 审核人
	private String fadminname;

	/********* 券商 ********/
	private int fagentid;

	public int getFagentid() {
		return fagentid;
	}

	public void setFagentid(int fagentid) {
		this.fagentid = fagentid;
	}

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFsysbankid() {
		return fsysbankid;
	}

	public void setFsysbankid(Integer fsysbankid) {
		this.fsysbankid = fsysbankid;
	}

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}


	public Integer getFcoinid() {
		return fcoinid;
	}

	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public Integer getFinouttype() {
		return finouttype;
	}

	public void setFinouttype(Integer finouttype) {
		this.finouttype = finouttype;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public String getFtype_s() {
		return CapitalOperationTypeEnum.getEnumString(this.getFtype());
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public String getFstatus_s() {
		int status = this.getFstatus();
		if (this.getFtype() == CapitalOperationTypeEnum.RMB_IN || this.getFtype() == CapitalOperationTypeEnum.OLRMB_INT || this.getFtype() == CapitalOperationTypeEnum.ALIPAY_INT || this.getFtype() == CapitalOperationTypeEnum.WECHAT_INT) {
			return CapitalOperationInStatus.getEnumString(status);
		} else {
			return CapitalOperationOutStatus.getEnumString(status);
		}
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	public Integer getFremittancetype() {
		return fremittancetype;
	}

	public void setFremittancetype(Integer fremittancetype) {
		this.fremittancetype = fremittancetype;
	}

	public String getFremark() {
		return fremark;
	}

	public void setFremark(String fremark) {
		this.fremark = fremark == null ? null : fremark.trim();
	}

	public String getFbank() {
		return fbank;
	}

	public void setFbank(String fbank) {
		this.fbank = fbank == null ? null : fbank.trim();
	}

	public String getFaccount() {
		return faccount;
	}

	public void setFaccount(String faccount) {
		this.faccount = faccount == null ? null : faccount.trim();
	}

	public String getFpayee() {
		return fpayee;
	}

	public void setFpayee(String fpayee) {
		this.fpayee = fpayee == null ? null : fpayee.trim();
	}

	public String getFbankCode() {
		return fbankCode;
	}

	public void setFbankCode(String fbankCode) {
		this.fbankCode = fbankCode;
	}

	public String getFphone() {
		return fphone;
	}

	public void setFphone(String fphone) {
		this.fphone = fphone == null ? null : fphone.trim();
	}

	public Date getFupdatetime() {
		return fupdatetime;
	}

	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}

	public Integer getFadminid() {
		return fadminid;
	}

	public void setFadminid(Integer fadminid) {
		this.fadminid = fadminid;
	}

	public BigDecimal getFfees() {
		return ffees;
	}

	public void setFfees(BigDecimal ffees) {
		this.ffees = ffees;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getFischarge() {
		return fischarge;
	}

	public void setFischarge(Boolean fischarge) {
		this.fischarge = fischarge;
	}

	public String getFaddress() {
		return faddress;
	}

	public void setFaddress(String faddress) {
		this.faddress = faddress == null ? null : faddress.trim();
	}

	public String getFloginname() {
		return floginname;
	}

	public void setFloginname(String floginname) {
		this.floginname = floginname;
	}

	public String getFnickname() {
		return fnickname;
	}

	public void setFnickname(String fnickname) {
		this.fnickname = fnickname;
	}

	public String getFrealname() {
		return frealname;
	}

	public void setFrealname(String frealname) {
		this.frealname = frealname;
	}

	public String getFadminname() {
		return fadminname;
	}

	public void setFadminname(String fadminname) {
		this.fadminname = fadminname;
	}

	public Integer getFsource() {
		return fsource;
	}

	public void setFsource(Integer fsource) {
		this.fsource = fsource;
	}

	public String getFsource_s() {
		this.fsource_s = DataSourceEnum.getValueByCode(this.fsource);
		return fsource_s;
	}

	public void setFsource_s(String fsource_s) {
		this.fsource_s = fsource_s;
	}

	public Integer getFplatform() {
		return fplatform;
	}

	public void setFplatform(Integer fplatform) {
		this.fplatform = fplatform;
	}

	public String getFplatform_s() {
		this.fplatform_s = PlatformEnum.getValueByCode(this.fplatform);
		return fplatform_s;
	}

	public void setFplatform_s(String fplatform_s) {
		this.fplatform_s = fplatform_s;
	}

	public String getFserialno() {
		return fserialno;
	}

	public void setFserialno(String fserialno) {
		this.fserialno = fserialno;
	}
}