package com.qkwl.common.redis;


/**
 * Redis前缀
 * @author TT
 */
public class RedisConstant {

	/**
	 * 行情 key前缀规范
	 */
	public static String TICKERE_KEY = "TICKER_";
	
	/**
	 * 行情 key前缀规范
	 */
	public static String THREETICKERE_KEY = "THREETICKER_";
	
	/**
	 * kline key前缀规范
	 */
	public static String KLINE_KEY = "KLINE_";
	
	/**
	 * lastkline key前缀规范
	 */
	public static String LASTKLINE_KEY = "LASTKLINE_";

	/**
	 * 买深度 key前缀规范
	 */
	public static String BUYDEPTH_KEY = "BUYDEPTH_";

	/**
	 * 卖深度 key前缀规范
	 */
	public static String SELLDEPTH_KEY = "SELLDEPTH_";

	/**
	 * 最新成交 key前缀规范
	 */
	public static String SUCCESSENTRUST_KEY = "SUCCESSENTRUST_";
	
	/**
	 * token key前缀规范
	 */
	public static String TOKEN_KEY = "TOKEN_";
	
	/**
	 * 用户 key前缀规范
	 */
	public static String USER_KEY = "USER_";
	
	/**
	 * 验证 key前缀规范
	 */
	public static String VALIDATE_KEY = "VALIDATE_CODE_";
	
	
	/**
	 * 绑定 key前缀规范
	 */
	public static String Bind_KEY = "BIND_CODE_";
	
	/**
	 * 委单 key前缀规范
	 */
	public static String ENTRUST_KEY = "ENTRUST_";
	
	/**
	 * 行情 key前缀规范
	 */
	public static String MARKET_KEY = "MARKET_";
	
	/**
	 * 定时器 key前缀规范
	 */
	public static String JOB_KET = "JOB_";
	
	/**
	 * web文章总页面 key前缀规范
	 */
	public static String WEB_ARTICLECOUNT_KET = "ARTICLEPAGECOUNT";
	
	/**
	 * app文章 key前缀规范
	 */
	public static String APP_ARTICLECOUNT_KET = "ARTICLEAPPPAGECOUNT";
	
	/**
	 * web文章 key前缀规范
	 */
	public static String WEB_ARTICLE_KET = "WEBARTICLE_";
	
	/**
	 * app文章 key前缀规范
	 */
	public static String APP_ARTICLE_KET = "APPBARTICLE_";
	
	/**
	 * web文章详情 key前缀规范
	 */
	public static String WEB_ARTICLEDETAIL_KET = "WEBARTICLEDETAIL_";
	
	/**
	 * app文章详情 key前缀规范
	 */
	public static String APP_ARTICLEDETAIL_KET = "APPARTICLEDETAIL_";
	
	/**
	 * 文章类型列表 key前缀规范
	 */
	public static String ARTICLE_TYPELIST_KET = "ARTICLETYPES";
	
	/**
	 * 文章类型 key前缀规范
	 */
	public static String ARTICLE_TYPE_KET = "ARTICLETYPE_";
	
	/**
	 * 关于我们列表 key前缀规范
	 */
	public static String ABOUT_LIST_KET = "ABOUTS";
	
	/**
	 * 关于我们 key前缀规范
	 */
	public static String ABOUT_KET = "ABOUT_";
	
	/**
	 * 关于我们类型列表 key前缀规范
	 */
	public static String ABOUT_TYPELIST_KET = "ABOUTTYPES";
	
	/**
	 * 关于我们类型 key前缀规范
	 */
	public static String ABOUT_TYPE_KET = "ABOUTTYPE_";
	
	/**
	 * 网站前台所有的系统参数
	 */
	public static String ARGS_KET_WEB = "WEBARGSLIST";
	
	/**
	 * 系统参数 key前缀规范
	 */
	public static String ARGS_KET = "ARGS_";
	
	/**
	 * 语言类型 key前缀规范
	 */
	public static String LANGUAGE_KET = "LANGUAGE_";
	/**
	 * 语言类型 key前缀规范
	 */
	public static String LANGUAGE_LIST_KET = "LANGUAGELIST_";
	
	/**
	 * 币种类型 key前缀规范
	 */
	public static String COIN_LIST_KEY = "COINS";
	
	/**
	 * 币种类型 key前缀规范
	 */
	public static String COIN_KEY = "COIN_";

	/**
	 * 币种介绍类型 key前缀规范
	 */
	public static String COIN_INFO_KEY = "COININFO_";

	/**
	 * 交易类型 key前缀规范
	 */
	public static String TRADE_LIST_KEY = "TRADES";

	/**
	 * 交易类型 key前缀规范
	 */
	public static String TRADE_KEY = "TRADE_";
	
	/**
	 * 网站基础 key前缀规范
	 */
	public static String WEBINFO_KEY = "WEBINFO";
	
	
	/**
	 * 系统充值银行状态 key前缀规范
	 */
	public static String SYS_RECHARGEBANKSTART_KEY = "RECHARGEBANKSTART_";
	
	/**
	 * 系统充值银行类型 key前缀规范
	 */
	public static String SYS_RECHARGEBANK_KEY = "RECHARGEBANK_";
	
