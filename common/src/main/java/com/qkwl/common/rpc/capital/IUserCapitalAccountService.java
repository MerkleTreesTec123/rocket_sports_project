package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.common.dto.capital.UserBankinfoDTO;
import com.qkwl.common.dto.capital.UserVirtualAddressWithdrawDTO;
import com.qkwl.common.result.Result;

import java.util.List;

/**
 * 用户银行卡、地址接口
 *
 * @author ZKF
 */
public interface IUserCapitalAccountService {

    /**
     * 创建银行信息
     *
     * @param userBankinfo 银行信息DTO
     * @return Result   返回结果<br/>
     * 200 : 成功
     * 1000: 提现银行未找到！<br/>
     * 1001: 修改失败！<br/>
     * 1002: 新增失败！<br/>
     * 1003: 您没有绑定手机或谷歌验证，请去<a href\='/user/security.html'>安全中心</a>绑定手机或谷歌验证后提现！<br/>
     * 1004: 请先完成实名认证！<br/>
     * 1005: 银行卡账号名必须与您的实名认证姓名一致！<br/>
     * 1006: 您已绑定该银行卡，请勿重复绑定！<br/>
     */
    Result createOrUpdateBankInfo(UserBankinfoDTO userBankinfo);

    /**
     * 删除银行卡信息
     *
     * @param userId 用户ID
     * @param bankId 银行卡ID
     * @return Result   返回结果<br/>
     * 200 : 删除成功
     * 1000: 记录不存在！<br/>
     * 1001: 删除失败！<br/>
     */
    Result deleteBankInfo(Integer userId, Integer bankId);

    /**
     * 查询银行信息列表
     *
     * @param userId 用户ID
     * @param coinId 币种ID
     * @param type   类型ID
     * @return List
     */
    List<FUserBankinfoDTO> listBankInfo(Integer userId, Integer coinId, Integer type);

    /**
     * 创建充值地址
     *
     * @param userId 用户ID
     * @param coinId 币种ID
     * @param ip     操作IP
     */
    Result createCoinAddressRecharge(Integer userId, Integer coinId, String ip);

    /**
     * 创建提现地址
     *
     * @param userVirtualAddressWithdrawDTO  用户虚拟币提现地址DTO
     * @return Result
     * 1000 请先绑定手机或谷歌
     * 1001 请先设置交易密码
     */
    Result createCoinAddressWithdraw(UserVirtualAddressWithdrawDTO userVirtualAddressWithdrawDTO);

    /**
     * 删除提现地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return Result
     * 1002 地址不存在
     */
    Result deleteCoinAddressWithdraw(Integer userId, Integer addressId);

    /**
     * 查询提现地址列表
     *
     * @param userId 用户ID
     * @param coinId 币种ID
     * @return List
     */
    List<FUserVirtualAddressWithdrawDTO> listCoinAddressWithdraw(Integer userId, Integer coinId);

}
