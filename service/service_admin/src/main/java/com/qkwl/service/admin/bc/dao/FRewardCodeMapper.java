package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FRewardCodeDTO;

/**
 * 兑换券数据操作接口-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FRewardCodeMapper {
	
	/**
	 * 删除兑换券
	 * @param fid 兑换券id
	 * @return 删除记录数
	 */
    int deleteByPrimaryKey(Integer fid);
    
    /**
     * 新增兑换券
     * @param record 兑换券实体
     * @return 插入记录数
     */
    int insert(FRewardCodeDTO record);
    
    /**
     * 批量新增兑换券
     * @param list 兑换券列表
     * @return 查询记录数
     */
    int insertList(List<FRewardCodeDTO> list);
    
    /**
     * 分页查询
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FRewardCodeDTO> getRewardCodePageList(Map<String, Object> map);
    
    /**
     * 分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数 
     */
    int countRewardCodePageList(Map<String, Object> map);


    BigDecimal getTotalAmount(Map<String, Object> map);
}