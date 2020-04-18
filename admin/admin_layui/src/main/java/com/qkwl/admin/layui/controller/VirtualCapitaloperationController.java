package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class VirtualCapitaloperationController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(VirtualCapitaloperationController.class);
	
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminUserService adminUserService;
	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;

	/**
	 * 虚拟币充值提现列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitaloperationList")
	public ModelAndView Index(
			@RequestParam(value = "fcoinid", required = false) Integer fcoinid,
			@RequestParam(value = "type", defaultValue = "0") Integer ftype,
			@RequestParam(value = "status", defaultValue =  "-1") Integer status,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualCapitaloperationList");
		
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> statusList = new ArrayList<>();

		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 虚拟币ID
		if (fcoinid !=null && fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
			modelAndView.addObject("fcoinid", fcoinid);
		} 
		
		// 类型提现or充值
		if (ftype > 0) {
			filterParam.setFtype(ftype);
			modelAndView.addObject("type", ftype);
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
		if (status >= 0 ){
			statusList.add(status);
			modelAndView.addObject("status", status);
		}

		// 页面参数
		Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
		coinMap.put(0, "全部");
		modelAndView.addObject("coinMap", coinMap);

		// 页面参数
		List<SystemCoinType> type = redisHelper.getCoinTypeList();
		Map<Integer, String> urlMap = new HashMap<Integer, String>();
		for (SystemCoinType coin : type) {
			urlMap.put(coin.getId(), coin.getExplorerUrl());
		}
		modelAndView.addObject("urlMap", urlMap);

		Map<Integer, String> statusMap = new HashMap<>();
		statusMap.put(-1, "全部");
		if(ftype == 1){
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_0,
					VirtualCapitalOperationInStatusEnum.getEnumString(VirtualCapitalOperationInStatusEnum.WAIT_0));
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_1,
					VirtualCapitalOperationInStatusEnum.getEnumString(VirtualCapitalOperationInStatusEnum.WAIT_1));
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_2,
					VirtualCapitalOperationInStatusEnum.getEnumString(VirtualCapitalOperationInStatusEnum.WAIT_2));
			statusMap.put(VirtualCapitalOperationInStatusEnum.SUCCESS,
					VirtualCapitalOperationInStatusEnum.getEnumString(VirtualCapitalOperationInStatusEnum.SUCCESS));
		}else{
			statusMap.put(VirtualCapitalOperationOutStatusEnum.WaitForOperation,
					VirtualCapitalOperationOutStatusEnum.getEnumString(VirtualCapitalOperationOutStatusEnum.WaitForOperation));
			statusMap.put(VirtualCapitalOperationOutStatusEnum.OperationLock,
					VirtualCapitalOperationOutStatusEnum.getEnumString(VirtualCapitalOperationOutStatusEnum.OperationLock));
			statusMap.put(VirtualCapitalOperationOutStatusEnum.OperationSuccess,
					VirtualCapitalOperationOutStatusEnum.getEnumString(VirtualCapitalOperationOutStatusEnum.OperationSuccess));
			statusMap.put(VirtualCapitalOperationOutStatusEnum.Cancel,
					VirtualCapitalOperationOutStatusEnum.getEnumString(VirtualCapitalOperationOutStatusEnum.Cancel));
			statusMap.put(VirtualCapitalOperationOutStatusEnum.LockOrder,
					VirtualCapitalOperationOutStatusEnum.getEnumString(VirtualCapitalOperationOutStatusEnum.LockOrder));
		}
		modelAndView.addObject("statusMap", statusMap);

		// 查询
		Pagination<FVirtualCapitalOperationDTO> pagination = adminUserCapitalService.selectVirtualCapitalOperationList(
				pageParam, filterParam, statusList, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("virtualCapitaloperationList", pagination);
		return modelAndView;
	}

	/**
	 * 待审核虚拟币提现列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutList")
	public ModelAndView virtualCapitalOutList(
			@RequestParam(value = "fcoinid", defaultValue = "0") Integer fcoinid,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualCapitalOutList");
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, numPerPage);
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
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		filterParam.setFtype(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode());
		status.add(VirtualCapitalOperationOutStatusEnum.WaitForOperation);
		status.add(VirtualCapitalOperationOutStatusEnum.OperationLock);
		status.add(VirtualCapitalOperationOutStatusEnum.LockOrder);

		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 虚拟币ID
		if (fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
		} 
		modelAndView.addObject("fcoinid", fcoinid);

		// 页面参数
		Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
		coinMap.put(0, "全部");
		modelAndView.addObject("coinMap", coinMap);
		// 查询
		Pagination<FVirtualCapitalOperationDTO> pagination = adminUserCapitalService.selectVirtualCapitalOperationList(
				pageParam, filterParam, status, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("virtualCapitaloperationList", pagination);
		return modelAndView;
	}

	/**
	 * 审核虚拟币提现-弹出确定框
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/goVirtualCapitaloperationJSP")
	public ModelAndView goVirtualCapitaloperationJSP() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (request.getParameter("uid") != null) {
			int fid = Integer.parseInt(request.getParameter("uid"));
			FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
			modelAndView.addObject("virtualCapitaloperation", virtualCapitaloperation);
		}

		List<SystemCoinType> type = redisHelper.getCoinTypeCoinList();
		Map<Integer, String> coinMap = new LinkedHashMap<>();
		for (SystemCoinType coin : type) {
			coinMap.put(coin.getId(), coin.getName());
		}
		modelAndView.addObject("coinMap", coinMap);
		return modelAndView;
	}


	/**
	 * 手动审核虚拟币提现订单-最后确定
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutManual")
	@ResponseBody
	public ReturnResult virtualCapitalOutManual() {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int fid = Integer.parseInt(request.getParameter("uid"));
		FVirtualCapitalOperationDTO fvirtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		int status = fvirtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}

		if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationLock);
			return ReturnResult.FAILUER("审核失败,只有状态为:" + status_s + "的提现记录才允许审核!");
		}

		if (fvirtualCapitaloperation.getFcoinid() != 44 && fvirtualCapitaloperation.getFcoinid() != 46) {
			return  ReturnResult.FAILUER("当前币种不支持手动审核");
		}
		String txId = request.getParameter("txid");
		if (TextUtils.isEmpty(txId)) {
			return ReturnResult.FAILUER("交易 ID");
		}
		String password = request.getParameter("fpassword");
		if (TextUtils.isEmpty(password)) {
			return ReturnResult.FAILUER("请输入交易密码");
		}

		try {
			if (!"c5o6dHT3nO5q4MHKCZJ3qg==".equals(Utils.MD5(password))) {
				return ReturnResult.FAILUER("密码不正确");
            }
		} catch (BCException e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("密码不正确");
		}

		fvirtualCapitaloperation.setFuniquenumber(txId);
		fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
		boolean flag = false;
		try {
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			fvirtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin, fvirtualCapitaloperation);
		} catch (BCException e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag) {
			return ReturnResult.SUCCESS("手动审核成功！");
		} else {
			return ReturnResult.FAILUER("未知错误，请刷新列表后重试！");
		}
	}

	
	/**
	 * 审核虚拟币提现订单-最后确定
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutAudit")
	@ResponseBody
	public ReturnResult virtualCapitalOutAudit() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int fid = Integer.parseInt(request.getParameter("uid"));
		FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		// 检测状态
		int status = virtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationLock);
			return ReturnResult.FAILUER("审核失败,只有状态为:" + status_s + "的提现记录才允许审核!");
		}
		//钱包密码
		String walletPass = request.getParameter("fpassword");
		if (walletPass == null || walletPass.equals("") || walletPass.length() <= 0) {
			return ReturnResult.FAILUER("请输入钱包密码！");
		}

		// 检测数量
		int userId = virtualCapitaloperation.getFuid();
		int coinTypeId = virtualCapitaloperation.getFcoinid();
		UserCoinWallet virtualWalletInfo = adminUserCapitalService.selectUserVirtualWallet(userId, coinTypeId);
		BigDecimal amount = MathUtils.add(MathUtils.add(virtualCapitaloperation.getFamount(),
				virtualCapitaloperation.getFfees()),virtualCapitaloperation.getFbtcfees());
		BigDecimal frozenRmb = virtualWalletInfo.getFrozen();
		if (MathUtils.sub(frozenRmb, amount).compareTo(BigDecimal.ZERO) < 0) {
			return ReturnResult.FAILUER("审核失败,冻结数量:" + frozenRmb + "小于提现数量:" + amount + "，操作异常!");
		}

		SystemCoinType virtualCoinType = redisHelper.getCoinTypeSystem(coinTypeId);
		if(virtualCoinType == null || !virtualCoinType.getIsWithdraw()){
			return ReturnResult.FAILUER("审核失败," + virtualCoinType.getName() + "已禁止提现!");
		}

		BigDecimal defaultNetworkFee = null;
		//提现网络手续费检测
		if (virtualCoinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())
				|| virtualCoinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode())
				|| virtualCoinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())) {
			String networkFee = request.getParameter("networkfee");

			if (virtualCoinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())) {
				defaultNetworkFee = Constant.BTC_FEES[0];
			}else if (TextUtils.isEmpty(virtualCoinType.getContractAccount())){
				defaultNetworkFee = new BigDecimal("20000000000");
			}else {
				defaultNetworkFee = new BigDecimal("25387963393");
			}
            System.out.println("networkFee = "+networkFee);
			if (!TextUtils.isEmpty(networkFee)) {
				try {
					BigDecimal networkFeeDecimal = new BigDecimal(networkFee);
					if (networkFeeDecimal.compareTo(BigDecimal.ZERO) <= 0){
						return ReturnResult.FAILUER("手续费不能小于0");
					}
					if (virtualCoinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())
                            && networkFeeDecimal.compareTo(BigDecimal.ONE) >= 0){
						return ReturnResult.FAILUER("BTC手续费不能大于1");
					}

					if (virtualCoinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())
                            || virtualCoinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode())) {
						defaultNetworkFee = networkFeeDecimal.multiply(new BigDecimal(10).pow(9));
					}else {
						defaultNetworkFee = networkFeeDecimal;
					}

                    System.out.println("networkFee "+networkFeeDecimal.toString());
				}catch (Exception e) {
					e.printStackTrace();
					return ReturnResult.FAILUER("手续费设置错误错误");
				}
			}
		}


		String accesskey = virtualCoinType.getAccessKey();
		String secretkey = virtualCoinType.getSecrtKey();
		String ip = virtualCoinType.getIp();
		String port = virtualCoinType.getPort();
		if (accesskey == null || secretkey == null || ip == null || port == null) {
			return ReturnResult.FAILUER("钱包数据缺少，请检查配置信息");
		}
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(virtualCoinType.getCoinType(), ip, port)
				.accessKey(accesskey)
				.secretKey(secretkey)
				.pass(walletPass)
				.assetId(virtualCoinType.getAssetId())
				.sendAccount(virtualCoinType.getEthAccount())
				.contractAccount(virtualCoinType.getContractAccount())
				.contractWei(virtualCoinType.getContractWei())
				.builder()
				.getDriver();

		//是否平台互转
		int addressNum = adminUserCapitalService.selectAddressNum(virtualCapitaloperation.getFwithdrawaddress());
		if (addressNum <= 0) {
			try {
				BigDecimal balance = coinDriver.getBalance();
				if(balance == null){
					return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
				}
				if (balance.compareTo(amount) == -1) {
					return ReturnResult.FAILUER("查询余额失败，钱包余额：" + balance + "小于" + amount);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
			}
		}
		// 设置订单状态和时间
		virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
		virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
		// 管理员ID
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		virtualCapitaloperation.setFadminid(admin.getFid());
		boolean flag = false;
		try {
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("审核失败,订单锁定失败,请稍后再试!");
			}
			flag = adminUserCapitalService.updateVirtualCapital(virtualCapitaloperation.getFid(), admin.getFid(), amount, addressNum,
					walletPass,virtualCoinType,defaultNetworkFee == null ?virtualCapitaloperation.getFbtcfees():defaultNetworkFee);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(e.getMessage());
		}
		if (!flag) {
			return ReturnResult.FAILUER("更新审核失败");
		}
		return ReturnResult.SUCCESS("审核成功");
	}

	/**
	 * 拒绝提现
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalRefusedManual")
	@ResponseBody
	public ReturnResult virtualCapitalOutAuditManual() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int fid = Integer.parseInt(request.getParameter("uid"));
		String note = request.getParameter("note");
		if (TextUtils.isEmpty(note)) {
			return ReturnResult.FAILUER("请填写拒绝原因");
		}
		FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		if (virtualCapitaloperation == null) {
			return ReturnResult.FAILUER("订单不存在");
		}
		// 检测状态
		int status = virtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationLock);
			return ReturnResult.FAILUER("拒绝失败,只有状态为:" + status_s + "的提现记录才允许审核!");
		}
		virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.RefuseOrder);
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		virtualCapitaloperation.setMemo(note);
		try {
			if (adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation)) {
				return ReturnResult.SUCCESS("操作成功");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(e.getMessage());
		}
		return ReturnResult.FAILUER("操作失败");
	}


	/**
	 * 修改订单状态
	 */
	@RequestMapping("admin/goVirtualCapitaloperationChangeStatus")
	@ResponseBody
	public ReturnResult goVirtualCapitaloperationChangeStatus() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int type = Integer.valueOf(request.getParameter("type"));
		int uid = Integer.valueOf(request.getParameter("uid"));
		FVirtualCapitalOperationDTO fvirtualCapitaloperation = adminUserCapitalService.selectVirtualById(uid);
		fvirtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
		int status = fvirtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder && type != 4) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		String tips = "";
		switch (type) {
			case 1:
				tips = "锁定";
				if (status != VirtualCapitalOperationOutStatusEnum.WaitForOperation) {
					String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.WaitForOperation);
					return ReturnResult.FAILUER("锁定失败,只有状态为:‘" + status_s + "’的充值记录才允许锁定!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
				break;
			case 2:
				tips = "取消锁定";
				if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
					String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationLock);
					return ReturnResult.FAILUER("取消锁定失败,只有状态为:‘" + status_s + "’的充值记录才允许取消锁定!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.WaitForOperation);
				break;
			case 3:
				tips = "取消提现";
				if (status == VirtualCapitalOperationOutStatusEnum.Cancel) {
					return ReturnResult.FAILUER("取消提现失败,该记录已处于取消状态!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.Cancel);
				break;
			case 4:
				tips = "恢复提现";
				if (status != VirtualCapitalOperationOutStatusEnum.LockOrder) {
					return ReturnResult.FAILUER("恢复提现失败,只有状态为:锁定的充值记录才允许恢复提现!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
				break;
//			case 5:
//			    if (fvirtualCapitaloperation.getFcoinid() != 44 && fvirtualCapitaloperation.getFcoinid() != 46) {
//			        return  ReturnResult.FAILUER("当前币种不支持手动审核");
//                }
//				tips = "手动审核";
//				if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
//					return ReturnResult.FAILUER("手动审核失败，只有状态为：正在处理才允许手动审核！");
//				}
//				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationSuccess);
		}

		boolean flag = false;
		try {	
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			fvirtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin, fvirtualCapitaloperation);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("未知错误，请刷新列表后重试！");
		}
		if (flag) {
			return ReturnResult.SUCCESS(tips + "成功！");
		} else {
			return ReturnResult.FAILUER("未知错误，请刷新列表后重试！");
		}
	}

	/**
	 * 虚拟币手工充值订单
	 */
	@RequestMapping("admin/virtualCoinRechargeOrderByWork")
	@ResponseBody
	public ReturnResult virtualCoinRechargeOrderByWork(
			@RequestParam(value = "fuid", required = true) Integer fuid,
			@RequestParam(value = "fcoinid", required = true) Integer fcoinid,
			@RequestParam(value = "famount", required = true) BigDecimal famount,
			@RequestParam(value = "frechargeaddress", required = true) String frechargeaddress,
			@RequestParam(value = "funiquenumber", required = true) String funiquenumber,
			@RequestParam(value = "fblocknumber", required = true) Integer fblocknumber
	){
		FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
		FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();

		operation.setFadminid(admin.getFid());
		operation.setFadminname(admin.getFname());

		FUser user = adminUserService.selectById(fuid);
		if(user == null){
			return ReturnResult.FAILUER("未找到此用户！");
		}

		operation.setFuid(fuid);
		operation.setFnickname(user.getFnickname());
		operation.setFrealname(user.getFrealname());
		operation.setFcoinid(fcoinid);
		operation.setFamount(famount);
		operation.setFrechargeaddress(frechargeaddress);
		operation.setFuniquenumber(funiquenumber);
		operation.setFblocknumber(fblocknumber);
		operation.setFconfirmations(0);
		operation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
		operation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
		operation.setFsource(DataSourceEnum.WEB.getCode());
		operation.setFbtcfees(BigDecimal.ZERO);
		operation.setFfees(BigDecimal.ZERO);
		operation.setFagentid(WebConstant.BCAgentId);
		operation.setFnonce(0);
		operation.setFhasowner(true);
		operation.setFplatform(PlatformEnum.BC.getCode());
		operation.setFcreatetime(new Date());
		operation.setFupdatetime(new Date());
		operation.setVersion(0);

		try{
			Result result = adminUserCapitalService.insertRecharge(operation);
			if(result.getSuccess()){
				return ReturnResult.SUCCESS("增加虚拟币充值订单成功！");
			}
			return ReturnResult.FAILUER(result.getMsg());
		}catch (Exception e){
			logger.error("新增虚拟币手工充值订单异常："+ JSON.toJSONString(operation), e);
		}
		return ReturnResult.FAILUER("新增虚拟币手工充值订单失败！");
	}


	/**
	 * 审核虚拟币手工充值订单
	 */
	@RequestMapping("admin/checkVirtualCoinRechargeOrder")
	@ResponseBody
	public ReturnResult checkVirtualCoinRechargeOrder(
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "confirmations", required = true) Integer confirmations){

		if(id == null){
			return ReturnResult.FAILUER("参数异常");
		}

		if(confirmations <= 0){
			return ReturnResult.FAILUER("确认数不足无法到账");
		}

		FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
		FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();

		operation.setFadminid(admin.getFid());
		operation.setFadminname(admin.getFname());
		operation.setFid(id);
		operation.setFconfirmations(confirmations);

		try{
			Result result = adminUserCapitalService.recheckVirtualRecharge(operation);
			if(result.getSuccess()){
				return ReturnResult.SUCCESS("审核虚拟币充值订单成功！");
			}
			return ReturnResult.FAILUER(result.getMsg());
		}catch (Exception e){
			logger.error("审核虚拟币充值订单失败: id-"+id + " confirmations-"+confirmations, e);
		}
		return ReturnResult.FAILUER("审核虚拟币充值订单失败！");
	}
	
	// 导出列名
	private static enum ExportFiled {
		UID, 会员登录名, 会员昵称, 会员真实姓名, 虚拟币类型, 状态, 交易类型, 交易数量, 网络手续费, 手续费, 提现地址, 充值地址, 交易ID, 交易时间, 交易成功时间;
	}
	/**
	 * 虚拟币充值提现列表导出数据
	 */
	@RequestMapping("admin/virtualCapitaloperationExport")
	@ResponseBody
	public ReturnResult virtualCapitaloperationExport(
			@RequestParam(value = "fcoinid", required = false) Integer fcoinid,
			@RequestParam(value = "type", defaultValue = "0") Integer ftype,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=virtualCapitaloperationList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;
		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, 100000);
		// 排序条件
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
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
		}
		// 虚拟币ID
		if (fcoinid !=null && fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
		} 
		
		// 类型提现or充值
		if (ftype > 0) {
			filterParam.setFtype(ftype);
		}
		// 查询
		pageParam = adminUserCapitalService.selectVirtualCapitalOperationList(pageParam, filterParam, status, isvip6.equals("on"));
		Collection<FVirtualCapitalOperationDTO> virtualCapitaloperationList = pageParam.getData();
		for (FVirtualCapitalOperationDTO capitalOperation : virtualCapitaloperationList) {
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
				case 虚拟币类型:
					e.setCell(filed.ordinal(), capitalOperation.getFcoinname());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 交易类型:
					e.setCell(filed.ordinal(), capitalOperation.getFtype_s());
					break;
				case 交易数量:
					e.setCell(filed.ordinal(), Utils.number4String(capitalOperation.getFamount(),4));
					break;
				case 网络手续费:
					String btcfees = capitalOperation.getFbtcfees() == null ? "0.0000" : Utils.number4String(capitalOperation.getFbtcfees(),4);
					e.setCell(filed.ordinal(), btcfees);
					break;
				case 手续费:
					e.setCell(filed.ordinal(), Utils.number4String(capitalOperation.getFfees(),4));
					break;
				case 提现地址:
					e.setCell(filed.ordinal(), capitalOperation.getFwithdrawaddress());
					break;
				case 充值地址:
					e.setCell(filed.ordinal(), capitalOperation.getFrechargeaddress());
					break;
				case 交易ID:
					e.setCell(filed.ordinal(), capitalOperation.getFuniquenumber());
					break;
				case 交易时间:
					e.setCell(filed.ordinal(), capitalOperation.getFcreatetime());
					break;
				case 交易成功时间:
					e.setCell(filed.ordinal(), capitalOperation.getFupdatetime());
					break;
				default:
					break;
				}
			}
		}
		e.exportXls(response);
		return ReturnResult.SUCCESS("导出成功！");
	}


}
