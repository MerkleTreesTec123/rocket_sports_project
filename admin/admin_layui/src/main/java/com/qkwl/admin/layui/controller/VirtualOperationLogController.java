package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.ArtificialRechargeTypeEnum;
import com.qkwl.common.dto.Enum.OperationlogEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VirtualOperationLogController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(VirtualOperationLogController.class);
	
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private RedisHelper redisHelper;

	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;

	/**
	 * 虚拟币手工充值列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/virtualoperationlogList")
	public ModelAndView Index(
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "ftype", defaultValue = "0") Integer type,
			@RequestParam(value = "coinId", defaultValue = "-1") Integer coinId,
			@RequestParam(value = "status", defaultValue = "0") Integer statusId,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualoperationlogList");
		Pagination<FLogConsoleVirtualRecharge> pageParam = new Pagination<>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		FLogConsoleVirtualRecharge filterParam = new FLogConsoleVirtualRecharge();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!org.springframework.util.StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 开始时间
		if (!org.springframework.util.StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}

		if(type != null && type > 0){
			filterParam.setFtype(type);
			modelAndView.addObject("ftype", type);
		}

		if(coinId > -1){
			filterParam.setFcoinid(coinId);
			modelAndView.addObject("coinId", coinId);
		}

		Map<Integer,String> typeMap = new HashMap<>();
		typeMap.put(0,"全部");
		typeMap.put(ArtificialRechargeTypeEnum.Common.getCode(),ArtificialRechargeTypeEnum.Common.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Capital.getCode(),ArtificialRechargeTypeEnum.Capital.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Activity.getCode(),ArtificialRechargeTypeEnum.Activity.getValue());
		modelAndView.addObject("typeMap", typeMap);


		Map<Integer, String> coinMap = new LinkedHashMap<>();
		coinMap.put(-1, "全部");
		List<SystemCoinType> coinList = redisHelper.getCoinTypeListAll();
		for(SystemCoinType coin : coinList){
			coinMap.put(coin.getId(), coin.getName());
		}
		modelAndView.addObject("coinMap", coinMap);

		Map<Integer, String> statusMap = new LinkedHashMap<>();
		statusMap.put(0, "全部");
		statusMap.put(OperationlogEnum.SAVE, "暂存");
		statusMap.put(OperationlogEnum.AUDIT, "已审核");
		statusMap.put(OperationlogEnum.FFROZEN, "冻结");
		modelAndView.addObject("statusMap", statusMap);
		modelAndView.addObject("status", statusId);

		if(statusId > 0){
			status.add(statusId);
		}

		Pagination<FLogConsoleVirtualRecharge> list = adminUserCapitalService.selectConsoleVirtualRechargeList(pageParam, filterParam, status);
		modelAndView.addObject("virtualoperationlogList", list);
		return modelAndView;
	}

	/**
	 * 人名币手工充值列表
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/virtualoperationlogList2")
	public ModelAndView Index2(
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "ftype", defaultValue = "0") Integer type,
			@RequestParam(value = "coinId", defaultValue = "-1") Integer coinId,
			@RequestParam(value = "status", defaultValue = "0") Integer statusId,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualoperationlogList2");
		Pagination<FLogConsoleVirtualRecharge> pageParam = new Pagination<>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		FLogConsoleVirtualRecharge filterParam = new FLogConsoleVirtualRecharge();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!org.springframework.util.StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 开始时间
		if (!org.springframework.util.StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}

		//获取人民币列表
		List<SystemCoinType> coinList = redisHelper.getCoinTypeCnyList();
		if (coinList == null || coinList.size() == 0){
			throw new Exception("没有人民币币种");
		}

		if(coinId > -1){
			filterParam.setFcoinid(coinId);
			modelAndView.addObject("coinId", coinId);
		}else {
			filterParam.setFcoinid(coinList.get(0).getId());
			modelAndView.addObject("coinId", coinList.get(0).getId());
		}

		if(type != null && type > 0){
			filterParam.setFtype(type);
			modelAndView.addObject("ftype", type);
		}

		//充值类型
		Map<Integer,String> typeMap = new HashMap<>();
		typeMap.put(0,"全部");
		typeMap.put(ArtificialRechargeTypeEnum.Common.getCode(),ArtificialRechargeTypeEnum.Common.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Capital.getCode(),ArtificialRechargeTypeEnum.Capital.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Activity.getCode(),ArtificialRechargeTypeEnum.Activity.getValue());
		modelAndView.addObject("typeMap", typeMap);

		//人民币币种
		Map<Integer, String> coinMap = new LinkedHashMap<>();
		for(SystemCoinType coin : coinList){
			coinMap.put(coin.getId(), coin.getName());
		}
		modelAndView.addObject("coinMap", coinMap);

		//状态
		Map<Integer, String> statusMap = new LinkedHashMap<>();
		statusMap.put(0, "全部");
		statusMap.put(OperationlogEnum.SAVE, "暂存");
		statusMap.put(OperationlogEnum.AUDIT, "已审核");
		statusMap.put(OperationlogEnum.FFROZEN, "冻结");
		modelAndView.addObject("statusMap", statusMap);
		modelAndView.addObject("status", statusId);

		if(statusId > 0){
			status.add(statusId);
		}

		Pagination<FLogConsoleVirtualRecharge> list = adminUserCapitalService.selectConsoleVirtualRechargeList(pageParam, filterParam, status);
		modelAndView.addObject("virtualoperationlogList", list);
		return modelAndView;
	}

	/**
	 * 页面加载
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/goVirtualOperationLogJSP")
	public ModelAndView goVirtualOperationLogJSP(
			@RequestParam(value = "uid", required = false) Integer fid,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "isCNY",required = false,defaultValue = "0") Integer isCNY) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		if (fid != null) {
			FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(fid);
			modelAndView.addObject("virtualoperationlog", virtualoperationlog);
		}

		if (isCNY > 0){
			modelAndView.addObject("allType",redisHelper.getCoinTypeCNYNameMap());
		}else {
			modelAndView.addObject("allType", redisHelper.getCoinTypeNameMap());
		}
		Map<Integer,String> typeMap = new HashMap<>();
		typeMap.put(ArtificialRechargeTypeEnum.Common.getCode(),ArtificialRechargeTypeEnum.Common.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Capital.getCode(),ArtificialRechargeTypeEnum.Capital.getValue());
		typeMap.put(ArtificialRechargeTypeEnum.Activity.getCode(),ArtificialRechargeTypeEnum.Activity.getValue());
		modelAndView.addObject("typeMap", typeMap);
		modelAndView.setViewName(url);
		return modelAndView;
	}

	/**
	 * 新增
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/saveVirtualOperationLog")
	@ResponseBody
	public ReturnResult saveVirtualOperationLog(
			@RequestParam(value = "userLookup.id", required = true) Integer userId,
			@RequestParam(value = "vid", required = true) Integer vid,
			@RequestParam(value = "ftype", required = true,defaultValue = "1") Integer type,
			@RequestParam(value = "famount", required = true) BigDecimal famount,
			@RequestParam(value = "finfo", required = true) String finfo,
			@RequestParam(value = "fisSendMsg", required = false) String fisSendMsg) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FLogConsoleVirtualRecharge virtualoperationlog = new FLogConsoleVirtualRecharge();
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");

		virtualoperationlog.setFtype(type);
		virtualoperationlog.setFamount(famount);
		virtualoperationlog.setFcoinid(vid);
		virtualoperationlog.setFuid(userId);
		virtualoperationlog.setFstatus(OperationlogEnum.SAVE);
		virtualoperationlog.setFcreatetime(Utils.getTimestamp());
		virtualoperationlog.setFinfo(finfo);
		if (fisSendMsg != null) {
			virtualoperationlog.setFissendmsg(1);
		} else {
			virtualoperationlog.setFissendmsg(0);
		}
		
		virtualoperationlog.setVersion(0);
		virtualoperationlog.setFcreatorid(sessionAdmin.getFid());
		adminUserCapitalService.insertConsoleVirtualRecharge(virtualoperationlog);

		return ReturnResult.SUCCESS("新增成功!");
	}

	/**
	 * 删除
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/deleteVirtualOperationLog")
	@ResponseBody
	public ReturnResult deleteVirtualOperationLog(
			@RequestParam(value = "uid", required = true) Integer fid) throws Exception {
		FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(fid);
		if (virtualoperationlog.getFstatus() != OperationlogEnum.SAVE) {
			return ReturnResult.FAILUER("删除失败，记录已审核");
		}
		adminUserCapitalService.deleteConsoleVirtualRechargeById(fid);
		return ReturnResult.SUCCESS("删除成功");
	}

	/**
	 * 审核
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/auditVirtualOperationLog")
	@ResponseBody
	public ReturnResult auditVirtualOperationLog(
			@RequestParam(value = "uid", required = true) Integer fid) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		boolean flag = false;
		FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(fid);
		if (virtualoperationlog.getFstatus() != OperationlogEnum.SAVE) {
			return ReturnResult.FAILUER("已审核，不允许重复审核");
		}
		try {
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			virtualoperationlog.setFstatus(OperationlogEnum.FFROZEN);
			virtualoperationlog.setFcreatorid(sessionAdmin.getFid());
			flag = adminUserCapitalService.updateConsoleVirtualRecharge(virtualoperationlog);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return ReturnResult.FAILUER("审核失败");
		}
		return ReturnResult.SUCCESS("审核成功");
	}

	/**
	 * 发放
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/sendVirtualOperationLog")
	@ResponseBody
	public ReturnResult sendVirtualOperationLog(
			@RequestParam(value = "uid", required = true) Integer fid) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		boolean flag = false;
		FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(fid);
		if (virtualoperationlog.getFstatus() != OperationlogEnum.FFROZEN) {
			return ReturnResult.FAILUER("只会状态为冻结，才允许发放！");
		}
		try {
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			virtualoperationlog.setFstatus(OperationlogEnum.AUDIT);
			virtualoperationlog.setFcreatorid(sessionAdmin.getFid());
			virtualoperationlog.setFcreatetime(Utils.getTimestamp());
			flag = adminUserCapitalService.updateConsoleVirtualRecharge(virtualoperationlog);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return ReturnResult.FAILUER("发放失败");
		}
		return ReturnResult.SUCCESS("发放成功");
	}
	/**
	 * 批量审核
	 */
	@RequestMapping("admin/auditVirtualOperationLogAll")
	@ResponseBody
	public ReturnResult auditVirtualOperationLogAll(
			@RequestParam(value = "ids", required = true) String ids) {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		String[] idString = ids.split(",");
		Integer errCount = 0;
		for (String id : idString) {
			FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(Integer.valueOf(id));
			if (virtualoperationlog.getFstatus() != OperationlogEnum.SAVE) {
				errCount++;
				continue;
			}
			try {
				virtualoperationlog.setFstatus(OperationlogEnum.FFROZEN);
				virtualoperationlog.setFcreatorid(sessionAdmin.getFid());
				virtualoperationlog.setFcreatetime(Utils.getTimestamp());
				if (!adminUserCapitalService.updateConsoleVirtualRecharge(virtualoperationlog)) {
					errCount++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				errCount++;
			}
		}
		return ReturnResult.SUCCESS((errCount > 0 ? ("部分审核成功，失败" + errCount + "条") : "批量审核成功"));
	}

	/**
	 * 批量发放
	 */
	@RequestMapping("admin/sendVirtualOperationLogAll")
	@ResponseBody
	public ReturnResult sendVirtualOperationLogAll(
			@RequestParam(value = "ids", required = true) String ids){
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		String[] idString = ids.split(",");
		Integer errCount = 0;
		for (String id : idString) {
			FLogConsoleVirtualRecharge virtualoperationlog = adminUserCapitalService.selectConsoleVirtualRechargeById(Integer.valueOf(id));
			if (virtualoperationlog.getFstatus() != OperationlogEnum.FFROZEN) {
				errCount++;
				continue;
			}
			try {
				virtualoperationlog.setFstatus(OperationlogEnum.AUDIT);
				virtualoperationlog.setFcreatorid(sessionAdmin.getFid());
				virtualoperationlog.setFcreatetime(Utils.getTimestamp());
				if (!adminUserCapitalService.updateConsoleVirtualRecharge(virtualoperationlog)) {
					errCount++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				errCount++;
			}
		}
		return ReturnResult.SUCCESS((errCount > 0 ? ("部分发放成功，失败" + errCount + "条") : "批量发放成功"));
	}

	@RequestMapping("admin/updateHistoryActivity")
	@ResponseBody
	public ReturnResult updateHistoryActivity(
			@RequestParam(value = "activityId", required = true) Integer activityId,
			@RequestParam(value = "finfo", required = true) String finfo
	){
		FLogConsoleVirtualRecharge virtualoperationlog = new FLogConsoleVirtualRecharge();
		virtualoperationlog.setFinfo(finfo);
		virtualoperationlog.setActivityId(activityId);
		Boolean result = adminUserCapitalService.updateHistoryActivity(virtualoperationlog);
		if(result){
			return ReturnResult.SUCCESS("修改成功!");
		}
		return ReturnResult.FAILUER("修改失败!");
	}
}
