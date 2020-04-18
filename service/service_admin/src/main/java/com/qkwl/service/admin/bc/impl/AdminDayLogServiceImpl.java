package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.service.admin.bc.dao.*;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.daylog.FDayCapitalCoin;
import com.qkwl.common.dto.daylog.FDayCapitalRmb;
import com.qkwl.common.dto.daylog.FDayOperat;
import com.qkwl.common.dto.daylog.FDaySum;
import com.qkwl.common.dto.daylog.FDayTradeCoin;
import com.qkwl.common.rpc.admin.IAdminDayLogService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 统计接口实现
 */
@Service("adminDayLogService")
public class AdminDayLogServiceImpl implements IAdminDayLogService {
	@Autowired
	private FDayCapitalCoinMapper dayCapitalCoinMapper;
	@Autowired
	private FDayCapitalRmbMapper dayCapitalRmbMapper;
	@Autowired
	private FDayOperatMapper dayOperatMapper;
	@Autowired
	private FDayTradeCoinMapper dayTradeCoinMapper;
	@Autowired
	private FDaySumMapper daySumMapper;
	@Autowired
	private SystemCoinTypeMapper systemCoinTypeMapper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	@Autowired
	private FEntrustHistoryMapper entrustHistoryMapper;
	@Autowired
	private SystemTradeTypeMapper systemTradeTypeMapper;
	
	/**
	 * 分页查询币种日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	@Override
	public Pagination<FDayCapitalCoin> selectDayCapitalCoinList(Pagination<FDayCapitalCoin> pageParam, FDayCapitalCoin filterParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("fcoinid", filterParam.getFcoinid());
		map.put("fcreatetime", filterParam.getFcreatetime());
		// 查询总数
		int count = dayCapitalCoinMapper.countAdminPageList(map);
		if(count > 0) {
			// 查询数据
			List<FDayCapitalCoin> list = dayCapitalCoinMapper.getAdminPageList(map);
			for (FDayCapitalCoin fDayCapitalCoin : list) {
				SystemCoinType coin = systemCoinTypeMapper.selectByPrimaryKey(fDayCapitalCoin.getFcoinid());
				if (coin != null) {
					fDayCapitalCoin.setFcoinname(coin.getName());
				}	
			}
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		pageParam.generate();
		return pageParam;
	}

	/**
	 * 分页查询人民币日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	@Override
	public Pagination<FDayCapitalRmb> selectDayCapitalRmbList(Pagination<FDayCapitalRmb> pageParam,
			FDayCapitalRmb filterParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("fcreatetime", filterParam.getFcreatetime());

		// 查询总数
		int count = dayCapitalRmbMapper.countPageList(map);
		if(count > 0) {
			// 查询数据
			List<FDayCapitalRmb> list = dayCapitalRmbMapper.getPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;		
	}

	/**
	 * 分页查询运营日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	@Override
	public Pagination<FDayOperat> selectDayOperatList(Pagination<FDayOperat> pageParam, FDayOperat filterParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("fcreatetime", filterParam.getFcreatetime());
		// 查询总数
		int count = dayOperatMapper.countPageList(map);
		if(count > 0) {
			// 查询数据
			List<FDayOperat> list = dayOperatMapper.getPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;		
	}

	/**
	 * 分页查询交易日统计
	 * @param pageParam 分页查询
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	@Override
	public Pagination<FDayTradeCoin> selectDayTradeCoinList(Pagination<FDayTradeCoin> pageParam,
			FDayTradeCoin filterParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("fcoinid", filterParam.getFcoinid());
		map.put("fcreatetime", filterParam.getFcreatetime());
		// 查询总数
		int count = dayTradeCoinMapper.countPageList(map);
		if(count > 0) {
			// 查询数据
			List<FDayTradeCoin> list = dayTradeCoinMapper.getPageList(map);
			for (FDayTradeCoin fDayTradeCoin : list) {
				SystemTradeType trade = systemTradeTypeMapper.selectByPrimaryKey(fDayTradeCoin.getFcoinid());
				if (trade != null) {
					SystemCoinType coin = systemCoinTypeMapper.selectByPrimaryKey(trade.getSellCoinId());
					fDayTradeCoin.setFcoinname(coin.getName());
				}
			}
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;				
	}

	/**
	 * 分页查询资产存量统计
	 * @param page 分页参数
	 * @param sum 实体参数
	 * @return 查询记录列表
	 */
	@Override
	public Pagination<FDaySum> selectDaySumList(Pagination<FDaySum> page, FDaySum sum) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("fcoinid", sum.getFcoinid());
		map.put("fcreatetime", sum.getFcreatetime());
		// 查询总数
		int count = daySumMapper.countPageList(map);
		if(count > 0) {
			// 查询数据
			List<FDaySum> list = daySumMapper.getPageList(map);
			for (FDaySum fDaySum : list) {
				SystemCoinType coin = systemCoinTypeMapper.selectByPrimaryKey(fDaySum.getFcoinid());
				if (coin != null) {
					fDaySum.setFcoinname(coin.getName());
				}
				if (fDaySum.getFcoinid() == 0) {
					fDaySum.setFcoinname("人民币");
				}
			}
			// 设置返回数据
			page.setData(list);
		}
		page.setTotalRows(count);
		
