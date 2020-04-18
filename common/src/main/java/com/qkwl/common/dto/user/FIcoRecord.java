package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
/**
 * 众筹记录
 * @author ZKF
 */
public class FIcoRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	//记录id
	private Integer fid;
	//用户id
    private Integer fuid;
    //众筹项目id
    private Integer ficoid;
    //众筹币量
    private BigDecimal famount;
    //实际众筹币量
    private BigDecimal fcoin;
    //实际众筹人民币
    private BigDecimal fcny;
    //下单时币价
    private BigDecimal fprice;
    //用户登录名
    private String floginname;
    //version
    private Integer version;
    //回报代币数量
    private Integer fleftgain;
    //回报积分
    private Integer fleftscore;
    //众筹时间
    private Date fcreatetime;
    
    
	public Integer getFid() {
		return fid;
	}
	public void setFid(Integer fid) {
		this.fid = fid;
	}
	public Integer getFuid() {
		return fuid;
	}
	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}
	public Integer getFicoid() {
		return ficoid;
	}
	public void setFicoid(Integer ficoid) {
		this.ficoid = ficoid;
	}
	public BigDecimal getFamount() {
		return famount;
	}
	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}
	public BigDecimal getFcoin() {
		return fcoin;
	}
	public void setFcoin(BigDecimal fcoin) {
		this.fcoin = fcoin;
	}
	public BigDecimal getFcny() {
		return fcny;
	}
	public void setFcny(BigDecimal fcny) {
		this.fcny = fcny;
	}
	public BigDecimal getFprice() {
		return fprice;
	}
	public void setFprice(BigDecimal fprice) {
		this.fprice = fprice;
	}
	public String getFloginname() {
		return floginname;
	}
	public void setFloginname(String floginname) {
		this.floginname = floginname;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getFleftgain() {
		return fleftgain;
	}
	public void setFleftgain(Integer fleftgain) {
		this.fleftgain = fleftgain;
	}
	public Integer getFleftscore() {
		return fleftscore;
	}
	public void setFleftscore(Integer fleftscore) {
		this.fleftscore = fleftscore;
	}
	public Date getFcreatetime() {
		return fcreatetime;
	}
	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

}