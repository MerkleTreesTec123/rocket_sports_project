package com.qkwl.common.dto.market;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.math.BigDecimal;
/**
 * 委单日志
 * @author TT
 */
public class EntrustLogData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private BigInteger fid;
	// 委单类型
	private Integer fentrusttype;
	// 价格
	private BigDecimal fprize;
	// 数量
	private BigDecimal fcount;
	// 创建时间
	private Date fcreatetime;

	public EntrustLogData(BigInteger fid, Integer fentrusttype, BigDecimal fprize, BigDecimal fcount, Date fcreatetime) {
		this.fid = fid;
		this.fentrusttype = fentrusttype;
		this.fprize = fprize;
		this.fcount = fcount;
		this.fcreatetime = fcreatetime;
	}

	public Integer getFentrusttype() {
		return fentrusttype;
	}

	public void setFentrusttype(Integer fentrusttype) {
		this.fentrusttype = fentrusttype;
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

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public BigInteger getFid() {
		return fid;
	}

	public void setFid(BigInteger fid) {
		this.fid = fid;
	}

}