	/**
	 * 系统充值银行(id) key前缀规范
	 */
	public static String SYS_RECHARGEBANKID_KEY = "RECHARGEBANKID_";
	
	/**
	 * 系统提现银行 key前缀规范
	 */
	public static String SYS_WITHDRAWBANK_KEY = "WITHDRAWBANK_";
	
	/**
	 * 系统提现银行 key前缀规范
	 */
	public static String SYS_WITHDRAWBANKLIST_KEY = "WITHDRAWBANKS";
	
	/**
	 * 人民币提现手续费 key前缀规范
	 */
	public static String SYS_WITHDRAWCNYFEES_KEY = "WITHDRAWCNYFEES_";
	
	/**
	 * 虚拟币手续费 key前缀规范(币种+等级)
	 */
	public static String SYS_VIRTUALCOINFEES_KEY = "VIRTUALCOINFEES_";
	
	/**
	 * 是否输入交易密码 key前缀规范
	 */
	public static String TRADE_NEED_PASSWORD = "TRADENEEDPASSWORD";
	
	/**
	 * 用户登录
	 */
	public static String ACCOUNT_LOGIN_TOTAL_KEY = "BCACCOUNT_LOGIN_";
	
	
	/**
	 * 用户APP登录
	 */
	public static String APP_LOGIN_TOTAL_KEY = "BCAPP_LOGIN_";
	
	/**
	 * 用户签名
	 */
	public static String ACCOUNT_SIGN__KEY = "BCSIGN_";
	
	/**
	 * 找回密码的信息的标志位
	 */
	public static String SETREDISINFO_KEY = "SETREDISINFO_";
	
	/**
	 * 图片验证码的标志位
	 */
	public static String VERIFYCODE_KEY = "VERIFYCODE_";
	
	/**
	 * 友情链接标志位
	 */
	public static String FRIEND_LINK = "FRIENDLINK_";
	
	/**
	 * API访问限制
	 */
	public static String APIACCESSLIMIT = "APIACCESSLIMIT_";
	
	/**
	 * 众筹支付签名
	 */
	public static String ICO_PAY_SIGN__KEY = "ICOPAYSIGN_";
	
	/**
	 * 券商
	 */
	public static String AGENT_LIST_KEY = "AGENTLIST";
	
	/**
	 * 券商
	 */
	public static String AGENT_KEY = "AGENT_";
	
	/**
	 * 微天使
	 */
	public static String WEAGNEL_KEY = "WEANGEL_DATA";
	
	/**
	 * 价格闹钟短信
	 */
	public static String PRICECLOCK_SMS_KEY = "PRICECLOCK_SMS_KEY";
	
	/**
	 * 价格闹钟邮件
	 */
	public static String PRICECLOCK_EMAIL_KEY = "PRICECLOCK_EMAIL_KEY";
	
	/**
	 * CountLimit访问限制
	 */
	public static String COUNTLIMIT_KEY = "COUNTLIMIT_KEY_";
	
	/**
	 * 存币理财设置
	 */
	public static String FINANCES_KEY = "FINANCES_KEY";
	
	/**
	 * 广告商访问统计
	 */
	public static String ADS_SOURCE_KEY = "ADS_SOURCE_";
	
	/**
	 * 广告商缓存
	 */
	public static String ADS_KEY = "ADS_";
	
	/**
	 * 广告商缓存
	 */
	public static String ADS_LIST_KEY = "ADS_LIST";

	/**
	 * ICO缓存
	 */
	public static String ICODETAIL_LIST_KEY = "ICODETAIL_LIST_KEY";

	/**
	 * ICO缓存
	 */
	public static String ICODETAIL_KEY = "ICODETAIL_";
	/**
	 * 投资人问卷
	 */
	public static String FQAS_USER_KEY = "FQAS_USER_KEY_";

	/**
	 * 验证发送明细统计
	 */
	public static String STATISTICS_VALIDATE_KEY = "STATISTICS_VALIDATE_";

	/**
	 * 验证平台
	 */
	public static String VALIDATE_PLATFORM = "VALIDATE_PLATFORM_";
	/**
	 * 验证账号
	 */
	public static String VALIDATE_ACCOUNT = "VALIDATE_ACCOUNT_";
	/**
	 * 验证模板
	 */
	public static String VALIDATE_TEMPLATE = "VALIDATE_TEMPLATE_";

	/**
	 * 代理充值号
	 */
	public static String RECHARGEAGENCY_KEY = "RECHARGEAGENCY_KEY";

	/**
	 * 分享送币活动
	 */
	public static String ACTIVITYSHARECOIN_KEY = "ACTIVITY_SHARE_COIN_";

	/**
	 * 分享送币活动类型
	 */
	public static String ACTIVITYSHARECOIN_TYPE_KEY = "ACTIVITY_SHARE_COIN_TYPE";

	/**
	 * 质数金服
	 */
	public static String ZSJFACCOUNTINFO_KEY = "ZSJFACCOUNTINFO_KEY";

	/**
	 * 是否获取第三方数据
	 */
	public static String IS_FETCH_THIRD_PLATFORM = "IS_FETCH_THIRD_PLATFORM";

	/**
	 * 是否是授权的api
	 */
	public static String IS_AUTH_API_KEY = "IS_AUTH_API_KEY_";


}
