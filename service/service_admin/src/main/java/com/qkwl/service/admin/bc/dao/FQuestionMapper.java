package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FQuestion;

/**
 * 问答数据操作接口-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FQuestionMapper {
    
    
    /**
     * 根据主键id查询问答信息
     * @param fid 问题id
     * @return 问题实体
     */
    FQuestion selectByPrimaryKey(Integer fid);
    
    /**
     * 分页查询问答记录
     * @param map 参数map
     * @return 问题列表
     */
    List<FQuestion> getQuestionPageList(Map<String, Object> map);
    
    /**
     * 分页查询问答记录的总记录数
     * @param map 参数map
     * @return 查询记录数 
     */
    int countQuestionPageList(Map<String, Object> map);
    
    /**
     * 更新客服回答
     * @param question 问题实体
     * @return 更新记录数
     */
    int answerQuestion(FQuestion question);
}