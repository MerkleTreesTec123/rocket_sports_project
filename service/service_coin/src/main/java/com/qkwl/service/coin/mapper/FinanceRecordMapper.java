package com.qkwl.service.coin.mapper;

import com.qkwl.common.dto.finances.FinanceRecordDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinanceRecordMapper {

    /**
     * 添加
     * @param
     * @return
     */
    int insert(FinanceRecordDTO financeRecordDTO);
//
//    /**
//     * 查询列表
//     * @param params
//     * @return
//     */
//    List<FinanceRecordDTO> selectByParams(Map<String, Object> params);
//
//    /**
//     * 查询接口
//     * @param params
//     * @return
//     */
//    Integer countPage(Map<String, Object> params);











}
