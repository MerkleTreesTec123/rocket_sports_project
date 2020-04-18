package com.qkwl.service.admin.bc.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.common.Enum.redis.RedisTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.service.admin.bc.dao.*;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.service.admin.validate.dao.ValidateAccountMapper;
import com.qkwl.service.admin.validate.dao.ValidatePlatformMapper;
import com.qkwl.service.admin.validate.dao.ValidateTemplateMapper;
import com.qkwl.service.admin.validate.model.ValidateAccountDO;
import com.qkwl.service.admin.validate.model.ValidatePlatformDO;
import com.qkwl.service.admin.validate.model.ValidateTemplateDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.dto.agent.FAgent;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.system.FSystemArgs;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.dto.web.FAboutType;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.rpc.redis.IRedisService;

/**
 * 初始化Redis参数
 * 
 */
@Component("redisInitService")
public class SystemRedisInit implements IRedisService {

	/**
	 * 日志
	 */
	private static Logger _logger = LoggerFactory.getLogger(SystemRedisInit.class);

	@Autowired
	private MemCache memCache;
	@Autowired
	private FArticleMapper articleMapper;
	@Autowired
	private FAboutMapper aboutMapper;
	@Autowired
	private FAboutTypeMapper aboutTypeMapper;
	@Autowired
	private FArticleTypeMapper articleTypeMapper;
	@Autowired
	private FSystemArgsMapper systemArgsMapper;
	@Autowired
	private FLanguageTypeMapper languageTypeMapper;
	@Autowired
	private FSystemBankinfoWithdrawMapper systemBankinfoWithdrawMapper;
	@Autowired
	private FSystemBankinfoRechargeMapper systemBankinfoRechargeMapper;
	@Autowired
	private FFriendLinkMapper fFriendLinkMapper;
	@Autowired
	private FAgentMapper agentMapper;
	@Autowired
	private FVirtualFinancesMapper virtualFinancesMapper;
	@Autowired
	private SystemCoinTypeMapper systemCoinTypeMapper;
	@Autowired
	private SystemTradeTypeMapper systemTradeTypeMapper;
	@Autowired
	private SystemCoinSettingMapper systemCoinSettingMapper;
	@Autowired
	private ValidateAccountMapper validateAccountMapper;
	@Autowired
	private ValidatePlatformMapper validatePlatformMapper;
	@Autowired
	private ValidateTemplateMapper validateTemplateMapper;
	@Autowired FApiAuthMapper apiAuthMapper;
	@Autowired
	private SystemCoinInfoMapper systemCoinInfoMapper;

	/**
	 * 初始化
	 */
	public void redisInit() {

		// web新闻
		System.out.println("SystemRedisInit --------------> WEB新闻开始初始化");
		initWebArticle(1);
		
		// APP新闻
		System.out.println("SystemRedisInit --------------> APP新闻开始初始化");
		initWebArticle(2);

		// 新闻类型
		System.out.println("SystemRedisInit --------------> 新闻类型开始初始化");
		initArticleType();

		// 关于我们
		System.out.println("SystemRedisInit --------------> 关于类型开始初始化");
		initAboutTypeList();

		// 友情链接
		System.out.println("SystemRedisInit --------------> 友情链接开始初始化");
		initFriendLinkList();

		// 系统参数
		System.out.println("SystemRedisInit --------------> 系统参数开始初始化");
		initSystemArgs();

		// 语言类型
		System.out.println("SystemRedisInit --------------> 语言类型开始初始化");
		initLanguageList();

		// 币种
		System.out.println("SystemRedisInit --------------> 币种类型开始初始化");
		initSystemCoinType();

		// 交易
		System.out.println("SystemRedisInit --------------> 交易类型开始初始化");
		initSystemTradeType();

		// 币种设置
		System.out.println("SystemRedisInit --------------> 币种设置开始初始化");
		initCoinSetting();

		// 币种信息
		System.out.println("SystemRedisInit --------------> 币种信息");
		initCoinInfo();

		// 系统提现银行
		System.out.println("SystemRedisInit --------------> 系统提现银行开始初始化");
		initBankinfoWithdraw();

		// 系统充值银行
		System.out.println("SystemRedisInit --------------> 系统充值银行开始初始化");
		initBankinfoRecharge(-1);

		// 提现手续费
		//System.out.println("SystemRedisInit --------------> 人民币提现手续费开始初始化");
		//initWithdrawFees();

		// 虚拟币手续费
		//System.out.println("SystemRedisInit --------------> 虚拟币手续费开始初始化");
		//initVirtualFees();
		
		// 券商列表
		System.out.println("SystemRedisInit --------------> 券商列表开始初始化");
		initAgent();		
		// 存币理财
		System.out.println("SystemRedisInit --------------> 存币理财开始初始化");
		initVirtualFinances();
		

		System.out.println("SystemRedisInit --------------> Validate服务开始初始化");
		initValidatePlatform();
		initValidateAccount();
		initValidateTemplate();

		System.out.println("SystemRedisInit --------------> 授权api初始化开始");
		initAuthApi();

		System.out.println("SystemRedisInit --------------> 初始化完成");
	}

