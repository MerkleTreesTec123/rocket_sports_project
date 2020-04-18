package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogModifyCapitalOperation;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WalletCapitaloperationController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(WalletCapitaloperationController.class);
	
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private ScoreHelper scoreHelper;
	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;

	/**
	 * 待审核人民币充值列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/capitalInList")
	public ModelAndView capitalInList(
			@RequestParam(value = "capitalId", defaultValue = "0") Integer capitalId,
			@RequestParam(value = "ftype", defaultValue = "0") Integer ftype,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "limit", required = false, defaultValue = "0") Boolean limit,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/capitalInList");
		// 定义查询条件
		Pagination<FWalletCapitalOperationDTO> pageParam = new Pagination<FWalletCapitalOperationDTO>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 开始时间
		if (!StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}
		
		FWalletCapitalOperationDTO filterParam = new FWalletCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		filterParam.setFinouttype(CapitalOperationInOutTypeEnum.IN.getCode());
		// 充值状态
		status.add(CapitalOperationInStatus.WaitForComing);
		// 充值类型
		if (ftype > 0) {
			filterParam.setFtype(ftype);
			modelAndView.addObject("ftype", ftype);
		}
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 充值ID
		if (capitalId  > 0) {
			filterParam.setFid(Integer.valueOf(capitalId));
			modelAndView.addObject("capitalId", capitalId);
		}
		
		// 页面参数
		Map<Integer, String> ftypeMap = new LinkedHashMap<Integer, String>();
		ftypeMap.put(0, "全部");
		ftypeMap.put(CapitalOperationTypeEnum.RMB_IN, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.RMB_IN));
		ftypeMap.put(CapitalOperationTypeEnum.OLRMB_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.OLRMB_INT));
		ftypeMap.put(CapitalOperationTypeEnum.ALIPAY_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.ALIPAY_INT));
		ftypeMap.put(CapitalOperationTypeEnum.WECHAT_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.WECHAT_INT));
		modelAndView.addObject("ftypeMap", ftypeMap);
		// 查询
		Pagination<FWalletCapitalOperationDTO> pagination = adminUserCapitalService.selectWalletCapitalOperationList(
				pageParam, filterParam, status, limit, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("capitaloperationList", pagination);
		modelAndView.addObject("limit", limit);
		return modelAndView;
	}

	/**
	 * 审核人民币充值
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitalInAudit")
	@ResponseBody
	public ReturnResult capitalInAudit(
			@RequestParam(value = "uid", required = true) Integer fid,
			@RequestParam(value = "serialno", required = false, defaultValue = "") String serialno) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(fid);
		int status = capitalOperation.getFstatus();
		if (status != CapitalOperationInStatus.WaitForComing) {
			String status_s = CapitalOperationInStatus.getEnumString(CapitalOperationInStatus.WaitForComing);
			return ReturnResult.FAILUER("审核失败,只有状态为:" + status_s + "的充值记录才允许审核!");
		}
		// 计算实际到账金额
		BigDecimal amount = capitalOperation.getFamount();
		// 充值操作
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		capitalOperation.setFstatus(CapitalOperationInStatus.Come);
		capitalOperation.setFupdatetime(Utils.getTimestamp());
		capitalOperation.setFadminid(admin.getFid());
		capitalOperation.setFserialno(serialno);
		boolean isFirstRecharge = adminUserCapitalService.selectIsFirstCharge(capitalOperation.getFuid());
		// 数据更新
		boolean flag = false;
		try {			
			flag = adminUserCapitalService.updateWalletCapital(admin, capitalOperation, amount, true);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return ReturnResult.FAILUER("审核失败");
		}
		// 增加积分
		FUser fuser = adminUserService.selectById(capitalOperation.getFuid());
		scoreHelper.SendUserScore(fuser.getFid(), capitalOperation.getFamount(), ScoreTypeEnum.RECHARGE.getCode(), "充值RMB" + ":" + capitalOperation.getFamount() + "元");
		// 首次充值奖励
		if(isFirstRecharge) {
			scoreHelper.SendUserScore(fuser.getFid(), BigDecimal.ZERO, ScoreTypeEnum.FIRSTCHARGE.getCode(), ScoreTypeEnum.FIRSTCHARGE.getValue().toString());
		}
		// 发送短信
		if (fuser.getFistelephonebind()) {
			validateHelper.smsUserRecharge(fuser.getFareacode(), fuser.getFtelephone(), capitalOperation.getFplatform(),
					BusinessTypeEnum.SMS_CNY_RECHARGE.getCode(), amount);
		}
		if (amount.compareTo(new BigDecimal("100000")) >= 0) {
			String riskphone = redisHelper.getSystemArgs(ArgsConstant.AUDITRISKPHONE);
			String[] riskphones = riskphone.split("#");
			if (riskphones.length > 0) {
				for (String string : riskphones) {
					validateHelper.smsRiskManage(admin.getFname(), string, PlatformEnum.BC.getCode(),
							BusinessTypeEnum.SMS_AUDIT_RISKMANAGE.getCode(), capitalOperation.getFbank(),
							amount, "人民币");
				}
			}
		}
		return ReturnResult.SUCCESS("审核成功");
	}

	/**
	 * 通用页面跳转
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/goCapitaloperationJSP")
	public ModelAndView goCapitaloperationJSP(
			@RequestParam(value = "url", required=false) String url,
			@RequestParam(value = "uid", defaultValue="0") Integer fid) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (fid != null) {
			FWalletCapitalOperationDTO capitaloperation = adminUserCapitalService.selectById(fid);
			modelAndView.addObject("capitaloperation", capitaloperation);
		}

		Map<Integer, String> rechargeMap = new LinkedHashMap<>();
		rechargeMap.put(CapitalOperationTypeEnum.RMB_IN, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.RMB_IN));
		rechargeMap.put(CapitalOperationTypeEnum.OLRMB_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.OLRMB_INT));
		rechargeMap.put(CapitalOperationTypeEnum.ALIPAY_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.ALIPAY_INT));
		rechargeMap.put(CapitalOperationTypeEnum.WECHAT_INT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.WECHAT_INT));

		Map<Integer, String> withdrawMap = new LinkedHashMap<>();
		withdrawMap.put(CapitalOperationTypeEnum.RMB_OUT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.RMB_OUT));
		withdrawMap.put(CapitalOperationTypeEnum.OLRMB_OUT, CapitalOperationTypeEnum.getEnumString(CapitalOperationTypeEnum.OLRMB_OUT));

		modelAndView.addObject("rechargeMap", rechargeMap);
		modelAndView.addObject("withdrawMap", withdrawMap);
		return modelAndView;
	}

	/**
	 * 修改充值金额
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/updateCapitalAmount")
	@ResponseBody
	public ReturnResult updateCapitalAmount(
			@RequestParam(value = "capitaloperation.famount", required=true) BigDecimal modifyMoney,
			@RequestParam(value = "capitaloperation.ftype", required=true) Integer ftype,
			@RequestParam(value = "capitaloperation.fid", required=true) Integer fid) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FWalletCapitalOperationDTO capitaloperation = adminUserCapitalService.selectById(fid);
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		// 判断金额是否允许修改
		if (MathUtils.toScaleNum(capitaloperation.getFamount(), MathUtils.INTEGER_SCALE).compareTo(modifyMoney) <= 0
			&& MathUtils.toScaleNumUp(capitaloperation.getFamount(), MathUtils.INTEGER_SCALE).compareTo(modifyMoney) > 0) {
			FLogModifyCapitalOperation transientInstance = new FLogModifyCapitalOperation();
			transientInstance.setFaccount(capitaloperation.getFaccount());
			transientInstance.setFamount(capitaloperation.getFamount());
			transientInstance.setFbank(capitaloperation.getFbank());
			transientInstance.setFmodifyamount(modifyMoney);
			transientInstance.setFupdatetime(Utils.getTimestamp());
			transientInstance.setFpayee(capitaloperation.getFpayee());
			transientInstance.setFphone(capitaloperation.getFphone());
			transientInstance.setFadminid(admin.getFid());

			capitaloperation.setFamount(modifyMoney);
			capitaloperation.setFtype(ftype);
			adminUserCapitalService.updateModifyCapital(capitaloperation, transientInstance);
			// 修改成功
			return ReturnResult.SUCCESS("更新成功");
		} else {
			return ReturnResult.FAILUER("修改失败，您修改的金额必须大于等于"
					+ MathUtils.toScaleNum(capitaloperation.getFamount(), MathUtils.INTEGER_SCALE) + "元，并且小于"
					+ MathUtils.toScaleNumUp(capitaloperation.getFamount(), MathUtils.INTEGER_SCALE) + "元");
		}
	}

	/**
	 * 取消充值
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/capitalInCancel")
	@ResponseBody
	public ReturnResult capitalInCancel(
			@RequestParam(value = "uid", required=true) Integer fid) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(fid);
		int status = capitalOperation.getFstatus();
		if (status == CapitalOperationInStatus.Come || status == CapitalOperationInStatus.Invalidate) {
			return ReturnResult.FAILUER("取消失败,该笔充值已充值成功");
		}
		capitalOperation.setFstatus(CapitalOperationInStatus.Invalidate);
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		capitalOperation.setFadminid(admin.getFid());
		adminUserCapitalService.updateWalletCapital(capitalOperation);
		return ReturnResult.SUCCESS("取消成功");
	}

	/**
	 * 批量取消充值
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/capitalInCancelAll")
	@ResponseBody
	public ReturnResult capitalInCancelAll(
			@RequestParam(value = "ids", required=true) String fids) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();

		String[] idString = fids.split(",");
		// 充值操作
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		for (int i = 0; i < idString.length; i++) {
			FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(Integer.valueOf(idString[i]));
			int status = capitalOperation.getFstatus();
			if (status == CapitalOperationInStatus.Come || status == CapitalOperationInStatus.Invalidate) {
				return ReturnResult.FAILUER("取消失败,该笔充值已充值成功");
			}
			capitalOperation.setFstatus(CapitalOperationInStatus.Invalidate);
			capitalOperation.setFadminid(admin.getFid());
			adminUserCapitalService.updateWalletCapital(capitalOperation);
		}
		return ReturnResult.SUCCESS("全部取消成功");

	}

	/**
	 * 恢复锁定状态的充值记录到待审核状态
	 */
	@ResponseBody
	@RequestMapping("/admin/capitalInRestore")
	public ReturnResult capitalInRestore(@RequestParam(value = "uid", required=true) Integer fid) throws BCException {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(fid);
		int status = capitalOperation.getFstatus();
		if (status != CapitalOperationInStatus.Expired) {
			return ReturnResult.FAILUER("恢复失败,只有状态为:取消的充值记录才允许恢复!！");
		}
		capitalOperation.setFstatus(CapitalOperationInStatus.WaitForComing);
		capitalOperation.setFupdatetime(Utils.getTimestamp());
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		capitalOperation.setFadminid(admin.getFid());
		adminUserCapitalService.updateWalletCapital(capitalOperation);
		return ReturnResult.SUCCESS("恢复成功");

	}

	/**
	 * 待审核人民币提现列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/capitalOutList")
	public ModelAndView capitalOutList(
			@RequestParam(value = "capitalId", defaultValue = "0") Integer capitalId,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keyWord,
			@RequestParam(value = "ftype",required=false) Integer type,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField",defaultValue="fupdatetime") String orderField,
			@RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/capitalOutList");
		
		// 定义查询条件
		Pagination<FWalletCapitalOperationDTO> pageParam = new Pagination<FWalletCapitalOperationDTO>(currentPage, numPerPage);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 开始时间
		if (!StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}
		
		FWalletCapitalOperationDTO filterParam = new FWalletCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		status.add(CapitalOperationOutStatus.WaitForOperation);
		status.add(CapitalOperationOutStatus.OperationLock);
		filterParam.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}

		// 充值ID
		if (capitalId  > 0) {
			filterParam.setFid(capitalId);
			modelAndView.addObject("capitalId", capitalId);
		}
		//充值卡类型
		if(type != null && type > 0){
			filterParam.setFtype(type);
		}
		modelAndView.addObject("ftype", type);
			
		// 查询
		Pagination<FWalletCapitalOperationDTO> pagination = adminUserCapitalService.selectWalletCapitalOperationList(
				pageParam, filterParam, status, null, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("capitaloperationList", pagination);
		return modelAndView;
	}

	/**
	 * 人民币提现操作
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitalOutAudit")
	@ResponseBody
	public ReturnResult capitalOutAudit(
			@RequestParam(value = "uid", required=true) Integer fid,
			@RequestParam(value = "type", required=true) Integer type,
			@RequestParam(value = "online", required=true,defaultValue = "0") Integer online) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(fid);
		int status = capitalOperation.getFstatus();
		if (status == CapitalOperationOutStatus.OnLineLock && !type.equals(5)) {
			return ReturnResult.FAILUER("审核失败,提现数据已提交至第三方接口，不能重复审核!");
		}
		switch (type) {
		case 1:
			if (status != CapitalOperationOutStatus.OperationLock) {
				String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationLock);
				return ReturnResult.FAILUER("审核失败,只有状态为:‘" + status_s + "’的提现记录才允许审核!");
			}
			break;
		case 2:
			if (status != CapitalOperationOutStatus.WaitForOperation) {
				String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.WaitForOperation);
				return ReturnResult.FAILUER("锁定失败,只有状态为:‘" + status_s + "’的提现记录才允许锁定!");
			}
			break;
		case 3:
			if (status != CapitalOperationOutStatus.OperationLock) {
				String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationLock);
				return ReturnResult.FAILUER("取消锁定失败,只有状态为:‘" + status_s + "’的提现记录才允许取消锁定!");
			}
			break;
		case 4:
			if (status == CapitalOperationOutStatus.Cancel || status == CapitalOperationOutStatus.OperationSuccess) {
				return ReturnResult.FAILUER("取消失败!");
			}
			break;
		case 5:
			if (status != CapitalOperationOutStatus.OnLineLock) {
				return ReturnResult.FAILUER("恢复提现失败,只有状态为“锁定”才可以恢复!");
			}
			break;
		default:
			return ReturnResult.FAILUER("非法提交！");
		}
		BigDecimal amount = capitalOperation.getFamount();
		BigDecimal frees = capitalOperation.getFfees();
		BigDecimal totalAmt = MathUtils.add(amount, frees);
		// 充值操作
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		capitalOperation.setFupdatetime(Utils.getTimestamp());
		capitalOperation.setFadminid(admin.getFid());
		// 钱包操作//1审核,2锁定,3取消锁定,4取消提现
		String tips = "";

		switch (type) {
		case 1:
			capitalOperation.setFstatus(CapitalOperationOutStatus.OperationSuccess);
			tips = "审核";
			break;
		case 2:
			capitalOperation.setFstatus(CapitalOperationOutStatus.OperationLock);
			tips = "锁定";
			break;
		case 3:
			capitalOperation.setFstatus(CapitalOperationOutStatus.WaitForOperation);
			tips = "取消锁定";
			break;
		case 4:
			capitalOperation.setFstatus(CapitalOperationOutStatus.Cancel);
			tips = "取消";
			break;
		case 5:
			capitalOperation.setFstatus(CapitalOperationOutStatus.OperationLock);
			tips = "恢复提现";
			break;
		}
		boolean flag = false;
		try {
			adminUserCapitalService.updateWalletCapital(admin, capitalOperation, totalAmt, false);
			flag = true;
		} catch (Exception e) {
			tips = e.getMessage() + "," + tips;
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return ReturnResult.FAILUER(tips + "失败");
		}
		return ReturnResult.SUCCESS(tips + "成功");
	}

	/**
	 * 提现批量锁定
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitalOutAuditAll")
	@ResponseBody
	public ReturnResult capitalOutAuditAll() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		String ids = request.getParameter("ids");
		String[] idString = ids.split(",");
		// 充值操作
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		for (int i = 0; i < idString.length; i++) {
			int id = Integer.parseInt(idString[i]);
			FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(id);
			int status = capitalOperation.getFstatus();
			if (status != CapitalOperationOutStatus.WaitForOperation) {
				String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.WaitForOperation);
				return ReturnResult.FAILUER("锁定失败,只有状态为:‘" + status_s + "’的提现记录才允许锁定!");
			}
			capitalOperation.setFupdatetime(Utils.getTimestamp());
			capitalOperation.setFadminid(admin.getFid());
			capitalOperation.setFstatus(CapitalOperationOutStatus.OperationLock);
			adminUserCapitalService.updateWalletCapital(capitalOperation);
		}
		return ReturnResult.SUCCESS("批量锁定成功!");
	}

	/**
	 * 查看会员信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/viewUser")
	public ModelAndView viewUser() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/viewUser");
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		String fid = request.getParameter("cid");
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(Integer.valueOf(fid));
		FUser fuser = adminUserService.selectById(capitalOperation.getFuid());
		modelAndView.addObject("fuser", ModelMapperUtils.mapper(fuser, FUserDTO.class));
		return modelAndView;
	}

	/**
	 * 查看会员钱包信息
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/viewUserWallet")
	public ModelAndView viewUserWallet() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		String fid = request.getParameter("cid");
		FWalletCapitalOperationDTO capitalOperation = adminUserCapitalService.selectById(Integer.valueOf(fid));
		UserCoinWallet userWallet = adminUserCapitalService.selectUserWallet(capitalOperation.getFuid(), capitalOperation.getFcoinid());
		modelAndView.addObject("userWallet", userWallet);
		modelAndView.setViewName("capital/viewUserWallet");
		return modelAndView;
	}

	/**
	 * 充值提现记录
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/capitaloperationList")
	public ModelAndView Index(
			@RequestParam(value = "capitalId", defaultValue = "0") Integer capitalId,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "orderType", required = false) Integer orderType,
			@RequestParam(value = "fstatus", defaultValue = "0") String fstatus,
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keyWord,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField",defaultValue="fupdatetime") String orderField,
			@RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/capitaloperationList");
		// 定义查询条件
		Pagination<FWalletCapitalOperationDTO> pageParam = new Pagination<FWalletCapitalOperationDTO>(currentPage, numPerPage);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);
		
		FWalletCapitalOperationDTO filterParam = new FWalletCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 类型-充值or提现
		if (type != null) {
			if (type != 0) {
				if (type == 1) {
					filterParam.setFinouttype(CapitalOperationInOutTypeEnum.IN.getCode());
				} else {
					filterParam.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
				}
			}
			modelAndView.addObject("type", type);
		}
		// 开始时间
		if (!StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}
		// 充值ID
		if (capitalId  > 0) {
			filterParam.setFid(Integer.valueOf(capitalId));
			modelAndView.addObject("capitalId", capitalId);
		}
		if (orderType != null && orderType  > 0) {
			filterParam.setFserialno(orderType.toString());
			modelAndView.addObject("orderType", orderType);
		}
		// 状态
		if (!fstatus.equals("0")) {
			if (fstatus.indexOf("充值") != -1) {
				status.add(Integer.valueOf(fstatus.replace("充值-", "")));
			} else if (fstatus.indexOf("提现") != -1) {
				status.add(Integer.valueOf(fstatus.replace("提现-", "")));
			}
		}
		modelAndView.addObject("fstatus", fstatus);	
			
		// 页面参数
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("0", "全部");
		if (type != null) {
			if (type == 1) {
				statusMap.put("充值-" + CapitalOperationInStatus.Come, "充值-" + CapitalOperationInStatus.getEnumString(CapitalOperationInStatus.Come));
				statusMap.put("充值-" + CapitalOperationInStatus.Invalidate, "充值-" + CapitalOperationInStatus.getEnumString(CapitalOperationInStatus.Invalidate));
				statusMap.put("充值-" + CapitalOperationInStatus.NoGiven, "充值-" + CapitalOperationInStatus.getEnumString(CapitalOperationInStatus.NoGiven));
				statusMap.put("充值-" + CapitalOperationInStatus.WaitForComing, "充值-" + CapitalOperationInStatus.getEnumString(CapitalOperationInStatus.WaitForComing));
			} else {
				statusMap.put("提现-" + CapitalOperationOutStatus.Cancel, "提现-" + CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.Cancel));
				statusMap.put("提现-" + CapitalOperationOutStatus.OperationLock, "提现-" + CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationLock));
				statusMap.put("提现-" + CapitalOperationOutStatus.OperationSuccess, "提现-" + CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationSuccess));
				statusMap.put("提现-" + CapitalOperationOutStatus.WaitForOperation, "提现-" + CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.WaitForOperation));
			}
		}
		modelAndView.addObject("statusMap", statusMap);
		// 查询
		Pagination<FWalletCapitalOperationDTO> pagination = adminUserCapitalService.selectWalletCapitalOperationList(
				pageParam, filterParam, status, null, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("capitaloperationList", pagination);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportFiled {
		UID, 会员登录名, 会员昵称, 会员真实姓名, 类型, 状态, 金额, 手续费, 银行, 开户行地址, 收款帐号, 手机号码, 备注, 创建时间, 最后修改时间, 收款银行, 卡号, 审核人;
	}
	
	
	// 导出列名
	private static enum ExportFiledRecharge {
		UID, 会员登录名, 会员昵称, 会员真实姓名, 类型, 状态, 金额, 手续费, 收款银行, 卡号, 开户行地址, 备注, 创建时间, 最后修改时间,  审核人;
	}

	// 导出列名
	private static enum ExportFiledZsjfRecharge {
		UID, 手机号, 邮箱地址, 流水号, 状态, 金额, 手续费, 汇款银行, 汇款帐号, 汇款人, 备注, 创建时间, 最后修改时间;
	}

	/**
	 * 导出提现记录
	 * 
	 * @para
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitalOutExport")
	@ResponseBody
	public ReturnResult capitalOutExport(
			@RequestParam(value = "capitalId", defaultValue = "0") Integer capitalId,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keyWord,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField",defaultValue="fupdatetime") String orderField,
			@RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection) throws Exception {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=capitalOutList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}

		// 定义查询条件
		Pagination<FWalletCapitalOperationDTO> pageParam = new Pagination<FWalletCapitalOperationDTO>(currentPage, 100000);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 日期
		if (!StringUtils.isEmpty(logDate)) {
			pageParam.setBegindate(logDate);
		}else{
			pageParam.setBegindate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 00:00:00");
		}

		// 日期
		if (!StringUtils.isEmpty(endDate)) {
			pageParam.setEnddate(endDate);
		}else{
			pageParam.setEnddate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 23:59:59");
		}

		
		FWalletCapitalOperationDTO filterParam = new FWalletCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		status.add(CapitalOperationOutStatus.WaitForOperation);
		status.add(CapitalOperationOutStatus.OperationLock);
		filterParam.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
		}

		// 充值ID
		if (capitalId  > 0) {
			filterParam.setFid(capitalId);
		}
			
		// 查询
		pageParam = adminUserCapitalService.selectWalletCapitalOperationList(
				pageParam, filterParam, status, null, isvip6.equals("on"));
		Collection<FWalletCapitalOperationDTO> capitalOperationList = pageParam.getData();
		for (FWalletCapitalOperationDTO capitalOperation : capitalOperationList) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case UID:
					e.setCell(filed.ordinal(), capitalOperation.getFuid());
					break;
				case 会员登录名:
					e.setCell(filed.ordinal(), capitalOperation.getFloginname());
					break;
				case 会员昵称:
					e.setCell(filed.ordinal(), capitalOperation.getFnickname());
					break;
				case 会员真实姓名:
					e.setCell(filed.ordinal(), capitalOperation.getFrealname());
					break;
				case 类型:
					e.setCell(filed.ordinal(), capitalOperation.getFtype_s());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 金额:
					e.setCell(filed.ordinal(), capitalOperation.getFamount().doubleValue());
					break;
				case 手续费:
					e.setCell(filed.ordinal(), capitalOperation.getFfees().doubleValue());
					break;
				case 银行:
					e.setCell(filed.ordinal(), capitalOperation.getFbank());
					break;
				case 开户行地址:
					e.setCell(filed.ordinal(), capitalOperation.getFaddress());
					break;
				case 收款帐号:
					e.setCell(filed.ordinal(), capitalOperation.getFaccount());
					break;
				case 手机号码:
					e.setCell(filed.ordinal(), capitalOperation.getFphone());
					break;
				case 备注:
					e.setCell(filed.ordinal(), capitalOperation.getFid());
					break;
				case 创建时间:
					e.setCell(filed.ordinal(), capitalOperation.getFcreatetime());
					break;
				case 最后修改时间:
					e.setCell(filed.ordinal(), capitalOperation.getFupdatetime());
					break;
				default:
					break;
				}
			}
		}
		e.exportXls(response);
		return ReturnResult.SUCCESS("导出成功");
	}

	/**
	 * 充值提现列表导出
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/capitaloperationExport")
	@ResponseBody
	public ReturnResult capitaloperationExport(
			@RequestParam(value = "capitalId", defaultValue = "0") Integer capitalId,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "orderType", required = false) Integer orderType,
			@RequestParam(value = "fstatus", defaultValue = "0") String fstatus,
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keyWord,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField",defaultValue="fupdatetime") String orderField,
			@RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection) throws Exception {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=capitaloperationList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (ExportFiledRecharge filed : ExportFiledRecharge.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		
		Pagination<FWalletCapitalOperationDTO> pageParam = new Pagination<FWalletCapitalOperationDTO>(currentPage, 100000);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 日期
		if (!StringUtils.isEmpty(logDate)) {
			pageParam.setBegindate(logDate);
		}else{
			pageParam.setBegindate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 00:00:00");
		}

		// 日期
		if (!StringUtils.isEmpty(endDate)) {
			pageParam.setEnddate(endDate);
		}else{
			pageParam.setEnddate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 23:59:59");
		}
		
		FWalletCapitalOperationDTO filterParam = new FWalletCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
		}
		// 类型-充值or提现
		if (type != null) {
			if (type != 0) {
				if (type == 1) {
					filterParam.setFinouttype(CapitalOperationInOutTypeEnum.IN.getCode());
				} else {
					filterParam.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
				}
			}
		}
		// 充值ID
		if (capitalId  > 0) {
			filterParam.setFid(Integer.valueOf(capitalId));
		}
		if (orderType != null && orderType  > 0) {
			filterParam.setFserialno(orderType.toString());
		}
		// 状态
		if (!fstatus.equals("0")) {
			if (fstatus.indexOf("充值") != -1) {
				status.add(Integer.valueOf(fstatus.replace("充值-", "")));
			} else if (fstatus.indexOf("提现") != -1) {
				status.add(Integer.valueOf(fstatus.replace("提现-", "")));
			}
		}
			
		// 查询
		pageParam = adminUserCapitalService.selectWalletCapitalOperationList(
				pageParam, filterParam, status, null, isvip6.equals("on"));
		Collection<FWalletCapitalOperationDTO> capitalOperationList = pageParam.getData();
		for (FWalletCapitalOperationDTO capitalOperation : capitalOperationList) {
			e.createRow(rowIndex++);
			for (ExportFiledRecharge filed : ExportFiledRecharge.values()) {
				switch (filed) {
				case UID:
					e.setCell(filed.ordinal(), capitalOperation.getFuid());
					break;
				case 会员登录名:
					e.setCell(filed.ordinal(), capitalOperation.getFloginname());
					break;
				case 会员昵称:
					e.setCell(filed.ordinal(), capitalOperation.getFnickname());
					break;
				case 会员真实姓名:
					e.setCell(filed.ordinal(), capitalOperation.getFrealname());
					break;
				case 收款银行:
					e.setCell(filed.ordinal(), capitalOperation.getFbank());
					break;
				case 开户行地址:
					e.setCell(filed.ordinal(), capitalOperation.getFaddress());
					break;
				case 卡号:
					e.setCell(filed.ordinal(), capitalOperation.getFaccount());
					break;
				case 类型:
					e.setCell(filed.ordinal(), capitalOperation.getFtype_s());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 金额:
					e.setCell(filed.ordinal(), capitalOperation.getFamount().doubleValue());
					break;
				case 手续费:
					e.setCell(filed.ordinal(), capitalOperation.getFfees().doubleValue());
					break;
				case 备注:
					e.setCell(filed.ordinal(), capitalOperation.getFid());
					break;
				case 创建时间:
					e.setCell(filed.ordinal(), capitalOperation.getFcreatetime());
					break;
				case 最后修改时间:
					e.setCell(filed.ordinal(), capitalOperation.getFupdatetime());
					break;
				case 审核人:
					e.setCell(filed.ordinal(), capitalOperation.getFadminname());
					break;
				default:
					break;
				}
			}
		}
		
		e.exportXls(response);
		return ReturnResult.SUCCESS("导出成功");
	}

}
