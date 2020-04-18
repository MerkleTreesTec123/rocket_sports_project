package com.qkwl.common.framework.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.ZsjfAccountInfoStatusEnum;
import com.qkwl.common.dto.agent.FRechargeAgency;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.coin.SystemCoinInfo;
import com.qkwl.common.dto.web.*;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.finances.FVirtualFinances;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.user.FUser;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;

import java.math.BigDecimal;
import java.util.*;

public class RedisHelper {

	/**
	 * 默认过期时间为1个小时
	 */
	private static final int expire_time = 60*60;
	
	protected MemCache memCache;
	protected Integer platformId = 1;

	public RedisHelper(){
		ParserConfig.getGlobalInstance().setAsmEnable(false);
	}

	/**
     * 设置缓存连接
     */
    public void setMemCache(MemCache memCache) {
        this.memCache = memCache;
    }
	/**
	 * 设置平台ID（默认HOTCOIN）
	 */
	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	/***************** User操作 *******************/
	/**
	 * 获取登录用户信息
	 * 
	 * @param token
	 *            登录token
	 * @return
	 */
	public FUser getCurrentUserInfoByToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		String json = memCache.get(token);
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(json);
		Object resultStr = obj.get("extObject");
		if (resultStr == null) {
			return null;
		}
		return JSON.parseObject(resultStr.toString(), FUser.class);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param token
	 *            登录token
	 * @param userInfo
	 *            修改后的用户信息
	 */
	public void updateUserInfo(String token, FUser userInfo) {
		if (token == null || StringUtils.isEmpty(token)) {
			return;
		}
		String bankinfoStr = memCache.get(token);
		if (StringUtils.isEmpty(bankinfoStr)) {
			return;
		}
		// 实体映射
		RedisObject obj = new RedisObject();
		obj.setExtObject(userInfo);
		memCache.set(token, JSON.toJSONString(obj), Constant.EXPIRETIME);
	}

	/**
	 * 删除登陆中的用户
	 * 
	 * @param token
	 *            登录token
	 */
	public void deleteUserInfo(String token) {
		if (StringUtils.isEmpty(token)) {
			return;
		}
		memCache.delete(token);
	}