	/**
	 * 重置redis数据
	 */
	public void resetRedis() {
		_logger.info("----> resetRedis start");
		redisInit();
		_logger.info("----> resetRedis success");
	}

	/**
	 * 重置redis数据
	 */
	public void clearRedis() {
		_logger.info("----> redisClear start");

		System.out.println("开始清空新闻redis");
		deleteNoExpire(RedisConstant.WEB_ARTICLECOUNT_KET);
		deleteNoExpire(RedisConstant.WEB_ARTICLE_KET);
		deleteNoExpire(RedisConstant.WEB_ARTICLEDETAIL_KET);

		System.out.println("开始清空新闻类型redis");
		deleteNoExpire(RedisConstant.ARTICLE_TYPE_KET);
		deleteNoExpire(RedisConstant.ARTICLE_TYPELIST_KET);

		System.out.println("开始清空关于我们redis");
		deleteNoExpire(RedisConstant.ABOUT_LIST_KET);
		deleteNoExpire(RedisConstant.ABOUT_KET);
		deleteNoExpire(RedisConstant.ABOUT_TYPE_KET);
		deleteNoExpire(RedisConstant.ABOUT_TYPELIST_KET);

		System.out.println("开始清空友链redis");
		deleteNoExpire(RedisConstant.FRIEND_LINK);

		System.out.println("开始清空币种redis");
		deleteNoExpire(RedisConstant.COIN_KEY);
		deleteNoExpire(RedisConstant.COIN_LIST_KEY);
		deleteNoExpire(RedisConstant.SYS_VIRTUALCOINFEES_KEY);
		deleteNoExpire(RedisConstant.COIN_INFO_KEY);

		System.out.println("开始清空交易redis");
		deleteNoExpire(RedisConstant.TRADE_KEY);
		deleteNoExpire(RedisConstant.TRADE_LIST_KEY);

		System.out.println("开始清空存币理财redis");
		deleteNoExpire(RedisConstant.FINANCES_KEY);

		System.out.println("开始清空系统参数redis");
		deleteNoExpire(RedisConstant.ARGS_KET);
		deleteNoExpire(RedisConstant.ARGS_KET_WEB);

		System.out.println("开始清空语言redis");
		deleteNoExpire(RedisConstant.LANGUAGE_KET);

		System.out.println("开始清空系统银行redis");
		deleteNoExpire(RedisConstant.SYS_WITHDRAWBANKLIST_KEY);
		deleteNoExpire(RedisConstant.SYS_WITHDRAWBANK_KEY);
		deleteNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY);
		deleteNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY);

		System.out.println("开始清空券商redis");
		deleteNoExpire(RedisConstant.AGENT_LIST_KEY);
		deleteNoExpire(RedisConstant.AGENT_KEY);

		System.out.println("开始清空广告redis");
		deleteNoExpire(RedisConstant.ADS_LIST_KEY);
		deleteNoExpire(RedisConstant.ADS_KEY);

		System.out.println("开始清空ICOredis");
		deleteNoExpire(RedisConstant.ICODETAIL_LIST_KEY);
		deleteNoExpire(RedisConstant.ICODETAIL_KEY);

		System.out.println("开始清空短信邮件配置redis");
		deleteNoExpire(RedisConstant.VALIDATE_PLATFORM);
		deleteNoExpire(RedisConstant.VALIDATE_ACCOUNT);
		deleteNoExpire(RedisConstant.VALIDATE_TEMPLATE);

		System.out.println("开始清空活动配置redis");
		deleteNoExpire(RedisConstant.ACTIVITYSHARECOIN_TYPE_KEY);

		System.out.println("开始清空质数金服账号配置redis");
		deleteNoExpire(RedisConstant.ZSJFACCOUNTINFO_KEY);

		System.out.println("开始清空授权api列表");
		deleteNoExpire(RedisConstant.IS_AUTH_API_KEY);

		System.out.println("redis清空已完成");
		_logger.info("----> redisClear success");
	}

	@Override
	public void resetRedis(int type){
		if(type == 0){
			redisInit();
		}
		if(type == RedisTypeEnum.Article.getCode()){
			System.out.println("开始重置WEB新闻redis");
			initWebArticle(1);
			System.out.println("开始重置APP新闻redis");
			initWebArticle(2);
			System.out.println("开始重置新闻类型redis");
			initArticleType();
		}
		if(type == RedisTypeEnum.AboutUs.getCode()){
			System.out.println("开始重置关于类型redis");
			initAboutTypeList();
		}
		if(type == RedisTypeEnum.FriendshipLink.getCode()){
			System.out.println("开始重置友情链接redis");
			initFriendLinkList();
		}
		if(type == RedisTypeEnum.Trade.getCode()){
			System.out.println("开始重置交易类型redis");
			initSystemTradeType();
		}
		if(type == RedisTypeEnum.Finance.getCode()){
			System.out.println("开始重置存币理财redis");
			initVirtualFinances();
		}
		if(type == RedisTypeEnum.SystemArgs.getCode()){
			System.out.println("开始重置系统参数redis");
			initSystemArgs();
		}
		if(type == RedisTypeEnum.Language.getCode()){
			System.out.println("开始重置语言redis");
			initLanguageList();
		}
		if(type == RedisTypeEnum.SystemBank.getCode()){
			System.out.println("开始重置系统银行redis");
			initBankinfoWithdraw();
			System.out.println("开始重置系统充值银行redis");
			initBankinfoRecharge(-1);
		}
		if(type == RedisTypeEnum.Agent.getCode()){
			System.out.println("开始重置券商redis");
			initAgent();
		}

		if(type == RedisTypeEnum.SmsEmail.getCode()){
			System.out.println("开始重置短信邮件配置redis");
			initValidatePlatform();
			initValidateAccount();
			initValidateTemplate();
		}

		if (type == RedisTypeEnum.AuthApi.getCode()) {
			System.out.println("开始重置授权api");
			initAuthApi();
		}

	}

	@Override
	public void clearRedis(int type){
		if(type == 0){
			clearRedis();
		}

		if(type == RedisTypeEnum.Article.getCode()){
			System.out.println("开始清空新闻redis");
			deleteNoExpire(RedisConstant.WEB_ARTICLECOUNT_KET);
			deleteNoExpire(RedisConstant.WEB_ARTICLE_KET);
			deleteNoExpire(RedisConstant.WEB_ARTICLEDETAIL_KET);

			System.out.println("开始清空新闻类型redis");
			deleteNoExpire(RedisConstant.ARTICLE_TYPE_KET);
			deleteNoExpire(RedisConstant.ARTICLE_TYPELIST_KET);
		}

		if(type == RedisTypeEnum.AboutUs.getCode()){
			System.out.println("开始清空关于我们redis");
			deleteNoExpire(RedisConstant.ABOUT_LIST_KET);
			deleteNoExpire(RedisConstant.ABOUT_KET);
			deleteNoExpire(RedisConstant.ABOUT_TYPE_KET);
			deleteNoExpire(RedisConstant.ABOUT_TYPELIST_KET);
		}
		if(type == RedisTypeEnum.FriendshipLink.getCode()){
			System.out.println("开始清空友链redis");
			deleteNoExpire(RedisConstant.FRIEND_LINK);
		}
		if(type == RedisTypeEnum.Coin.getCode()){
			System.out.println("开始清空币种redis");
			deleteNoExpire(RedisConstant.COIN_KEY);
			deleteNoExpire(RedisConstant.COIN_LIST_KEY);
			deleteNoExpire(RedisConstant.SYS_VIRTUALCOINFEES_KEY);
		}
		if(type == RedisTypeEnum.Trade.getCode()){
			System.out.println("开始清空交易redis");
			deleteNoExpire(RedisConstant.TRADE_KEY);
			deleteNoExpire(RedisConstant.TRADE_LIST_KEY);
		}
		if(type == RedisTypeEnum.Finance.getCode()){
			System.out.println("开始清空存币理财redis");
			deleteNoExpire(RedisConstant.FINANCES_KEY);
		}
		if(type == RedisTypeEnum.SystemArgs.getCode()){
			System.out.println("开始清空系统参数redis");
			deleteNoExpire(RedisConstant.ARGS_KET);
			deleteNoExpire(RedisConstant.ARGS_KET_WEB);
		}
		if(type == RedisTypeEnum.Language.getCode()){
			System.out.println("开始清空语言redis");
			deleteNoExpire(RedisConstant.LANGUAGE_KET);
		}
		if(type == RedisTypeEnum.SystemBank.getCode()){
			System.out.println("开始清空系统银行redis");
			deleteNoExpire(RedisConstant.SYS_WITHDRAWBANKLIST_KEY);
			deleteNoExpire(RedisConstant.SYS_WITHDRAWBANK_KEY);
			deleteNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY);
			deleteNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY);
		}
		if(type == RedisTypeEnum.Agent.getCode()){
			System.out.println("开始清空券商redis");
			deleteNoExpire(RedisConstant.AGENT_LIST_KEY);
			deleteNoExpire(RedisConstant.AGENT_KEY);
		}
		if(type == RedisTypeEnum.SmsEmail.getCode()){
			System.out.println("开始清空短信邮件配置redis");
			deleteNoExpire(RedisConstant.VALIDATE_PLATFORM);
			deleteNoExpire(RedisConstant.VALIDATE_ACCOUNT);
			deleteNoExpire(RedisConstant.VALIDATE_TEMPLATE);
		}
		if (type == RedisTypeEnum.AuthApi.getCode()){
			deleteNoExpire(RedisConstant.IS_AUTH_API_KEY);
		}


	}

	/**
	 * 初始化Web新闻
	 * @param type 类型
	 */
	public void initWebArticle(int type) {
		// 劵商
		List<FAgent> agents = agentMapper.selectAll();
		for (FAgent fAgent : agents) {
			List<FArticleType> articleTypes = articleTypeMapper.selectAll(null);
			for (FArticleType fArticleType : articleTypes) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("limit", Constant.webPageSize);
				map.put("farticletype", fArticleType.getFid());
				map.put("fagentid", fAgent.getFid());
				map.put("ftype", type);
				int count = articleMapper.getFArticleCount(map);
				RedisObject articlePageCountRedis = new RedisObject();
				articlePageCountRedis.setExtObject(count);
				setNoExpire(RedisConstant.WEB_ARTICLECOUNT_KET + type + "_" + fAgent.getFid() + "_" + fArticleType.getFid(), articlePageCountRedis);
				if (count > 0) {
					int rest = count % 10;
					int pageNum = 1;
					if (rest > 0) {
						pageNum = count / 10 + 1;
					} else {
						pageNum = count / 10;
					}
					for (int i = 0; i < pageNum; i++) {
						int offset = i * Constant.webPageSize;
						map.put("offset", offset);

						List<FArticle> articleList = articleMapper.getFArticlePaginationList(map);

						RedisObject articlesRedis = new RedisObject();
						articlesRedis.setExtObject(articleList);
						setNoExpire(RedisConstant.WEB_ARTICLE_KET + type  + "_" + fAgent.getFid() + "_" + fArticleType.getFid() + "_" + (i + 1), articlesRedis);
						for (FArticle fArticle : articleList) {
							RedisObject articleRedis = new RedisObject();
							articleRedis.setExtObject(fArticle);
							setNoExpire(RedisConstant.WEB_ARTICLEDETAIL_KET + type  + "_" + fAgent.getFid() + "_" + fArticle.getFid(), articleRedis);
						}
					}
				}
			}
		}
		// 主站数据
		List<FArticleType> articleTypes = articleTypeMapper.selectAll(null);
		for (FArticleType fArticleType : articleTypes) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("limit", Constant.webPageSize);
			map.put("farticletype", fArticleType.getFid());
			map.put("fagentid", 0);
			map.put("ftype", type);
			int count = articleMapper.getFArticleCount(map);
			RedisObject articlePageCountRedis = new RedisObject();
			articlePageCountRedis.setExtObject(count);
			setNoExpire(RedisConstant.WEB_ARTICLECOUNT_KET + type + "_" + 0 + "_" + fArticleType.getFid(), articlePageCountRedis);
			if (count > 0) {
				int rest = count % 10;
				int pageNum = 1;
				if (rest > 0) {
					pageNum = count / 10 + 1;
				} else {
					pageNum = count / 10;
				}
				for (int i = 0; i < pageNum; i++) {
					int offset = i * Constant.webPageSize;
					map.put("offset", offset);

					List<FArticle> articleList = articleMapper.getFArticlePaginationList(map);

					RedisObject articlesRedis = new RedisObject();
					articlesRedis.setExtObject(articleList);
					setNoExpire(RedisConstant.WEB_ARTICLE_KET + type  + "_" + 0 + "_" + fArticleType.getFid() + "_" + (i + 1), articlesRedis);
					for (FArticle fArticle : articleList) {
						RedisObject articleRedis = new RedisObject();
						articleRedis.setExtObject(fArticle);
						setNoExpire(RedisConstant.WEB_ARTICLEDETAIL_KET + type  + "_" + 0 + "_" + fArticle.getFid(), articleRedis);
					}
				}
			}
		}
	}

	/**
	 * 初始化新闻类型
	 */
	public void initArticleType() {
		List<FSystemLan> languageList = languageTypeMapper.selectAll();
		for (FSystemLan fSystemLan : languageList) {
			List<FArticleType> articleTypes = articleTypeMapper.selectAll(fSystemLan.getFid());
			RedisObject articleTypeListRedis = new RedisObject();
			articleTypeListRedis.setExtObject(articleTypes);
			setNoExpire(RedisConstant.ARTICLE_TYPELIST_KET + fSystemLan.getFid(), articleTypeListRedis);
			setNoExpire(RedisConstant.ARTICLE_TYPELIST_KET, articleTypeListRedis);
			for (FArticleType fArticleType : articleTypes) {
				RedisObject articleTypeRedis = new RedisObject();
				articleTypeRedis.setExtObject(fArticleType);
				setNoExpire(RedisConstant.ARTICLE_TYPE_KET + fSystemLan.getFid() + "_" + fArticleType.getFtypeid(), articleTypeRedis);
			}
		}
	}

	/**
	 * 初始化关于我们
	 */
	public void initAboutTypeList() {
		// 语言
		List<FSystemLan> languageList = languageTypeMapper.selectAll();
		for (FSystemLan fSystemLan : languageList) {
			// 劵商
			List<FAgent> agents = agentMapper.selectAll();
			for (FAgent fAgent : agents) {
				List<FAboutType> aboutTypeList = aboutTypeMapper.selectAll(fSystemLan.getFid(), fAgent.getFid());
				for (FAboutType fAboutType : aboutTypeList) {
					List<FAbout> aboutList = aboutMapper.selectByType(fAboutType.getFid());
					RedisObject aboutListRedis = new RedisObject();
					aboutListRedis.setExtObject(aboutList);
					setNoExpire(RedisConstant.ABOUT_LIST_KET + "_" + fAgent.getFid(), aboutListRedis);
					for (FAbout fAbout : aboutList) {
						RedisObject aboutRedis = new RedisObject();
						aboutRedis.setExtObject(fAbout);
						setNoExpire(RedisConstant.ABOUT_KET + fSystemLan.getFid() + "_" + fAgent.getFid() + "_" + fAbout.getFshowid(),
								aboutRedis);
						// 记录子节点的名称和id
						fAboutType.getChild().put(fAbout.getFshowid(), fAbout.getFtitle());
					}
				}
				RedisObject aboutTypeListRedis = new RedisObject();
				aboutTypeListRedis.setExtObject(aboutTypeList);
				setNoExpire(RedisConstant.ABOUT_TYPELIST_KET + "_" + fSystemLan.getFid() + "_" + fAgent.getFid(),
						aboutTypeListRedis);
			}
			// 主站数据
			List<FAboutType> aboutTypeList = aboutTypeMapper.selectAll(fSystemLan.getFid(), 0);
			for (FAboutType fAboutType : aboutTypeList) {
				List<FAbout> aboutList = aboutMapper.selectByType(fAboutType.getFid());
				RedisObject aboutListRedis = new RedisObject();
				aboutListRedis.setExtObject(aboutList);
				setNoExpire(RedisConstant.ABOUT_LIST_KET + "_0", aboutListRedis);

				for (FAbout fAbout : aboutList) {
					RedisObject aboutRedis = new RedisObject();
					aboutRedis.setExtObject(fAbout);
					setNoExpire(RedisConstant.ABOUT_KET + fSystemLan.getFid() + "_0_" + fAbout.getFshowid(), aboutRedis);
					// 记录子节点的名称和id
					fAboutType.getChild().put(fAbout.getFshowid(), fAbout.getFtitle());
				}
			}

			RedisObject aboutTypeListRedis = new RedisObject();
			aboutTypeListRedis.setExtObject(aboutTypeList);
			setNoExpire(RedisConstant.ABOUT_TYPELIST_KET + "_" + fSystemLan.getFid() + "_0", aboutTypeListRedis);
		}
	}

	/**
	 * 初始化友情链接
	 */
	public void initFriendLinkList() {
		List<FFriendLink> friendLinks = this.fFriendLinkMapper.selectAll();
		RedisObject friendLinkstRedis = new RedisObject();
		friendLinkstRedis.setExtObject(friendLinks);
		setNoExpire(RedisConstant.FRIEND_LINK, friendLinkstRedis);
	}

	/**
	 * 初始化初始化币种列表(多平台)
	 */
	public void initSystemCoinType() {
		deleteNoExpire(RedisConstant.COIN_KEY);
		List<SystemCoinType> coinTypeList = systemCoinTypeMapper.selectAll();
		RedisObject coins = new RedisObject();
		for (PlatformEnum platformEnum : PlatformEnum.values()) {
			List<SystemCoinType> platformCoinTypeList = new ArrayList<>();
			for (SystemCoinType coin : coinTypeList) {
				if (coin.getPlatformId().equals(0) || coin.getPlatformId().equals(platformEnum.getCode())) {
					coins.setExtObject(coin);
					setNoExpire(RedisConstant.COIN_KEY + coin.getId() + "_" + platformEnum.getCode(), coins);
					platformCoinTypeList.add(coin);
				}
			}
			coins.setExtObject(platformCoinTypeList);
			setNoExpire(RedisConstant.COIN_LIST_KEY + "_" + platformEnum.getCode(), coins);
		}
		// 全币种
		coins.setExtObject(coinTypeList);
		setNoExpire(RedisConstant.COIN_LIST_KEY, coins);
		// 币种
		for (SystemCoinType coin : coinTypeList) {
			coins.setExtObject(coin);
			setNoExpire(RedisConstant.COIN_KEY + coin.getId(), coins);
		}
	}

	/**
	 * 初始化交易信息列表
	 */
	public void initSystemTradeType() {
		List<FAgent> agents = agentMapper.selectAll();
		List<SystemTradeType> tradeTypeList = systemTradeTypeMapper.selectAll();
		// 扩展数据加载
		for (SystemTradeType tradeType : tradeTypeList) {
			SystemCoinType buyCoin = systemCoinTypeMapper.selectByPrimaryKey(tradeType.getBuyCoinId());
			SystemCoinType sellCoin = systemCoinTypeMapper.selectByPrimaryKey(tradeType.getSellCoinId());
			tradeType.setBuySymbol(buyCoin.getSymbol());
			tradeType.setBuyName(buyCoin.getName());
			tradeType.setBuyShortName(buyCoin.getShortName());
			tradeType.setBuyWebLogo(buyCoin.getWebLogo());
			tradeType.setSellSymbol(sellCoin.getSymbol());
			tradeType.setSellName(sellCoin.getName());
			tradeType.setSellShortName(sellCoin.getShortName());
			tradeType.setSellWebLogo(sellCoin.getWebLogo());
			tradeType.setSellAppLogo(sellCoin.getAppLogo());
		}
		// 劵商
		for (FAgent fAgent : agents) {
			List<SystemTradeType> agentsTradeTypeList = new ArrayList<>();
			for (SystemTradeType tradeType : tradeTypeList) {
				if (tradeType.getAgentId().equals(0) || tradeType.getAgentId().equals(fAgent.getFid())) {
					agentsTradeTypeList.add(tradeType);
				}
			}
			RedisObject coins = new RedisObject();
			coins.setExtObject(agentsTradeTypeList);
			setNoExpire(RedisConstant.TRADE_LIST_KEY + "_" + fAgent.getFid(), coins);
		}
		// 主站数据
		List<SystemTradeType> agentsTradeTypeList = new ArrayList<>();
		for (SystemTradeType tradeType : tradeTypeList) {
			if (tradeType.getAgentId().equals(0)) {
				agentsTradeTypeList.add(tradeType);
			}
		}
		RedisObject coins = new RedisObject();
		coins.setExtObject(agentsTradeTypeList);
		setNoExpire(RedisConstant.TRADE_LIST_KEY + "_" + 0, coins);
		// 全币种
		RedisObject allcoins = new RedisObject();
		allcoins.setExtObject(tradeTypeList);
		setNoExpire(RedisConstant.TRADE_LIST_KEY, allcoins);
		// 币种
		for (SystemTradeType tradeType : tradeTypeList) {
			RedisObject coinObj = new RedisObject();
			coinObj.setExtObject(tradeType);
			setNoExpire(RedisConstant.TRADE_KEY + tradeType.getId() + "_" + tradeType.getAgentId(), coinObj);
		}
	}

	/**
	 * 初始币种设置
	 */
	public void initCoinSetting() {
		List<SystemCoinSetting> coinSettingList = systemCoinSettingMapper.selectAll();
		for (SystemCoinSetting coinSetting : coinSettingList) {
			RedisObject virtualFees = new RedisObject();
			virtualFees.setExtObject(coinSetting);
			setNoExpire(RedisConstant.SYS_VIRTUALCOINFEES_KEY + coinSetting.getCoinId() + "_"
					+ coinSetting.getLevelVip(), virtualFees);
		}
	}

	/**
	 * 初始化币种信息
	 */
	public void initCoinInfo(){
		List<SystemCoinInfo> systemCoinInfoList = systemCoinInfoMapper.selectAll();
		for (SystemCoinInfo systemCoinInfo : systemCoinInfoList) {
			RedisObject redisObject = new RedisObject();
			redisObject.setExtObject(systemCoinInfo);
			setNoExpire(RedisConstant.COIN_INFO_KEY + systemCoinInfo.getCoinId() + "_" + systemCoinInfo.getLanName(),redisObject);
		}
	}


	/**
	 * 初始化虚拟币存币理财
	 */
	public void initVirtualFinances() {
		deleteNoExpire(RedisConstant.FINANCES_KEY);
		List<FVirtualFinances> finances = virtualFinancesMapper.selectAll();
		for (FVirtualFinances fVirtualFinances : finances) {
			if(fVirtualFinances.getFstate()==1){
				RedisObject virtualFinances = new RedisObject();
				virtualFinances.setExtObject(fVirtualFinances);
				setNoExpire(RedisConstant.FINANCES_KEY + "_" + fVirtualFinances.getFcoinid() + "_" + fVirtualFinances.getFid(), virtualFinances);
			}
		}
		List<SystemCoinType> coinTypeList = systemCoinTypeMapper.selectAll();
		for (SystemCoinType fVirtualCoinType : coinTypeList) {
			List<FVirtualFinances> financesList = virtualFinancesMapper.selectByCoinId(fVirtualCoinType.getId(), 1);
			if(financesList.size()>0){
				RedisObject virtualFinances = new RedisObject();
				virtualFinances.setExtObject(financesList);
				setNoExpire(RedisConstant.FINANCES_KEY + "_" + fVirtualCoinType.getId(), virtualFinances);
			}
		}
	}
	/**
	 * 初始化系统参数
	 */
	public void initSystemArgs() {
		// 网站前台需要的系统参数
		Map<String, Object> webSysArgs = new HashMap<String, Object>();
		List<FSystemArgs> argsList = systemArgsMapper.selectAll();
		// 系统单一参数存储
		for (FSystemArgs fSystemArgs : argsList) {
			RedisObject args = new RedisObject();
			args.setExtObject(fSystemArgs.getFvalue());
			setNoExpire(RedisConstant.ARGS_KET + fSystemArgs.getFkey(), args);
			// 记录网站前台需要的参数
			if (fSystemArgs.getFtype().equals(SystemArgsTypeEnum.FRONT.getCode())) {
				webSysArgs.put(fSystemArgs.getFkey(), fSystemArgs.getFvalue());
			}
		}

		// 网站前台的系统参数
		RedisObject webSystemArgs = new RedisObject();
		webSystemArgs.setExtObject(webSysArgs);
		setNoExpire(RedisConstant.ARGS_KET_WEB, webSystemArgs);
	}

	/**
	 * 初始化语言类型
	 */
	public void initLanguageList() {
		List<FSystemLan> languageList = languageTypeMapper.selectAll();
		for (FSystemLan fLanguageType : languageList) {
			RedisObject language = new RedisObject();
			language.setExtObject(fLanguageType);
			setNoExpire(RedisConstant.LANGUAGE_KET + fLanguageType.getFshortname(), language);
		}
		RedisObject language = new RedisObject();
		language.setExtObject(languageList);
		setNoExpire(RedisConstant.LANGUAGE_LIST_KET , language);
	}

	/**
	 * 初始化初始化提现银行
	 */
	public void initBankinfoWithdraw() {
		List<FSystemBankinfoWithdraw> withdrawBankList = systemBankinfoWithdrawMapper.selectAll();
		RedisObject withdrawList = new RedisObject();
		withdrawList.setExtObject(withdrawBankList);
		setNoExpire(RedisConstant.SYS_WITHDRAWBANKLIST_KEY, withdrawList);
		for (FSystemBankinfoWithdraw fSystemBankinfoWithdraw : withdrawBankList) {
			RedisObject withdraw = new RedisObject();
			withdraw.setExtObject(fSystemBankinfoWithdraw);
			setNoExpire(RedisConstant.SYS_WITHDRAWBANK_KEY + fSystemBankinfoWithdraw.getFid(), withdraw);
		}
	}

	/**
	 * 初始化充值银行
	 * @param type -1-初始化所有，0-初始网银，1-初始化支付宝，2-初始化网银直充
	 */
	public void initBankinfoRecharge(int type) {
		deleteNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY);
		deleteNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY);
		//if (type == -1 || type == BankTypeEnum.Transfer.getCode()) {
			List<FSystemBankinfoRecharge> transferbanklist = systemBankinfoRechargeMapper.selectByType(BankTypeEnum.Transfer.getCode());
			RedisObject transfer = new RedisObject();
			transfer.setExtObject(transferbanklist);
			setNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY + BankTypeEnum.Transfer.getCode(), transfer);
			for (FSystemBankinfoRecharge systemBankinfoRecharge : transferbanklist) {
				RedisObject rechageBankId = new RedisObject();
				rechageBankId.setExtObject(systemBankinfoRecharge);
				setNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY + systemBankinfoRecharge.getFid(), rechageBankId);
			}
		//}

		//if (type == -1 || type == BankTypeEnum.Alipay.getCode()) {
			List<FSystemBankinfoRecharge> alipayBankList = systemBankinfoRechargeMapper.selectByType(BankTypeEnum.Alipay.getCode());
			RedisObject alipay = new RedisObject();
			alipay.setExtObject(alipayBankList);
			setNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY + BankTypeEnum.Alipay.getCode(), alipay);
			for (FSystemBankinfoRecharge systemBankinfoRecharge : alipayBankList) {
				RedisObject rechageBankId = new RedisObject();
				rechageBankId.setExtObject(systemBankinfoRecharge);
				setNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY + systemBankinfoRecharge.getFid(), rechageBankId);
			}
		//}

		//if (type == -1 || type == BankTypeEnum.Recharge.getCode()) {
			List<FSystemBankinfoRecharge> rechargeBankList = systemBankinfoRechargeMapper.selectByType(BankTypeEnum.Recharge.getCode());
			RedisObject recharge = new RedisObject();
			recharge.setExtObject(rechargeBankList);
			setNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY + BankTypeEnum.Recharge.getCode(), recharge);
			for (FSystemBankinfoRecharge systemBankinfoRecharge : rechargeBankList) {
				RedisObject rechageBankId = new RedisObject();
				rechageBankId.setExtObject(systemBankinfoRecharge);
				setNoExpire(RedisConstant.SYS_RECHARGEBANKID_KEY + systemBankinfoRecharge.getFid(), rechageBankId);
			}
		//}

		//if (type == -1 || type == BankTypeEnum.ZsjfOnLine.getCode()) {
			List<FSystemBankinfoRecharge> rechargeZsjfBankList = systemBankinfoRechargeMapper.selectByType(BankTypeEnum.ZsjfOnLine.getCode());
			RedisObject zsjfRecharge = new RedisObject();
			zsjfRecharge.setExtObject(rechargeZsjfBankList);
			setNoExpire(RedisConstant.SYS_RECHARGEBANK_KEY + BankTypeEnum.ZsjfOnLine.getCode(), zsjfRecharge);
		//}

		// 初始化所有启用的银行
		List<FSystemBankinfoRecharge> rechargeBankListByEnable = systemBankinfoRechargeMapper.selectAllByStatus(BankStatusEnum.Enable.getCode());
		RedisObject bankEnable = new RedisObject();
		bankEnable.setExtObject(rechargeBankListByEnable);
		setNoExpire(RedisConstant.SYS_RECHARGEBANKSTART_KEY + BankStatusEnum.Enable.getCode(), bankEnable);
	}
	
	
	/**
	 * 初始化券商列表
	 */
	public void initAgent() {
		List<FAgent> agentlist = agentMapper.selectAll();
		RedisObject agentObj = new RedisObject();
		agentObj.setExtObject(agentlist);
		setNoExpire(RedisConstant.AGENT_LIST_KEY, agentObj);
		for (FAgent fAgent : agentlist) {
			RedisObject agent = new RedisObject();
			agent.setExtObject(fAgent);
			setNoExpire(RedisConstant.AGENT_KEY + fAgent.getFid(), agent);
		}
	}
	
	public void initValidatePlatform(){
		List<ValidatePlatformDO> plateformList = validatePlatformMapper.selectAll();
		for (ValidatePlatformDO platform: plateformList) {
			RedisObject ro = new RedisObject();
			ro.setExtObject(platform);
			setNoExpire(RedisConstant.VALIDATE_PLATFORM + platform.getId(), ro);
		}
	}

	public void initValidateAccount(){
		List<ValidateAccountDO> accountList = validateAccountMapper.selectAll();
		for (ValidateAccountDO account: accountList) {
			RedisObject ro = new RedisObject();
			ro.setExtObject(account);
			setNoExpire(RedisConstant.VALIDATE_ACCOUNT + account.getId(), ro);
		}
	}

	public void initValidateTemplate(){
		List<ValidateTemplateDO> templateList = validateTemplateMapper.selectAll();
		for (ValidateTemplateDO template: templateList) {
			//businessType修改为
			String[] BusionessTypes = template.getBusinessType().split("#");
			for(int i = 0; i < BusionessTypes.length; i++){
				RedisObject ro = new RedisObject();
				ro.setExtObject(template);
				setNoExpire(RedisConstant.VALIDATE_TEMPLATE + template.getPlatform()
						+ "_" + template.getSendType()
						+ "_" + BusionessTypes[i]
						+ "_" + template.getLanguage() , ro);
			}
		}
	}

	public void initAuthApi(){
		List<FApiAuth> fApiAuths = apiAuthMapper.selectAll();
		for (FApiAuth apiAuth:fApiAuths){
			RedisObject redisObject = new RedisObject();
			redisObject.setExtObject(apiAuth);
			setNoExpire(RedisConstant.IS_AUTH_API_KEY+apiAuth.getFuid(),redisObject);
			setNoExpire(RedisConstant.IS_AUTH_API_KEY+apiAuth.getFapikey(),redisObject);
		}

	}

	/**
	 * Redis设置
	 * @param key key
	 * @param obj obj
	 */
	public void setNoExpire(String key, RedisObject obj) {
		memCache.setNoExpire(key, JSON.toJSONString(obj));

	}
	
	/**
	 * Redis删除（重置之前，批量删除）
	 * @param key key
	 */
	public void deleteNoExpire(String key) {
		memCache.removeByPattern(key);
	}
	
}
