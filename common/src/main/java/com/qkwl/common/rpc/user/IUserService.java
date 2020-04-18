package com.qkwl.common.rpc.user;

import java.math.BigDecimal;
import java.util.List;

import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.activity.FActivityRecord;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserPriceclock;
import com.qkwl.common.dto.user.FUserInfo;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.result.Result;

/**
 * 用户Service接口
 * @author ZKF
 */
public interface IUserService {

	/**
	 * 用户登录
	 * @param userinfo 请求用户信息的参数实体
	 * @param type 登录类型
	 * @param ip 登录IP地址
	 * @return 登录后响应的用户信息实体对象
	 * 200  登录成功
	 * 300  登录失败
	 * 400  参数错误
	 * 1000 用户名或密码不能为空
	 * 1001 用户不存在
	 * 1002 用户名或密码错误
	 * 1003 用户积分等级不存在
	 * 1004 系统升级中，暂停登录
	 * 1005 账户出现安全隐患被冻结，请尽快联系客服
	 * 1006 ip限制中，请稍候登录
	 * @throws Exception
	 */
	Result updateCheckLogin(RequestUserInfo userinfo, UserLoginType type, String ip, LocaleEnum lan) throws Exception;

	/**
	 * 用户注册
	 * @param fuser 用户实体对象
	 * @param ip 注册IP地址
	 * @return 登录后响应的用户信息实体对象
	 * 200 注册成功
	 * 300 注册失败
	 * 400 参数异常
	 * 1000 用户名或密码不能为空
	 * 1001 手机号格式错误
	 * 1002 手机号已存在
	 * 1003 邮件格式错误
	 * 1004 邮箱已存在
	 * 1005 第三方账号已存在
	 * 1006 推荐人不存在
	 * 1007 当前IP注册频繁
	 * 10004 手机验证码错误
	 * 10005 手机验证码错误达到上限，请两小时后重试!
	 * 10014 邮箱验证码错误
	 * 10015 邮箱验证码错误达到上限，请两小时后重试!
	 * @throws Exception
	 */
	Result insertRegister(RequestUserInfo userinfo, UserLoginType type) throws Exception;
	
	/**
	 * 查询IP注册人数
	 * @param ipLong
	 * @return
	 */
	int selectIpCount(String ipLong);

	/**
	 * 根据条件校验用户是否存在(登录名、登录密码、邮箱、手机号、身份证、微信UnionId、QQOpenid)
	 * @param user 用户
	 * @return 是否存在 true-存在，false-不存在
	 */
	boolean selectIsExistByParam(FUser user);
	
	/**
	 * 修改密码
	 * @param user 用户实体对象
	 * @param type 修改密码类型 0登录密码 1交易密码
	 * @param newPwd 新密码
	 * @return 0(成功) 1(异常) -1(登录密码与交易密码不能一样) -2( 交易密码与登录密码不能一样 )
	 * @throws BCException 执行异常
	 */
	int updatePwd(FUser user, int type, String newPwd) throws BCException;
	
	/**
	 * 根据条件查找用户(登录名、登录密码、邮箱、手机号、身份证、微信UnionId、QQOpenid)
	 * @param user 用户实体对象
	 * @return 用户实体对象
	 */
	FUser selectUserByParam(FUser user);
	
	/**
	 * 根据条件查找用户列表
	 * @param user 用户实体对象
	 * @return 用户实体对象列表
	 */
	List<FUser> selectUserListByParam(FUser user);

	/**
	 * 查询推广用户的个数
	 * @param fuid 用户ID
	 * @return 推广人数
	 */
	Integer selectIntroUserCount(Integer fuid);
	
	/**
	 * 根据用户Id获取用户
	 * @param id 用户ID
	 * @return 用户实体对象
	 */
	FUser selectUserById(int id);
	
	/**
	 * 根据用户显示Id获取用户
	 * @param showId 用户显示ID
	 * @return 用户实体对象
	 */
	FUser selectUserByShowId(int showId);
	
