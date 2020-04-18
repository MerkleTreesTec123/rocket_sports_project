package com.qkwl.common.dto.log;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.qkwl.common.dto.Enum.CapitalOperationTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import java.math.BigDecimal;
/**
 * 用户行为日志
 * @author TT
 */
public class FLogUserAction implements Serializable {
	
	private static final long serialVersionUID = 1L;

	// uuid
	private String uuid;
	// 主键ID
    private Integer fid;
    // 用户ID
    private Integer fuid;
    // 卷商ID
    private Integer fagentid;
    // 类型
    private Integer ftype;
    private String ftype_s;
    // 数据类型
    private Integer fdatatype;
    private String fdatatype_s;
    // 数据类型
    private Integer fcapitaltype;
    // 数据内容
    private BigDecimal fdata;
    // 数据内容
    private BigDecimal ffees;
    // 网络手续费
    private BigDecimal fbtcfees;
    // 描述
    private String fcontent;
    // IP
    private String fip;
    // 更新时间
    private Date fupdatetime;
    // 创建时间
    private Date fcreatetime;
	// 次数
	private Integer num;

    public FLogUserAction() {
    	getFdatatype_s();
		this.uuid = UUID.randomUUID().toString();
    }

    public FLogUserAction(Integer ftype, Integer fagentid) {
    	this.ftype = ftype;
    	this.fagentid = fagentid;
		this.uuid = UUID.randomUUID().toString();
    }
    
    public FLogUserAction(Integer fuid, Integer ftype, Integer fdatatype, BigDecimal fdata, String fcontent, Integer fagentid) {
    	this.fuid = fuid;
    	this.ftype = ftype;
    	this.fdatatype = fdatatype;
    	this.fdata = fdata;
    	this.fcontent = fcontent;
    	this.fagentid = fagentid;
		this.uuid = UUID.randomUUID().toString();
    }
    
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

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

    public Integer getFtype() {
        return ftype;
    }

    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }

    public String getFcontent() {
        return fcontent;
    }

    public void setFcontent(String fcontent) {
        this.fcontent = fcontent == null ? null : fcontent.trim();
    }
    
	public Integer getFdatatype() {
		return fdatatype;
	}

	public void setFdatatype(Integer fdatatype) {
		this.fdatatype = fdatatype;
	}

	public Integer getFcapitaltype() {
		return fcapitaltype;
	}

	public void setFcapitaltype(Integer fcapitaltype) {
		this.fcapitaltype = fcapitaltype;
	}

	public BigDecimal getFdata() {
		return fdata;
	}

	public void setFdata(BigDecimal fdata) {
		this.fdata = fdata;
	}

	public BigDecimal getFfees() {
		return ffees;
	}

	public void setFfees(BigDecimal ffees) {
		this.ffees = ffees;
	}

	public String getFip() {
        return fip;
    }

    public void setFip(String fip) {
        this.fip = fip == null ? null : fip.trim();
    }

	public Date getFupdatetime() {
		return fupdatetime;
	}

	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public BigDecimal getFbtcfees() {
		return fbtcfees;
	}

	public void setFbtcfees(BigDecimal fbtcfees) {
		this.fbtcfees = fbtcfees;
	}

	public String getFtype_s() {
		ftype_s = LogUserActionEnum.getValueByCode(ftype);
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public Integer getFagentid() {
		return fagentid;
	}

	public void setFagentid(Integer fagentid) {
		this.fagentid = fagentid;
	}

	public String getFdatatype_s() {
		if(getFdatatype() == null || getFtype() == null){
			return "未知";
		}
		switch (getFtype()) {
			case 21:
				fdatatype_s=CapitalOperationTypeEnum.getEnumString(fdatatype);
				break;
			case 33:
				fdatatype_s=ScoreTypeEnum.getValueByCode(fdatatype);
				break;
			case 36:
				fdatatype_s=CapitalOperationTypeEnum.getEnumString(fdatatype);
				break;
			default:
				fdatatype_s=LogUserActionEnum.getValueByCode(ftype);
		}
		return fdatatype_s;
	}

	public void setFdatatype_s(String fdatatype_s) {
		this.fdatatype_s = fdatatype_s;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
}