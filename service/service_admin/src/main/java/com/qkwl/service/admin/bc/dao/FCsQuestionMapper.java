package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.admin.FCsQuestion;

@Mapper
public interface FCsQuestionMapper {
    int deleteByPrimaryKey(Integer fid);

    int insert(FCsQuestion record);

    FCsQuestion selectByPrimaryKey(Integer fid);

    List<FCsQuestion> selectAll();

    int updateByPrimaryKey(FCsQuestion record);
    
    List<FCsQuestion> selectByPage(Map<String, Object> map);
    
    int countByPage(Map<String, Object> map);
}