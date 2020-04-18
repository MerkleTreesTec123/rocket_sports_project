package com.qkwl.service.admin.bc.impl;

import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FUserPushDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import com.qkwl.common.dto.log.FLogModifyCapitalOperation;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.*;
import com.qkwl.service.admin.bc.utils.MQSend;
import com.qkwl.service.common.mapper.CommonUserCoinWalletMapper;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户资金操作
 * @author ZKF
 */
@Service("adminUserCapitalService")
public class AdminUserCapitalServiceImpl implements IAdminUserCapitalService {

	private static final Logger logger = LoggerFactory.getLogger(AdminUserCapitalServiceImpl.class);
	
	@Autowired
	private FWalletCapitalOperationMapper walletCapitalOperationMapper;
	@Autowired
	private FVirtualCapitalOperationMapper virtualCapitalOperationMapper;
	@Autowired
	private FLogModifyCapitalOperationMapper logModifyCapitalOperationMapper;
	@Autowired
	private CommonUserCoinWalletMapper userCoinWalletMapper;
	@Autowired
	private UserCoinWalletMapper operationCoinWalletMapper;
	@Autowired
	private FUserVirtualAddressMapper userVirtualAddressMapper;
	@Autowired
	private FLogConsoleVirtualRechargeMapper logConsoleVirtualRechargeMapper;
//	@Autowired
//	private RedisHelper redisHelper;
	@Autowired
	private MQSend mqSend;
	@Autowired
	private FUserPushMapper userPushMapper;
//	@Autowired
//	private FActivityRecordMapper activityRecordMapper;
//	@Autowired
//	private FUserScoreMapper userScoreMapper;
//	@Autowired
//	private FUserMapper userMapper;

	@Autowired
	private FinanceRecordMapper financeRecordMapper;

	/**
	 * 分页查询钱包操作记录
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectWalletCapitalOperationList(com.qkwl.common.dto.common.Pagination,
	 * com.qkwl.common.dto.capital.FWalletCapitalOperationDTO, List,Boolean,Boolean)
	 */
	@Override
	public Pagination<FWalletCapitalOperationDTO> selectWalletCapitalOperationList(
            Pagination<FWalletCapitalOperationDTO> pageParam, FWalletCapitalOperationDTO record, List<Integer> status,
            Boolean limit, Boolean isvip6) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("finouttype", record.getFinouttype());
		map.put("fstatus", status.size() > 0 ? status : null);
		map.put("fid", record.getFid());
		map.put("isvip6", isvip6);
		map.put("start", pageParam.getBegindate());
		map.put("end", pageParam.getEnddate());
		map.put("amountLimit",limit);
		map.put("serialno",record.getFserialno());
		// 查询总数
		int count = walletCapitalOperationMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<FWalletCapitalOperationDTO> list = walletCapitalOperationMapper.getAdminPageList(map);
//			String exchangeRate = redisHelper.getSystemArgs("rechargeUSDTPrice");
//			BigDecimal exchangeRateNumber;
//        if (exchangeRate != null && exchangeRate.length() != 0) {
//            exchangeRateNumber = new BigDecimal(exchangeRate);
//			for (FWalletCapitalOperationDTO model :list) {
//				model.setfRmbAmount(model.getFamount().multiply(exchangeRateNumber));
//
//			}
//        }

//			LinkedList<FWalletCapitalOperationDTO> datalist = new LinkedList<>();
//			for(FWalletCapitalOperationDTO dto : list){
//				FUserScore score = userScoreMapper.selectByUid(dto.getFuid());
//				if(score != null && score.getFlevel().equals(6)){
//					dto.setLevel(6);
//					datalist.addFirst(dto);
//				}else{
//					datalist.add(dto);
//				}
//			}
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 根据id查询钱包操作记录
	 * @param fid 操作id
	 * @return 钱包操作实体
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectById(int)
	 */
	@Override
	public FWalletCapitalOperationDTO selectById(int fid) {
		return walletCapitalOperationMapper.selectByPrimaryKey(fid);
	}



