package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FBeautiful;
import com.qkwl.common.rpc.admin.IAdminBeautifulService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller
public class BeautifulController extends WebBaseController {
	@Autowired
	private IAdminBeautifulService adminBeautifulService;
	
	/**
	 * 列表加载
	 */
	@RequestMapping("/admin/beautifulList")
	public ModelAndView beautifulList(
			@RequestParam(value = "keywords", required = false) String keyWord, 
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/beautifulList");
		// 定义查询条件
		Pagination<FBeautiful> pageParam = new Pagination<FBeautiful>(currentPage, Constant.adminPageSize);
		// 参数判断
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		pageParam.setOrderField("fcreatetime");
		pageParam.setOrderDirection("desc");
		pageParam = adminBeautifulService.selectBeautifulPageList(pageParam, false);
		modelAndView.addObject("dataList", pageParam);
		return modelAndView;
	}
	
	/**
	 * 查询跳转
	 */
	@RequestMapping("admin/goBeautifulJSP")
	public ModelAndView goBeautifulJSP(
			@RequestParam(required=false) String url,
			@RequestParam(required=false) Integer fid
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		
		if (fid != null) {
			FBeautiful beautiful = adminBeautifulService.selectBeautiful(fid);
			modelAndView.addObject("beautiful", beautiful);
		}

		return modelAndView;
	}
	
	/**
	 * 新增
	 */
	@RequestMapping("admin/saveBeautiful")
	@ResponseBody
	public ReturnResult saveBeautiful(
			@RequestParam(value = "fbid", required = true) Integer fbid,
			@RequestParam(value = "fuid", required = true) Integer fuid
			) throws Exception {
		try{
			FBeautiful beautiful = new FBeautiful();
			beautiful.setFbid(fbid);
			beautiful.setFuid(fuid);
			beautiful.setFcreatetime(new Date());
			beautiful.setVersion(0);
			
			if(adminBeautifulService.insertBeautiful(beautiful)){				
				return ReturnResult.SUCCESS("新增成功!");
			}else{
				return ReturnResult.FAILUER("新增失败!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("网络超时!");
		}
	}
	
	/**
	 * 更新
	 */
	@RequestMapping("admin/updateBeautiful")
	@ResponseBody
	public ReturnResult updateBeautiful(
			@RequestParam(value = "fid", required = true) Integer fid,
			@RequestParam(value = "fbid", required = true) Integer fbid,
			@RequestParam(value = "fuid", required = true) Integer fuid
			) throws Exception {
		try{
			FBeautiful beautiful = adminBeautifulService.selectBeautiful(fid);
			if(beautiful==null){
				return ReturnResult.FAILUER("修改失败!");
			}
			if(beautiful.getFupdatetime()!=null){
				return ReturnResult.FAILUER("该靓号已使用，禁止修改!");
			}
			beautiful.setFuid(fuid);
			beautiful.setFbid(fbid);
			
			if(adminBeautifulService.updateBeautiful(beautiful)){				
				return ReturnResult.SUCCESS("修改成功!");
			}else{
				return ReturnResult.FAILUER("修改失败!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("网络超时!");
		}
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("admin/deleteBeautiful")
	@ResponseBody
	public ReturnResult deleteBeautiful(
			@RequestParam(value = "fid" , required = false) Integer fid
			) throws Exception {
		FBeautiful beautiful = adminBeautifulService.selectBeautiful(fid);
		if(beautiful.getFupdatetime()!=null){
			return ReturnResult.FAILUER("该靓号已使用，禁止删除！");
		}
		if(adminBeautifulService.deleteBeautiful(fid)){
			return ReturnResult.SUCCESS("删除成功！");
		} else{
			return ReturnResult.FAILUER("删除失败！");
		}
	}

	/**
	 * 查找带回列表加载
	 */
	@RequestMapping("/user/lookBeautifulList")
	public ModelAndView lookBeautifulList(
			@RequestParam(value = "keywords", required = false) String keyWord, 
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/lookBeautifulList");
		// 定义查询条件
		Pagination<FBeautiful> pageParam = new Pagination<FBeautiful>(currentPage, Constant.adminPageSize);
		// 参数判断
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		pageParam.setOrderField("fcreatetime");
		pageParam.setOrderDirection("desc");
		pageParam = adminBeautifulService.selectBeautifulPageList(pageParam, true);
		modelAndView.addObject("page", pageParam);
		return modelAndView;
	}
}
