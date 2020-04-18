package com.qkwl.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.IdentityTypeEnum;
import com.qkwl.common.dto.Enum.LimitTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FIdentityInfo;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.id.IDCard;
import com.qkwl.common.result.Result;
import com.qkwl.common.id.IDCardRemoteAuth;
import com.qkwl.common.util.Utils;
import com.qkwl.service.user.dao.FIdentityInfoMapper;
import com.qkwl.service.user.dao.FUserIdentityMapper;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.rpc.user.IUserIdentityService;

import com.qkwl.service.user.dao.FUserMapper;
import com.qkwl.service.user.utils.MQSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户实名接口实现
 */
@Service("userIdentityService")
public class UserIdentityServiceImpl implements IUserIdentityService {

	private static final Logger logger = LoggerFactory.getLogger(UserIdentityServiceImpl.class);


	@Autowired
	private FUserIdentityMapper userIdentityMapper;
	@Autowired
	private FIdentityInfoMapper identityInfoMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private MQSend mqSend;
	@Autowired
	private ScoreHelper scoreHelper;
	@Autowired
	private LimitHelper limitHelper;

	@Override
	public FUserIdentity selectByUser(Integer fuid) {
		return userIdentityMapper.selectByUser(fuid);
	}

	/**
	 * 普通实名认证提交
	 * @param identity
	 * 200 : 成功
	 * 1011: 身份证号码已被实名验证，不能重复验证，请注意您的信息安全！<br/>
	 * 1015: 非法请求！<br/>
	 */
	@Override
	public Result updateNormalIdentity(FUserIdentity identity) {
		FUser fuser = userMapper.selectByPrimaryKey(identity.getFuid());
		if (fuser.getFhasrealvalidate()) {
			return Result.failure(1015,"非法请求！");
		}

		// 验证本地用户是否已绑定
		FUser user = new FUser();
		user.setFidentityno(identity.getFcode());
		List<FUser> users = userMapper.getUserListByParam(user);
		if (users != null && users.size() > 0) {
			return Result.failure(1011,"身份证号码已被实名验证，不能重复验证，请注意您的信息安全！");// 身份证已被使用
		}

		//身份证已经提交，重复提交的时候非用同一个用户
		FUserIdentity fUserIdentity = this.selectByCode(identity.getFcode());
		if (fUserIdentity != null && !fUserIdentity.getFuid().equals(identity.getFuid())) {
			return Result.failure(1011,"身份证号码已被实名验证，不能重复验证，请注意您的信息安全！");// 身份证已被使用
		}

		FUserIdentity userIdentity = userIdentityMapper.selectByUser(identity.getFuid());
		if(userIdentity != null && !userIdentity.getFstatus().equals(IdentityStatusEnum.NOTPASS.getCode())){
			return Result.failure("身份证号码已被实名验证，不能重复验证，请注意您的信息安全！");
		} else if(userIdentity != null && userIdentity.getFstatus().equals(IdentityStatusEnum.NOTPASS.getCode())){
			identity.setFid(userIdentity.getFid());
		}

		boolean result = false;
		if(identity.getFid() != null){
			result = userIdentityMapper.updateByPrimaryKey(identity) > 0;
		} else{
			result = userIdentityMapper.insert(identity) > 0;
		}
		if(result){
			return Result.success();
		}
		return Result.failure(1015,"非法请求！");
	}

