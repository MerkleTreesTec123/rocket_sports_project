package com.qkwl.service.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.activity.FActivityRecord;

/**
 * 活动数据操作接口
 * @author LY
 *
 */
@Mapper
public interface FActivityRecordMapper {

	/**
	 * 插入
	 * @param record
	 * @return
	 */
    int insert(FActivityRecord record);
	
    /**
     * 分页查询列表
     * @param map
     * @return
     */
	List<FActivityRecord> selectActivityRecordList(Map<String, Object> map);

	/**
	 * 查询总条数
	 * @param map
	 * @return
	 */
	int selectActivityRecordCount(Map<String, Object> map);
	
	/**
     * 查询用户推广人数
     * @param fintrouid
     * @return
     */
	int selectByUserIntro(@Param("fintrouid") Integer fintrouid);

}