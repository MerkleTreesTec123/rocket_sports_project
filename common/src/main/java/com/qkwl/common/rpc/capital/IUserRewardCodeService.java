package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.result.Result;

/**
 * 兑换码接口
 *
 * @author ZKF
 */
public interface IUserRewardCodeService {

    /**
     * 分页查询兑换码
     *
     * @param page 分页实体对象
     * @param code 兑换码实体对象
     * @return 分页实体对象
     */
    Pagination<FRewardCodeDTO> listRewardeCode(Pagination<FRewardCodeDTO> page, FRewardCodeDTO code);

    /**
     * 使用兑换码
     *
     * @param userId 用户ID
     * @param code   兑换码实体对象
     * @param ip   IP地址
     * @return 返回结果 <br/>
     * 200：使用成功 <br/>
     * 300：使用失败 <br/>
     * 400：参数错误 <br/>
     * 1000：充值码错误 <br/>
     * 1001：充值码已激活 <br/>
     * 1002：充值码已绑定用户 <br/>
     * 1003：您本次活动已多次使用充值码 <br/>
     * 1004：充值码已过期 <br/>
     */
    Result UseRewardCode(Integer userId, String code, String ip);

}
