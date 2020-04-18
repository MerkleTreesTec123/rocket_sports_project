package com.qkwl.common.rpc.entrust;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.EntrustOrderDTO;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.result.Result;

import java.math.BigInteger;
import java.util.List;

/**
 * 委单接口
 *
 * @author TT
 */
public interface IEntrustServer {

    /**
     * 买单
     *
     * @param entrustDTO 委单数据
     * @return Result 返回结果 <br/>
     * 200：委托成功 <br/>
     * 300：委托失败 <br/>
     * 400：参数错误 <br/>
     * 1000：当前币种交易错误 <br/>
     * 1001：交易额必须大于0.01 <br/>
     * 1002：当前时间段停止交易 <br/>
     * 1003：交易价格必须大于 + Result.getData() <br/>
     * 1004：交易价格必须小于 + Result.getData() <br/>
     * 1005：交易数量必须大于 + Result.getData() <br/>
     * 1006：交易数量必须小于 + Result.getData() <br/>
     * 1007：交易价格不能高于开盘价的 + Result.getData() <br/>
     * 1008：交易价格不能低于开盘价的 + Result.getData() <br/>
     * 1009：该交易暂未开放 <br/>
     * 1010：交易价格不能高于最新价的 + Result.getData() <br/>
     * 1011：交易价格不能低于最新价的 + Result.getData() <br/>
     */
    Result createBuyEntrust(EntrustOrderDTO entrustDTO);

    /**
     * 卖单
     *
     * @param entrustDTO 委单数据
     * @return Result 返回结果 <br/>
     * 200：委托成功 <br/>
     * 300：委托失败 <br/>
     * 400：参数错误 <br/>
     * 1000：当前币种交易错误 <br/>
     * 1001：交易额必须大于0.01 <br/>
     * 1002：当前时间段停止交易 <br/>
     * 1003：交易价格必须大于 + Result.getData() <br/>
     * 1004：交易价格必须小于 + Result.getData() <br/>
     * 1005：交易数量必须大于 + Result.getData() <br/>
     * 1006：交易数量必须小于 + Result.getData() <br/>
     * 1007：交易价格不能高于开盘价的 + Result.getData() <br/>
     * 1008：交易价格不能低于开盘价的 + Result.getData() <br/>
     * 1009：该交易暂未开放 <br/>
     * 1010：交易价格不能高于最新价的 + Result.getData() <br/>
     * 1011：交易价格不能低于最新价的 + Result.getData() <br/>
     */
    Result createSellEntrust(EntrustOrderDTO entrustDTO);

    /**
     * 取消委单
     *
     * @param userId    用户ID
     * @param entrustId 委单id
     * @return Result 返回结果 <br/>
     * 200：取消成功 <br/>
     * 300：取消失败 <br/>
     * 400：参数错误 <br/>
     * 1000：委单记录不存在 <br/>
     * 1001：委单已取消 <br/>
     */
    Result cancelEntrust(Integer userId, BigInteger entrustId);

    /**
     * 分页查询当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    委单实体
     * @param stateList  委单状态列表
     * @return 分页实体对象
     */
    Pagination<FEntrust> listEntrust(Pagination<FEntrust> paginParam, FEntrust entrust, List<Integer> stateList);

    /**
     * 分页查询历史当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    历史委单实体
     * @param stateList  委单状态列表
     * @return 分页实体对象
     */
    Pagination<FEntrustHistory> listEntrustHistory(Pagination<FEntrustHistory> paginParam, FEntrustHistory entrust, List<Integer> stateList);

    /**
     * 根据用户和委单ID读取委单
     *
     * @param userId       用户ID
     * @param entrustId 委单ID
     * @return 委单实体对象
     */
    FEntrust getEntrust(Integer userId, BigInteger entrustId);

    /**
     * 根据用户和委单ID读取委单历史记录
     *
     * @param userId       用户ID
     * @param entrustId 委单ID
     * @return 历史委单实体对象
     */
    FEntrustHistory getEntrustHistory(Integer userId, BigInteger entrustId);

    /**
     * 读取委单交易明细
     *
     * @param entrustId 委单ID
     * @return 委单实体对象列表
     */
    List<FEntrustLog> getEntrustLog(BigInteger entrustId);
}
