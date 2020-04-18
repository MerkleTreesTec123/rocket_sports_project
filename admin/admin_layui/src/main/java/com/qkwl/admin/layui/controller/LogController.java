package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
	
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.Constant;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.rpc.admin.IAdminLogService;
import com.qkwl.admin.layui.base.WebBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 日志管理
 * @author ZKF
 */
@Controller
public class LogController extends WebBaseController {
	
	@Autowired
	private IAdminLogService adminLogService;
	@Autowired
	private RedisHelper redisHelper;
	

	/**
	 * 用户IP登录次数排序列表
	 */
	@RequestMapping("/log/ipLoginList")
	public ModelAndView ipLoginList(
			@RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
			@RequestParam(value = "beginDate", required = false) String beginDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "keywords" , required = false ) String keywords){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("log/ipLoginList");

		Pagination<FLogUserAction> page = new Pagination<FLogUserAction>(currentPage, Constant.adminPageSize);

		//查询关键字
		if(!org.springframework.util.StringUtils.isEmpty(keywords)){
			page.setKeyword(keywords);
			modelAndView.addObject("keywords", keywords);
		}
		if(!org.springframework.util.StringUtils.isEmpty(beginDate)){
			page.setBegindate(beginDate);
			modelAndView.addObject("beginDate", beginDate);
		}
		if(!org.springframework.util.StringUtils.isEmpty(endDate)){
			page.setEnddate(endDate);
			modelAndView.addObject("endDate", endDate);
		}

		if(org.springframework.util.StringUtils.isEmpty(beginDate) && org.springframework.util.StringUtils.isEmpty(endDate)){
			modelAndView.addObject("logList", page);
			return modelAndView;
		}

		page = adminLogService.selectIpLoginRankPageList(page, null);
		modelAndView.addObject("logList", page);
		return modelAndView;
	}
	
	/**
	 * 管理员操作日志
	 */
	@RequestMapping("/log/logadminList")
	public ModelAndView logadminList(
			@RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
			@RequestParam(value = "orderField" , defaultValue = "fcreatetime" ) String orderField,
			@RequestParam(value = "orderDirection" , defaultValue = "desc" ) String orderDirection,
			@RequestParam(value = "beginDate",required=false) String beginDate,
			@RequestParam(value = "endDate",required=false) String endDate,
			@RequestParam(value = "keywords" , required = false ) String keywords){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("log/logadminList");
		
		Pagination<FLogAdminAction> page = new Pagination<FLogAdminAction>(currentPage, Constant.adminPageSize);
		//排序字段
		if(!StringUtils.isEmpty(orderField)){
			page.setOrderField(orderField);
		}
		//正序倒序
		if(!StringUtils.isEmpty(orderDirection)){
			page.setOrderDirection(orderDirection);
		}
		//查询关键字
		if(!StringUtils.isEmpty(keywords)){
			page.setKeyword(keywords);
		}

		if(!StringUtils.isEmpty(beginDate)){
			page.setBegindate(beginDate);
			modelAndView.addObject("beginDate", beginDate);
		}
		if(!StringUtils.isEmpty(endDate)){
			page.setEnddate(endDate);
			modelAndView.addObject("endDate", endDate);
		}
		
		FLogAdminAction log = new FLogAdminAction();
		
		page = adminLogService.selectAdminPageList(page, log);
		
		modelAndView.addObject("keywords", keywords);
		modelAndView.addObject("logadminList", page);
		
		return modelAndView;
	}
	

	
	// Vip6购买导出列名
	private static enum ExportFiled {
		用户id, 类型, 购买时间;
	}


	/**
	 * 用户积分日志
	 */
	@RequestMapping("/log/logscoreList")
	public ModelAndView logScoreList(
			@RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
			@RequestParam(value = "orderField" , defaultValue = "fcreatetime" ) String orderField,
			@RequestParam(value = "orderDirection" , defaultValue = "desc" ) String orderDirection,
			@RequestParam(value = "keywords" , required = false ) String keywords,
			@RequestParam(value = "ftype" , required = false, defaultValue = "-1") Integer ftype){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("log/logScoreList");
		
		Pagination<FLogUserScore> page = new Pagination<FLogUserScore>(currentPage, Constant.adminPageSize);
		
		//查询关键字
		if(!StringUtils.isEmpty(keywords)){
			page.setKeyword(keywords);
			modelAndView.addObject("keywords", keywords);
		}		
		page.setOrderField(orderField);
		page.setOrderDirection(orderDirection);
		FLogUserScore log = new FLogUserScore();
		if(ftype>0){
			log.setFtype(ftype);
			modelAndView.addObject("ftype", ftype);
		}
		
		page = adminLogService.selectUserScorePageList(page, log);
		
		// 页面查询参数
		Map<Integer, String> typeMap = new LinkedHashMap<>();
		typeMap.put(-1, "全部");
		for (ScoreTypeEnum typeEnum : ScoreTypeEnum.values()) {
			typeMap.put(typeEnum.getCode(), typeEnum.getValue().toString());
		}
		modelAndView.addObject("typeMap", typeMap);
		
		modelAndView.addObject("logList", page);
		return modelAndView;
	}

}
