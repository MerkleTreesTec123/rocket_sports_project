package com.qkwl.service.admin.bc.impl;

import com.qkwl.common.dto.report.ReportCapital;
import com.qkwl.common.dto.report.ReportTrade;
import com.qkwl.common.rpc.admin.IAdminReportService;
import com.qkwl.service.admin.bc.dao.OperationDataMapper;
import com.qkwl.service.admin.bc.dao.ReportCapitalMapper;
import com.qkwl.service.admin.bc.dao.ReportTradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 报表查询接口实现
 */
@Service("adminReportService")
public class AdminReportServiceImpl implements IAdminReportService {

    @Autowired
    private ReportTradeMapper reportTradeMapper;
    @Autowired
    private ReportCapitalMapper reportCapitalMapper;
    @Autowired
    private OperationDataMapper operationDataMapper;

    @Override
    public List<ReportTrade> selectHourTradeReport(Map<String, Object> map) {
        return reportTradeMapper.selectReportByHour(map);
    }

    @Override
    public List<ReportTrade> selectDayTradeReport(Map<String, Object> map) {
        return reportTradeMapper.selectReportByDay(map);
    }

    @Override
    public List<ReportCapital> selectHourCapitalReport(Map<String, Object> map) {
        return reportCapitalMapper.selectReportByHour(map);
    }

    @Override
    public List<ReportCapital> selectDayCapitalReport(Map<String, Object> map) {
        return reportCapitalMapper.selectReportByDay(map);
    }

    @Override
    public List<ReportCapital> selectIncomeReport(Map<String, Object> map) {
        return reportCapitalMapper.selectIncomeReportByHours(map);
    }

    @Override
    public Integer selectEmailNum(Map<String, Object> map) {
        return operationDataMapper.selectEmailNum(map);
    }

    @Override
    public Integer selectSmsNum(Map<String, Object> map) {
        return operationDataMapper.selectSmsNum(map);
    }
}
