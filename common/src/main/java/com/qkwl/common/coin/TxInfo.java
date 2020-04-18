package com.qkwl.common.coin;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TxInfo
 * @author jany
 */
public class TxInfo {

	// 帐户，USERID
	private String account;
	// 充向地址
	private String address;
	// 类型  receive or SEND
	private String category;
	// 数量
	private BigDecimal amount;
	// 确认数
	private Integer confirmations;
	// 交易ID
	private String txid;
	// 时间
	private Date time;
	// 备注
	private String comment;

	// vout(BTC)
	private Integer vout;

	// 发送地址
	private String from;
	// 接受地址
	private String to;
	// 区块高度
	private Integer blockNumber;
	
	// 资产类型（小企链使用）
	private Integer type;

	// GXS-uid
	private Integer uid;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(int confirmations) {
		this.confirmations = confirmations;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Integer getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(Integer blockNumber) {
		this.blockNumber = blockNumber;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getVout() {
		return vout;
	}

	public void setVout(Integer vout) {
		this.vout = vout;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
}
