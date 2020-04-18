package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FUserPushDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * PUSH资产数据操作接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FUserPushMapper {
	/**
	 * 新增
	 * 
	 * @param record
	 * @return
	 */
	int insert(FUserPushDO record);

	/**
	 * 查询
	 * 
	 * @param fid
	 * @return
	 */
	FUserPushDO selectByPrimaryKey(Integer fid);

	/**
	 * 更新
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(FUserPushDO record);
	
	/**
	 * 分页查询PUSH资产数据
	 * @param map 条件MAP
	 * @return
	 */
	List<FUserPushDO> selectUserPushList(Map<String, Object> map);
	
	/**
	 * 分页查询PUSH资产数据条数
	 * @param map  条件MAP
	 * @return
	 */
	int selectUserPushCount(Map<String, Object> map);
}