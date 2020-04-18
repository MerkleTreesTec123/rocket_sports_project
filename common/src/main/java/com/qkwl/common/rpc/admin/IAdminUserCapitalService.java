package com.qkwl.common.rpc.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FUserPushDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import com.qkwl.common.dto.log.FLogModifyCapitalOperation;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.result.Result;

/**
 * 后台用户资金管理
 * @author LY
 */
public interface IAdminUserCapitalService {

	/**
	 * 分页查询钱包操作记录
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 */
	public Pagination<FWalletCapitalOperationDTO> selectWalletCapitalOperationList(
			Pagination<FWalletCapitalOperationDTO> pageParam, FWalletCapitalOperationDTO record, List<Integer> status,
			Boolean limit, Boolean isvip6);

	/**
	 * 根据id查询钱包操作记录
	 * @param fid 操作id
	 * @return 钱包操作实体
	 */
	public FWalletCapitalOperationDTO selectById(int fid);

	/**
	 * 获取用户钱包
	 * @param fuid 用户id
 	 * @return 用户钱包实体
	 */
	UserCoinWallet selectUserWallet(int fuid, int fcoinid);

	/**
	 * 更新钱包操作
	 * @param admin 管理员
 	 * @param capital 钱包操作记录
	 * @param amount 总量
	 * @param isRecharge 是否充值
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	boolean updateWalletCapital(
			FAdmin admin, FWalletCapitalOperationDTO capital, BigDecimal amount, boolean isRecharge) throws Exception;

	/**
	 * 更新在线提现的状态
	 * @param adminId
	 * @param orderId
	 * @param status
	 * @return
	 * @throws Exception
	 */
//	Result updateOnlineWithdrawStatus(Integer adminId,String orderId, Boolean status) throws Exception;

	/**
	 * 更新钱包操作记录
	 * @param capital 操作记录
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	boolean updateWalletCapital(FWalletCapitalOperationDTO capital) throws BCException;

	/**
	 * 是否第一次充值
	 * @param fuid 用户id
	 * @return 是否第一次充值
	 */
	public boolean selectIsFirstCharge(int fuid);

	/**
	 * 更新充值记录
	 * @param capital 钱包操作记录
	 * @param capitalLog 修改日志
	 * @return 是否修改成功
	 * @throws BCException 更新失败
	 */
	public boolean updateModifyCapital(FWalletCapitalOperationDTO capital, FLogModifyCapitalOperation capitalLog) throws BCException;

	/**
	 * 分页查询虚拟币操作记录 
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 */
	public Pagination<FVirtualCapitalOperationDTO> selectVirtualCapitalOperationList(
			Pagination<FVirtualCapitalOperationDTO> pageParam, FVirtualCapitalOperationDTO record,
			List<Integer> status, Boolean isvip6);

	/**
	 * 根据id查询虚拟币操作记录
	 * @param fid 操作id
	 * @return 操作记录实体
	 */
	public FVirtualCapitalOperationDTO selectVirtualById(int fid);

	/**
	 * 重置虚拟币审核
	 * @param id 主键ID
	 * @param adminId 管理员ID
	 * @return Result
	 */
	Result resetAuditVirtualCapitalOperation (Integer id, Integer adminId)  throws Exception;

	/**
	 * 获取用户虚拟币钱包
	 * @param fuid 用户id
	 * @param fcoinid 币种id
	 * @return 虚拟币钱包实体
	 */
	public UserCoinWallet selectUserVirtualWallet(int fuid, int fcoinid);

	/**
	 * 获取地址数量
	 * @param address 地址
	 * @return 数量
	 */
	public int selectAddressNum(String address);

	/**
	 * 更新用户虚拟币操作
	 * @param recordId 虚拟币操作
     * @param adminId 管理id
	 * @param amount 总量
	 * @param addressNum 地址数量
	 * @param coinDriver 钱包工具
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
//    boolean updateVirtualCapital(Integer recordId, Integer adminId, BigDecimal amount, int addressNum,
//                                CoinDriver coinDriver) throws BCException;

	/**
	 * 更新用户虚拟币操作
	 * @param recordId 虚拟币操作
	 * @param adminId 管理id
	 * @param amount 总量
	 * @param addressNum 地址数量
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	boolean updateVirtualCapital(Integer recordId, Integer adminId, BigDecimal amount, int addressNum,
								 String walletPassword, SystemCoinType coinType, BigDecimal networkFee) throws BCException;

	/**
	 * 新增用户虚拟币充值订单
	 * @param operation
	 * @return
	 */
	Result insertRecharge(FVirtualCapitalOperationDTO operation);

