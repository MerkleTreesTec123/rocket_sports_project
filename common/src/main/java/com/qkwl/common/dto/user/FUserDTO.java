package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.UserStatusEnum;

/**
 * 用户
 * 
 * @author LY
 */
public class FUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 主键Id
	private Integer fid;
	// 显示ID
	private Integer fshowid;
	// 登录名
	private String floginname;
	// 昵称
	private String fnickname;
	// 绑定手机
	private String ftelephone;
	// 绑定邮箱
	private String femail;
	// 真实姓名
	private String frealname;
	// 身份证号
	private String fidentityno;
	// 实名认证类型
	private Integer fidentitytype;
	// 谷歌验证URL
	private String fgoogleurl;
	// 用户状态
	private Integer fstatus;
	private String fstatus_s;
	// 是否实名认证
	private Boolean fhasrealvalidate;
	// 实名认证时间
	private Date fhasrealvalidatetime;
	// 是否绑定手机
	private Boolean fistelephonebind;
	// 是否绑定邮箱
	private Boolean fismailbind;
	// 是否绑定谷歌
	private Boolean fgooglebind;
	// 最后更新时间
	private Date fupdatetime;
	// 手机区号
	private String fareacode;
	// 推荐人ID
	private Integer fintrouid;
	// 推荐人数
	private Integer finvalidateintrocount;
	private Integer fiscny;
	private String fiscny_s;
	// 是否允许虚拟币提现
	private Integer fiscoin;
	private String fiscoin_s;
	// 生日
	private Date fbirth;
	// 最后登录时间
	private Date flastlogintime;
	// 注册时间
	private Date fregistertime;
	// QQ登录ID
	private String fqqopenid;
	// 微信登录ID
	private String funionid;
	// 劵商ID
	private Integer fagentid;
	// 杠杆
	private Integer fleverlock;
	/********** 扩展字段 ***********/
	// IP
	private String ip;
	// 积分
	private Long score;
	// 等级
	private int level;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFshowid() {
		return fshowid;
	}

	public void setFshowid(Integer fshowid) {
		this.fshowid = fshowid;
	}

	public String getFloginname() {
		return floginname;
	}

	public void setFloginname(String floginname) {
		this.floginname = floginname == null ? null : floginname.trim();
	}

	public String getFnickname() {
		return fnickname;
	}

	public void setFnickname(String fnickname) {
		this.fnickname = fnickname == null ? null : fnickname.trim();
	}

	public String getFtelephone() {
		return ftelephone;
	}

	public void setFtelephone(String ftelephone) {
		this.ftelephone = ftelephone == null ? null : ftelephone.trim();
	}

	public String getFemail() {
		return femail;
	}

	public void setFemail(String femail) {
		this.femail = femail == null ? null : femail.trim();
	}

	public String getFrealname() {
		return frealname;
	}

	public void setFrealname(String frealname) {
		this.frealname = frealname == null ? null : frealname.trim();
	}

	public String getFidentityno() {
		return fidentityno;
	}

	public void setFidentityno(String fidentityno) {
		this.fidentityno = fidentityno == null ? null : fidentityno.trim();
	}

	public Integer getFidentitytype() {
		return fidentitytype;
	}

	public void setFidentitytype(Integer fidentitytype) {
		this.fidentitytype = fidentitytype;
	}

	public String getFgoogleurl() {
		return fgoogleurl;
	}

	public void setFgoogleurl(String fgoogleurl) {
		this.fgoogleurl = fgoogleurl == null ? null : fgoogleurl.trim();
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public Boolean getFhasrealvalidate() {
		return fhasrealvalidate;
	}

	public void setFhasrealvalidate(Boolean fhasrealvalidate) {
		this.fhasrealvalidate = fhasrealvalidate;
	}

	public Date getFhasrealvalidatetime() {
		return fhasrealvalidatetime;
	}

	public void setFhasrealvalidatetime(Date fhasrealvalidatetime) {
		this.fhasrealvalidatetime = fhasrealvalidatetime;
	}

	public Boolean getFistelephonebind() {
		return fistelephonebind;
	}

	public void setFistelephonebind(Boolean fistelephonebind) {
		this.fistelephonebind = fistelephonebind;
	}

	public Boolean getFismailbind() {
		return fismailbind;
	}

	public void setFismailbind(Boolean fismailbind) {
		this.fismailbind = fismailbind;
	}

	public Boolean getFgooglebind() {
		return fgooglebind;
	}

	public void setFgooglebind(Boolean fgooglebind) {
		this.fgooglebind = fgooglebind;
	}

	public Date getFupdatetime() {
		return fupdatetime;
	}

	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}

	public String getFareacode() {
		return fareacode;
	}

	public void setFareacode(String fareacode) {
		this.fareacode = fareacode == null ? null : fareacode.trim();
	}

	public Integer getFintrouid() {
		return fintrouid;
	}

	public void setFintrouid(Integer fintrouid) {
		this.fintrouid = fintrouid;
	}

	public Integer getFinvalidateintrocount() {
		return finvalidateintrocount;
	}

	public void setFinvalidateintrocount(Integer finvalidateintrocount) {
		this.finvalidateintrocount = finvalidateintrocount;
	}

	public Integer getFiscny() {
		return fiscny;
	}

	public void setFiscny(Integer fiscny) {
		this.fiscny = fiscny;
	}

	public Integer getFiscoin() {
		return fiscoin;
	}

	public void setFiscoin(Integer fiscoin) {
		this.fiscoin = fiscoin;
	}

	public Date getFbirth() {
		return fbirth;
	}

	public void setFbirth(Date fbirth) {
		this.fbirth = fbirth;
	}

	public Date getFlastlogintime() {
		return flastlogintime;
	}

	public void setFlastlogintime(Date flastlogintime) {
		this.flastlogintime = flastlogintime;
	}

	public Date getFregistertime() {
		return fregistertime;
	}

	public void setFregistertime(Date fregistertime) {
		this.fregistertime = fregistertime;
	}

	public Integer getFleverlock() {
		return fleverlock;
	}

	public void setFleverlock(Integer fleverlock) {
		this.fleverlock = fleverlock;
	}

	public String getFqqopenid() {
		return fqqopenid;
	}

	public void setFqqopenid(String fqqopenid) {
		this.fqqopenid = fqqopenid == null ? null : fqqopenid.trim();
	}

	public String getFunionid() {
		return funionid;
	}

	public void setFunionid(String funionid) {
		this.funionid = funionid == null ? null : funionid.trim();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public String getFstatus_s() {
		return UserStatusEnum.getEnumString(this.fstatus);
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	public String getFiscny_s() {
		return UserStatusEnum.getEnumString(this.fiscny);
	}

	public void setFiscny_s(String fiscny_s) {
		this.fiscny_s = fiscny_s;
	}

	public String getFiscoin_s() {
		return UserStatusEnum.getEnumString(this.fiscoin);
	}

	public void setFiscoin_s(String fiscoin_s) {
		this.fiscoin_s = fiscoin_s;
	}

	public Integer getFagentid() {
		return fagentid;
	}

	public void setFagentid(Integer fagentid) {
		this.fagentid = fagentid;
	}

}