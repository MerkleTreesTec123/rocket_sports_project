package com.qkwl.service.activity.dao;


import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;

import com.qkwl.common.dto.report.ReportCapital;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 钱包充值提现记录数据操作接口
 *
 * @author ZKF
 */
@Mapper
public interface FWalletCapitalOperationMapper {

    /**
     * 根据参数查询记录的状态
     *
     * @param map 查询条件MAP
     * @return 实体对象列表
     */
    List<FWalletCapitalOperationDTO> getWalletCapitalOperationByParam(Map<String, Object> map);

    /**
     * 根据参数查询记录的状态
     *
     * @param map 查询条件MAP
     * @return 实体对象列表
     */
    List<FWalletCapitalOperationDTO> getWalletCapitalOperationStatusByParam(Map<String, Object> map);

    /**
     * 更新记录的状态
     *
     * @param record 实体对象
     * @return 成功条数
     */
    int updateStatusByPrimaryKey(FWalletCapitalOperationDTO record);


    /**
     * 统计人民币充提报表数据
     * @param map
     * @return
     */
    ReportCapital selectReport(Map<String, Object> map);

    /**
     * 分页查询记录的记录数
     * @param map
     * @return
     */
    int countWalletCapitalOperation(Map<String,Object> map);

}