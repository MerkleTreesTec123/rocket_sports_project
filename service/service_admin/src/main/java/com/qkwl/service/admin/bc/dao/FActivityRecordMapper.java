package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
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
     * 查询用户注册赠送记录
     * @param map
     * @return
     */
	List<FActivityRecord> selectByUserList(Map<String, Object> map);
	
	/**
	 * 查询活动赠送
	 * @param fuid
	 * @return
	 */
	BigDecimal selectByUserAmount(@Param("fuid") Integer fuid, @Param("fcoinid") Integer fcoinid);

}