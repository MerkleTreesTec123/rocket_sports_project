package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.admin.layui.base.WebBaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class AddressCheckController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(AddressCheckController.class);
	
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private RedisHelper redisHelper;

	/**
	 *	通过币地址查找用户
 	 */
	@RequestMapping("/admin/findFuserByAddress")
	public ModelAndView Index(
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "fuid", required = false) Integer fuid,
			@RequestParam(value = "ftype", defaultValue = "0") Integer ftype,
			@RequestParam(value = "orderField", required=false,defaultValue="fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", required=false,defaultValue="desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/findFuserByAddress");
		
		FUserVirtualAddressDTO userVirtualAddress = new FUserVirtualAddressDTO();
		FUserVirtualAddressWithdrawDTO userVirtualAddressWithdraw = new FUserVirtualAddressWithdrawDTO();
		
		if(!StringUtils.isEmpty(keywords)){
			userVirtualAddress.setFadderess(keywords);
			userVirtualAddressWithdraw.setFadderess(keywords);
			modelAndView.addObject("keywords", keywords);
		}
		if (fuid != null) {
			try {
				userVirtualAddress.setFuid(fuid);
				userVirtualAddressWithdraw.setFuid(fuid);
				modelAndView.addObject("fuid", fuid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (ftype >= 0) {
			userVirtualAddress.setFcoinid(ftype);
			userVirtualAddressWithdraw.setFcoinid(ftype);
		}
		modelAndView.addObject("ftype", ftype);
		
		
		Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
		typeMap.put(-1, "全部");
		modelAndView.addObject("typeMap", typeMap);
		
		if(StringUtils.isEmpty(keywords) && fuid == null && ftype == 0){
			return modelAndView;
		}
		
		List<FUserVirtualAddressDTO> vaddress = adminUserService.selectVirtualAddressByUser(userVirtualAddress);
		List<FUserVirtualAddressWithdrawDTO> vwaddress = adminUserService.selectVirtualWithdrawAddressByUser(userVirtualAddressWithdraw);
		modelAndView.addObject("vaddress", vaddress);
		modelAndView.addObject("vwaddress", vwaddress);
		// 总数量
		modelAndView.addObject("totalCount", 0);	
		return modelAndView;
	}
	
	
	/**
	 *	查看会员信息
	 */
	@RequestMapping("/admin/viewUserByAddress")
	public ModelAndView viewUser(
			@RequestParam(value = "cid", required = true) Integer fuid) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/viewUser");
		FUser fuser = adminUserService.selectById(fuid);
		modelAndView.addObject("fuser", ModelMapperUtils.mapper(fuser, FUserDTO.class));
		return modelAndView;
	}
}
