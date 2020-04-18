package com.qkwl.admin.layui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.agent.FAgent;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminManageService;
import com.qkwl.admin.layui.base.WebBaseController;

/**
 * 券商管理
 * @author ZKF
 */
@Controller
public class AgentController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
	
	@Autowired
	private IAdminManageService adminManageService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/agent/agentList")
	public ModelAndView agentList(
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("agent/agentList");
		// 定义查询条件
		Pagination<FAgent> pageParam = new Pagination<FAgent>(currentPage, Constant.adminPageSize);
		FAgent agent = new FAgent();
		// 参数判断
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		
		pageParam = adminManageService.selectAgentPageList(pageParam, agent);
		modelAndView.addObject("page", pageParam);
		return modelAndView;	
	}
	
	/**
	 * 查询跳转
	 */
	@RequestMapping("agent/goAgent")
	public ModelAndView goAgent(
			@RequestParam(required=false) String url,
			@RequestParam(required=false) Integer fid
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		
		if (fid != null) {
			FAgent agent = adminManageService.selectAgent(fid);
			modelAndView.addObject("agent", agent);
		}

		return modelAndView;
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	@RequestMapping("agent/saveAgent")
	public ReturnResult saveAgent(
			@RequestParam(value = "fname", required = true) String fname,
			@RequestParam(value = "fphone", required = true) String fphone,
			@RequestParam(value = "fdomain", required = true) String fdomain,
			@RequestParam(value = "fremark", required = true) String fremark
			) throws Exception {
		try{
			FAgent agent = new FAgent();
			agent.setFname(fname);
			agent.setFphone(fphone);
			agent.setFdomain(fdomain);
			agent.setFremark(fremark);
			agent.setFcreatetime(Utils.getTimestamp());
			
			if(adminManageService.insertAgent(agent)){
				return ReturnResult.SUCCESS("新增成功");
			}else{
				return ReturnResult.FAILUER("新增失败");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("网络超时");
		}
	}
	
	/**
	 * 更新
	 */
	@ResponseBody
	@RequestMapping("agent/updateAgent")
	public ReturnResult updateAgent(
			@RequestParam(value = "fid", required = true) Integer fid,
			@RequestParam(value = "fname", required = true) String fname,
			@RequestParam(value = "fphone", required = true) String fphone,
			@RequestParam(value = "fdomain", required = true) String fdomain,
			@RequestParam(value = "fremark", required = true) String fremark
			) throws Exception {
		try{
			FAgent agent = adminManageService.selectAgent(fid);
			if(agent==null){
				return ReturnResult.FAILUER("修改失败");
			}
			agent.setFname(fname);
			agent.setFphone(fphone);
			agent.setFdomain(fdomain);
			agent.setFremark(fremark);
			
			if(adminManageService.updateAgent(agent)){
				return ReturnResult.SUCCESS("修改成功");
			}else{
				return ReturnResult.FAILUER("修改失败");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("网络超时");
		}
	}
	
	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping("agent/deleteAgent")
	public ReturnResult deleteAgent(
			@RequestParam(value = "fid" , required = false) Integer fid
			) throws Exception {
		if(adminManageService.deleteAgent(fid)){
			return ReturnResult.SUCCESS("删除成功");
		} else{
			return ReturnResult.FAILUER("删除失败");
		}
	}
	
	/**
	 * 用户查找带回
	 */
	@RequestMapping("/agent/agentLookup")
	public ModelAndView agentLookup(
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("agent/agentLookup");
		// 定义查询条件
		Pagination<FAgent> pageParam = new Pagination<FAgent>(currentPage, Constant.adminPageSize);
		FAgent agent = new FAgent();
		// 参数判断
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		
		pageParam = adminManageService.selectAgentPageList(pageParam, agent);
		modelAndView.addObject("page", pageParam);
		return modelAndView;
	}
}