	/**
	 * 审核虚拟币充值订单-虚拟币充值订单手工到账
	 */
	Result recheckVirtualRecharge(FVirtualCapitalOperationDTO operation) throws Exception;


	/**
	 * 更新虚拟币操作记录
	 * @param fAdmin 管理员
	 * @param record 记录
	 * @return 是否执行成功
	 * @throws BCException 更新异常
	 */
	public boolean updateVirtualCapital(FAdmin fAdmin, FVirtualCapitalOperationDTO record) throws BCException;

	/**
	 * 新增虚拟币充值记录
	 * @param record 充值实体
	 * @return 是否新增成功
	 */
	public boolean insertConsoleVirtualRecharge(FLogConsoleVirtualRecharge record);

	/**
	 * 根据id查询虚拟币充值
	 * @param fid 充值id
	 * @return 虚拟币充值实体
	 */
	public FLogConsoleVirtualRecharge selectConsoleVirtualRechargeById(int fid);
	
	/**
	 * 删除虚拟币充值
	 * @param fid 充值id
	 * @return 是否删除成功
	 */
	public boolean deleteConsoleVirtualRechargeById(int fid);

	/**
	 * 更新历史活动数据
	 * @param record
	 * @return
	 */
	public boolean updateHistoryActivity(FLogConsoleVirtualRecharge record);

	/**
	 * 更新虚拟币充值
	 * @param record 虚拟币充值
	 * @return 是否更新成功
	 * @throws BCException 更新异常
	 */
	public boolean updateConsoleVirtualRecharge(FLogConsoleVirtualRecharge record) throws BCException;
	
	/**
	 * 分页查询虚拟币充值
	 * @param pageParam 分页参数
	 * @param record 实体参数
	 * @param status 状态列表
	 * @return 分页查询记录列表
	 */
	public Pagination<FLogConsoleVirtualRecharge> selectConsoleVirtualRechargeList(
			Pagination<FLogConsoleVirtualRecharge> pageParam, FLogConsoleVirtualRecharge record, List<Integer> status);



	/**
	 * 分页查询用户钱包
	 * @param pageParam 分页参数
	 * @param fuids 用户ids
	 * @return 分页查询记录列表
	 */
	public Pagination<UserCoinWallet> selectUserWalletList(Pagination<UserCoinWallet> pageParam, List<Integer> fuids);
	
	/**
	 * 分页查询用户钱包
	 * @param pageParam 分页参数
	 * @return 分页查询记录列表
	 */
	public Pagination<UserCoinWallet> selectUserWalletList(Pagination<UserCoinWallet> pageParam);
	
	/**
	 * 分页查询虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @param fuids 用户ids
	 * @return 分页查询记录列表
	 */
	public Pagination<UserCoinWallet> selectUserVirtualWalletList(
			Pagination<UserCoinWallet> pageParam, UserCoinWallet filterParam, List<Integer> fuids);

	/**
	 * 分页查询虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<UserCoinWallet> selectUserVirtualWalletListByCoin(
			Pagination<UserCoinWallet> pageParam, UserCoinWallet filterParam);
	
	/**
	 * 根据类型查询虚拟币操作总金额
	 * @param fuid   用户id
	 * @param coinid 币种id
	 * @param type	 类型
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 */
	BigDecimal selectVirtualWalletTotalAmount(Integer fuid, Integer coinid, Integer type, Integer status, Date start, Date end);

	/**
	 * 根据类型查询人民币操作总金额
	 * @param fuid   用户id
	 * @param type 类型
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 */
	BigDecimal selectWalletTotalAmount(Integer fuid, Integer type, Integer status, Date start, Date end);

	/**
	 * 根据类型查询手工充值虚拟币操作总金额
	 * @param fuid   用户id
	 * @param coinid 币种id
	 * @param status 状态
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 总金额
	 */
	public BigDecimal selectAdminRechargeVirtualWalletTotalAmount(
			Integer fuid, Integer coinid, Integer status, Date start, Date end);


	/**
	 * PUSH资产平衡统计
	 * @param uid 用户显示ID
	 * @param pushid push用户显示ID
	 * @param coinid 币种ID
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	public FUserPushDTO selectUserPushBalance(Integer uid, Integer pushid, Integer coinid, Integer state, Date start, Date end);


	/**
	 * 修改资金信息
	 * @param uid
	 * @param coinId
	 * @param amount
	 * @return
	 */
	boolean updateUserWallet(Integer uid, Integer coinId, BigDecimal amount);
}
