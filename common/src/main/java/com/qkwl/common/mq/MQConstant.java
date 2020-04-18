package com.qkwl.common.mq;

/**
 * 消息队列
 * @author TT
 */
public class MQConstant {
		
	/**
	 * 过期时间
	 */
	public static final int MQ_EXPRIE_TIME = 2 * 60 * 60;

	/**
	 * TAG-委单状态
	 */
	public static final String TAG_ENTRUST_STATE = "TAG_ENTRUST_STATE";

	/**
	 * TAG-用户行为
	 */
	public static final String TAG_USER_ACTION = "TAG_USER_ACTION";

	/**
	 * TAG-用户积分
	 */
	public static final String TAG_USER_SCORE = "TAG_USER_SCORE";

	/**
	 * TAG-管理员行为
	 */
	public static final String TAG_ADMIN_ACTION = "TAG_ADMIN_ACTION";
	
	/**
	 * TAG-短信验证相关
	 */
	public static final String TAG_SMS_VALIDATE = "TAG_SMS_VALIDATE";
	
	/**
	 * TAG-邮件验证相关
	 */
	public static final String TAG_EMAIL_VALIDATE = "TAG_EMAIL_VALIDATE";
}
