package com.qkwl.common.dto.mq;

import com.qkwl.common.dto.Enum.EntrustChangeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 委单状态通知
 * @author TT
 */
public class MQEntrustState implements Serializable{
	
	/**
	 * ID
	 */
	private static final long serialVersionUID = -2769880805358038560L;
	
	// 交易id
	private Integer tradeId;
	
	// 状态
	private EntrustChangeEnum type;
	
	// 撤单类型
	private Integer cancelType;
	
	// 成交类型
	private Integer matchType;
	
	// 订单id
	private BigInteger buyOrderId;
	
	// 订单id
	private BigInteger sellOrderId;
	
	// 买方ID
	private Integer buyID;
	
	// 卖方ID
	private Integer sellID;
	
	// 买价格
	private BigDecimal buyPrize;
	
	// 卖价格
	private BigDecimal sellPrize;
	
	// 数量
	private BigDecimal count;
	
	// 成交价
	private BigDecimal last;
	
	
	public MQEntrustState() {
		
	}

	public BigInteger getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(BigInteger buyOrderId) {
		this.buyOrderId = buyOrderId;
	}

	public BigInteger getSellOrderId() {
		return sellOrderId;
	}

	public void setSellOrderId(BigInteger sellOrderId) {
		this.sellOrderId = sellOrderId;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public EntrustChangeEnum getType() {
		return type;
	}

	public void setType(EntrustChangeEnum type) {
		this.type = type;
	}

	public Integer getCancelType() {
		return cancelType;
	}

	public void setCancelType(Integer cancelType) {
		this.cancelType = cancelType;
	}

	public Integer getBuyID() {
		return buyID;
	}

	public void setBuyID(Integer buyID) {
		this.buyID = buyID;
	}

	public Integer getSellID() {
		return sellID;
	}

	public void setSellID(Integer sellID) {
		this.sellID = sellID;
	}

	public BigDecimal getBuyPrize() {
		return buyPrize;
	}

	public void setBuyPrize(BigDecimal buyPrize) {
		this.buyPrize = buyPrize;
	}

	public BigDecimal getSellPrize() {
		return sellPrize;
	}

	public void setSellPrize(BigDecimal sellPrize) {
		this.sellPrize = sellPrize;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}
}