	/**
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
	 * @return
	 */
	@Override
	public Result updateChinaIdentity(FUserIdentity identity) {

		FUser fuser = userMapper.selectByPrimaryKey(identity.getFuid());
		if (fuser.getFhasrealvalidate()) {
			return Result.failure(1015,"非法请求！");
		}
		try{
			// 验证身份证号是否符合规则
			Result result = IDCard.IDCardValidate(identity.getFcode());
			if (!result.getSuccess()) {
				return result;
			}
		} catch (Exception e){
			logger.debug(e.toString());
			return Result.failure(1010,"身份证格式验证异常！");
		}

		// 验证本地用户是否已绑定
		FUser user = new FUser();
		user.setFidentityno(identity.getFcode());
		List<FUser> users = userMapper.getUserListByParam(user);
		if (users != null && users.size() > 0) {
			return Result.failure(1011,"身份证号码已被实名验证，不能重复验证，请注意您的信息安全！");// 身份证已被使用
		}
		// 验证本地身份证库是否已存在
		FIdentityInfo iden = new FIdentityInfo();
		iden.setFidentityno(identity.getFcode());
		List<FIdentityInfo> idenList = identityInfoMapper.selectByReal(iden);
		if (idenList != null && idenList.size() > 0) {
			FIdentityInfo identityInfo = idenList.get(0);
			if (identityInfo.getFisok() != 1) {
				return Result.failure(1012,"身份证号与姓名不统一！"); // 身份证号与姓名不统一
			}
			if (!identity.getFname().equals(identityInfo.getFusername())) {
				return Result.failure(1012,"身份证号与姓名不统一！");// 身份证号与姓名不统一
			}
			try {
				updateUserIdentity(identity);
				return Result.success("实名成功！");
			} catch (BCException e) {
				e.printStackTrace();
				return Result.failure(1014,"网络超时！");
			}
		}

		// 远程调用第三方查询接口
		Map<String, String> params = new HashMap<String, String>();
		params.put("idcard", identity.getFcode());
		params.put("realname", identity.getFname());
		params.put("key", redisHelper.getSystemArgs("IdentityAppKey"));
		try {
			String resultStr = IDCardRemoteAuth.IDCardAuth(params);
			JSONObject json = JSONObject.parseObject(resultStr);
			int errorcode = json.getInteger("error_code");
			if (errorcode != 0) {
				return Result.failure(1013,"认证失败，错误码：" + errorcode + "，请联系客服！",errorcode);
			}

			// 成功,保存数据
			JSONObject identityJSON = json.getJSONObject("result");
			int isok = identityJSON.getInteger("res");
			FIdentityInfo identityInfo = new FIdentityInfo();
			identityInfo.setFusername(identityJSON.getString("realname"));
			identityInfo.setFidentityno(identityJSON.getString("idcard"));
			identityInfo.setFisok(isok);
			identityInfo.setFcreatetime(Utils.getTimestamp());
			if(!limitHelper.checkLimit(identity.getIp(), LimitTypeEnum.RealName.getCode(),2) || isok == 1){
				if (identityInfoMapper.insert(identityInfo) <= 0) {
					throw new Exception();
				}
			}

			if (isok != 1) {
				limitHelper.updateLimit(identity.getIp(), LimitTypeEnum.RealName.getCode());
				return Result.failure(1012,"身份证号与姓名不统一！");// 身份证号与姓名不统一
			}
			limitHelper.deleteLimit(identity.getIp(), LimitTypeEnum.RealName.getCode());
			updateUserIdentity(identity);
			return Result.success("实名成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(1014,"网络超时！");
		}
	}

	@Override
	public FUserIdentity selectByCode(String code) {
		return userIdentityMapper.selectByCode(code);
	}


	/**
	 * 修改用户的身份证信息
	 */
	private void updateUserIdentity(FUserIdentity identity) throws BCException {
		String brondate = IDCard.GetIDBirthday(identity.getFcode());
		Timestamp t = Timestamp.valueOf(brondate + " 00:00:00");
		FUser fuser = userMapper.selectByPrimaryKey(identity.getFuid());
		fuser.setFbirth(t);
		fuser.setFidentityno(identity.getFcode());
		fuser.setFrealname(identity.getFname());
		fuser.setFidentitytype(IdentityTypeEnum.IDCARD);
		fuser.setFhasrealvalidate(true);
		fuser.setFhasrealvalidatetime(Utils.getTimestamp());
		fuser.setFupdatetime(Utils.getTimestamp());
		int resultUp = userMapper.updateByPrimaryKey(fuser);
		if (resultUp <= 0) {
			throw new BCException("更新用户实名信息失败！");
		} else{
			identity.setFstatus(IdentityStatusEnum.PASS.getCode());
			if(identity.getFid() != null){
				if(userIdentityMapper.updateByPrimaryKey(identity) <= 0){
					throw new BCException("更新用户实名信息失败！");
				}
			} else{
				if(userIdentityMapper.insert(identity) <= 0){
					throw new BCException("更新用户实名信息失败！");
				}
			}
		}
		// MQ_USER_ACTION
		mqSend.SendUserAction(fuser.getFagentid(), fuser.getFid(), LogUserActionEnum.BIND_IDCARD, identity.getIp());
		//增加积分
		scoreHelper.SendUserScore(fuser.getFid(), BigDecimal.ZERO, ScoreTypeEnum.REALNAME.getCode(), ScoreTypeEnum.REALNAME.getValue().toString());
	}
}
