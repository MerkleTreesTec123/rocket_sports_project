package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.SystemBankInfoEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SystemBankInfoController extends WebBaseController {

	@Autowired
	private IAdminSettingService adminSettingService;
	
	@RequestMapping("/admin/systemBankList")
	public ModelAndView Index(
			@RequestParam(value="pageNum", required=false,defaultValue="1") Integer currentPage,
			@RequestParam(required=false) String keywords,
			@RequestParam(required=false,defaultValue="fcreatetime") String orderField,
			@RequestParam(required=false,defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("system/systemBankList");
		
		Pagination<FSystemBankinfoRecharge> page=new Pagination<FSystemBankinfoRecharge>(currentPage, Constant.adminPageSize);
		page.setOrderDirection(orderDirection);
		page.setOrderField(orderField);
		page.setKeyword(keywords);
		
		FSystemBankinfoRecharge systemBankinfoRecharge=new FSystemBankinfoRecharge();
		page=adminSettingService.selectBankPageList(page,systemBankinfoRecharge);
		
		if(keywords != null && !"".equals(keywords)){
			modelAndView.addObject("keywords", keywords);
		}

		modelAndView.addObject("systembankList", page);
		return modelAndView;
	}

	@RequestMapping("admin/goSystemBankJSP")
	public ModelAndView goSystemBankJSP(
			@RequestParam(required=false,defaultValue="1") String url,
			@RequestParam(required=false,defaultValue="1") Integer fid
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (fid != null) {
			FSystemBankinfoRecharge systemBankinfoRecharge = this.adminSettingService.selectBankInfoById(fid);
			modelAndView.addObject("systemBank", systemBankinfoRecharge);
		}
		return modelAndView;
	}

	@RequestMapping("admin/modifySystemBank")
	@ResponseBody
	public ReturnResult modifySystemBank(
			@RequestParam(value="systemBank.fbankAddress",required=false,defaultValue="1") String fbankAddress,
			@RequestParam(value="systemBank.fbankNumber",required=false,defaultValue="1") String fbankNumber,
			@RequestParam(value="systemBank.fId",required=false,defaultValue="1") Integer fid,
			@RequestParam(value="systemBank.fbankName",required=false,defaultValue="1") String fbankName,
			@RequestParam(value="systemBank.fownerName",required=false,defaultValue="1") String fownerName,
			@RequestParam(value="systemBank.FSortId",required=false,defaultValue="1") Integer FSortId
			) throws Exception {
		FSystemBankinfoRecharge systemBankinfoRecharge = this.adminSettingService.selectBankInfoById(fid);

		if (systemBankinfoRecharge == null) {
			return ReturnResult.FAILUER("修改失败，银行信息不存在！");
		} else {
			systemBankinfoRecharge.setFbanknumber(fbankNumber);
			systemBankinfoRecharge.setFbankaddress(fbankAddress);
			systemBankinfoRecharge.setFbankname(fbankName);
			systemBankinfoRecharge.setFownername(fownerName);
			systemBankinfoRecharge.setFsortid(FSortId);
			boolean i = adminSettingService.updateBankInfo(systemBankinfoRecharge);

			if(i){
				return ReturnResult.SUCCESS("修改成功！");
			}
			return ReturnResult.FAILUER("修改失败！");
		}
	}

	@RequestMapping("admin/saveSystemBank")
	@ResponseBody
	public ReturnResult saveSystemBank(
			@RequestParam(value="systemBank.fbankAddress",required=false,defaultValue="1") String fbankAddress,
			@RequestParam(value="systemBank.fbankName",required=false,defaultValue="1") String fbankName,
			@RequestParam(value="systemBank.fbankNumber",required=false,defaultValue="1") String fbankNumber,
			@RequestParam(value="systemBank.fownerName",required=false,defaultValue="1") String fownerName,
			@RequestParam(value="systemBank.fBankType",required=false,defaultValue="1") Integer fBankType,
			@RequestParam(value="systemBank.FSortId",required=false,defaultValue="1") Integer FSortId
			) throws Exception {
		FSystemBankinfoRecharge bankInfo = new FSystemBankinfoRecharge();
		bankInfo.setFbankaddress(fbankAddress);
		bankInfo.setFbankname(fbankName);
		bankInfo.setFbanknumber(fbankNumber);
		bankInfo.setFownername(fownerName);
		bankInfo.setFbanktype(fBankType);
		bankInfo.setFusetype(fBankType);
		bankInfo.setFsortid(FSortId);
		bankInfo.setFcreatetime(Utils.getTimestamp());
		bankInfo.setVersion(0);
		bankInfo.setFstatus(SystemBankInfoEnum.NORMAL_VALUE);
		
		boolean i = this.adminSettingService.insertBank(bankInfo);
		if(i){
			return ReturnResult.SUCCESS("新增成功！");
		}
		return ReturnResult.FAILUER("新增失败！");
	}

	@RequestMapping("admin/forbbinSystemBank")
	@ResponseBody
	public ReturnResult forbbinSystemBank(
			@RequestParam(required=false,defaultValue="1") Integer fid,
			@RequestParam(required=false,defaultValue="1") Integer status
			) throws Exception {
		FSystemBankinfoRecharge systemBankinfoRecharge = this.adminSettingService.selectBankInfoById(fid);
		String msg = "";
		if (status == 1) {
			systemBankinfoRecharge.setFstatus(SystemBankInfoEnum.ABNORMAL);
			msg = "禁用";
		} else {
			systemBankinfoRecharge.setFstatus(SystemBankInfoEnum.NORMAL_VALUE);
			msg = "解除禁用";
		}

		boolean i = adminSettingService.updateBankInfo(systemBankinfoRecharge);

		if(i){
			return ReturnResult.SUCCESS(msg+"成功！");
		}

		return ReturnResult.FAILUER(msg+"失败！");
	}

}
