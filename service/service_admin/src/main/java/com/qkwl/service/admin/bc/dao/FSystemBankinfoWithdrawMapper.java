package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;

import java.util.List;

/**
 * 系统提现银行卡-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FSystemBankinfoWithdrawMapper {

    /**
     * 查询体现的银行记录
     * @return 银行卡列表
     */
    List<FSystemBankinfoWithdraw> selectAll();

}