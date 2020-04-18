package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.Enum.SystemTradeBlockEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminSystemTradeTypeService;
import com.qkwl.common.framework.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class SystemTradeTypeController extends WebBaseController {
	
	@Autowired
	private IAdminSystemTradeTypeService adminSystemTradeTypeService;

	@Autowired
	private RedisHelper redisHelper;

	/**
	 * 加载交易信息列表
	 */
	@RequestMapping("/admin/tradeTypeList")
	public ModelAndView tradeTypeList(
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keywords,
			@RequestParam(value = "orderField",defaultValue="gmt_create") String orderField,
			@RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("trade/tradeTypeList");
		
		Pagination<SystemTradeType> page=new Pagination<SystemTradeType>(currentPage,Constant.adminPageSize);
		page.setOrderDirection(orderDirection);
		page.setOrderField(orderField);
		page.setKeyword(keywords);
		
		SystemTradeType tradeType = new SystemTradeType();
		tradeType.setAgentId(WebConstant.BCAgentId);
		
		page=adminSystemTradeTypeService.selectSystemTradeTypeList(page, tradeType);
		
		if(keywords != null && !"".equals(keywords)){
			modelAndView.addObject("keywords", keywords);
		}

		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);

		Map<Integer, Object> statusMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeStatusEnum statusEnum : SystemTradeStatusEnum.values()){
			statusMap.put(statusEnum.getCode(), statusEnum.getValue());
		}
		modelAndView.addObject("statusMap", statusMap);
		
		modelAndView.addObject("page", page);
		return modelAndView;
	}
	
	/**
	 * 页面跳转
	 */
	@RequestMapping("admin/goTradeTypeJSP")
	public ModelAndView goVirtualCoinTypeJSP(
			@RequestParam(value = "url", required=false) String url,
			@RequestParam(value = "tradeId", defaultValue="0") Integer tradeId
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (tradeId > 0) {
			SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(tradeId);
			modelAndView.addObject("tradeType", tradeType);
		}

		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);

		Map<Integer, Object> typeMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()){
			typeMap.put(typeEnum.getCode(), typeEnum.getValue());
		}
		modelAndView.addObject("typeMap", typeMap);


		Map<Integer, Object> blockMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeBlockEnum blockEnum : SystemTradeBlockEnum.values()){
			blockMap.put(blockEnum.getCode(), blockEnum.getValue());
		}
		modelAndView.addObject("blockMap", blockMap);


		Map<Integer, Object> statusMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeStatusEnum statusEnum : SystemTradeStatusEnum.values()){
			statusMap.put(statusEnum.getCode(), statusEnum.getValue());
		}
		modelAndView.addObject("statusMap", statusMap);

		return modelAndView;
	}
	
	/**
	 * 新增交易信息
	 */
	@RequestMapping("admin/insertTradeType")
	@ResponseBody
	public ReturnResult insertTradeType(
			@RequestParam(value = "blockType", required = false) Integer blockType,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "sortId", required = false) Integer sortId,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "buyCoinId", required = false) Integer buyCoinId,
			@RequestParam(value = "sellCoinId", required = false) Integer sellCoinId,
			@RequestParam(value = "isShare", required = false) String isShare,
			@RequestParam(value = "isStop", required = false) String isStop,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "stopTime", required = false) String stopTime,
			@RequestParam(value = "buyFee", required = false) BigDecimal buyFee,
			@RequestParam(value = "sellFee", required = false) BigDecimal sellFee,
			@RequestParam(value = "remoteId", required = false) Integer remoteId,
			@RequestParam(value = "priceWave", required = false) BigDecimal priceWave,
			@RequestParam(value = "priceRange", required = false) BigDecimal priceRange,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "maxCount", required = false) BigDecimal maxCount,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
			@RequestParam(value = "orderAmount", required = false) BigDecimal orderAmount,
			@RequestParam(value = "amountOffset", required = false) String amountOffset,
			@RequestParam(value = "priceOffset", required = false) String priceOffset,
			@RequestParam(value = "digit", required = false) String digit,
			@RequestParam(value = "openPrice", required = false) BigDecimal openPrice) {
		SystemTradeType tradeType = new SystemTradeType();
		tradeType.setTradeBlock(blockType);
		tradeType.setType(type);
		tradeType.setSortId(sortId);
		tradeType.setStatus(status);
		tradeType.setBuyCoinId(buyCoinId);
		tradeType.setSellCoinId(sellCoinId);
		SystemTradeType existTradeType = adminSystemTradeTypeService.selectSystemTradeTypeByCoinId(sellCoinId, buyCoinId);
		if (existTradeType != null) {
			return ReturnResult.FAILUER("新增失败，当前交易对已经存在");
		}
		if (isShare != null && !isShare.isEmpty()) {
			tradeType.setIsShare(true);
		} else {
			tradeType.setIsShare(false);
		}
		if (isStop != null && !isStop.isEmpty()) {
			tradeType.setIsStop(true);
		} else {
			tradeType.setIsStop(false);
		}
		if (openTime != null && !openTime.isEmpty()  && !openTime.equals("00:00:00")) {
			tradeType.setOpenTime(Time.valueOf(openTime));
		} else {
			tradeType.setOpenTime(null);
		}
		if (stopTime != null && !stopTime.isEmpty()  && !stopTime.equals("00:00:00")) {
			tradeType.setStopTime(Time.valueOf(stopTime));
		} else {
			tradeType.setStopTime(null);
		}
		tradeType.setBuyFee(buyFee);
		tradeType.setSellFee(sellFee);
		if(remoteId != null){
			tradeType.setRemoteId(remoteId);
		}
		tradeType.setPriceWave(priceWave);
		tradeType.setPriceRange(priceRange);
		tradeType.setMinCount(minCount);
		tradeType.setMaxCount(maxCount);
		tradeType.setMinPrice(minPrice);
		tradeType.setMaxPrice(maxPrice);
		tradeType.setAmountOffset(amountOffset);
		tradeType.setPriceOffset(priceOffset);
		tradeType.setDigit(digit);
		tradeType.setOpenPrice(openPrice);
		tradeType.setGmtCreate(new Date());
		tradeType.setGmtModified(new Date());
		tradeType.setVersion(0);
		tradeType.setAgentId(WebConstant.BCAgentId);
		tradeType.setOrderAmount(orderAmount);
		if(adminSystemTradeTypeService.insertSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("新增成功！");
		}
		return ReturnResult.FAILUER("新增失败！");
	}
	
	/**
	 * 修改交易信息
	 */
	@RequestMapping("admin/updateTradeType")
	@ResponseBody
	public ReturnResult updateTradeType(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "blockType", required = false) Integer blockType,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "sortId", required = false) Integer sortId,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "buyCoinId", required = false) Integer buyCoinId,
			@RequestParam(value = "sellCoinId", required = false) Integer sellCoinId,
			@RequestParam(value = "isShare", required = false) String isShare,
			@RequestParam(value = "isStop", required = false) String isStop,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "stopTime", required = false) String stopTime,
			@RequestParam(value = "buyFee", required = false) BigDecimal buyFee,
			@RequestParam(value = "sellFee", required = false) BigDecimal sellFee,
			@RequestParam(value = "remoteId", required = false) Integer remoteId,
			@RequestParam(value = "priceWave", required = false) BigDecimal priceWave,
			@RequestParam(value = "priceRange", required = false) BigDecimal priceRange,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "maxCount", required = false) BigDecimal maxCount,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
			@RequestParam(value = "orderAmount", required = false) BigDecimal orderAmount,
			@RequestParam(value = "amountOffset", required = false) String amountOffset,
			@RequestParam(value = "priceOffset", required = false) String priceOffset,
			@RequestParam(value = "digit", required = false) String digit,
			@RequestParam(value = "openPrice", required = false) BigDecimal openPrice) {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(id);
		if(tradeType == null){
			return ReturnResult.FAILUER("交易信息错误！");
		}
		if (openPrice != null && openPrice.compareTo(BigDecimal.ZERO) < 0) {
			return ReturnResult.FAILUER("开盘价不能小于0");
		}	
		tradeType.setTradeBlock(blockType);
		tradeType.setType(type);
		tradeType.setSortId(sortId);
		tradeType.setStatus(status);
		tradeType.setBuyCoinId(buyCoinId);
		tradeType.setSellCoinId(sellCoinId);
		if (isShare != null && !isShare.isEmpty()) {
			tradeType.setIsShare(true);
		} else {
			tradeType.setIsShare(false);
		}
		if (isStop != null && !isStop.isEmpty()) {
			tradeType.setIsStop(true);
		} else {
			tradeType.setIsStop(false);
		}
		if (openTime != null && !openTime.isEmpty()  && !openTime.equals("00:00:00")) {
			tradeType.setOpenTime(Time.valueOf(openTime));
		} else {
			tradeType.setOpenTime(null);
		}
		if (stopTime != null && !stopTime.isEmpty()  && !stopTime.equals("00:00:00")) {
			tradeType.setStopTime(Time.valueOf(stopTime));
		} else {
			tradeType.setStopTime(null);
		}
		tradeType.setBuyFee(buyFee);
		tradeType.setSellFee(sellFee);
		if(remoteId != null){
			tradeType.setRemoteId(remoteId);
		}
		tradeType.setPriceWave(priceWave);
		tradeType.setPriceRange(priceRange);
		tradeType.setMinCount(minCount);
		tradeType.setMaxCount(maxCount);
		tradeType.setMinPrice(minPrice);
		tradeType.setMaxPrice(maxPrice);
		tradeType.setAmountOffset(amountOffset);
		tradeType.setPriceOffset(priceOffset);
		tradeType.setDigit(digit);
		tradeType.setOpenPrice(openPrice);
		tradeType.setGmtModified(new Date());
		tradeType.setOrderAmount(orderAmount);
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("修改成功！");
		}
		return ReturnResult.FAILUER("修改失败！");
	}
		
	/**
	 * 状态操作
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/updateTradeStatus")
	@ResponseBody
	public ReturnResult updateTradeStatus(
			@RequestParam(required = false) int tradeId, @RequestParam(required = false) int status) throws Exception {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(tradeId);
		if(tradeType == null){
			return ReturnResult.FAILUER("ID错误，操作失败！");
		}
		// 禁用
		if(status == 1){
			tradeType.setStatus(SystemCoinStatusEnum.ABNORMAL.getCode());
		}
		// 停盘
		if(status == 2){
			tradeType.setIsStop(true);
		}
		// 开盘
		if(status == 3){
			tradeType.setIsStop(false);
		}
		tradeType.setGmtModified(new Date());
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("操作成功！");
		}else{
			return ReturnResult.FAILUER("操作失败！");
		}
	}
	
	/**
	 * 启用交易
	 */
	@RequestMapping("admin/updateTradeStop")
	@ResponseBody
	public ReturnResult updateTradeStop(@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "status", required = false) Integer status) {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(id);
		if(tradeType == null){
			return ReturnResult.FAILUER("交易信息错误！");
		}
		tradeType.setStatus(status);
		tradeType.setGmtModified(new Date());
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("启用成功！");
		}
		return ReturnResult.FAILUER("启用失败!");
	}
}
