package com.qkwl.service.entrust.model;

import com.qkwl.common.dto.Enum.EntrustSourceEnum;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 历史委单
 * @author TT
 */
public class EntrustHistoryDO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 主键ID
	private BigInteger fid;
	// 原委单ID
	private BigInteger fentrustid;
	// 用户ID
	private Integer fuid;
	// 交易id
	private Integer ftradeid;
	// 买方币id
	private Integer fbuycoinid;
	// 卖方币id(虚拟币)
	private Integer fsellcoinid;
	// 委单状态
	private Integer fstatus;
	private String fstatus_s;
	// 委单类型
	private Integer ftype;
	private String ftype_s;
	// 撮合类型
	private Integer fmatchtype;
	// 成交价格
	private BigDecimal flast;
	// 下单价格
	private BigDecimal fprize;
	// 下单数量
	private BigDecimal fcount;
	// 下单总价
	private BigDecimal famount;
	// 成交总价
	private BigDecimal fsuccessamount;
	// 未成交数量
	private BigDecimal fleftcount;
	// 手续费
	private BigDecimal ffees;
	// 剩余手续费
	private BigDecimal fleftfees;
	// 委单来源
	private Integer fsource;
	private String fsource_s;
	// 火币关联委单ID
	private BigInteger fhuobientrustid;
	// 火币管理账号ID
	private Integer fhuobiaccountid;
	// 上次更新时间
	private Date flastupdattime;
	// 创建时间
	private Date fcreatetime;

	/************ 扩展字段 ***************/

	// 登录名
	private String floginname;
	// 昵称
	private String fnickname;
	// 真实姓名
	private String frealname;
	// 虚拟币名称
	private String fcoinname;

	/********* 券商 ********/
	private int fagentid;

	public int getFagentid() {
		return fagentid;
	}

	public void setFagentid(int fagentid) {
		this.fagentid = fagentid;
	}

	public BigInteger getFid() {
		return fid;
	}

	public void setFid(BigInteger fid) {
		this.fid = fid;
	}

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public BigDecimal getFlast() {
		return flast;
	}

	public void setFlast(BigDecimal flast) {
		this.flast = flast;
	}

	public BigDecimal getFprize() {
		return fprize;
	}

	public void setFprize(BigDecimal fprize) {
		this.fprize = fprize;
	}

	public BigDecimal getFcount() {
		return fcount;
	}

	public void setFcount(BigDecimal fcount) {
		this.fcount = fcount;
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public BigDecimal getFsuccessamount() {
		return fsuccessamount;
	}

	public void setFsuccessamount(BigDecimal fsuccessamount) {
		this.fsuccessamount = fsuccessamount;
	}

	public BigDecimal getFleftcount() {
		return fleftcount;
	}

	public void setFleftcount(BigDecimal fleftcount) {
		this.fleftcount = fleftcount;
	}

	public BigDecimal getFleftfees() {
		return fleftfees;
	}

	public void setFleftfees(BigDecimal fleftfees) {
		this.fleftfees = fleftfees;
	}

	public BigDecimal getFfees() {
		return ffees;
	}

	public void setFfees(BigDecimal ffees) {
		this.ffees = ffees;
	}

	public Integer getFmatchtype() {
		return fmatchtype;
	}

	public void setFmatchtype(Integer fmatchtype) {
		this.fmatchtype = fmatchtype;
	}

	public Integer getFsource() {
		return fsource;
	}

	public void setFsource(Integer fsource) {
		this.fsource = fsource;
	}

	public String getFsource_s() {
		fsource_s = EntrustSourceEnum.getFrontValueByCode(this.fsource);
		return fsource_s;
	}

	public void setFsource_s(String fsource_s) {
		this.fsource_s = fsource_s;
	}

	public BigInteger getFhuobientrustid() {
		return fhuobientrustid;
	}

	public void setFhuobientrustid(BigInteger fhuobientrustid) {
		this.fhuobientrustid = fhuobientrustid;
	}

	public Integer getFhuobiaccountid() {
		return fhuobiaccountid;
	}

	public void setFhuobiaccountid(Integer fhuobiaccountid) {
		this.fhuobiaccountid = fhuobiaccountid;
	}

	public Date getFlastupdattime() {
		return flastupdattime;
	}

	public void setFlastupdattime(Date flastupdattime) {
		this.flastupdattime = flastupdattime;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public String getFstatus_s() {
		fstatus_s = EntrustStateEnum.getEntrustStateValueByCode(fstatus);
		return fstatus_s;
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
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

	public String getFcoinname() {
		return fcoinname;
	}

	public void setFcoinname(String fcoinname) {
		this.fcoinname = fcoinname;
	}

	public String getFtype_s() {
		this.ftype_s = EntrustTypeEnum.getEntrustTypeValueByCode(this.ftype);
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public BigInteger getFentrustid() {
		return fentrustid;
	}

	public void setFentrustid(BigInteger fentrustid) {
		this.fentrustid = fentrustid;
	}

	public Integer getFtradeid() {
		return ftradeid;
	}

	public void setFtradeid(Integer ftradeid) {
		this.ftradeid = ftradeid;
	}

	public Integer getFbuycoinid() {
		return fbuycoinid;
	}

	public void setFbuycoinid(Integer fbuycoinid) {
		this.fbuycoinid = fbuycoinid;
	}

	public Integer getFsellcoinid() {
		return fsellcoinid;
	}

	public void setFsellcoinid(Integer fsellcoinid) {
		this.fsellcoinid = fsellcoinid;
	}
}