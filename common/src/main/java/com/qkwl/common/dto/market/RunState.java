package com.qkwl.common.dto.market;

import java.io.Serializable;

/**
 * 运行状态
 * @author TT
 */
public class RunState implements Serializable {
	
	private static final Long serialVersionUID = 1L;
	
	// 当前币种ID
	private Integer coinId;
	// 买单数量
	private Integer buySize;
	// 卖单数量
	private Integer sellSize;
	// 运行间隔
	private Long runtime;
	// 当前币种ID
	private Integer huobiCoinId;
	// 委单数量
	private Integer huobiSize;
	// 运行间隔
	private Long huobiRuntime;

	public RunState() {
		this.coinId = 0;
		this.buySize = 0;
		this.sellSize = 0;
		this.runtime = 0l;
		this.huobiCoinId = 0;
		this.huobiSize = 0;
		this.huobiRuntime = 0l;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public Integer getBuySize() {
		return buySize;
	}

	public void setBuySize(Integer buySize) {
		this.buySize = buySize;
	}

	public Integer getSellSize() {
		return sellSize;
	}

	public void setSellSize(Integer sellSize) {
		this.sellSize = sellSize;
	}

	public Long getRuntime() {
		return runtime;
	}

	public void setRuntime(Long runtime) {
		this.runtime = runtime;
	}

	public Integer getHuobiCoinId() {
		return huobiCoinId;
	}

	public void setHuobiCoinId(Integer huobiCoinId) {
		this.huobiCoinId = huobiCoinId;
	}

	public Integer getHuobiSize() {
		return huobiSize;
	}

	public void setHuobiSize(Integer huobiSize) {
		this.huobiSize = huobiSize;
	}

	public Long getHuobiRuntime() {
		return huobiRuntime;
	}

	public void setHuobiRuntime(Long huobiRuntime) {
		this.huobiRuntime = huobiRuntime;
	}
}
