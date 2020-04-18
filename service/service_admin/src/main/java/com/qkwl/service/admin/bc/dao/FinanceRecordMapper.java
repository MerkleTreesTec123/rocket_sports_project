package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.finances.FinanceRecordDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinanceRecordMapper {

    /**
     * 添加
     * @param
     * @return
     */
    int insert(FinanceRecordDTO financeRecordDTO);

    /**
     * 更新状态
     * @param financeRecordDTO
     * @return
     */
    int updatePyPrimaryKey(FinanceRecordDTO financeRecordDTO);

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











}
