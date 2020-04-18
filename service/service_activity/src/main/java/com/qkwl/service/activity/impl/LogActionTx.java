package com.qkwl.service.activity.impl;

import com.qkwl.common.dto.daylog.*;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("logActionTx")
public class LogActionTx {

	@Autowired
	private FDayOperatMapper fDayOperatMapper;
	@Autowired
	private FDayTradeCoinMapper fDayTradeCoinMapper;
	@Autowired
	private FDayCapitalRmbMapper fDayCapitalRmbMapper;
	@Autowired
	private FDayCapitalCoinMapper fDayCapitalCoinMapper;
	@Autowired
	private FDaySumMapper fDaySumMapper;
	
	/**
	 * 获取每日运营
	 * @param time 时间
	 * @param fagentid 卷商ID
	 * @return 每日运营
	 */
	public FDayOperat insertfDayOperat(String time, Integer fagentid) {
		List<FDayOperat> fDayOperats = this.fDayOperatMapper.selectByDate(time, fagentid);
		FDayOperat fDayOperat = fDayOperats.size() > 0 ? fDayOperats.get(0) : null;
		if (fDayOperat == null) {
			fDayOperat = new FDayOperat();
			fDayOperat.setFcode(0);
			fDayOperat.setFlogin(0);
			fDayOperat.setFmail(0);
			fDayOperat.setFrealname(0);
			fDayOperat.setFregister(0);
			fDayOperat.setFreplyquestion(0);
			fDayOperat.setFscore(0);
			fDayOperat.setFsms(0);
			fDayOperat.setFsubmitquestion(0);
			fDayOperat.setFvip6(0);
			fDayOperat.setFcreatetime(Utils.getTimestamp());
			fDayOperat.setFupdatetime(Utils.getTimestamp());
			fDayOperat.setFagentid(fagentid);
			fDayOperatMapper.insert(fDayOperat);
		}
		return fDayOperat;
	}

	/**
	 * 保存每日运营
	 * @param fDayOperat 每日运营
	 * @return
	 */
	public int updatefDayOperat(FDayOperat fDayOperat) {
		return this.fDayOperatMapper.updateByModel(fDayOperat);
	}

	/**
	 * 获取每日交易
	 * @param coinId 虚拟币ID
	 * @param time 时间
	 * @param fagentid 卷商ID
	 * @return 每日交易
	 */
	public FDayTradeCoin insertfDayTradeCoin(int coinId, String time, Integer fagentid) {
		List<FDayTradeCoin> fDayTradeCoins = this.fDayTradeCoinMapper.selectByDate(coinId, time, fagentid);
		FDayTradeCoin fDayTradeCoin = fDayTradeCoins.size() > 0 ? fDayTradeCoins.get(0) : null;
		if (fDayTradeCoin == null) {
			fDayTradeCoin = new FDayTradeCoin();
			fDayTradeCoin.setFcoinid(coinId);
			fDayTradeCoin.setFbuy(BigDecimal.ZERO);
			fDayTradeCoin.setFsell(BigDecimal.ZERO);
			fDayTradeCoin.setFbuyfees(BigDecimal.ZERO);
			fDayTradeCoin.setFsellfees(BigDecimal.ZERO);
			fDayTradeCoin.setFbuyperson(0);
			fDayTradeCoin.setFsellperson(0);
			fDayTradeCoin.setFbuyentrust(0);
			fDayTradeCoin.setFsellentrust(0);
			fDayTradeCoin.setFagentid(fagentid);
			fDayTradeCoin.setFupdatetime(Utils.getTimestamp());
			fDayTradeCoin.setFcreatetime(Utils.getTimestamp());
			fDayTradeCoinMapper.insert(fDayTradeCoin);
		}
		return fDayTradeCoin;
	}

	/**
	 * 保存每日交易
	 * @param fDayTradeCoin 每日交易
	 * @return
	 */
	public int updatefDayTradeCoin(FDayTradeCoin fDayTradeCoin) {
		return this.fDayTradeCoinMapper.updateByModel(fDayTradeCoin);
	}

