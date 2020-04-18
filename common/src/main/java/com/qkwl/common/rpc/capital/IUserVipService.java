package com.qkwl.common.rpc.capital;

import com.qkwl.common.result.Result;

/**
 * 用户VIP
 */
public interface IUserVipService {
    /**
     * 创建用户VIP购买订单
     *
     * @param userId    用户ID
     * @param coinId    币种ID
     * @param teadePass 交易密码
     * @param ip        IP地址
     * @return 返回结果 <br/>
     * 200：购买成功 <br/>
     * 300：购买失败 <br/>
     * 400：参数错误 <br/>
     * 1000：您已经是VIP6 <br/>
     * 10118：余额不足 <br/>
     */
    Result createUserVipOrder(Integer userId, Integer coinId, String teadePass, String ip);
}
