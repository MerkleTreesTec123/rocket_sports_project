package com.qkwl.service.admin.bc.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.entrust.FEntrust;

/**
 * 委单-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FEntrustMapper {

	/**
	 * 插入委单
	 * 
	 * @param entrust 委单实体
	 * @return 插入记录数
	 */
	int insert(FEntrust entrust);

	/**
	 * 更新委单
	 * 
	 * @param entrust 委单实体
	 * @return 更新记录数
	 */
	int updateByfId(FEntrust entrust);

	/**
	 * 根据ID获取委单 （加锁）
	 * @param Id 委单id
	 * @param uId 用户id
	 * @return 委单实体
	 */
	FEntrust selectByIdLock(@Param("fuid") int uId, @Param("fid") int Id);

	/**
	 * 根据ID获取委单（未加锁）
	 * @param Id 委单id
	 * @param uId 用户id
	 * @return 委单实体
	 */
	FEntrust selectById(@Param("fuid") int uId, @Param("fid") BigInteger Id);

	/**
	 * 单状态读取委单
	 * 
	 * @param uId 用户id
	 * @param coinId 币种id
	 * @param type 类型
	 * @param state 状态
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 委单列表
	 */
	List<FEntrust> selectByTS(@Param("fuid") int uId, @Param("fcoinid") int coinId, @Param("ftype") int type, @Param("fstatus") int state, @Param("start") int start, @Param("end") int end);

	/**
	 * 多状态读取委单
	 * 
	 * @param uId 用户id
	 * @param coinId 币种id
	 * @param statelist 状态列表
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 委单列表
	 */
	List<FEntrust> selectBySList(@Param("fuid") int uId, @Param("fcoinid") int coinId, @Param("statelist") List<Integer> statelist, @Param("start") int start, @Param("end") int end);

	/**
	 * 多状态读取类型委单
	 * 
	 * @param uId 用户id
	 * @param coinId 币种id
	 * @param type 类型
	 * @param statelist 状态列表
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 委单列表
	 */
	List<FEntrust> selectByTSList(@Param("fuid") int uId, @Param("fcoinid") int coinId, @Param("ftype") int type, @Param("statelist") List<Integer> statelist, @Param("start") int start, @Param("end") int end);

	/**
	 * 多状态获取委单数量
	 * 
	 * @param uId 用户id
	 * @param coinId 币种id
	 * @param statelist 状态列表
	 * @return 查询记录数
	 */
	int selectByCount(@Param("fuid") int uId, @Param("fcoinid") int coinId, @Param("statelist") List<Integer> statelist);

	/**
	 * 根据指定条件查询委单记录
	 * 
	 * @param map 参数map
	 * @return 委单列表
	 */
	List<FEntrust> selectByFentrustList(Map<String, Object> map);

	/**
	 * 根据指定条件查询委单总数
	 * 
	 * @param map 参数map
	 * @return 查询记录数
	 */
	Integer selectByFentrustCount(Map<String, Object> map);

	/*********** 控台部分 *************/
	/**
	 * 分页查询数据
	 *  
	 * @param map 参数map
	 * @return 委单列表
	 */
	List<FEntrust> getAdminPageList(Map<String, Object> map);

	/**
	 * 分页查询数据总条数
	 * 
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countAdminPage(Map<String, Object> map);

	/**
	 * 根据主键ID获取委单
	 * 
	 * @param Id 委单id
	 * @return 委单实体
	 */
	FEntrust getById(@Param("fid") int Id);
	
	/**
     * 根据类型查询委单统计
     * @param map 参数map
     * @return 委单统计
     */
    FEntrust getTotalAmountByType(Map<String, Object> map);
}