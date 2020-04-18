package com.qkwl.common.dto.daylog;

import java.io.Serializable;
import java.math.BigDecimal;

public class FCoinCharts implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private BigDecimal free;
	private BigDecimal frozen;
	private BigDecimal total;
	public BigDecimal getFree() {
		return free;
	}
	public void setFree(BigDecimal free) {
		this.free = free;
	}
	public BigDecimal getFrozen() {
		return frozen;
	}
	public void setFrozen(BigDecimal frozen) {
		this.frozen = frozen;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	

}
