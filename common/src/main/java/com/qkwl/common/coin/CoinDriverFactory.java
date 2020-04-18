package com.qkwl.common.coin;

import com.qkwl.common.coin.driver.*;

import java.math.BigInteger;


/**
 * CoinDriverFactory
 * @author jany
 */
public class CoinDriverFactory {
	private final int coinType;
	private final String accessKey;
	private final String secretKey;
	private final String ip;
	private final String port;
	private final String pass;
	private final BigInteger assetId;
	private final String sendAccount;
	private final String contractAccount;
	private final int contractWei;

	private CoinDriverFactory(Builder builder) {
		this.coinType = builder.coinType;
		this.accessKey = builder.accessKey;
		this.secretKey = builder.secretKey;
		this.ip = builder.ip;
		this.port = builder.port;
		this.pass = builder.pass;
		this.assetId = builder.assetId;
		this.sendAccount = builder.sendAccount;
		this.contractAccount = builder.contractAccount;
		this.contractWei = builder.contractWei;
	}
	/**
	 * 创建CoinDriver
	 */
	public CoinDriver getDriver() {
		switch (coinType) {
		case 2: // BTC
			return new BTCDriver(accessKey, secretKey, ip, port, pass, coinType);
		case 3: // ETH
		case 5: // ETC
			return new ETCDriver(ip, port, pass, coinType, sendAccount, contractAccount, contractWei);
		case 4: // ICS
		case 8: // MIC
			return new ICSDriver(accessKey, secretKey, ip, port, pass, assetId, coinType);
		case 6: // ETP
			return new ETPDriver(accessKey, secretKey, ip, port, pass, coinType, sendAccount);
		case 7: // GXB
			return new GXBDriver(coinType, accessKey, secretKey, ip, port, sendAccount);
		case 9: // EOS
			return new EOSDriver(accessKey,secretKey,ip,port,pass,coinType,sendAccount,contractAccount,contractWei);
		case 10: // USDT
			return new USDTDriver(accessKey,secretKey,ip,port,pass,1,sendAccount);
		default:
			return null;
		}
	}

	public static class Builder
	{
		private int coinType;
		private String accessKey;
		private String secretKey;
		private String ip;
		private String port;
		private String pass;
		private BigInteger assetId;
		private String sendAccount;
		private String contractAccount;
		private int contractWei;

		public Builder(int coinType, String ip, String port) {
			this.coinType = coinType;
			this.ip = ip;
			this.port = port;
		}

		public Builder accessKey(String accessKey) {
			this.accessKey = accessKey;
			return this;
		}

		public Builder secretKey(String secretKey) {
			this.secretKey = secretKey;
			return this;
		}

		public Builder pass(String pass) {
			this.pass = pass;
			return this;
		}

		public Builder assetId(BigInteger assetId) {
			this.assetId = assetId;
			return this;
		}

		public Builder sendAccount(String sendAccount) {
			this.sendAccount = sendAccount;
			return this;
		}

		public Builder contractAccount(String contractAccount) {
			this.contractAccount = contractAccount;
			return this;
		}

		public Builder contractWei(int contractWei) {
			this.contractWei = contractWei;
			return this;
		}

		public CoinDriverFactory builder() {
			return new CoinDriverFactory(this);
		}
	}
}
