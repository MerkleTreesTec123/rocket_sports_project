package com.qkwl.common.rpc.admin;

import java.math.BigDecimal;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.log.FLogUserScore;

/**
 * 日志管理接口
 * @author ZKF
 */
public interface IAdminLogService {

	/**
	 * 分页查询管理员日志
	 * @param page 分页参数
	 * @param log 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<FLogAdminAction> selectAdminPageList(Pagination<FLogAdminAction> page, FLogAdminAction log);
	
	/**
	 * 分页查询用户积分日志
	 * @param page
	 * @param log
	 * @return
	 */
	public Pagination<FLogUserScore> selectUserScorePageList(Pagination<FLogUserScore> page, FLogUserScore log);
	
	/**
	 * 查询vip6购买记录
	 */
	public FLogUserAction selectVip6ByUser(Integer fuid, Integer type);

	/**
	 * 用户登陆IP排序查询
	 */
	Pagination<FLogUserAction> selectIpLoginRankPageList(Pagination<FLogUserAction> page, FLogUserAction log);
}
