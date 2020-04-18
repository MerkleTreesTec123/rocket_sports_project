package com.qkwl.service.match.dao;

import com.qkwl.common.dto.user.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommissionRecordMapper {

    int add(CommissionRecord record);

}
