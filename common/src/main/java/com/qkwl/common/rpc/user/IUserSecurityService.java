package com.qkwl.common.rpc.user;

import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.result.Result;

/**
 * 安全设置
 */
public interface IUserSecurityService {
    /**
     * 更新用户安全信息
     * @param user          用户信息
     * @param action        用户行为
     * @param scoreType     积分类型
     * @return Result
     * 200 : 成功
     * 1000: 用户不存在！<br/>
     * 1001: 邮箱已绑定！<br/>
     * 1002: 邮箱地址已绑定，请更换邮箱地址！<br/>
     * 1003: 邮箱地址不能为空！<br/>
     * 1004: 手机已绑定！<br/>
     * 1006: 手机号已存在！<br/>
     * 1007: 谷歌已绑定！<br/>
     * 1008: 参数不合法！<br/>
     * 1009: 您没有绑定手机或谷歌验证暂不允许修改密码。<br/>
     * 1010: 登录密码不允许与交易密码一致！<br/>
     * 1011: 请先完成实名认证！<br/>
     * 1012: 请输入身份证号！<br/>
     * 1013: 身份证号码错误！<br/>
     * 1014: 交易密码不允许与登录密码一致<br/>
     * 1015: 5分钟内只能发送一次邮件！<br/>
     * 1016: 邮件发送失败，请稍后再试！<br/>
     * 1017= 验证邮件已发送，请及时验证！<br/>
     * 1018: 原始密码错误！<br/>
     */
    Result updateUserSecurityInfo(FUser user, ValidateParamInfo validateParam, LogUserActionEnum action, ScoreTypeEnum scoreType) throws BCException;

    /**
     * 重置登录密码
     * @param user              用户信息
     * @param validateParam     验证信息
     * @param findType          找回密码的类型
     * 200 : 成功
     * 1000: 该密码找回链接已过期！<br/>
     * 1001: 登录密码不允许与交易密码一致！<br/>
     * 1002: 手机号码不存在，请确认您的账号！<br/>
     * 1003: 未知错误！<br/>
     * 1004: 找回密码失败，请稍后再试！<br/>
     */
    Result restLoginPasword(FUser user, ValidateParamInfo validateParam, Integer findType);

    /**
     * 找回交易密码
     * @param user              用户信息
     * @param validateParam     验证信息
     * 200 : 成功
     * 1000: 该密码找回链接已过期！<br/>
     * 1001: 登录密码不允许与交易密码一致！<br/>
     * 1002: 手机号码不存在，请确认您的账号！<br/>
     * 1003: 未知错误！<br/>
     * 1004: 找回密码失败，请稍后再试！<br/>
     */
    Result restTradePassword(FUser user, ValidateParamInfo validateParam);

    /**
     * 根据用户查用户安全信息日志记录
     * @param uid 用户ID
     * @param page 分页实体对象
     * @param logType 日志记录类型<br/>
     *            logType = 1 登陆日志<br/>
     *            logType = 2 安全设置日志<br/>
     * @return 分页实体对象
     */
    Pagination<FLogUserAction> listSettingLogByUser(Integer uid, Pagination<FLogUserAction> page, Integer logType);

}
