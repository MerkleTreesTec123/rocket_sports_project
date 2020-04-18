package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.report.ReportCapital;
import com.qkwl.common.dto.report.ReportTrade;

import java.util.List;
import java.util.Map;

/**
 * 报表接口
 * Created by ZKF on 2017/7/25.
 */
public interface IAdminReportService {

    /**
     * 查询交易报表数据
     * @return
     */
    List<ReportTrade> selectHourTradeReport(Map<String, Object> map);

    /**
     * 查询交易日报表数据
     * @return
     */
    List<ReportTrade> selectDayTradeReport(Map<String, Object> map);

    /**
     * 查询充提报表数据
     * @return
     */
    List<ReportCapital> selectHourCapitalReport(Map<String, Object> map);

    /**
     * 查询充提日报表数据
     * @return
     */
    List<ReportCapital> selectDayCapitalReport(Map<String, Object> map);

    /**
     * 查询收益数据
     */
    List<ReportCapital> selectIncomeReport(Map<String, Object> map);

    /**
     * 查询运营数据-邮件
     */
    Integer selectEmailNum(Map<String, Object> map);

    /**
     * 查询运营数据-短信
     */
    Integer selectSmsNum(Map<String, Object> map);
}