		return page;				
	}
	
	/**
	 * 手动更新资产存量统计
	 * @return 是否执行成功
	 * @throws BCException 更新失败
	 * @see com.qkwl.common.rpc.admin.IAdminDayLogService#updateDaySum()
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateDaySum() throws BCException{
		// 更新RMB存量
		FDaySum fDaySumRMB = selectOrUpdateDaySum(0, Utils.dateFormatYYYYMMDD(Utils.getTimestamp()));
		Map<String, Object> sumlWallet = userCoinWalletMapper.selectSum(fDaySumRMB.getFcoinid());
		fDaySumRMB.setFtotle(new BigDecimal(sumlWallet.get("total").toString()));
		fDaySumRMB.setFrozen(new BigDecimal(sumlWallet.get("frozen").toString()));
		fDaySumRMB.setFcreatetime(Utils.getTimestamp());
		if(savefDaySum(fDaySumRMB) < 1){
			throw new BCException("更新RMB存量失败");
		}
		// 币种
		List<SystemCoinType> fVirtualCoinTypes = systemCoinTypeMapper.selectAll();
		for (SystemCoinType fVirtualCoinType : fVirtualCoinTypes) {
			if (fVirtualCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				continue;
			}
			// 当前时间
			String nowTime = Utils.dateFormatYYYYMMDD(Utils.getTimestamp());
			int fcoinid = fVirtualCoinType.getId();
			// 更新虚拟币存量
			FDaySum fDaySum = selectOrUpdateDaySum(fVirtualCoinType.getId(), nowTime);
			Map<String, Object> sumVirtualWallet = userCoinWalletMapper.selectSum(fcoinid);
			fDaySum.setFtotle(new BigDecimal(sumVirtualWallet.get("total").toString()));
			fDaySum.setFrozen(new BigDecimal(sumVirtualWallet.get("frozen").toString()));
			fDaySum.setFcreatetime(Utils.getTimestamp());
			if(savefDaySum(fDaySum) < 1){
				throw new BCException("更新虚拟币存量失败");
			}
		}
		return true;
	}
	
	
	/**
	 * 获取单个币种的存量
	 * @param coinid 币种id
	 * @return 存量键值对
	 * @see com.qkwl.common.rpc.admin.IAdminDayLogService#selectCoinSum(int)
	 */
	public Map<String, Object> selectCoinSum(int coinid){
		return  userCoinWalletMapper.selectSum(coinid);
	}
	
	
	/**
	 * 手动刷新交易日统计
	 * @return 是否执行成功
	 * @throws BCException 刷新失败
	 * @see com.qkwl.common.rpc.admin.IAdminDayLogService#updateDayTrade()
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateDayTrade() throws BCException{
		// 币种
		// List<SystemCoinType> fVirtualCoinTypes = systemCoinTypeMapper.selectAll();
		List<SystemTradeType> tradeTypeList = systemTradeTypeMapper.selectAll();
		for (SystemTradeType tradeType : tradeTypeList) {
			// 当前时间
			String nowTime = Utils.dateFormatYYYYMMDD(Utils.getTimestamp());
			int tradeTypeId = tradeType.getId();
			
			// 更新买卖单
			FDayTradeCoin fDayTradeCoin = selectOrUpdateDayTradeCoin(tradeType.getId(), nowTime);
			Map<String, Object> entrustTotal = entrustHistoryMapper.selectTotal(tradeTypeId, nowTime);
			Map<String, Object> entrustPerson = entrustHistoryMapper.selectPerson(tradeTypeId, nowTime);
			fDayTradeCoin.setFbuy(entrustTotal != null ? new BigDecimal(entrustTotal.get("sumBuy").toString()) : BigDecimal.ZERO);
			fDayTradeCoin.setFsell(entrustTotal != null ? new BigDecimal(entrustTotal.get("sumSell").toString()) : BigDecimal.ZERO);
			fDayTradeCoin.setFbuyfees(entrustTotal != null ? new BigDecimal(entrustTotal.get("feesBuy").toString()) : BigDecimal.ZERO);
			fDayTradeCoin.setFsellfees(entrustTotal != null ? new BigDecimal(entrustTotal.get("feesSell").toString()) : BigDecimal.ZERO);
			fDayTradeCoin.setFbuyentrust(Integer.parseInt(entrustTotal != null ? entrustTotal.get("buyCount").toString() : "0"));
			fDayTradeCoin.setFsellentrust(Integer.parseInt(entrustTotal != null ? entrustTotal.get("sellCount").toString() : "0"));
			fDayTradeCoin.setFbuyperson(Integer.parseInt(entrustPerson != null ? entrustPerson.get("buyCount").toString() : "0"));
			fDayTradeCoin.setFsellperson(Integer.parseInt(entrustPerson != null ? entrustPerson.get("sellCount").toString() : "0"));
			fDayTradeCoin.setFupdatetime(Utils.getTimestamp());
			fDayTradeCoin.setFagentid(tradeType.getAgentId());
			if(savefDayTradeCoin(fDayTradeCoin) < 1){
				throw new BCException("更新交易流水失败");
			}
		}
		return true;
	}
	
	/**
	 * 获取每日交易
	 * @param coinId 币种
	 * @param time 时间
	 * @return FDayTradeCoin 交易统计记录
	 */
	private FDayTradeCoin selectOrUpdateDayTradeCoin(int coinId, String time) {
		FDayTradeCoin fDayTradeCoin = dayTradeCoinMapper.selectByDate(coinId,time);
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
			fDayTradeCoin.setFupdatetime(Utils.getTimestamp());
			fDayTradeCoin.setFcreatetime(Utils.getTimestamp());
			fDayTradeCoin.setFagentid(0);
			dayTradeCoinMapper.insert(fDayTradeCoin);
		}
		return fDayTradeCoin;
	}

	/**
	 * 更新每日交易
	 * @param fDayTradeCoin 交易日统计实体
	 * @return 更新记录数
	 */
	private int savefDayTradeCoin(FDayTradeCoin fDayTradeCoin) {
		return dayTradeCoinMapper.updateByModel(fDayTradeCoin);
	}  
	
	/**
	 * 获取每日存量
	 * @param fcoinid 币种
	 * @param time 时间
	 * @return FDayTradeCoin 资产存量统计记录
	 */
	private FDaySum selectOrUpdateDaySum(int fcoinid ,String time) {
		FDaySum fDaySum = daySumMapper.selectByDate(fcoinid, time);
		if (fDaySum == null) {
			fDaySum = new FDaySum();
			fDaySum.setFcoinid(fcoinid);
			fDaySum.setFtotle(BigDecimal.ZERO);
			fDaySum.setFrozen(BigDecimal.ZERO);
			fDaySum.setFcreatetime(Utils.getTimestamp());
			daySumMapper.insert(fDaySum);
		}
		return fDaySum;
	}

	/**
	 * 更新每日存量
	 * @param fDaySum 运营日统计实体
	 * @return 更新记录数
	 */
	private int savefDaySum(FDaySum fDaySum) {
		return daySumMapper.updateByModel(fDaySum);
	}

}
