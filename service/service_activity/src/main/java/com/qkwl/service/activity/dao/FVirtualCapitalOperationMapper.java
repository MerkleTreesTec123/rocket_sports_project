package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.report.ReportCapital;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 虚拟币资金操作数据接口
 * Created by ZKF on 2017/7/24.
 */
@Mapper
public interface FVirtualCapitalOperationMapper {

    /**
     * 统计虚拟币充提报表数据
     * @param map
     * @return
    */
    ReportCapital selectReport(Map<String, Object> map);

    /**
     * 分页查询记录总条数
     * @param map
     * @return
     */
    int countVirtualCapitalOperation(Map<String, Object> map);


}