	/***************** Article操作 *******************/
	/**
	 * 获取所有的新闻类型
	 * @return List<FArticleType>
	 */
	public List<FArticleType> getArticleTypeList() {
		String articleTypes = memCache.get(RedisConstant.ARTICLE_TYPELIST_KET);
		if (StringUtils.isEmpty(articleTypes)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(articleTypes);
		List<FArticleType> articleTypeList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(),
				FArticleType.class);
		return articleTypeList;
	}
	/**
	 * 获取所有的新闻类型
	 * 
	 * @return List<FArticleType>
	 */
	public List<FArticleType> getArticleTypeList(int lanId) {
		String articleTypes = memCache.get(RedisConstant.ARTICLE_TYPELIST_KET+ lanId);
		if (StringUtils.isEmpty(articleTypes)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(articleTypes);
		List<FArticleType> articleTypeList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), FArticleType.class);
		return articleTypeList;
	}

	/**
	 * 获取指定ID的新闻类型
	 * 
	 * @param typeId
	 *            新闻类型ID
	 * @return FArticleType
	 */
	public FArticleType getArticleType(int typeId, int lanId) {
		String articleTypeStr = memCache.get(RedisConstant.ARTICLE_TYPE_KET + lanId + "_" + typeId);
		if (StringUtils.isEmpty(articleTypeStr)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(articleTypeStr);
		FArticleType articleType = JSON.parseObject(obj.get("extObject").toString(), FArticleType.class);
		return articleType;
	}

	/**
	 * 获取新闻列表
	 * 
	 * @param type
	 *            新闻端（1、手机新闻，2、web新闻）
	 * @param Articlestype
	 *            新闻类型ID
	 * @param currentPage
	 *            当前页数
	 * @param agentId
	 *            劵商ID
	 * @return List<FArticle>
	 */
	public List<FArticle> getArticles(int type, int Articlestype, int currentPage, int agentId) {
		String articles = memCache.get(RedisConstant.WEB_ARTICLE_KET + type + "_" + agentId + "_" + Articlestype + "_" + currentPage);
		if (StringUtils.isEmpty(articles)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(articles);
		List<FArticle> articleList = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), FArticle.class);
		return articleList;
	}

	/**
	 * 获取指定ID新闻
	 * 
	 * @param type
	 *            新闻端（1、手机新闻，2、web新闻）
	 * @param id
	 *            新闻ID
	 * @param agentId
	 *            劵商ID
	 * @return FArticle
	 */
	public FArticle getArticleById(int type, int id, int agentId) {
		String articles = memCache.get(RedisConstant.WEB_ARTICLEDETAIL_KET + type + "_" + agentId + "_" + id);
		if (StringUtils.isEmpty(articles)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(articles);
		FArticle fArticle = JSON.parseObject(obj.get("extObject").toString(), FArticle.class);
		return fArticle;
	}

	/**
	 * 获取新闻总条数
	 * 
	 * @param type
	 *            新闻端（1、手机新闻，2、web新闻）
	 * @param articlestype
	 *            新闻类型ID
	 * @param agentId
	 *            劵商ID
	 * @return int
	 */
	public int getArticlesPageCont(int type, int articlestype, int agentId) {
		String articlePageCont = memCache.get(RedisConstant.WEB_ARTICLECOUNT_KET + type + "_" + agentId + "_" + articlestype);
		if (StringUtils.isEmpty(articlePageCont)) {
			return 0;
		}
		JSONObject obj = JSON.parseObject(articlePageCont);
		return Integer.valueOf(obj.get("extObject").toString());
	}

	/***************** About操作 *******************/
	/**
	 * 获取关于我们类型列表
	 * @param agentId 劵商ID
	 * @return List<FAboutType>
	 */
	public List<FAboutType> getAboutTypeList(int lanId,int agentId) {
		String aboutTypes = memCache.get(RedisConstant.ABOUT_TYPELIST_KET + "_" + lanId + "_" + agentId);
		if (StringUtils.isEmpty(aboutTypes)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(aboutTypes);
		List<FAboutType> aboutType = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(),
				FAboutType.class);
		return aboutType;
	}

	/**
	 * 根据ID获取关于我们
	 * @param id 关于我们ID
	 * @param agentId 劵商ID
	 * @return FAbout
	 */
	public FAbout getAbout(int id, int lanId, int agentId) {
		String abouts = memCache.get(RedisConstant.ABOUT_KET + lanId + "_" + agentId + "_" + id);
		if (StringUtils.isEmpty(abouts)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(abouts);
		FAbout about = JSON.parseObject(obj.get("extObject").toString(), FAbout.class);
		return about;
	}

	/***************** SystemArgs操作 *******************/
	/**
	 * 获取所有的系统参数
	 * 
	 * @return
	 */
	public Map<String, Object> getSystemArgsList() {
		String args = memCache.get(RedisConstant.ARGS_KET_WEB);
		if (StringUtils.isEmpty(args)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(args);
		String value = obj.get("extObject").toString();
		JSONObject jsonMap = JSON.parseObject(value);
		Map<String, Object> sysArgsMap = new HashMap<String, Object>();
		for (String key : jsonMap.keySet()) {
			sysArgsMap.put(key, jsonMap.get(key));
		}
		return sysArgsMap;
	}

	/**
	 * 更具key获取系统参数
	 * 
	 * @param key
	 * @return
	 */
	public String getSystemArgs(String key) {
		String args = memCache.get(RedisConstant.ARGS_KET + key);
		if (StringUtils.isEmpty(args)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(args);
		String value = obj.get("extObject").toString();
		return value;
	}

	/***************** CoinType操作 *******************/
	/**
	 * 获取系统所有币种列表(禁用，未禁用)
	 * @return List
	 */
	public List<SystemCoinType> getCoinTypeListSystem(){
		String coins = memCache.get(RedisConstant.COIN_LIST_KEY);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		return JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemCoinType.class);
	}
	/**
	 * 获取系统币种信息
	 * @param coinId 主键ID
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeSystem(Integer coinId){
		String coins = memCache.get(RedisConstant.COIN_KEY + coinId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		return obj.getObject("extObject", SystemCoinType.class);
	}
	/**
	 * 获取系统币种信息
	 * @param shortName 简称
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeShortNameSystem(String shortName) {
		List<SystemCoinType> coinList = getCoinTypeListSystem();
		if(coinList == null){
			return null;
		}
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
					&& systemCoinType.getShortName().equals(shortName)) {
				return systemCoinType;
			}
		}
		return null;
	}
	/**
	 * 获取系统所有币种名称MAP
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> getCoinTypeNameMap(){
		Map<Integer,String> coinMap = new TreeMap<Integer,String>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				// 降序排序
				return o1.compareTo(o2);
			}
		});
		List<SystemCoinType> coinTypes = getCoinTypeListSystem();
		for (SystemCoinType coinType : coinTypes){
			coinMap.put(coinType.getId(),coinType.getName());
		}
		return coinMap;
	}

	/**
	 * 获取系统所有币种名称MAP
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> getCoinTypeCNYNameMap(){
		Map<Integer,String> coinMap = new TreeMap<Integer,String>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				// 降序排序
				return o1.compareTo(o2);
			}
		});
		List<SystemCoinType> coinTypes = getCoinTypeListSystem();
		for (SystemCoinType coinType : coinTypes){
			if (coinType.getType() == SystemCoinTypeEnum.CNY.getCode()) {
				coinMap.put(coinType.getId(), coinType.getName());
			}
		}
		return coinMap;
	}
	/**
	 * 获取系统所有币种简称MAP
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> getCoinTypeShortNameMap(){
		Map<Integer,String> coinMap = new LinkedHashMap<>();
		List<SystemCoinType> coinTypes = getCoinTypeListSystem();
		for (SystemCoinType coinType : coinTypes){
			coinMap.put(coinType.getId(),coinType.getShortName());
		}
		return coinMap;
	}
	/**
	 * 获取平台所有币种列表(禁用，未禁用)
	 * @return List
	 */
	public List<SystemCoinType> getCoinTypeListAll(){
		String coins = memCache.get(RedisConstant.COIN_LIST_KEY + "_" + platformId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		return JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemCoinType.class);
	}
	/**
	 * 获取未禁用的币种列表
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeList(){
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取未禁用的第一个币种
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeFirst(){
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				return systemCoinType;
			}
		}
		return null;
	}

	/**
	 * 获取币种信息
	 * @param coinId 主键ID
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinType(Integer coinId){
		String coins = memCache.get(RedisConstant.COIN_KEY + coinId + "_" + platformId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		return obj.getObject("extObject", SystemCoinType.class);
	}

	/**
	 * 验证是否本平台币种
	 * @return true 是,false 否
	 */
	public boolean hasCoinId(int coinId){
		SystemCoinType coinType = getCoinType(coinId);
		return coinType != null;
	}


	/**
	 * 获取币种信息
	 * @param shortName 简称
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeShortName(String shortName) {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
					&& systemCoinType.getShortName().equals(shortName)) {
				return systemCoinType;
			}
		}
		return null;
	}

	/**
	 * 获取币种信息(可提现)
	 * @param type 币种类型
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeIsWithdrawFirst(Integer type) {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) && systemCoinType.getIsWithdraw()
					&& systemCoinType.getType().equals(type)) {
				return systemCoinType;
			}
		}
		return null;
	}

	/**
	 * 获取币种信息(可提现)
	 * @param type 币种类型
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeIsWithdrawList(Integer type) {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) && systemCoinType.getIsWithdraw()
					&& systemCoinType.getType().equals(type)) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取币种信息(可充值)
	 * @param type 币种类型
	 * @return SystemCoinType
	 */
	public SystemCoinType getCoinTypeIsRechargeFirst(Integer type) {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) && systemCoinType.getIsRecharge()
					&& systemCoinType.getType().equals(type)) {
				return systemCoinType;
			}
		}
		return null;
	}

	/**
	 * 获取币种信息(可充值)
	 * @param type 币种类型
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeIsRechargeList(Integer type) {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) && systemCoinType.getIsRecharge()
					&& systemCoinType.getType().equals(type)) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取币种信息(可PUSH)
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypePushList() {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
					&& systemCoinType.getIsPush()) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取币种信息(存币理财)
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeFinancesList() {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) && systemCoinType.getIsFinances()) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取币种信息(虚拟币)
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeCoinList() {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
					&& systemCoinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}
	/**
	 * 获取币种信息(法币)
	 * @return List<SystemCoinType>
	 */
	public List<SystemCoinType> getCoinTypeCnyList() {
		List<SystemCoinType> coinList = getCoinTypeListAll();
		if(coinList == null){
			return null;
		}
		List<SystemCoinType> newCoinList = new ArrayList<>();
		for (SystemCoinType systemCoinType : coinList) {
			if (!systemCoinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
					&& systemCoinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
				newCoinList.add(systemCoinType);
			}
		}
		return newCoinList;
	}
	/**
	 * 查询虚拟币手续费
	 * @param coinId 币种ID
	 * @param level 用户等级
	 * @return FVirtualFees
	 */
	public SystemCoinSetting getCoinSetting(int coinId, int level) {
		String fees = memCache.get(RedisConstant.SYS_VIRTUALCOINFEES_KEY + coinId + "_" + level);
		if (StringUtils.isEmpty(fees)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(fees);
		return JSON.parseObject(obj.get("extObject").toString(), SystemCoinSetting.class);
	}

	/**
	 * 获取币种介绍
	 * @param coinId
	 * @param lanName
	 * @return
	 */
	public SystemCoinInfo getCoinInfo(int coinId,String lanName) {
		String coinInfo = memCache.get(RedisConstant.COIN_INFO_KEY + coinId + "_" + lanName);
		if (StringUtils.isEmpty(coinInfo)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coinInfo);
		return JSON.parseObject(obj.get("extObject").toString(), SystemCoinInfo.class);
	}

	/*****************TradeType操作 *******************/
	/**
	 * 获取所有的交易列表
	 * @param agentId 劵商ID
	 * @return
	 */
	public List<SystemTradeType> getAllTradeTypeList(Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		List<SystemTradeType> coinList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
		return coinList;
	}
	/**
	 * 获取未禁用的交易列表(所有)
	 * @param agentId 劵商ID
	 * @return
	 */
	public List<SystemTradeType> getTradeTypeList(Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		List<SystemTradeType> coinList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
		List<SystemTradeType> newCoinList = new ArrayList<>();
		for (SystemTradeType systemTradeType : coinList) {
			if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
				newCoinList.add(systemTradeType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取未禁用的交易列表(可交易)
	 * @param agentId 劵商ID
	 * @return
	 */
	public List<SystemTradeType> getTradeTypeShare(Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		List<SystemTradeType> coinList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
		List<SystemTradeType> newCoinList = new ArrayList<>();
		for (SystemTradeType systemTradeType : coinList) {
			if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) && systemTradeType.getIsShare()) {
				newCoinList.add(systemTradeType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取未禁用的交易列表(根据交易类型)
	 * @param agentId 劵商ID
	 * @return
	 */
	public List<SystemTradeType> getTradeTypeSort(Integer type,Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		List<SystemTradeType> coinList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
		List<SystemTradeType> newCoinList = new ArrayList<>();
		for (SystemTradeType systemTradeType : coinList) {
			if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) && systemTradeType.getType().equals(type)) {
				newCoinList.add(systemTradeType);
			}
		}
		return newCoinList;
	}

	/**
	 * 获取未禁用的交易(第一条)
	 * @param agentId 劵商ID
	 * @return
	 */
	public SystemTradeType getTradeTypeFirst(Integer type, Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		List<SystemTradeType> coinList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
		for (SystemTradeType systemTradeType : coinList) {
			if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())
					&& systemTradeType.getIsShare() && systemTradeType.getType().equals(type)) {
				return systemTradeType;
			}
		}
		return null;
	}

	/**
	 * 获取交易币种信息
	 * @param tradeId 主键ID
	 * @param agentId 劵商ID
	 * @return
	 */
	public SystemTradeType getTradeType(Integer tradeId, Integer agentId){
		String coins = memCache.get(RedisConstant.TRADE_KEY + tradeId + "_" + agentId);
		if (StringUtils.isEmpty(coins)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(coins);
		SystemTradeType tradeType = obj.getObject("extObject", SystemTradeType.class);
		return tradeType;
	}

	/**
	 * 获取币种与交易映射
	 * @return Map<coinId, tradeId>
	 */
	public Map<Integer, Integer> getCoinIdToTradeId(Integer agentId) {
		Map<Integer, Integer> coinIdToTradeId = new HashMap<>();
		List<SystemTradeType> tradeTypes = getTradeTypeSort(SystemTradeTypeEnum.USDT.getCode(),agentId);
		if (tradeTypes == null) {
			return coinIdToTradeId;
		}
		for (SystemTradeType tradeType : tradeTypes) {
			coinIdToTradeId.put(tradeType.getSellCoinId(), tradeType.getId());
		}
		return coinIdToTradeId;
	}

	/**
	 * 获取币种与交易映射
	 * @return Map<coinId, tradeId>
	 */
	public Map<Integer, Integer> getCoinIdToTradeIdWithUsdt(Integer agentId) {
		Map<Integer, Integer> coinIdToTradeId = new HashMap<>();
		List<SystemTradeType> tradeTypes = getTradeTypeSort(SystemTradeTypeEnum.USDT.getCode(),agentId);
		if (tradeTypes == null) {
			return coinIdToTradeId;
		}
		for (SystemTradeType tradeType : tradeTypes) {
			coinIdToTradeId.put(tradeType.getSellCoinId(), tradeType.getId());
		}
		return coinIdToTradeId;
	}

	public Map<Integer, Integer> getCoinIdToTradeIdWithTradeType(Integer agentId,Integer type) {
		Map<Integer, Integer> coinIdToTradeId = new HashMap<>();
		List<SystemTradeType> tradeTypes = getTradeTypeSort(type,agentId);
		if (tradeTypes == null) {
			return coinIdToTradeId;
		}
		for (SystemTradeType tradeType : tradeTypes) {
			coinIdToTradeId.put(tradeType.getSellCoinId(), tradeType.getId());
		}
		return coinIdToTradeId;
	}


	/***************** Language操作 *******************/
	/**
	 * 获取语言信息
	 * @param shortname 语言简称（cn，en，tw）
	 * @return FLanguageType
	 */
	public FSystemLan getLanguageType(String shortname) {
		String language = memCache.get(RedisConstant.LANGUAGE_KET + shortname);
		if (StringUtils.isEmpty(language)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(language);
		FSystemLan languageType = JSON.parseObject(obj.get("extObject").toString(), FSystemLan.class);
		return languageType;
	}

	/**
	 * 获取语言信息列表
	 * @return
	 */
	public List<FSystemLan> getLanguageTypeList() {
		String language = memCache.get(RedisConstant.LANGUAGE_LIST_KET);
		if (StringUtils.isEmpty(language)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(language);
		List<FSystemLan> lans = JSON.parseArray(obj.get("extObject").toString(), FSystemLan.class);
		return lans;
	}
	/***************** BankInfo操作 *******************/
	/**
	 * 获取充值银行卡列表
	 *
	 * @param type
	 *            类型
	 * @return List<FSystemBankinfoRecharge>
	 */
	public List<FSystemBankinfoRecharge> getRechargeBank(int type) {
		String bankinfo = memCache.get(RedisConstant.SYS_RECHARGEBANK_KEY + type);
		if (StringUtils.isEmpty(bankinfo)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(bankinfo);
		List<FSystemBankinfoRecharge> bankinfos = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), FSystemBankinfoRecharge.class);
		return bankinfos;
	}

	/**
	 * 获取可提现银行列表
	 *
	 * @return List<FSystemBankinfoWithdraw>
	 */
	public List<FSystemBankinfoWithdraw> getWithdrawBankList() {
		String bankinfo = memCache.get(RedisConstant.SYS_WITHDRAWBANKLIST_KEY);
		if (StringUtils.isEmpty(bankinfo)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(bankinfo);
		List<FSystemBankinfoWithdraw> bankinfos = JSONArray.parseArray(obj.getJSONArray("extObject").toJSONString(), FSystemBankinfoWithdraw.class);
		return bankinfos;
	}

	/**
	 * 根据ID获取提现银行信息
	 *
	 * @param id
	 *            提现银行ID
	 * @return FSystemBankinfoWithdraw
	 */
	public FSystemBankinfoWithdraw getWithdrawBank(int id) {
		String bankinfo = memCache.get(RedisConstant.SYS_WITHDRAWBANK_KEY + id);
		if (StringUtils.isEmpty(bankinfo)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(bankinfo);
		FSystemBankinfoWithdraw bi = JSON.parseObject(obj.get("extObject").toString(), FSystemBankinfoWithdraw.class);
		return bi;
	}

	/***************** tradepassword操作 *******************/
	/**
	 * 设置交易密码
	 *
	 * @param fuid
	 *            用户ID
	 */
	public void setNeedTradePassword(int fuid) {
		if (getNeedTradePassword(fuid)) {
			String args = memCache.get(RedisConstant.ARGS_KET + "tradePasswordHour");
			String value = "5";
			if (!StringUtils.isEmpty(args)) {
				JSONObject obj = JSON.parseObject(args);
				value = obj.get("extObject").toString();
			}
			int tradePasswordHour = Integer.valueOf(value);
			RedisObject obj = new RedisObject();
			obj.setExtObject(true);
			String token = RedisConstant.TRADE_NEED_PASSWORD + MD5Util.md5(String.valueOf(fuid));
			memCache.set(token, JSON.toJSONString(obj), tradePasswordHour * 60 * 60);
		}
	}

	/**
	 * 获取是否需要交易密码
	 *
	 * @param fuid
	 *            用户ID
	 * @return boolean
	 */
	public boolean getNeedTradePassword(Integer fuid) {
		String token = RedisConstant.TRADE_NEED_PASSWORD + MD5Util.md5(String.valueOf(fuid));
		String args = memCache.get(token);
		return StringUtils.isEmpty(args);
	}

	/**
	 * 根据id查询提现银行
	 *
	 * @param fid
	 *            银行卡Id
	 * @return FSystemBankinfoWithdraw
	 */
	public FSystemBankinfoWithdraw getWithdrawBankById(int fid) {
		String bankinfoStr = memCache.get(RedisConstant.SYS_WITHDRAWBANK_KEY + fid);
		if (StringUtils.isEmpty(bankinfoStr)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(bankinfoStr);
		FSystemBankinfoWithdraw bankinfo = JSON.parseObject(obj.get("extObject").toString(), FSystemBankinfoWithdraw.class);
		return bankinfo;
	}

	/**
	 * 根据id查询充值银行
	 *
	 * @param fid
	 *            银行卡Id
	 * @return FSystemBankinfoRecharge
	 */
	public FSystemBankinfoRecharge getRechargeBankById(int fid) {
		String bankinfoStr = memCache.get(RedisConstant.SYS_RECHARGEBANKID_KEY + fid);
		if (StringUtils.isEmpty(bankinfoStr)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(bankinfoStr);
		FSystemBankinfoRecharge bankinfo = JSON.parseObject(obj.get("extObject").toString(), FSystemBankinfoRecharge.class);
		return bankinfo;
	}

	/**
	 * 获取友情链接列表
	 * 
	 * @return List<FFriendLink>
	 */
	public List<FFriendLink> getFFriendLinkList() {
		String friendLinkStr = memCache.get(RedisConstant.FRIEND_LINK);
		if (StringUtils.isEmpty(friendLinkStr)) {
			return null;
		}
		JSONObject obj = JSON.parseObject(friendLinkStr);
		List<FFriendLink> friendLinks = JSON.parseArray(obj.get("extObject").toString(), FFriendLink.class);
		return friendLinks;
	}
	
	/**
	 * 获取成交价
	 * @param tradeId
	 * @return
	 */
	public BigDecimal getLastPrice(int tradeId) {
		String result = memCache.get(RedisConstant.TICKERE_KEY + tradeId);
		if (result == null || "".equals(result)) {
			return BigDecimal.ZERO;
		}
		RedisObject redisObject = JSON.parseObject(result, RedisObject.class);
		TickerData tickerData = JSON.parseObject(redisObject.getExtObject().toString(), TickerData.class);
		if (tickerData == null) {
			return BigDecimal.ZERO;
		}
		return tickerData.getLast();
	}

	/**
	 * 获取开盘价
	 * @param tradeId
	 * @return
	 */
	public BigDecimal getKaiPrice(int tradeId) {
		String result = memCache.get(RedisConstant.TICKERE_KEY + tradeId);
		if (result == null || "".equals(result)) {
			return BigDecimal.ZERO;
		}
		RedisObject redisObject = JSON.parseObject(result, RedisObject.class);
		TickerData tickerData = JSON.parseObject(redisObject.getExtObject().toString(), TickerData.class);
		if (tickerData == null) {
			return BigDecimal.ZERO;
		}
		return tickerData.getKai();
	}

	/**
	 * 获取存币理财设置
	 * @param fid 设置ID
	 * @param fcoinid 币种ID
	 * @return
	 */
	public FVirtualFinances getVirtualFinances(Integer fid, Integer fcoinid){
		String result = memCache.get(RedisConstant.FINANCES_KEY + "_" + fcoinid + "_" + fid);
		if (result == null || "".equals(result)) {
			return null;
		}
		RedisObject redisObject = JSON.parseObject(result, RedisObject.class);
		FVirtualFinances virtualFinances = JSON.parseObject(redisObject.getExtObject().toString(), FVirtualFinances.class);
		return virtualFinances;
	}

	/**
	 * 获取存币理财设置列表
	 * @param fcoinid 币种ID
	 * @return
	 */
	public List<FVirtualFinances> getVirtualFinancesList(Integer fcoinid){
		String result = memCache.get(RedisConstant.FINANCES_KEY + "_" + fcoinid);
		if (TextUtils.isEmpty(result)) {
			return null;
		}
		RedisObject redisObject = JSON.parseObject(result, RedisObject.class);
		List<FVirtualFinances> virtualFinances = JSON.parseArray(redisObject.getExtObject().toString(), FVirtualFinances.class);
		return virtualFinances;
	}

	/**
	 * 获取充值代理列表
	 * @return
	 */
	public List<FRechargeAgency> getRechargeAgencyList(){
		String result = memCache.get(RedisConstant.RECHARGEAGENCY_KEY);
		if (TextUtils.isEmpty(result)) {
			return null;
		}
		RedisObject redisObject = JSON.parseObject(result, RedisObject.class);
		List<FRechargeAgency> rechargeAgencyList = JSON.parseArray(redisObject.getExtObject().toString(), FRechargeAgency.class);
		return rechargeAgencyList;
	}


	/**
	 * 设置缓存数据
	 */
	public void setRedisData(String token, Object restInfo) {
		RedisObject obj = new RedisObject();
		obj.setExtObject(restInfo);
		// 30分钟过期时间
		memCache.set(token, JSON.toJSONString(obj), Constant.RESTPASSEXPIRETIME);
	}
	
	/**
	 * 设置缓存数据
	 * @param token
	 * @param restInfo
	 * @param expireSeconds
	 */
	public void setRedisData(String token, Object restInfo, int expireSeconds) {
		RedisObject obj = new RedisObject();
		obj.setExtObject(restInfo);
		// 30分钟过期时间
		memCache.set(token, JSON.toJSONString(obj), expireSeconds);
	}

	/**
	 * 删除缓存数据
	 */
	public void deletRedisData(String token) {
		if (TextUtils.isEmpty(token)) {
			return;
		}
		memCache.delete(token);
	}

	/**
	 * 获取缓存数据
	 */
	public String getRedisData(String token) {
		if (token == null || StringUtils.isEmpty(token)) {
			return null;
		}
		String rest = memCache.get(token);
		if (StringUtils.isEmpty(rest)) {
			return null;
		}
		RedisObject redisObject = JSON.parseObject(rest, RedisObject.class);
		return redisObject.getExtObject().toString();
	}
	
	/**
	 * 获取缓存数据
	 */
	public RedisObject getRedisObject(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		String rest = memCache.get(key);
		if (StringUtils.isEmpty(rest)) {
			return null;
		}
		RedisObject redisObject = JSON.parseObject(rest, RedisObject.class);
		return redisObject;
	}
	
	/**
     * 新增不过期的数据
     * @param key 键
     * @param value 值
     * @return ok
     */
    public boolean setNoExpire(String key, String value) {
       return memCache.setNoExpire(key, value);
    }
	
	public void set(String key, RedisObject t) {
		set(key, t, expire_time);
	}
	
	public void setNoExpire(String key, RedisObject t) {
		memCache.setNoExpire(key, JSON.toJSONString(t));
	}
	
	public void set(String key, RedisObject t, int seconds) {
		memCache.set(key, JSON.toJSONString(t), seconds);
	}

	
	public void remove(String key) {
		memCache.delete(key);
	}
	
	public String get(String key) {
		return memCache.get(key);
	}
	
	public void delete(String key) {
		memCache.delete(key);
	}

	public void addAdsSourceCounter(String source){
		String token = RedisConstant.ADS_SOURCE_KEY + "pv" + "_" + source;
		
		String json = memCache.get(token);
		
		RedisObject obj = null;
		if(!StringUtils.isEmpty(json)){
			obj = JSON.parseObject(json, RedisObject.class);
			if (obj != null) {
				int counter =  (Integer)obj.getExtObject() + 1;
				obj.setLastActiveDateTime(Utils.getTimestamp().getTime()/1000);
				obj.setExtObject(counter);
				memCache.setNoExpire(token, JSON.toJSONString(obj));
			}
		}else{
			obj = new RedisObject();
			obj.setLastActiveDateTime(Utils.getTimestamp().getTime()/1000);
			obj.setExtObject(1);
			
			memCache.setNoExpire(token, JSON.toJSONString(obj));
		}
	}

	/**
	 * 设置投资人问卷答题状态
	 * @param uid 用户ID
	 * @return true 成功，false 失败
	 */
	public Boolean setRedisFqasStatus(Integer uid){
		String key = RedisConstant.FQAS_USER_KEY + uid;
		return memCache.setNoExpire(RedisDBConstant.REDIS_DB_FQAS,key,"true");
	}

	/**
	 * 获取投资人问卷答题状态
	 * @param uid 用户ID
	 * @return true 已答题，false 未答题
	 */
	public Boolean getRedisFqasStatus(Integer uid){
		String key = RedisConstant.FQAS_USER_KEY + uid;
		String status = memCache.get(RedisDBConstant.REDIS_DB_FQAS,key);
		if(StringUtils.isEmpty(status)){
			return false;
		}
		return true;
	}

	/*********操作有序集合（sorted set）************/
	public Boolean addShareMember(Integer batch,String memberInfo){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		boolean status = memCache.zadd(RedisDBConstant.ACTIVITY_SHARE_COIN,key,1.0,memberInfo);
		if(!status){
			return false;
		}
		return true;
	}

	/**
	 * 修改次数
	 * @param batch
	 * @param memberInfo
	 * @return
	 */
	public Boolean addShareMemberCount(Integer batch,String memberInfo,String ipAddress){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		String keySet = RedisConstant.ACTIVITYSHARECOIN_KEY + "ip_" + batch;
		//判断ip地址是否存在
		/*if(memCache.sismember(RedisDBConstant.ACTIVITY_SHARE_COIN,keySet,ipAddress)){
			return false;
		}*/
		// 记录当前ip
		if(!memCache.sadd(RedisDBConstant.ACTIVITY_SHARE_COIN,keySet,ipAddress)){
			// 重复不需要再添加
			return true;
		}

		boolean status = memCache.zincrby(RedisDBConstant.ACTIVITY_SHARE_COIN,key,1.0,memberInfo);
		if(!status){
			return false;
		}
		return true;
	}
	/**
	 * 删除用户的分数
	 * @param batch
	 * @param memberInfo
	 * @return
	 */
	public boolean deleteMemberScore(Integer batch,String memberInfo){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		return memCache.zrem(RedisDBConstant.ACTIVITY_SHARE_COIN,key,memberInfo);
	}
	/**
	 * 查询用户的分数
	 * @param batch
	 * @param memberInfo
	 * @return
	 */
	public Double getMemberScore(Integer batch,String memberInfo){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		Double score = memCache.zscore(RedisDBConstant.ACTIVITY_SHARE_COIN,key,memberInfo);
		return score;
	}

	/**
	 * 查询用户的排名
	 * @param batch
	 * @param memberInfo
	 * @return
	 */
	public Long getMemberRanking(Integer batch,String memberInfo){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		Long ranking = memCache.zrevrank(RedisDBConstant.ACTIVITY_SHARE_COIN,key,memberInfo);
		return ranking;
	}

	/**
	 * 查询制定数量的排名（从大到小）
	 * @param batch
	 * @return
	 */
	public Set<String> getAllRanking(Integer batch,Double max,Double min,Integer offset, Integer count){
		String key = RedisConstant.ACTIVITYSHARECOIN_KEY + "sorted_" + batch;
		Set<String> set = memCache.zrevrangeByScore(RedisDBConstant.ACTIVITY_SHARE_COIN,key,max,min,offset,count);
		return set;
	}

	/**
	 *  是否获取三方平台的数据
	 * @param value
	 * @return
	 */
	public boolean setIsFetchThirdPlatform(String value){
		return memCache.setNoExpire(RedisConstant.IS_FETCH_THIRD_PLATFORM,value);
	}

	/**
	 * 是否获取第三方平台的数据
	 *
	 * @return
	 */
	public boolean isFetchThirdPlatform(){
		String value = memCache.get(RedisConstant.IS_FETCH_THIRD_PLATFORM);
		//如果值为空 或者 1 表示需要获取三方平台的数据
		if (StringUtils.isEmpty(value) || "1".equals(value)){
			return true;
		}
		return false;
	}

	/**
	 * 根据key获取apiAuth
	 *
	 * @param apikey uid or apiKey
	 * @return
	 */
	public FApiAuth getApiAuthByKey(String apikey){
		try {
			RedisObject redisObject = getRedisObject(apikey);
			if (redisObject == null){
				return null;
			}
			return JSONObject.parseObject(redisObject.getExtObject().toString(), FApiAuth.class);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;

	}





}
