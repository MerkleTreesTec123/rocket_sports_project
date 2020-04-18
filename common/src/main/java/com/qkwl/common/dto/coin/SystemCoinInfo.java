package com.qkwl.common.dto.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 币种基本信息
 */
public class SystemCoinInfo implements Serializable{
    private static final long serialVersionUID = -7657964649576942870L;
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 币种ID
     */
    private Integer coinId;
    /**
     * 名称
     */
    private String nameZh;
    /**
     * 英文名称
     */
    private String nameEn;
    /**
     * 简称
     */
    private String nameShort;
    /**
     * 发行总量
     */
    private String total;
    /**
     * 发行价格
     */
    private String price;
    /**
     * 流通量
     */
    private String circulation;
    /**
     * 核心算法
     */
    private String algorithm;
    /**
     * 区块速度
     */
    private String blockVelocity;
    /**
     * 区块大小
     */
    private String blockSize;
    /**
     * 是否ICO
     */
    private String ico;
    /**
     * ICO平台
     */
    private String icoPlatform;
    /**
     * 官方网站
     */
    private String linkWebsite = "javascript:void(0)";
    /**
     * 钱包下载
     */
    private String linkWallet = "javascript:void(0)";
    /**
     * 区块查询
     */
    private String linkBlock = "javascript:void(0)";

    /**
     * 白皮书
     */
    private String linkWhitepaper;

    public String getLinkWhitepaper() {
        return linkWhitepaper;
    }

    public void setLinkWhitepaper(String linkWhitepaper) {
        this.linkWhitepaper = linkWhitepaper;
    }

    /**
     * 官方QQ
     */
    private String officialQq;
    /**
     * 官方微信
     */
    private String officialWechat;
    /**
     * 官方电话
     */
    private String officialPhone;
    /**
     * 币种描述
     */
    private String info;
    /**
     * 币种描述缩写
     */
    private String shortInfo;
    /**
     * 语言简称
     */
    private String lanName;
    /**
     * 推出时间
     */
    private String gmtRelease;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;
    /**
     * 版本号
     */
    private Integer version;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh == null ? null : nameZh.trim();
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn == null ? null : nameEn.trim();
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort == null ? null : nameShort.trim();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCirculation() {
        return circulation;
    }

    public void setCirculation(String circulation) {
        this.circulation = circulation;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm == null ? null : algorithm.trim();
    }

    public String getBlockVelocity() {
        return blockVelocity;
    }

    public void setBlockVelocity(String blockVelocity) {
        this.blockVelocity = blockVelocity == null ? null : blockVelocity.trim();
    }

    public String getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(String blockSize) {
        this.blockSize = blockSize == null ? null : blockSize.trim();
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico == null ? null : ico.trim();
    }

    public String getIcoPlatform() {
        return icoPlatform;
    }

    public void setIcoPlatform(String icoPlatform) {
        this.icoPlatform = icoPlatform == null ? null : icoPlatform.trim();
    }

    public String getLinkWebsite() {
        return linkWebsite;
    }

    public void setLinkWebsite(String linkWebsite) {
        this.linkWebsite = linkWebsite == null ? null : linkWebsite.trim();
    }

    public String getLinkWallet() {
        return linkWallet;
    }

    public void setLinkWallet(String linkWallet) {
        this.linkWallet = linkWallet == null ? null : linkWallet.trim();
    }

    public String getLinkBlock() {
        return linkBlock;
    }

    public void setLinkBlock(String linkBlock) {
        this.linkBlock = linkBlock == null ? null : linkBlock.trim();
    }

    public String getOfficialQq() {
        return officialQq;
    }

    public void setOfficialQq(String officialQq) {
        this.officialQq = officialQq == null ? null : officialQq.trim();
    }

    public String getOfficialWechat() {
        return officialWechat;
    }

    public void setOfficialWechat(String officialWechat) {
        this.officialWechat = officialWechat == null ? null : officialWechat.trim();
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public void setOfficialPhone(String officialPhone) {
        this.officialPhone = officialPhone == null ? null : officialPhone.trim();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public String getLanName() {
        return lanName;
    }

    public void setLanName(String lanName) {
        this.lanName = lanName;
    }

    public String getGmtRelease() {
        return gmtRelease;
    }

    public void setGmtRelease(String gmtRelease) {
        this.gmtRelease = gmtRelease;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SystemCoinInfo{" +
                "id=" + id +
                ", coinId=" + coinId +
                ", nameZh='" + nameZh + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", total=" + total +
                ", price=" + price +
                ", circulation=" + circulation +
                ", algorithm='" + algorithm + '\'' +
                ", blockVelocity='" + blockVelocity + '\'' +
                ", blockSize='" + blockSize + '\'' +
                ", ico='" + ico + '\'' +
                ", icoPlatform='" + icoPlatform + '\'' +
                ", linkWebsite='" + linkWebsite + '\'' +
                ", linkWallet='" + linkWallet + '\'' +
                ", linkBlock='" + linkBlock + '\'' +
                ", officialQq='" + officialQq + '\'' +
                ", officialWechat='" + officialWechat + '\'' +
                ", officialPhone='" + officialPhone + '\'' +
                ", info='" + info + '\'' +
                ", lanName='" + lanName + '\'' +
                ", gmtRelease=" + gmtRelease +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", version=" + version +
                '}';
    }
}