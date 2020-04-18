package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.IcoStateEnum;
import java.math.BigDecimal;
/**
 * 众筹项目
 * @author ZKF
 */
public class FIcoList implements Serializable{
	private static final long serialVersionUID = 1L;
	//项目id
	private Integer fid;
	//项目名称
    private String ftitle;
    //众筹代币id
    private Integer fcoinid;
  	//众筹代币单价
    private BigDecimal fcoinprice;
    //众筹币种id
    private Integer fpaycoinid;
    //众筹币种目标数量
    private BigDecimal famount;
    //众筹最高目标限制
    private BigDecimal fmaxamount;
    //首页图片
    private String findexpic;
    //列表图片
    private String flistpic;
    //app图片
    private String fapppic;
    //申请时间
    private Date fcreatetime;
    //开始时间
    private Date fstartime;
    //结束时间
    private Date fendtime;
    //状态
    private Integer fstate;
    //状态字符串
    private String fstate_s;
    //支持人数
    private Integer fsupport;
    //已筹数量
    private BigDecimal ficonum;
    //更新时间
    private Date fupdatetime;
    //预热时间
    private Date fwarmtime;
    //点赞
    private Integer flike;
    //关注
    private Integer fattention;
    //version
    private Integer version;
    //项目介绍
    private String fintroduce;
    //团队介绍
    private String fteam;
    //规则介绍
    private String frules;
    //剩余时间
    private String flefthour;
    //倒计时
    private Long flefttime;
    //众筹进度
    private BigDecimal fprocess;
	public Integer getFid() {
		return fid;
	}
	public void setFid(Integer fid) {
		this.fid = fid;
	}
	public String getFtitle() {
		return ftitle;
	}
	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}
	public Integer getFcoinid() {
		return fcoinid;
	}
	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}
	public BigDecimal getFcoinprice() {
		return fcoinprice;
	}
	public void setFcoinprice(BigDecimal fcoinprice) {
		this.fcoinprice = fcoinprice;
	}
	public Integer getFpaycoinid() {
		return fpaycoinid;
	}
	public void setFpaycoinid(Integer fpaycoinid) {
		this.fpaycoinid = fpaycoinid;
	}
	public BigDecimal getFamount() {
		return famount;
	}
	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}
	public String getFindexpic() {
		return findexpic;
	}
	public void setFindexpic(String findexpic) {
		this.findexpic = findexpic;
	}
	public String getFlistpic() {
		return flistpic;
	}
	public void setFlistpic(String flistpic) {
		this.flistpic = flistpic;
	}
	public String getFapppic() {
		return fapppic;
	}
	public void setFapppic(String fapppic) {
		this.fapppic = fapppic;
	}
	public Date getFcreatetime() {
		return fcreatetime;
	}
	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}
	public Date getFstartime() {
		return fstartime;
	}
	public void setFstartime(Date fstartime) {
		this.fstartime = fstartime;
	}
	public Date getFendtime() {
		return fendtime;
	}
	public void setFendtime(Date fendtime) {
		this.fendtime = fendtime;
	}
	public Integer getFstate() {
		return fstate;
	}
	public void setFstate(Integer fstate) {
		this.fstate = fstate;
	}
	public String getFstate_s() {
		this.fstate_s=IcoStateEnum.getIcoStateValueByCode(this.fstate);
		return fstate_s;
	}
	public void setFstate_s(String fstate_s) {
		this.fstate_s = fstate_s;
	}
	public Integer getFsupport() {
		return fsupport;
	}
	public void setFsupport(Integer fsupport) {
		this.fsupport = fsupport;
	}
	public BigDecimal getFiconum() {
		return ficonum;
	}
	public void setFiconum(BigDecimal ficonum) {
		this.ficonum = ficonum;
	}
	public Date getFupdatetime() {
		return fupdatetime;
	}
	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}
	public Date getFwarmtime() {
		return fwarmtime;
	}
	public void setFwarmtime(Date fwarmtime) {
		this.fwarmtime = fwarmtime;
	}
	public Integer getFlike() {
		return flike;
	}
	public void setFlike(Integer flike) {
		this.flike = flike;
	}
	public Integer getFattention() {
		return fattention;
	}
	public void setFattention(Integer fattention) {
		this.fattention = fattention;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getFintroduce() {
		return fintroduce;
	}
	public void setFintroduce(String fintroduce) {
		this.fintroduce = fintroduce;
	}
	public String getFteam() {
		return fteam;
	}
	public void setFteam(String fteam) {
		this.fteam = fteam;
	}
	public String getFrules() {
		return frules;
	}
	public void setFrules(String frules) {
		this.frules = frules;
	}
	public String getFlefthour() {
		return flefthour;
	}
	public void setFlefthour(String flefthour) {
		this.flefthour = flefthour;
	}
	public BigDecimal getFprocess() {
		return fprocess;
	}
	public void setFprocess(BigDecimal fprocess) {
		this.fprocess = fprocess;
	}
	public Long getFlefttime() {
		return flefttime;
	}
	public void setFlefttime(Long flefttime) {
		this.flefttime = flefttime;
	}
	public BigDecimal getFmaxamount() {
		return fmaxamount;
	}
	public void setFmaxamount(BigDecimal fmaxamount) {
		this.fmaxamount = fmaxamount;
	}

    

}