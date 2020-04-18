package com.qkwl.common.rpc.market;

/**
 * 行情接口
 * @author TT
 */
public interface IMarketService {
	
	/**
	 * 重置所有行情数据
	 */
	public void restMarket();

	/**
	 * 初始化行情实体,用于添加币和启动币
	 * @param coinId 虚拟币ID
	 */
	public void initMarketBean(int coinId);
	
	/**
	 * 初始化行情数据,用于添加币和启动币
	 * @param coinId 虚拟币ID
	 * @return boolean
	 */
	public boolean initMarket(int coinId);
}
