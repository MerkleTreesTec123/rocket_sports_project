package com.qkwl.service.entrust.dao;

import com.qkwl.service.entrust.model.EntrustLogDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * 委单成交明细数据操作接口
 *
 * @author LY
 */
@Mapper
public interface FEntrustLogMapper {

    /**
     * 查找委单成交明细
     *
     * @param fentrustid 委单ID
     * @return 委单成交明细实体对象列表
     */
    List<EntrustLogDO> selectByEntrustId(@Param("fentrustid") BigInteger fentrustid);
}