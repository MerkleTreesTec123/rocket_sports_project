package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.user.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommissionRecordMapper {

    /**
     * 查询所有的佣金记录
     *
     * @param commissionRecord
     * @return
     */
    List<CommissionRecord> getAll(CommissionRecord commissionRecord);

    /**
     * 更新
     * @param record
     * @return
     */
    int update(CommissionRecord record);

    /**
     *
     *
     * @param id
     * @return
     */
    CommissionRecord select(Integer id);

    /**
     * 获取总数
     * @param params
     * @return
     */
    int getCount(Map<String,String> params);

    /**
     * 分页获取
     *
     * @param params
     * @return
     */
    List<CommissionRecord> getPageRecord(Map<String,String> params);












}
