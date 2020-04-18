package com.qkwl.common.coin;

import java.math.BigDecimal;
import java.util.List;

/**
 * 虚拟币驱动
 * @author jany
 */
public interface CoinDriver {
	
	/**
	 * 获取分类
	 * @return
	 */
	Integer getCoinSort();

	/**
	 * 获取高度
	 * @return
	 */
	Integer getBestHeight();
	
	/**
	 * 获取余额
	 * @return
	 */
	BigDecimal getBalance();
	
	/**
	 * 生成地址
	 * @param uId
	 * @return
	 */
	String getNewAddress(String time);
	
	/**
	 * 钱包加锁
	 */
	boolean walletLock();
	
	/**
	 * 钱包解锁
	 * @param times
	 */
	boolean walletPassPhrase(int times);
	
	/**
	 * 设置手续费
	 * @param fee
	 * @return
	 */
	boolean setTxFee(BigDecimal fee);
	
	/**
	 * 获取交易列表
	 * @param count
	 * @param from
	 * @return
	 */
	List<TxInfo> listTransactions(int count, int from);
	
	/**
	 * 获取交易详情
	 * @param txId
	 * @return
	 */
	TxInfo getTransaction(String txId);
	
	/**
	 * 发送
	 * @param address
	 * @param account
	 * @param comment
	 * @param fee
	 * @return
	 */
	String sendToAddress(String to, BigDecimal amount, String comment, BigDecimal fee);

	/**
	 * 公信宝发送
	 * @param address
	 * @param amount
	 * @param comment
	 * @param fee
	 * @param memo
	 * @return
	 */
	String sendToAddress(String address, BigDecimal amount, String comment, BigDecimal fee, String memo);
	
	/**
	 * ETC发送
	 * @param from
	 * @param to
	 * @param amount
	 * @param nonce
	 * @return
	 */
	String sendToAddress(String to, String amount, String nonce);

	/**
	 * ETH获取nonce
	 * @return
	 */
	Integer getTransactionCount();
	/**
	 * ETCSHA3签名
	 * @param str
	 * @return
	 */
	String getETCSHA3(String str);
}
