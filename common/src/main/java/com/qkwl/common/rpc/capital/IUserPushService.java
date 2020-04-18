package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.capital.FUserPushDTO;
import com.qkwl.common.dto.capital.UserPushOrderDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.result.Result;

/**
 * PUSH资产接口
 *
 * @author LY
 */
public interface IUserPushService {

    /**
     * 创建PUSH资产记录
     *
     * @param userPushOrder PUSH DTO
     * @return Result 返回结果 <br/>
     * 200：订单创建成功 <br/>
     * 300：订单创建失败 <br/>
     * 400：参数错误 <br/>
     * 1000：对方UID不能为自己的UID <br/>
     * 1001：对方UID不存在 <br/>
     * 10018：余额不足 <br/>
     */
    Result createUserPushOrder(UserPushOrderDTO userPushOrder);

    /**
     * 取消PUSH资产记录
     *
     * @param id     主键ID
     * @param userId 取消用户ID
     * @return Result 返回结果 <br/>
     * 200：PUSH记录取消成功 <br/>
     * 300：PUSH记录取消失败 <br/>
     * 400：参数错误 <br/>
     * 1000：PUSH记录未找到 <br/>
     * 1001：PUSH记录状态错误 <br/>
     */
    Result cancleUserPushOrder(Integer id, Integer userId);

    /**
     * 付款USH资产记录
     *
     * @param id        主键ID
     * @param userId    付款用户ID
     * @param coinId    支付币种ID
     * @param tradePass 交易密码
     * @param ip        IP地址
     * @return Result 返回结果 <br/>
     * 200：付款成功 <br/>
     * 300：付款失败 <br/>
     * 400：参数错误 <br/>
     * 1000：PUSH记录未找到 <br/>
     * 1001：PUSH记录已取消 <br/>
     * 1002：PUSH记录已付款 <br/>
     * 10120：请设置交易密码 <br/>
     * 10118：余额不足 <br/>
     */
    Result payUserPushOrder(Integer id, Integer userId, Integer coinId, String tradePass, String ip);

    /**
     * 分页查询PUSH资产记录
     *
     * @param page     分页对象
     * @param userPush 实体对象
     * @return 分页对象
     */
    Pagination<FUserPushDTO> listUserPushOrder(Pagination<FUserPushDTO> page, FUserPushDTO userPush);
}
