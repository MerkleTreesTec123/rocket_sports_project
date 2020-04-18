package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.capital.UserFinancesOrderDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.result.Result;

/**
 * 用户理财接口
 *
 * @author LY
 */
public interface IUserFinancesService {
    /**
     * 创建用户理财订单
     *
     * @param userFinancesOrder 理财ID
     * @return Result 返回结果 <br/>
     * 200：理财订单创建成功 <br/>
     * 300：理财订单创建失败 <br/>
     * 400：参数错误 <br/>
     * 1000： 存币类型不存在 <br/>
     * 1001： 余额不足 <br/>
     */
    Result createUserFinancesOrder(UserFinancesOrderDTO userFinancesOrder);

    /**
     * 取消用户理财记录
     *
     * @param id 理财记录ID
     * @return Result 返回结果 <br/>
     * 200：存币记录取消成功 <br/>
     * 300：存币记录取消失败 <br/>
     * 400：参数错误 <br/>
     * 1000： 存币记录不存在 <br/>
     * 1001： 存币记录已发放 <br/>
     * 1002： 存币记录已取消 <br/>
     */
    Result cancleUserFinancesOrder(Integer id, Integer userId);

    /**
     * 分页查询用户理财记录
     *
     * @param page         分页参数
     * @param userFinances 理财实体
     * @return 记录列表
     */
    Pagination<FUserFinancesDTO> ListUserFinances(Pagination<FUserFinancesDTO> page, FUserFinancesDTO userFinances);
}