	/**
	 * 根据用户参数查询用户集合
	 * @param fuser 用户实体对象
	 * @return 用户实体对象列表
	 */
	List<FUser> selectUserByUser(FUser fuser);
	
	/**
	 * 根据id更新用户
	 * @param user 用户实体对象
	 * @param action 用户行为类型
	 * @param scoreType 积分类型
	 * @return true：成功，false：失败
	 * @throws BCException 执行失败
	 */
	boolean updateUserById(FUser user, LogUserActionEnum action, ScoreTypeEnum scoreType) throws BCException;
	
	/**
	 * 更新登陆时间
	 * @param user 用户
	 * @return true：成功，false：失败
	 */
	boolean updateLoginTime(FUser user);
	
	/**
	 * 更新用户爆仓状态
	 * @param user 用户实体对象
	 * @return true：成功，false：失败
	 */
	boolean updateLeverLock(FUser user);
	
	/**
	 * 查询用户积分记录
	 * @param fuid 用户ID
	 * @param page 分页实体对象
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @return 分页实体对象
	 */
	Pagination<FLogUserAction> selectScoreListByUser(int fuid,String beginDate,String endDate,Pagination<FLogUserAction> page);
	
	/**
	 * 根据用户ID查找等级积分
	 * @param userid 用户ID
	 * @return 用户积分实体对象
	 */
	FUserScore selectUserScoreById(int userid);

	/**
	 * 用户积分增加
	 * @param fuid 用户ID
	 * @param fscore 增加积分
	 * @param fscoretype 增加积分类型
	 * @param famount 增加积分金额（没有为null）
	 * @param fratio 积分增加比例（没有为null）
	 * @param ip 操作IP地址
	 * @return true：成功，false：失败
	 * @throws BCException 执行失败
	 */
	@Deprecated
	boolean updateUserScore(int fuid, int fscore, int fscoretype, BigDecimal famount, BigDecimal fratio, String ip) throws BCException;

	
	/**
	 * 根据用户查用户积分记录
	 * @param page 分页实体对象
	 * @param score
	 * @return 分页实体对象
	 */
	Pagination<FLogUserScore> selectUserScoreByPage(Pagination<FLogUserScore> page, FLogUserScore score);
	
	/**
	 * 更新价格闹钟
	 * @param price 闹钟实体
	 * @return 是否保存成功
	 */
	boolean updateUserPriceClock(FUserPriceclock price);
	
	/**
	 * 根据用户查询价格闹钟
	 * @param fuid 用户id
	 * @return 闹钟列表
	 */
	List<FUserPriceclock> selectPriceClockByUser(Integer fuid);
	
	/**
	 * 根据用户id和币种查询闹钟
	 * @param price 闹钟实体
	 * @return 查询实体
	 */
	FUserPriceclock selectPriceClockByClock(FUserPriceclock price);
	
	/**
	 * 新增用户联系地址
	 * @param userInfo 用户信息实体
	 * @return true 成功，false 失败
	 */
	Boolean insertUserInfo(FUserInfo userInfo) ;
	/**
	 * 修改用户联系地址
	 * @param userInfo 用户信息实体
	 * @return true 成功，false 失败
	 */
	Boolean updateUserInfo(FUserInfo userInfo) ;
	
	/**
	 * 根据用户ID查询用户联系地址
	 * @param fuid 用户ID
	 * @return 用户信息实体
	 */
	FUserInfo selectUserInfo(Integer fuid);

	/**
	 * 查询活动推广人数
	 * @param fuid 用户ID
	 * @return
	 */
	Integer selectActivityIntroCount(Integer fuid);
	
	/**
	 * 查询活动推广记录
	 * @param page 分页参数
	 * @param record 实体参数
	 * @return
	 */
	Pagination<FActivityRecord> selectActivityRecord(Pagination<FActivityRecord> page, FActivityRecord record);

	/**
	 * 新增迁移用户
	 * @param user
	 * @return
	 */
	Integer insertMigrationUser(FUser user) throws BCException;
}