	/**
	 * 获取每日人民币流水
	 * @param time 时间
	 * @param fagentid 卷商ID
	 * @return 每日人民币流水
	 */
	public FDayCapitalRmb insertFDayCapitalRmb(String time, Integer fagentid) {
		List<FDayCapitalRmb> fDayCapitalRmbs = this.fDayCapitalRmbMapper.selectByDate(time, fagentid);
		FDayCapitalRmb fDayCapitalRmb = fDayCapitalRmbs.size() > 0 ? fDayCapitalRmbs.get(0) : null;
		if (fDayCapitalRmb == null) {
			fDayCapitalRmb = new FDayCapitalRmb();
			fDayCapitalRmb.setFbank(BigDecimal.ZERO);
			fDayCapitalRmb.setFfees(BigDecimal.ZERO);
			fDayCapitalRmb.setFleverborrow(BigDecimal.ZERO);
			fDayCapitalRmb.setFleverrepay(BigDecimal.ZERO);
			fDayCapitalRmb.setFsuma(BigDecimal.ZERO);
			fDayCapitalRmb.setFwithdraw(BigDecimal.ZERO);
			fDayCapitalRmb.setFwithdrawother(BigDecimal.ZERO);
			fDayCapitalRmb.setFwithdrawwait(BigDecimal.ZERO);
			fDayCapitalRmb.setFwx(BigDecimal.ZERO);
			fDayCapitalRmb.setFzfb(BigDecimal.ZERO);
			fDayCapitalRmb.setFupdatetime(Utils.getTimestamp());
			fDayCapitalRmb.setFcreatetime(Utils.getTimestamp());
			fDayCapitalRmb.setFagentid(fagentid);
			fDayCapitalRmbMapper.insert(fDayCapitalRmb);
		}
		return fDayCapitalRmb;
	}

	/**
	 * 保存每日人民币流水
	 * @param fdCapitalRmb 每日人民币流水
	 * @return
	 */
	public int updateFDayCapitalRmb(FDayCapitalRmb fdCapitalRmb) {
		return this.fDayCapitalRmbMapper.updateByModel(fdCapitalRmb);
	}

	/**
	 * 获取每日虚拟币流水
	 * @param coinId 虚拟币ID
	 * @param time 时间
	 * @param fagentid 卷商ID
	 * @return 每日虚拟币流水
	 */
	public FDayCapitalCoin insertfDayCapitalCoin(int coinId, String time, Integer fagentid) {
		List<FDayCapitalCoin> fDayCapitalCoins = this.fDayCapitalCoinMapper.selectByDate(coinId, time, fagentid);
		FDayCapitalCoin fDayCapitalCoin = fDayCapitalCoins.size() > 0 ? fDayCapitalCoins.get(0) : null;
		if (fDayCapitalCoin == null) {
			fDayCapitalCoin = new FDayCapitalCoin();
			fDayCapitalCoin.setFcoinid(coinId);
			fDayCapitalCoin.setFcoinname("");
			fDayCapitalCoin.setFfees(BigDecimal.ZERO);
			fDayCapitalCoin.setFleverborrow(BigDecimal.ZERO);
			fDayCapitalCoin.setFleverrepay(BigDecimal.ZERO);
			fDayCapitalCoin.setFnetfees(BigDecimal.ZERO);
			fDayCapitalCoin.setFrecharge(BigDecimal.ZERO);
			fDayCapitalCoin.setFwithdraw(BigDecimal.ZERO);
			fDayCapitalCoin.setFwithdrawwait(BigDecimal.ZERO);
			fDayCapitalCoin.setFupdatetime(Utils.getTimestamp());
			fDayCapitalCoin.setFcreatetime(Utils.getTimestamp());
			fDayCapitalCoin.setFagentid(fagentid);
			fDayCapitalCoinMapper.insert(fDayCapitalCoin);
		}
		return fDayCapitalCoin;
	}

	/**
	 * 保存每日虚拟币流水
	 * @param fDayCapitalCoin 每日虚拟币流水
	 * @return
	 */
	public int updatefDayCapitalCoin(FDayCapitalCoin fDayCapitalCoin) {
		return this.fDayCapitalCoinMapper.updateByModel(fDayCapitalCoin);
	}

	/**
	 * 获取每日存量
	 * @param fcoinid 虚拟币ID
	 * @param time 时间
	 * @return 每日存量
	 */
	public FDaySum insertfDaySum(int fcoinid, String time) {
		List<FDaySum> fDaySums = this.fDaySumMapper.selectByDate(fcoinid, time);
		FDaySum fDaySum = fDaySums.size() > 0 ? fDaySums.get(0) : null;
		if (fDaySum == null) {
			fDaySum = new FDaySum();
			fDaySum.setFcoinid(fcoinid);
			fDaySum.setFtotle(BigDecimal.ZERO);
			fDaySum.setFrozen(BigDecimal.ZERO);
			fDaySum.setFcreatetime(Utils.getTimestamp());
			fDaySumMapper.insert(fDaySum);
		}
		return fDaySum;
	}

	/**
	 * 保存每日存量
	 * @param fDaySum 每日存量
	 * @return
	 */
	public int updatefDaySum(FDaySum fDaySum) {
		return this.fDaySumMapper.updateByModel(fDaySum);
	}
}
