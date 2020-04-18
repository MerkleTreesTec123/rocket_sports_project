package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserPriceclock;
import java.util.List;

/**
 * 价格闹钟数据操作接口
 * @author LY
 *
 */
@Mapper
public interface FUserPriceclockMapper {
	
	/**
	 * 插入
	 * @param record
	 * @return
	 */
    int insert(FUserPriceclock record);

    /**
     * 根据用户id查询用户所有价格闹钟数据
     * @param fuid 用户id
     * @return
     */
    List<FUserPriceclock> selectByUser(Integer fuid);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(FUserPriceclock record);
    
    /**
     * 查找用户价格闹钟数据
     * @param record
     * @return
     */
    FUserPriceclock selectByParam(FUserPriceclock record);
}