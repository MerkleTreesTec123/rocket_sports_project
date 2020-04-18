package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.capital.*;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.result.Result;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户资产计算接口
 *
 * @author ZKF
 */
public interface IUserCapitalService {

    /**
     * 获取是否首次充值
     *
     * @param userId 用户ID
     * @return true：是，false：否
     */
    Boolean getIsFirstCharge(int userId);

    /**
     * 创建虚拟币操作订单(充值提现)
     *
     * @param orderDTO       订单DTO
     * @return Result 返回结果 <br/>
     * 充值： <br/>
     * 200：订单创建成功 <br/>
     * 300：订单创建失败 <br/>
     * 400：参数错误 <br/>
     * 1000： 充值订单已存在 <br/>
     * 提现： <br/>
     * 1001： 最小提现数量为+Result.getData <br/>
     * 1002： 最大提现数量为+Result.getData <br/>
     * 1003： 每日虚拟币提现最多+Result.getData+次，请明天提现 <br/>
     * 1004： 您今日总共还可提现虚拟币+Result.getData+个 <br/>
     * 1005： 提现地址错误 <br/>
     * 10118： 余额不足 <br/>
     */
    Result createCoinOperationOrder(CoinOperationOrderDTO orderDTO);

    /**
     * 取消虚拟币操作订单
     *
     * @param userId   用户ID
     * @param recordId 记录ID
     * @param Ip       操作IP
     * @return Result 返回结果 <br/>
     * 充值： <br/>
     * 200：订单撤销成功 <br/>
     * 300：订单撤销失败 <br/>
     * 400：参数错误 <br/>
     * 1000： 订单记录不存在 <br/>
     * 1001： 订单记录状态错误<br/>
     */
    Result cancleCoinOperationOrder(Integer userId, Integer recordId, String Ip);

    /**
     * 查询虚拟币操作订单记录
     *
     * @param param     分页实体对象
     * @param operation 虚拟币充提记录表实体对象
     * @return Pagination
     */
    Pagination<FVirtualCapitalOperationDTO> listVirtualCapitalOperation(Pagination<FVirtualCapitalOperationDTO> param,
                                                                        FVirtualCapitalOperationDTO operation);

    /**
     * 创建法币操作订单(充值提现)
     *
     * @param orderDTO 订单DTO
     * @return Result
     * 1000 银行卡未找到
     * 1004 币种设置为空
     * 1005 最小提现数量
     * 1006 最大提现数量
     * 1007 每日人民币提现最多
     * 1008 您今日总共还可提现人民币
     * 1009 余额不足
     * 1100： 充值银行未找到 <br/>
     * 1101： 系统银行账号已停用<br/>
     * 1102： 最小充值金额为%d <br/>
     * 1103： 最大充值金额为%d <br/>
     * 1104： 订单创建失败 <br/>
     * 1105： 请先完成实名认证！ <br/>
     */
    Result createBankOperationOrder(BankOperationOrderDTO orderDTO);

    /**
     * 取消订单信息
     * @param userId 用户id
     * @param recordId 订单id
     * @param ip    ip地址
     * @return Result
     * 200 : 订单撤销成功
     * 1000: 订单记录不存在！<br/>
     * 1001: 订单撤销失败！<br/>
     * 1002：订单状态已改变，请刷新重试 <br/>
     */
    Result cancelBankOperationOrder(Integer userId, Integer recordId, String ip);

    /**
     * 根据id查询充值记录
     * @param id
     * @return
     */
    FWalletCapitalOperationDTO getWalletCapitalOperationById(Integer id);

    /**
     * 查询法币操作记录
     *
     * @param param     分页实体对象
     * @param operation 人民币充提记录表实体对象
     * @return Pagination
     */
    Pagination<FWalletCapitalOperationDTO> listWalletCapitalOperation(Pagination<FWalletCapitalOperationDTO> param,
                                                                      FWalletCapitalOperationDTO operation);

    /**
     *
     * @param page 分页实体对象
     * @param record 手工充值记录
     * @return Pagination
     */
    Pagination<FLogConsoleVirtualRecharge> listLogConsoleVirtualRecharge(Pagination<FLogConsoleVirtualRecharge> page,
                                                                         FLogConsoleVirtualRecharge record);

    /**
     *
     * 收益记录
     * @return Pagination
     */
    Pagination<CommissionRecord> listCommissionRecord(Pagination<CommissionRecord> page,Integer uid,Integer coinid);

    /**
     * 查询币种的收益
     * @param uid
     * @param coinid
     * @return
     */
    BigDecimal getCommissionTotalByID(Integer uid,Integer coinid);

    Pagination<FinanceRecordDTO> listFinanceRecord(Pagination<FinanceRecordDTO> page, Integer uid,Integer coinId,Integer op);









}
