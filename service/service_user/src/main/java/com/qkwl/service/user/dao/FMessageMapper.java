package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FMessage;

import java.util.List;
import java.util.Map;

/**
 * 消息数据操作接口
 * @author ZKF
 */
@Mapper
public interface FMessageMapper {

	/**
	 * 新增消息
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(FMessage record);
    
    /**
     * 根据主键id查询用户消息
     * @param fid 主键ID
     * @return 实体对象
     */
    FMessage selectByPrimaryKey(Integer fid);
    
    /**
     * 查询用户消息记录
     * @param map 条件MAP
     * @return 实体对象列表
     */
    List<FMessage> getPageFMessageList(Map<String, Object> map);
    
    /**
     * 查询用户消记录总数
     * @param map 条件对象
     * @return 记录总数
     */
    int getPageFMessageCount(Map<String, Object> map);
    
    /**
     * 更新用户系统消息
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FMessage record);
    
    /**
     * 根据主键id删除用户消息记录
     * @param fid 主键ID
     * @return 成功条数
     */
    int deleteByPrimaryKey(Integer fid);
}