	/**
	 * 获取用户钱包
	 * @param fuid 用户id
	 * @return 用户钱包实体
	 */
	@Override
	public UserCoinWallet selectUserWallet(int fuid, int fcoinid) {
		return userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinid);
	}

	/**
	 * 更新钱包操作
	 * @param admin 管理员
 	 * @param capital 钱包操作记录
	 * @param amount 总量
	 * @param isRecharge 是否充值
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateWalletCapital(FAdmin admin, FWalletCapitalOperationDTO capital,
                                       BigDecimal amount, boolean isRecharge)  throws Exception {
		UserCoinWallet userWallet = userCoinWalletMapper.selectByUidAndCoinId(capital.getFuid(), capital.getFcoinid());
		if (isRecharge) {
			// 审核充值
            userWallet.setTotal(amount);
            userWallet.setFrozen(BigDecimal.ZERO);
			///userWallet.setTotal(MathUtils.add(userWallet.getTotal(), amount));

//            FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
//            financeRecordDTO.setAmount(amount);
//            financeRecordDTO.setCreateDate(new Date());
//            financeRecordDTO.setUpdateDate(new Date());
//            financeRecordDTO.setUid(capital.getFuid());
//            financeRecordDTO.setOperation(FinanceRecordOperationEnum.In.getCode());
//            financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
//            financeRecordDTO.setRelationCoinId(capital.getFcoinid());
//            financeRecordDTO.setRelationCoinName("");
//            financeRecordDTO.setRelationId(capital.getFid());
//            financeRecordDTO.setTxId("");
//            financeRecordDTO.setRechargeAddress("");
//            financeRecordDTO.setWithdrawAddress("");
//            financeRecordDTO.setMemo("");
//            financeRecordDTO.setFee(BigDecimal.ZERO);
//            financeRecordDTO.setWalletOperationDate(new Date());
//
//            financeRecordMapper.insert(financeRecordDTO);


			Map<String,Object> params = new HashMap<>();
			params.put("operation", FinanceRecordOperationEnum.Receive.getCode());
			params.put("relationId",capital.getFid());
			List<FinanceRecordDTO> financeRecordDTOS = financeRecordMapper.selectByParams(params);
			if (financeRecordDTOS == null || financeRecordDTOS.size() != 1) {
				throw new BCException("提现日志 FinanceRecord 不存在记录");
			}
			FinanceRecordDTO financeRecordDTO = financeRecordDTOS.get(0);
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			financeRecordDTO.setTxId("");
			financeRecordDTO.setUpdateDate(new Date());
			financeRecordDTO.setWalletOperationDate(new Date());
			if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
				throw new BCException("更新日志失败");
			}

		} else {
			if(MathUtils.sub(userWallet.getFrozen(),amount).compareTo(BigDecimal.ZERO)<0){
				throw new Exception("钱包冻结异常");
			}

			Map<String,Object> params = new HashMap<>();
			params.put("operation", FinanceRecordOperationEnum.Transfer.getCode());
			params.put("relationId",capital.getFid());
			List<FinanceRecordDTO> financeRecordDTOS = financeRecordMapper.selectByParams(params);
			if (financeRecordDTOS == null || financeRecordDTOS.size() != 1) {
				throw new BCException("提现日志 FinanceRecord 不存在记录");
			}
			FinanceRecordDTO financeRecordDTO = financeRecordDTOS.get(0);
			financeRecordDTO.setTxId("");
			financeRecordDTO.setUpdateDate(new Date());
			financeRecordDTO.setWalletOperationDate(new Date());

			// 审核提现
			if (capital.getFstatus() == CapitalOperationOutStatus.OperationSuccess) {
				userWallet.setFrozen(MathUtils.positive2Negative(amount));
				userWallet.setTotal(BigDecimal.ZERO);
//
//                FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
//                financeRecordDTO.setAmount(amount);
//                financeRecordDTO.setCreateDate(Utils.getTimestamp());
//                financeRecordDTO.setUpdateDate(Utils.getTimestamp());
//                financeRecordDTO.setUid(capital.getFuid());
//                financeRecordDTO.setOperation(FinanceRecordOperationEnum.Out.getCode());
//                financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
//                financeRecordDTO.setRelationCoinId(capital.getFcoinid());
//                financeRecordDTO.setRelationCoinName("");
//                financeRecordDTO.setRelationId(capital.getFid());
//                financeRecordDTO.setTxId("");
//                financeRecordDTO.setRechargeAddress("");
//                financeRecordDTO.setWithdrawAddress("");
//                financeRecordDTO.setMemo("");
//                financeRecordDTO.setFee(capital.getFfees() == null?BigDecimal.ZERO:capital.getFfees());
//                financeRecordDTO.setWalletOperationDate(new Date());
//                financeRecordMapper.insert(financeRecordDTO);
				financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			}
			// 取消提现
			if (capital.getFstatus() == CapitalOperationOutStatus.Cancel) {
				userWallet.setFrozen(MathUtils.positive2Negative(amount));
				userWallet.setTotal(amount);
				financeRecordDTO.setStatus(FinanceRecordStatusEnum.Cancel.getCode());
			}

			if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
				throw new BCException("更新日志失败");
			}

		}
		//充值提成推荐人提成
//		if(isRecharge && capital.getFstatus().equals(CapitalOperationInStatus.Come)){
//			try {
//				BigDecimal percent = new BigDecimal(redisHelper.getSystemArgs(ArgsConstant.REFERRER_RECORD_PERCENT));
//				if(percent.compareTo(BigDecimal.ZERO) > 0){
//					FUser user = userMapper.selectByPrimaryKey(capital.getFuid());
//					if (user.getFintrouid() != null && user.getFhasrealvalidate() && user.getFintrouid() != null && user.getFintrouid() > 0) {
//						FUser introUser = userMapper.selectByPrimaryKey(user.getFintrouid());
//						if (introUser != null && introUser.getFhasrealvalidate()) {
//							FActivityRecord referrerRecord = new FActivityRecord();
//							referrerRecord.setFstate(ReferrerRecordStateEnum.Non_Release.getCode());
//							referrerRecord.setFtype(1);//充值赠送
//							referrerRecord.setFamount(MathUtils.toScaleNum(MathUtils.mul(capital.getFamount(), percent),
//									MathUtils.ENTER_COIN_SCALE));
//							referrerRecord.setFrecharge(capital.getFamount());
//							referrerRecord.setFuid(introUser.getFid());
//							referrerRecord.setFintrouid(capital.getFuid());
//							referrerRecord.setFremark("推荐人" + user.getFid() + capital.getFbank() +"，充值金额:"
//									+ MathUtils.toScaleNum(capital.getFamount(), MathUtils.ENTER_CNY_SCALE));
//							referrerRecord.setVersion(0);
//							referrerRecord.setFcoinid(capital.getFcoinid());
//							referrerRecord.setFcreatetime(new Date());
//							if (activityRecordMapper.insert(referrerRecord) <= 0) {
//								throw new Exception("update referrerRecord err");
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				throw new Exception("update activity err");
//			}
//		}

		userWallet.setGmtModified(Utils.getTimestamp());
		if (walletCapitalOperationMapper.updateByPrimaryKey(capital) <= 0) {
			throw new Exception("update Capital err");
		}
		if(userCoinWalletMapper.changeFinance(userWallet) <= 0){
			throw new Exception("update userWallet err");
		}
		// MQ
		if (isRecharge) {
			// RMB充值
			mqSend.SendUserAction(capital.getFagentid(), capital.getFuid(), LogUserActionEnum.RMB_RECHARGE, capital.getFtype(), amount);
			mqSend.SendAdminAction(capital.getFagentid(), admin.getFid(), capital.getFuid(), LogAdminActionEnum.SYSTEM_RMB_RECHARGE, capital.getFtype(), amount);
		} else {
			// RMB提现
			if (capital.getFstatus() == CapitalOperationOutStatus.OperationSuccess) {
				mqSend.SendUserAction(capital.getFagentid(), capital.getFuid(), LogUserActionEnum.RMB_WITHDRAW, CapitalOperationTypeEnum.RMB_OUT ,amount, capital.getFfees());
				mqSend.SendAdminAction(capital.getFagentid(), admin.getFid(), capital.getFuid(), LogAdminActionEnum.SYSTEM_COIN_WITHDRAW, capital.getFtype(), amount);
			}
			// RMB取消提现
			if (capital.getFstatus() == CapitalOperationOutStatus.Cancel) {
				mqSend.SendUserAction(capital.getFagentid(), capital.getFuid(), LogUserActionEnum.RMB_WITHDRAW_CANCEL, CapitalOperationTypeEnum.RMB_OUT, amount, "admin_" + admin.getFid());
				mqSend.SendAdminAction(capital.getFagentid(), admin.getFid(), capital.getFuid(), LogAdminActionEnum.SYSTEM_RMB_WITHDRAW, capital.getFtype(), amount);
			}
		}
		return true;
	}

	/**
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result updateOnlineWithdrawStatus(Integer adminId, String orderId, Boolean status) throws Exception {
		FWalletCapitalOperationDTO capital = walletCapitalOperationMapper.selectBySerialNumber(orderId);
		if(capital == null){
			logger.error("无提现记录,orderId:{}",orderId);
			return Result.failure("无提现记录");
		}
		//状态判断
		if(!capital.getFstatus().equals(CapitalOperationOutStatus.OnLineLock)){
			logger.error("状态错误，订单号为：{},状态为：{}",capital.getFstatus(),orderId);
			return Result.failure("状态错误");
		}

		capital.setFupdatetime(Utils.getTimestamp());
		if(status) {
			capital.setFstatus(CapitalOperationOutStatus.OperationSuccess);
		} else {
			capital.setFstatus(CapitalOperationOutStatus.OperationLock);
			capital.setFremark("提现回调状态为失败，请联系技术确认");
		}
		//更新记录信息
		if(walletCapitalOperationMapper.updateByPrimaryKey(capital) <= 0){
			return Result.failure("更新记录失败");
		}
		if(status) {
			BigDecimal amount = capital.getFamount();
			BigDecimal frees = capital.getFfees();
			BigDecimal totalAmt = MathUtils.add(amount, frees);

			UserCoinWallet userWallet = userCoinWalletMapper.selectByUidAndCoinId(capital.getFuid(), capital.getFcoinid());
			// 审核提现
			userWallet.setFrozen(MathUtils.positive2Negative(totalAmt));
			userWallet.setTotal(BigDecimal.ZERO);
			userWallet.setGmtModified(Utils.getTimestamp());
			if(userCoinWalletMapper.changeFinance(userWallet) <= 0){
				throw new Exception("update userWallet err");
			}

			mqSend.SendUserAction(capital.getFagentid(), capital.getFuid(), LogUserActionEnum.RMB_WITHDRAW_ONLINE, CapitalOperationTypeEnum.RMB_OUT ,amount, capital.getFfees());

		}
		return Result.success("提现成功！");
	} **/


	/**
	 * 更新钱包操作记录
	 * @param capital 操作记录
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#updateWalletCapital(FWalletCapitalOperationDTO)
	 */
	@Override
	public boolean updateWalletCapital(FWalletCapitalOperationDTO capital) throws BCException {
		int result = walletCapitalOperationMapper.updateByPrimaryKey(capital);
		if (result <= 0) {
			return false;
		}
		mqSend.SendAdminAction(capital.getFagentid(), capital.getFadminid(), capital.getFuid(), LogAdminActionEnum.CANCEL_RMB_RECHARGE, capital.getFtype(), capital.getFamount());
		return true;
	}

	/**
	 * 是否第一次充值
	 * @param fuid 用户id
	 * @return 是否第一次充值
	 */
	@Override
	public boolean selectIsFirstCharge(int fuid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
		map.put("fstatus", CapitalOperationInStatus.Come);
		int countCny = walletCapitalOperationMapper.countWalletCapitalOperation(map);
		if(countCny > 0){
			return false;
		}

		map.clear();
		map.put("fuid", fuid);
		map.put("ftype", VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
		map.put("fstatus", VirtualCapitalOperationInStatusEnum.SUCCESS);
		int countCoin = virtualCapitalOperationMapper.countVirtualCapitalOperation(map);

		return (countCny + countCoin) <= 0;
	}

	/**
	 * 更新充值记录
	 * @param capital 钱包操作记录
	 * @param capitalLog 修改日志
	 * @return 是否修改成功
	 * @throws BCException 更新失败
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#updateModifyCapital(FWalletCapitalOperationDTO, com.qkwl.common.dto.log.FLogModifyCapitalOperation)
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateModifyCapital(FWalletCapitalOperationDTO capital, FLogModifyCapitalOperation capitalLog) throws BCException {
		int result = walletCapitalOperationMapper.updateByPrimaryKey(capital);
		if (result <= 0) {
			return false;
		}
		result = logModifyCapitalOperationMapper.insert(capitalLog);
		if (result <= 0) {
			throw new BCException();
		}

		mqSend.SendAdminAction(0, capitalLog.getFadminid(), capital.getFuid(), LogAdminActionEnum.SYSTEM_MODIFY_RMB_RECHARGE, capital.getFtype(), capitalLog.getFamount());
		return true;
	}

	/**
	 * 分页查询虚拟币操作记录
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectVirtualCapitalOperationList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO, List,
	 * Boolean)
	 */
	@Override
	public Pagination<FVirtualCapitalOperationDTO> selectVirtualCapitalOperationList(
            Pagination<FVirtualCapitalOperationDTO> pageParam, FVirtualCapitalOperationDTO record,
            List<Integer> status, Boolean isvip6) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("ftype", record.getFtype());
		map.put("fcoinid", record.getFcoinid());
		map.put("fstatus", status.size() > 0 ? status : null);
		map.put("isvip6", isvip6);
		map.put("start", pageParam.getBegindate());
		map.put("end", pageParam.getEnddate());
		// 查询总数
		int count = virtualCapitalOperationMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<FVirtualCapitalOperationDTO> list = virtualCapitalOperationMapper.getAdminPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 根据id查询虚拟币操作记录
	 * @param fid 操作id
	 * @return 操作记录实体
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectVirtualById(int)
	 */
	@Override
	public FVirtualCapitalOperationDTO selectVirtualById(int fid) {
		return virtualCapitalOperationMapper.selectAllById(fid);
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result resetAuditVirtualCapitalOperation(Integer id, Integer adminId) throws Exception {
		FVirtualCapitalOperationDTO operation = virtualCapitalOperationMapper.selectAllById(id);
		if(operation == null){
			return Result.failure("记录未找到");
		}
		if(!operation.getFstatus().equals(VirtualCapitalOperationOutStatusEnum.OperationSuccess)
				|| !operation.getFtype().equals(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode())){
			return Result.failure("只能重置提现成功数据");
		}
		operation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
		operation.setFupdatetime(new Date());
		operation.setFadminid(adminId);
		if(virtualCapitalOperationMapper.updateByPrimaryKey(operation)<=0){
			return Result.failure("重置失败");
		}
		UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoinId(operation.getFuid(),operation.getFcoinid());
		if(wallet == null){
			throw new Exception("wallet is null,uid:"+operation.getFuid()+",coinId： "+operation.getFcoinid());
		}
        BigDecimal frozen = MathUtils.add(MathUtils.add(operation.getFamount(), operation.getFfees()), operation.getFbtcfees());
        wallet.setFrozen(frozen);
        wallet.setTotal(BigDecimal.ZERO);
		wallet.setGmtModified(new Date());
		if(userCoinWalletMapper.changeFinance(wallet)<=0){
			throw new Exception("wallet is err,uid:"+operation.getFuid()+",coinId： "+operation.getFcoinid());
		}
		mqSend.SendAdminAction(operation.getFagentid(), operation.getFadminid(), operation.getFuid(), LogAdminActionEnum.RESET_COIN_WITHDRAW, operation.getFcoinid(), operation.getFamount(),operation.getFuniquenumber());
		return Result.success("重置成功");
	}

	/**
	 * 获取用户虚拟币钱包
	 * @param fuid 用户id
	 * @param fcoinid 币种id
	 * @return 虚拟币钱包实体
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectUserVirtualWallet(int, int)
	 */
	@Override
	public UserCoinWallet selectUserVirtualWallet(int fuid, int fcoinid) {
		return userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinid);
	}

	/**
	 * 获取地址数量
	 * @param address 地址
	 * @return 数量
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectAddressNum(String)
	 */
	@Override
	public int selectAddressNum(String address) {
		return userVirtualAddressMapper.getAddressNum(address);
	}

	/**
	 * 更新用户虚拟币操作
	 * @param recordId 虚拟币操作
	 * @param amount 总量
	 * @param addressNum 地址数量
 	 * @param coinDriver 钱包工具
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */

//	@Override
//	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//	public boolean updateVirtualCapital(Integer recordId, Integer adminId, BigDecimal amount, int addressNum,
//										CoinDriver coinDriver) throws BCException {
//
//		FVirtualCapitalOperationDTO record = virtualCapitalOperationMapper.selectAllById(recordId);
//		if(!record.getFstatus().equals(VirtualCapitalOperationOutStatusEnum.LockOrder)){
//			throw new BCException("订单锁定中，不允许重复操作！");
//		}
//
//		int fuid = record.getFuid();
//		int fcoinid = record.getFcoinid();
//		UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinid);
//		if (userVirtualWallet == null) {
//			throw new BCException("虚拟币钱包为空");
//		}
//		if (MathUtils.sub(userVirtualWallet.getFrozen(), amount).compareTo(BigDecimal.ZERO) == -1) {
//			throw new BCException("虚拟币钱包冻结余额不足");
//		}
//		// 提现人钱包
//		userVirtualWallet.setFrozen(MathUtils.positive2Negative(amount));
//		userVirtualWallet.setTotal(BigDecimal.ZERO);
//		userVirtualWallet.setGmtModified(Utils.getTimestamp());
//
//		record.setFadminid(adminId);
//		record.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
//		// 提现地址
//		String address = record.getFwithdrawaddress();
//		// 平台互转
//		if (addressNum > 0) {
//			List<FUserVirtualAddressDTO> userVirtualAddresses = userVirtualAddressMapper.getUserByAddress(address);
//			if (userVirtualAddresses == null || userVirtualAddresses.size() != 1) {
//				throw new BCException("平台互转转入地址为空");
//			}
//			// 转入地址信息
//			FUserVirtualAddressDTO userVirtualAddress = userVirtualAddresses.get(0);
//			// 转入钱包信息
//			UserCoinWallet virtualwalletTo = userCoinWalletMapper.selectByUidAndCoinId(userVirtualAddress.getFuid(), fcoinid);
//			if (virtualwalletTo == null) {
//				throw new BCException("平台互转转入虚拟币钱包为空");
//			}
//			// 写记录
//			FVirtualCapitalOperationDTO tovirtualcaptualoperation = new FVirtualCapitalOperationDTO();
//			tovirtualcaptualoperation.setFuid(userVirtualAddress.getFuid());
//			tovirtualcaptualoperation.setFcoinid(fcoinid);
//			tovirtualcaptualoperation.setFamount(record.getFamount());
//			tovirtualcaptualoperation.setFfees(BigDecimal.ZERO);
//			tovirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
//			tovirtualcaptualoperation.setFhasowner(true);
//			tovirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);// 收款成功
//			tovirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());// 收款
//			tovirtualcaptualoperation.setFuniquenumber("[平台互转]" + Utils.UUID());
//			tovirtualcaptualoperation.setFrechargeaddress(record.getFwithdrawaddress());
//			tovirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
//			tovirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
//			tovirtualcaptualoperation.setVersion(0);
//			tovirtualcaptualoperation.setFplatform(record.getFplatform());
//			if (virtualCapitalOperationMapper.insert(tovirtualcaptualoperation) <= 0) {
//				throw new BCException("平台互转记录写入出错");
//			}
//			// 更新钱包信息
//			virtualwalletTo.setTotal(record.getFamount());
//			virtualwalletTo.setFrozen(BigDecimal.ZERO);
//			virtualwalletTo.setGmtModified(Utils.getTimestamp());
//			if (userCoinWalletMapper.changeFinance(virtualwalletTo) <= 0) {
//				throw new BCException("平台互转对方钱包被锁定");
//			}
//			// 更新提现信息
//			record.setFuniquenumber("[平台互转]" + Utils.UUID());
//		} else {
//			// 返回TXID
//			String resultTX;
//			Integer nonceTmp = null;
//			try {
//				if (coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETH.getCode()) || coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETC.getCode()) ) {
//					nonceTmp = coinDriver.getTransactionCount();
//					String nonce = "0x" + Integer.toHexString(nonceTmp);
//					resultTX = coinDriver.sendToAddress(address, record.getFamount().toString(), nonce);
//				} else if (coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETP.getCode())) {
//					resultTX = coinDriver.sendToAddress(address, record.getFamount().toString(), null);
//				} else if (coinDriver.getCoinSort().equals(SystemCoinSortEnum.GXS.getCode())) {
//					resultTX = coinDriver.sendToAddress(address, record.getFamount(), record.getFid().toString(),
//							record.getFbtcfees(), record.getMemo());
//				} else {
//					resultTX = coinDriver.sendToAddress(address, record.getFamount(), record.getFid().toString(),
//							record.getFbtcfees());
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new BCException("打币链接钱包出现错误");
//			}
//			if (resultTX == null || "".equals(resultTX)) {
//				throw new BCException("钱包连接错误  : " + resultTX + "_" + nonceTmp);
//			}
//			// 打币记录返回TXID
//			record.setFuniquenumber(resultTX);
//			record.setFnonce(nonceTmp);
//		}
//		// 更新数据
//		if (virtualCapitalOperationMapper.updateByPrimaryKey(record) <= 0) {
//			throw new BCException("更新订单失败");
//		}
//		if (userCoinWalletMapper.changeFinance(userVirtualWallet) <= 0) {
//			throw new BCException("更新用户钱包失败");
//		}
//
//        FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
//        financeRecordDTO.setAmount(amount);
//        financeRecordDTO.setCreateDate(new Date());
//        financeRecordDTO.setUpdateDate(new Date());
//        financeRecordDTO.setUid(record.getFuid());
//        financeRecordDTO.setOperation(FinanceRecordOperationEnum.Out.getCode());
//        financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
//        financeRecordDTO.setRelationCoinId(record.getFcoinid());
//        financeRecordDTO.setRelationCoinName("");
//        financeRecordDTO.setRelationId(record.getFid());
//        financeRecordDTO.setTxId(record.getFuniquenumber());
//        financeRecordDTO.setRechargeAddress("");
//        financeRecordDTO.setWithdrawAddress(record.getFwithdrawaddress());
//        financeRecordDTO.setMemo(record.getMemo() == null?"":record.getMemo());
//        financeRecordDTO.setFee(record.getFfees());
//        financeRecordDTO.setWalletOperationDate(new Date());
//
//        financeRecordMapper.insert(financeRecordDTO);
//
//
//        // MQ
//		mqSend.SendUserAction(record.getFagentid(), record.getFuid(), LogUserActionEnum.COIN_WITHDRAW,
//				record.getFcoinid(), amount, record.getFfees(), record.getFbtcfees());
//
//		// MQ
//		mqSend.SendAdminAction(record.getFagentid(), record.getFadminid(), record.getFuid(),
//				LogAdminActionEnum.SYSTEM_COIN_WITHDRAW, record.getFcoinid(), record.getFamount());
//		return true;
//	}

	/**
	 * 更新用户虚拟币操作
	 * @param recordId 虚拟币操作
	 * @param amount 总量
	 * @param addressNum 地址数量
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateVirtualCapital(Integer recordId, Integer adminId, BigDecimal amount, int addressNum,
										String walletPassword, SystemCoinType virtualCoinType, BigDecimal networkFee) throws BCException {
		if(virtualCoinType == null || !virtualCoinType.getIsWithdraw()){
			return false;
		}
		String accesskey = virtualCoinType.getAccessKey();
		String secretkey = virtualCoinType.getSecrtKey();
		String ip = virtualCoinType.getIp();
		String port = virtualCoinType.getPort();
		if (accesskey == null || secretkey == null || ip == null || port == null) {
			return false;
		}
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(virtualCoinType.getCoinType(), ip, port)
				.accessKey(accesskey)
				.secretKey(secretkey)
				.pass(walletPassword)
				.assetId(virtualCoinType.getAssetId())
				.sendAccount(virtualCoinType.getEthAccount())
				.contractAccount(virtualCoinType.getContractAccount())
				.contractWei(virtualCoinType.getContractWei())
				.builder()
				.getDriver();

		FVirtualCapitalOperationDTO record = virtualCapitalOperationMapper.selectAllById(recordId);
		if(!record.getFstatus().equals(VirtualCapitalOperationOutStatusEnum.LockOrder)){
			throw new BCException("订单锁定中，不允许重复操作！");
		}

		int fuid = record.getFuid();
		int fcoinid = record.getFcoinid();
		UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinid);
		if (userVirtualWallet == null) {
			throw new BCException("虚拟币钱包为空");
		}
		if (MathUtils.sub(userVirtualWallet.getFrozen(), amount).compareTo(BigDecimal.ZERO) == -1) {
			throw new BCException("虚拟币钱包冻结余额不足");
		}
		// 提现人钱包
		userVirtualWallet.setFrozen(MathUtils.positive2Negative(amount));
		userVirtualWallet.setTotal(BigDecimal.ZERO);
		userVirtualWallet.setGmtModified(Utils.getTimestamp());

		record.setFadminid(adminId);
		record.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
		// 提现地址
		String address = record.getFwithdrawaddress();
		// 平台互转
		if (addressNum > 0) {
			List<FUserVirtualAddressDTO> userVirtualAddresses = userVirtualAddressMapper.getUserByAddress(address);
			if (userVirtualAddresses == null || userVirtualAddresses.size() != 1) {
				throw new BCException("平台互转转入地址为空");
			}
			// 转入地址信息
			FUserVirtualAddressDTO userVirtualAddress = userVirtualAddresses.get(0);
			// 转入钱包信息
			UserCoinWallet virtualwalletTo = userCoinWalletMapper.selectByUidAndCoinId(userVirtualAddress.getFuid(), fcoinid);
			if (virtualwalletTo == null) {
				throw new BCException("平台互转转入虚拟币钱包为空");
			}
			// 写记录
			FVirtualCapitalOperationDTO tovirtualcaptualoperation = new FVirtualCapitalOperationDTO();
			tovirtualcaptualoperation.setFuid(userVirtualAddress.getFuid());
			tovirtualcaptualoperation.setFcoinid(fcoinid);
			tovirtualcaptualoperation.setFamount(record.getFamount());
			tovirtualcaptualoperation.setFfees(BigDecimal.ZERO);
			tovirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
			tovirtualcaptualoperation.setFhasowner(true);
			tovirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);// 收款成功
			tovirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());// 收款
			tovirtualcaptualoperation.setFuniquenumber("[平台互转]" + Utils.UUID());
			tovirtualcaptualoperation.setFrechargeaddress(record.getFwithdrawaddress());
			tovirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
			tovirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
			tovirtualcaptualoperation.setVersion(0);
			tovirtualcaptualoperation.setFplatform(record.getFplatform());
			if (virtualCapitalOperationMapper.insert(tovirtualcaptualoperation) <= 0) {
				throw new BCException("平台互转记录写入出错");
			}
			// 更新钱包信息
			virtualwalletTo.setTotal(record.getFamount());
			virtualwalletTo.setFrozen(BigDecimal.ZERO);
			virtualwalletTo.setGmtModified(Utils.getTimestamp());
			if (userCoinWalletMapper.changeFinance(virtualwalletTo) <= 0) {
				throw new BCException("平台互转对方钱包被锁定");
			}
			// 更新提现信息
			record.setFuniquenumber("[平台互转]" + Utils.UUID());

			FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
			financeRecordDTO.setAmount(tovirtualcaptualoperation.getFamount());
			financeRecordDTO.setCreateDate(new Date());
			financeRecordDTO.setUpdateDate(new Date());
			financeRecordDTO.setUid(userVirtualAddress.getFuid());
			financeRecordDTO.setOperation(FinanceRecordOperationEnum.In.getCode());
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			financeRecordDTO.setRelationCoinId(tovirtualcaptualoperation.getFcoinid());
			financeRecordDTO.setRelationCoinName("");
			financeRecordDTO.setRelationId(tovirtualcaptualoperation.getFid());
			financeRecordDTO.setTxId(tovirtualcaptualoperation.getFuniquenumber());
			financeRecordDTO.setWithdrawAddress("");
			financeRecordDTO.setRechargeAddress(tovirtualcaptualoperation.getFrechargeaddress());
			financeRecordDTO.setMemo(tovirtualcaptualoperation.getMemo());
			financeRecordDTO.setFee(tovirtualcaptualoperation.getFfees());
			financeRecordDTO.setWalletOperationDate(new Date());
			if (financeRecordMapper.insert(financeRecordDTO) <= 0) {
				throw new BCException("添加提现日志失败");
			}	

		} else {
			// 返回TXID
			String resultTX;
			Integer nonceTmp = null;
			try {
				if (coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETH.getCode()) || coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETC.getCode()) ) {
					nonceTmp = coinDriver.getTransactionCount();
					String nonce = "0x" + Integer.toHexString(nonceTmp);
					resultTX = coinDriver.sendToAddress(address,record.getFamount(),nonce,networkFee);
				} else if (coinDriver.getCoinSort().equals(SystemCoinSortEnum.ETP.getCode())) {
					resultTX = coinDriver.sendToAddress(address, record.getFamount().toString(), null);
				} else if(coinDriver.getCoinSort().equals(SystemCoinSortEnum.EOS.getCode())) {
					resultTX = coinDriver.sendToAddress(address,record.getFamount().setScale(4,BigDecimal.ROUND_UP).toPlainString(),record.getMemo());
				}else {
					resultTX = coinDriver.sendToAddress(address, record.getFamount(), record.getFid().toString(),
							record.getFbtcfees());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new BCException("打币链接钱包出现错误");
			}
			if (resultTX == null || "".equals(resultTX)) {
				throw new BCException("钱包连接错误  : " + resultTX + "_" + nonceTmp);
			}
			// 打币记录返回TXID
			record.setFuniquenumber(resultTX);
			record.setFnonce(nonceTmp);
		}
		// 更新数据
		if (virtualCapitalOperationMapper.updateByPrimaryKey(record) <= 0) {
			throw new BCException("更新订单失败");
		}
		if (userCoinWalletMapper.changeFinance(userVirtualWallet) <= 0) {
			throw new BCException("更新用户钱包失败");
		}

//		FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
//		financeRecordDTO.setAmount(amount);
//		financeRecordDTO.setCreateDate(new Date());
//		financeRecordDTO.setUpdateDate(new Date());
//		financeRecordDTO.setUid(record.getFuid());
//		financeRecordDTO.setOperation(FinanceRecordOperationEnum.Out.getCode());
//		financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
//		financeRecordDTO.setRelationCoinId(record.getFcoinid());
//		financeRecordDTO.setRelationCoinName("");
//		financeRecordDTO.setRelationId(record.getFid());
//		financeRecordDTO.setTxId(record.getFuniquenumber());
//		financeRecordDTO.setRechargeAddress("");
//		financeRecordDTO.setWithdrawAddress(record.getFwithdrawaddress());
//		financeRecordDTO.setMemo(record.getMemo() == null?"":record.getMemo());
//		financeRecordDTO.setFee(record.getFfees());
//		financeRecordDTO.setWalletOperationDate(new Date());
//
//		financeRecordMapper.insert(financeRecordDTO);

		Map<String,Object> params = new HashMap<>();
		params.put("operation", FinanceRecordOperationEnum.Out.getCode());
		params.put("relationId",record.getFid());
		List<FinanceRecordDTO> financeRecordDTOS = financeRecordMapper.selectByParams(params);
		if (financeRecordDTOS == null || financeRecordDTOS.size() != 1) {
			throw new BCException("提现日志 FinanceRecord 不存在记录");
		}
		FinanceRecordDTO financeRecordDTO = financeRecordDTOS.get(0);
		financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
		financeRecordDTO.setTxId(record.getFuniquenumber());
		financeRecordDTO.setUpdateDate(new Date());
		financeRecordDTO.setWalletOperationDate(new Date());
		if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
			throw new BCException("更新日志失败");
		}

		// MQ
		mqSend.SendUserAction(record.getFagentid(), record.getFuid(), LogUserActionEnum.COIN_WITHDRAW,
				record.getFcoinid(), amount, record.getFfees(), record.getFbtcfees());

		// MQ
		mqSend.SendAdminAction(record.getFagentid(), record.getFadminid(), record.getFuid(),
				LogAdminActionEnum.SYSTEM_COIN_WITHDRAW, record.getFcoinid(), record.getFamount());
		return true;
	}

	/**
	 * 更新虚拟币操作记录
	 * @param fAdmin 管理员
	 * @param record 记录
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#updateVirtualCapital(com.qkwl.common.dto.admin.FAdmin, FVirtualCapitalOperationDTO)
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateVirtualCapital(FAdmin fAdmin, FVirtualCapitalOperationDTO record) throws BCException {
		Map<String,Object> params = new HashMap<>();
		params.put("operation", FinanceRecordOperationEnum.Out.getCode());
		params.put("relationId",record.getFid());
		List<FinanceRecordDTO> financeRecordDTOS = financeRecordMapper.selectByParams(params);
		if (financeRecordDTOS == null || financeRecordDTOS.size() != 1) {
			throw new BCException("提现日志 FinanceRecord 不存在记录");
		}
		FinanceRecordDTO financeRecordDTO = financeRecordDTOS.get(0);
		financeRecordDTO.setUpdateDate(new Date());
		if (record.getFstatus() == VirtualCapitalOperationOutStatusEnum.Cancel) {
			UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(record.getFuid(), record.getFcoinid());
			BigDecimal amountfees = MathUtils.add(record.getFamount(), record.getFfees());
			BigDecimal amount = MathUtils.add(amountfees, record.getFbtcfees());
			BigDecimal frozenRmb = userVirtualWallet.getFrozen();
			if (MathUtils.sub(frozenRmb, amount).compareTo(BigDecimal.ZERO) < 0) {
				throw new BCException("虚拟币冻结金额异常");
			}
			userVirtualWallet.setTotal(amount);
			userVirtualWallet.setFrozen(MathUtils.positive2Negative(amount));
			userVirtualWallet.setGmtModified(Utils.getTimestamp());
			if (userCoinWalletMapper.changeFinance(userVirtualWallet) <= 0) {
				throw new BCException("更新虚拟钱包失败");
			}
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.Cancel.getCode());
			if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
				throw new BCException("更新日志失败");
			}

		} else if(record.getFstatus() == VirtualCapitalOperationOutStatusEnum.RefuseOrder){
			UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(record.getFuid(), record.getFcoinid());
			BigDecimal amountfees = MathUtils.add(record.getFamount(), record.getFfees());
			BigDecimal amount = MathUtils.add(amountfees, record.getFbtcfees());
			BigDecimal frozenRmb = userVirtualWallet.getFrozen();
			if (MathUtils.sub(frozenRmb, amount).compareTo(BigDecimal.ZERO) < 0) {
				throw new BCException("虚拟币冻结金额异常");
			}
			userVirtualWallet.setTotal(amount);
			userVirtualWallet.setFrozen(MathUtils.positive2Negative(amount));
			userVirtualWallet.setGmtModified(Utils.getTimestamp());
			if (userCoinWalletMapper.changeFinance(userVirtualWallet) <= 0) {
				throw new BCException("更新虚拟钱包失败");
			}

			financeRecordDTO.setStatus(FinanceRecordStatusEnum.Fail.getCode());
			financeRecordDTO.setMemo(record.getMemo());
			if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
				throw new BCException("更新日志失败");
			}

		} else if (record.getFstatus() == VirtualCapitalOperationOutStatusEnum.OperationSuccess) {
			UserCoinWallet userCoinWallet = userCoinWalletMapper.selectByUidAndCoinId(record.getFuid(), record.getFcoinid());
			BigDecimal totalAmount = MathUtils.add(record.getFamount(), record.getFfees());
			if (MathUtils.sub(userCoinWallet.getFrozen(),totalAmount).compareTo(BigDecimal.ZERO) < 0) {
				throw new BCException("虚拟币冻结金额异常");
			}
			userCoinWallet.setTotal(BigDecimal.ZERO);
			userCoinWallet.setFrozen(MathUtils.positive2Negative(totalAmount));
			userCoinWallet.setGmtModified(Utils.getTimestamp());
			if (userCoinWalletMapper.changeFinance(userCoinWallet) <= 0) {
				throw new BCException("更新虚拟币钱包失败");
			}

			if (TextUtils.isEmpty(record.getFuniquenumber())) {
				throw new BCException("请输入交易 ID");
			}

            record.setFadminid(fAdmin.getFid());
            record.setFupdatetime(Utils.getTimestamp());

			financeRecordDTO.setTxId(record.getFuniquenumber());
			financeRecordDTO.setWalletOperationDate(new Date());
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			if (financeRecordMapper.updatePyPrimaryKey(financeRecordDTO) <= 0) {
				throw new BCException("更新日志失败");
			}
		}
		if (virtualCapitalOperationMapper.updateByPrimaryKey(record) <= 0) {
			throw new BCException("更新订单失败");
		}
		// MQ
		if (record.getFstatus() == VirtualCapitalOperationOutStatusEnum.Cancel) {
			mqSend.SendUserAction(record.getFagentid(), record.getFuid(), LogUserActionEnum.COIN_WITHDRAW_CANCEL, record.getFcoinid(), record.getFamount(), "admin_" + fAdmin.getFid());

			mqSend.SendAdminAction(record.getFagentid(), record.getFadminid(), record.getFuid(), LogAdminActionEnum.CANCEL_COIN_WITHDRAW, record.getFcoinid(), record.getFamount());
		}
		return true;
	}

	/**
	 * 新增虚拟币充值记录
	 * @param record 充值实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#insertConsoleVirtualRecharge(com.qkwl.common.dto.log.FLogConsoleVirtualRecharge)
	 */
	@Override
	public boolean insertConsoleVirtualRecharge(FLogConsoleVirtualRecharge record) {
		int i = logConsoleVirtualRechargeMapper.insert(record);
		return i > 0;
	}

	/**
	 * 根据id查询虚拟币充值
	 * @param fid 充值id
	 * @return 虚拟币充值实体
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectConsoleVirtualRechargeById(int)
	 */
	@Override
	public FLogConsoleVirtualRecharge selectConsoleVirtualRechargeById(int fid) {
		return logConsoleVirtualRechargeMapper.selectByPrimaryKey(fid);
	}

	/**
	 * 删除虚拟币充值
	 * @param fid 充值id
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#deleteConsoleVirtualRechargeById(int)
	 */
	@Override
	public boolean deleteConsoleVirtualRechargeById(int fid) {
		int i = logConsoleVirtualRechargeMapper.deleteByPrimaryKey(fid);
		return i > 0;
	}

	/**
	 * 更新虚拟币充值
	 * @param record 虚拟币充值
	 * @return 是否更新成功
	 * @throws BCException 更新异常
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#updateConsoleVirtualRecharge(com.qkwl.common.dto.log.FLogConsoleVirtualRecharge)
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateConsoleVirtualRecharge(FLogConsoleVirtualRecharge record) throws BCException {
		if (record.getFstatus() == OperationlogEnum.FFROZEN) {
			int fuid = record.getFuid();
			int fcoinId = record.getFcoinid();
			BigDecimal qty = record.getFamount();
			UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinId);
			if (userVirtualWallet == null) {
				return false;
			}
			userVirtualWallet.setFrozen(qty);
			userVirtualWallet.setTotal(BigDecimal.ZERO);
			userVirtualWallet.setGmtModified(Utils.getTimestamp());
			userCoinWalletMapper.changeFinance(userVirtualWallet);
		}
		if (record.getFstatus() == OperationlogEnum.AUDIT) {
			int fuid = record.getFuid();
			int fcoinId = record.getFcoinid();
			BigDecimal qty = record.getFamount();
			UserCoinWallet userVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(fuid, fcoinId);
			if (userVirtualWallet == null) {
				return false;
			}
			userVirtualWallet.setFrozen(MathUtils.positive2Negative(qty));
			userVirtualWallet.setTotal(qty);
			userVirtualWallet.setGmtModified(Utils.getTimestamp());
			userCoinWalletMapper.changeFinance(userVirtualWallet);

			FinanceRecordDTO financeRecordDTO = new FinanceRecordDTO();
			financeRecordDTO.setAmount(record.getFamount());
			financeRecordDTO.setCreateDate(new Date());
			financeRecordDTO.setUpdateDate(new Date());
			financeRecordDTO.setUid(fuid);
			financeRecordDTO.setOperation(FinanceRecordOperationEnum.In.getCode());
			financeRecordDTO.setStatus(FinanceRecordStatusEnum.SUCCESS.getCode());
			financeRecordDTO.setRelationCoinId(fcoinId);
			financeRecordDTO.setRelationCoinName("");
			financeRecordDTO.setRelationId(record.getFid());
			financeRecordDTO.setTxId("");
			financeRecordDTO.setRechargeAddress("");
			financeRecordDTO.setWithdrawAddress("");
			financeRecordDTO.setMemo(TextUtils.isEmpty(record.getFinfo())?"":record.getFinfo());
			financeRecordDTO.setFee(BigDecimal.ZERO);
			financeRecordDTO.setWalletOperationDate(new Date());
			if (financeRecordMapper.insert(financeRecordDTO) <= 0) {
				throw new BCException("添加日志 FinanceRecord 失败");
			}
		}
		int result = logConsoleVirtualRechargeMapper.updateByPrimaryKey(record);
		if (result <= 0) {
			throw new BCException();
		}
		mqSend.SendAdminAction(0, record.getFcreatorid(), record.getFuid(), LogAdminActionEnum.ADMIN_COIN_RECHARGE, record.getFcoinid(), record.getFamount());


		return true;
	}

	/**
	 * 分页查询虚拟币充值
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectConsoleVirtualRechargeList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.log.FLogConsoleVirtualRecharge, List)
	 */
	@Override
	public Pagination<FLogConsoleVirtualRecharge> selectConsoleVirtualRechargeList(Pagination<FLogConsoleVirtualRecharge> pageParam, FLogConsoleVirtualRecharge record, List<Integer> status) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("fstatus", status.size() > 0 ? status : null);
		map.put("ftype", record.getFtype());
		map.put("coinId", record.getFcoinid());
		map.put("start", pageParam.getBegindate());
		map.put("end", pageParam.getEnddate());
		// 查询总数
		int count = logConsoleVirtualRechargeMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<FLogConsoleVirtualRecharge> list = logConsoleVirtualRechargeMapper.getAdminPageList(map);
//			//计算汇率
//			String exchangeRate = redisHelper.getSystemArgs("rechargeUSDTPrice");
//			if (exchangeRate != null && exchangeRate.length() != 0) {
//				BigDecimal exchangeRateNumber = new BigDecimal(exchangeRate);
//				for (FLogConsoleVirtualRecharge obj:list) {
//					if (obj.getFamount() != null){
//						obj.setRmbAmount(obj.getFamount().multiply(exchangeRateNumber));
//					}
//				}
//			}


			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 分页查询用户钱包
	 * @param pageParam 分页参数
	 * @param fuids 用户ids
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectUserWalletList(com.qkwl.common.dto.common.Pagination, List)
	 */
	@Override
	public Pagination<UserCoinWallet> selectUserWalletList(Pagination<UserCoinWallet> pageParam, List<Integer> fuids) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("fuids", fuids);
		// 查询总数
		int count = operationCoinWalletMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<UserCoinWallet> list = operationCoinWalletMapper.getAdminPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 分页查询用户钱包
	 * @param pageParam 分页参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectUserWalletList(com.qkwl.common.dto.common.Pagination)
	 */
	@Override
	public Pagination<UserCoinWallet> selectUserWalletList(Pagination<UserCoinWallet> pageParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		// 查询总数
		int count = operationCoinWalletMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<UserCoinWallet> list = operationCoinWalletMapper.getAdminPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 分页查询虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @param fuids 用户ids
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectUserVirtualWalletList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.wallet.UserCoinWallet, List)
	 */
	@Override
	public Pagination<UserCoinWallet> selectUserVirtualWalletList(Pagination<UserCoinWallet> pageParam, UserCoinWallet filterParam, List<Integer> fuids) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("coinId", filterParam.getCoinId());
		map.put("fuids", fuids);
		// 查询总数
		int count = operationCoinWalletMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<UserCoinWallet> list = operationCoinWalletMapper.getAdminPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}


	/**
	 * 分页查询虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectUserVirtualWalletListByCoin(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.wallet.UserCoinWallet)
	 */
	@Override
	public Pagination<UserCoinWallet> selectUserVirtualWalletListByCoin(Pagination<UserCoinWallet> pageParam, UserCoinWallet filterParam) {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("coinId", filterParam.getCoinId());
		// 查询总数
		int count = operationCoinWalletMapper.countAdminPage(map);
		if(count > 0) {
			// 查询数据
			List<UserCoinWallet> list = operationCoinWalletMapper.getAdminPageList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 根据类型查询人民币操作总金额
	 * @param fuid   用户id
	 * @param type 类型
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectWalletTotalAmount(Integer, Integer, Integer, Date, Date)
	 */
	@Override
	public BigDecimal selectWalletTotalAmount(Integer fuid, Integer type, Integer status, Date start, Date end){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("type", type);
		map.put("status", status);
		map.put("start", start);
		map.put("end", end);
		return walletCapitalOperationMapper.getTotalAmountByType(map);
	}

	/**
	 * 根据类型查询虚拟币操作总金额
	 * @param fuid   用户id
	 * @param coinid 币种id
	 * @param type	 类型
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectVirtualWalletTotalAmount(Integer, Integer,Integer, Integer, Date, Date)
	 */
	@Override
	public BigDecimal selectVirtualWalletTotalAmount(Integer fuid, Integer coinid, Integer type, Integer status, Date start, Date end){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("coinid", coinid);
		map.put("type", type);
		map.put("status", status);
		map.put("start", start);
		map.put("end", end);
		return virtualCapitalOperationMapper.getTotalAmountByType(map);
	}


	/**
	 * 根据类型查询手工充值虚拟币操作总金额
	 * @param fuid   用户id
	 * @param coinid 币种id
	 * @param status 状态
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 * @see com.qkwl.common.rpc.admin.IAdminUserCapitalService#selectAdminRechargeVirtualWalletTotalAmount(Integer, Integer, Integer, Date, Date)
	 */
	@Override
	public BigDecimal selectAdminRechargeVirtualWalletTotalAmount(Integer fuid, Integer coinid, Integer status, Date start, Date end){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("coinid", coinid);
		map.put("status", status);
		map.put("start", start);
		map.put("end", end);
		return logConsoleVirtualRechargeMapper.getTotalAmountByStatus(map);
	}

	@Override
	public FUserPushDTO selectUserPushBalance(Integer uid, Integer pushid, Integer coinid, Integer state, Date start, Date end) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", uid);
		map.put("fpushuid", pushid);
		map.put("fcoinid", coinid);
		map.put("state", state);
		map.put("start", start);
		map.put("end", end);
		return userPushMapper.selectUserPushBalance(map);
	}


	/**
	 * 修改资金信息
	 */
	@Override
	public boolean updateUserWallet(Integer uid, Integer coinId, BigDecimal amount){
		UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoinId(uid,coinId);
		if(wallet == null){
			return false;
		}
		wallet.setTotal(amount);
		if (MathUtils.compareTo(amount,BigDecimal.ZERO) > 0) {
			wallet.setFrozen(MathUtils.positive2Negative(amount));
		} else {
			wallet.setFrozen(MathUtils.negative2Positive(amount));
		}
		wallet.setGmtModified(Utils.getTimestamp());
		return userCoinWalletMapper.changeFinance(wallet) > 0;
	}

	@Override
	public boolean updateHistoryActivity(FLogConsoleVirtualRecharge record){
		int i = logConsoleVirtualRechargeMapper.updateByHistoryActivity(record);
		return i > 0;
	}

	@Override
	public Result insertRecharge(FVirtualCapitalOperationDTO operation){
		int count = virtualCapitalOperationMapper.selectByTx(operation.getFuniquenumber());
		if(count > 0){
			return Result.failure("已存在此交易！");
		}

		if(virtualCapitalOperationMapper.insertRecharge(operation) > 0){
			return Result.success("新增成功！");
		}
		return Result.failure("新增失败！");
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result recheckVirtualRecharge(FVirtualCapitalOperationDTO operation) throws Exception{

		FVirtualCapitalOperationDTO virtualOperation = virtualCapitalOperationMapper.selectAllById(operation.getFid());
		if(virtualOperation == null){
			return Result.failure("未找到此记录！");
		}

		if(virtualOperation.getFstatus().equals(VirtualCapitalOperationInStatusEnum.SUCCESS)){
			return Result.failure("此记录已到帐，不能再次审核！");
		}

		virtualOperation.setFconfirmations(operation.getFconfirmations());
		virtualOperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);
		virtualOperation.setFupdatetime(new Date());

		UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoinId(virtualOperation.getFuid(),
				virtualOperation.getFcoinid());
		wallet.setTotal( virtualOperation.getFamount());
		wallet.setFrozen(BigDecimal.ZERO);
		wallet.setGmtModified(new Date());
		if(userCoinWalletMapper.changeFinance(wallet) <= 0){
			throw new Exception("更新钱包失败！");
		}

		if(virtualCapitalOperationMapper.updateByPrimaryKey(virtualOperation) <= 0){
			throw new Exception("更新充值记录失败！");
		}

		return Result.success("审核成功！");
	}

}
