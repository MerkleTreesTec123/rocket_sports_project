package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.exceptions.BCException;

import java.util.List;

/**
 * 佣金记录
 */
public interface IAdminCommissionRecordService {


    List<CommissionRecord> getAll(Integer uid,Integer introuid,Integer status);

    boolean update(Integer id,Integer status) throws BCException;



}
