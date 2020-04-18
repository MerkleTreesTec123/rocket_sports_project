package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.activity.FActivityRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 活动数据操作接口
 * @author LY
 *
 */
@Mapper
public interface FActivityRecordMapper {



    /**
     * 分页查询列表
     * @param map
     * @return
     */
	List<FActivityRecord> selectActivityRecordList(Map<String, Object> map);


	int updateStatusByPrimaryKey(FActivityRecord record);

}