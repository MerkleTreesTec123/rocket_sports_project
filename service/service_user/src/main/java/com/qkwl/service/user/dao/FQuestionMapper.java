package com.qkwl.service.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FQuestion;

/**
 * 问答数据操作接口
 * @author ZKF
 */
@Mapper
public interface FQuestionMapper {
    
	/**
	 * 更新问答
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int updateByPrimaryKey(FQuestion record);
    
    /**
     * 插入问答数据
     * @param record 实体对象
     * @return 成功条数
     */
    int insert(FQuestion record);
    
    /**
     * 根据主键id查询问答信息
     * @param fid 主键ID
     * @return 实体对象
     */
    FQuestion selectByPrimaryKey(Integer fid);
    
    /**
     * 查询问答记录
     * @param map 条件对象
     * @return 实体对象列表
     */
    List<FQuestion> getPageFQuestionList(Map<String, Object> map);
    
    /**
     * 查询问答记录总数
     * @param map 条件对象
     * @return 记录总数
     */
    int getPageFQuestionCount(Map<String, Object> map);
    
    /**
     * 根据主键删除问答记录
     * @param fid 主键ID
     * @return 成功条数
     */
    int deleteByPrimaryKey(Integer fid);
    
}