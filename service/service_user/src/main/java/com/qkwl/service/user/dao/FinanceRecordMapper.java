package com.qkwl.service.user.dao;

import com.qkwl.common.dto.finances.FinanceRecordDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinanceRecordMapper {


    /**
     * 查询列表
     * @param params
     * @return
     */
    List<FinanceRecordDTO> selectByParams(Map<String, Object> params);

    /**
     * 查询接口
     * @param params
     * @return
     */
    Integer countPage(Map<String, Object> params);

    /**
     * 添加
     * @param
     * @return
     */
    int insert(FinanceRecordDTO financeRecordDTO);

    /***
     * 修改状态
     * @param financeRecordDTO
     * @return
     */
    int updatePyPrimaryKey(FinanceRecordDTO financeRecordDTO);











}
