package com.qkwl.common.rpc.user;


import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.ReturnResult;

/**
 * 用户实名接口
 * @author ZKF
 */
public interface IUserIdentityService {

	/**
	 * 普通实名认证提交
	 * @param identity
	 * 200 : 成功
	 * 1014: 网络超时！<br/>
	 */
	Result updateNormalIdentity(FUserIdentity identity);

	/**
	 * 国内实名认证提交
	 * @param identity
	 * @return Result   返回结果<br/>
	 * 200 : 成功
	 * 1000: 身份证号码长度应该为15位或18位。<br/>
	 * 1001: 身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。<br/>
	 * 1002: 身份证生日无效。<br/>
	 * 1003: 身份证生日不在有效范围。<br/>
	 * 1004: 身份证月份无。<br/>
	 * 1005: 身份证日期无效。<br/>
	 * 1006: 身份证地区编码错误。<br/>
	 * 1007: 身份证无效，不是合法的身份证号码。<br/>
	 * 1010: 身份证格式验证异常！<br/>
	 * 1011: 身份证号码已被实名验证，不能重复验证，请注意您的信息安全！<br/>
	 * 1012: 身份证号与姓名不统一！<br/>
	 * 1013: 认证失败，错误码：" + errorcode + "，请联系客服！<br/>
	 * 1014: 网络超时！<br/>
	 * 1015: 非法请求！<br/>
	 */
	Result updateChinaIdentity(FUserIdentity identity);

	/**
	 * 根据uid查询用户实名
	 * @param fuid
	 * @return
	 */
	FUserIdentity selectByUser(Integer fuid);

	/**
	 * 根据身份证号查询用户实名
	 * @param code
	 * @return
	 */
	FUserIdentity selectByCode(String code);

}
