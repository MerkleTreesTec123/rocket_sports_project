package com.qkwl.common.dto.market;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
/**
 * 行情数据
 * @author TT
 */
public class FPeriod implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
    private Integer fid;
    // 交易ID
    private Integer ftradeid;
    // 开
    private BigDecimal fkai;
    // 高
    private BigDecimal fgao;
    // 低
    private BigDecimal fdi;
    // 收
    private BigDecimal fshou;
    // 量
    private BigDecimal fliang;
    // 生成时间
    private Date ftime;
    
    public FPeriod() {
    	
    }

    public FPeriod(int ftradeid, BigDecimal fshou, Date ftime) {
    	this.ftradeid = ftradeid;
    	this.fshou = fshou;
    	this.ftime = ftime;
    }
    
    public FPeriod(int ftradeid, BigDecimal fkai, BigDecimal fgao, BigDecimal fdi, BigDecimal fshou, BigDecimal fliang, Date ftime) {
    	this.ftradeid = ftradeid;
    	this.fkai = fkai;
    	this.fgao = fgao;
    	this.fdi = fdi;
    	this.fshou = fshou;
    	this.fliang = fliang;
    	this.ftime = ftime;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFtradeid() {
        return ftradeid;
    }

    public void setFtradeid(Integer ftradeid) {
        this.ftradeid = ftradeid;
    }

    public BigDecimal getFkai() {
        return fkai;
    }

    public void setFkai(BigDecimal fkai) {
        this.fkai = fkai;
    }

    public BigDecimal getFgao() {
        return fgao;
    }

    public void setFgao(BigDecimal fgao) {
        this.fgao = fgao;
    }

    public BigDecimal getFdi() {
        return fdi;
    }

    public void setFdi(BigDecimal fdi) {
        this.fdi = fdi;
    }

    public BigDecimal getFshou() {
        return fshou;
    }

    public void setFshou(BigDecimal fshou) {
        this.fshou = fshou;
    }

    public BigDecimal getFliang() {
        return fliang;
    }

    public void setFliang(BigDecimal fliang) {
        this.fliang = fliang;
    }

    public Date getFtime() {
        return ftime;
    }

    public void setFtime(Date ftime) {
        this.ftime = ftime;
    }
}