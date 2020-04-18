package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.web.FSystemLan;

import java.util.List;

/**
 * 语言类型-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FLanguageTypeMapper {

    /**
     * 查询
     * @param fid 语言类型id
     * @return 语言类型实体
     */
    FSystemLan selectByPrimaryKey(Integer fid);

    /**
     * 查询所有
     * @return 语言类型列表
     */
    List<FSystemLan> selectAll();

}