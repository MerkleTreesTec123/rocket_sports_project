package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.MQSend;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.id.IDCard;
import com.qkwl.common.rpc.admin.*;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.common.dto.user.FBeautiful;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserInfo;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController extends WebBaseController {


	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private MQSend mqSend;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private IAdminBeautifulService adminBeautifulService;
	@Autowired
	private IAdminUserFinances adminUserFinances;
	@Autowired
	private ScoreHelper scoreHelper;
	@Autowired
	private IAdminApiAuthService adminApiAuthService;
	@Autowired
	private IAdminCommissionRecordService adminCommissionRecordService;

	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
	
	@RequestMapping("/admin/userList")
	public ModelAndView userList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="keywords",required=false) String keyWord,
			@RequestParam(value="uid",required=false,defaultValue="0") Integer uid,
			@RequestParam(value="orderField",required=false,defaultValue="fregistertime") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection,
			@RequestParam(value="ftype",required=false,defaultValue="0") Integer type,
			@RequestParam(value="regDate",required=false) String regDate,
			@RequestParam(value="logDate",required=false) String logDate,
			@RequestParam(value="fisbirthday",required=false,defaultValue="0") Integer fisbirthday
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/userList");
		// 定义查询条件
		Pagination<FUser> pageParam = new Pagination<FUser>(currentPage, numPerPage);
		FUser fUser = new FUser();
		//参数判断
		if (!StringUtils.isEmpty(keyWord)) {
			modelAndView.addObject("keywords", keyWord);
			pageParam.setKeyword(keyWord);
		}
		if (uid > 0) {
			fUser.setFid(uid);
			modelAndView.addObject("uid", uid);
		}
		if (type > 0) {
			fUser.setFstatus(type);
			modelAndView.addObject("ftype", type);
		}
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (!StringUtils.isEmpty(regDate)){
			fUser.setFregistertime(format.parse(regDate));
			modelAndView.addObject("regDate", regDate);
		}
		if (!StringUtils.isEmpty(logDate)){
			fUser.setFlastlogintime(format.parse(logDate));
			modelAndView.addObject("logDate", logDate);
		}
		if(fisbirthday == 1){
			fUser.setFbirth(new Date());
		}
		modelAndView.addObject("fisbirthday", fisbirthday);
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);
		
		//页面参数
		Map<Integer, String> typeMap = new HashMap<Integer, String>();
		typeMap.put(0, "全部");
		typeMap.put(UserStatusEnum.NORMAL_VALUE, UserStatusEnum.getEnumString(UserStatusEnum.NORMAL_VALUE));
		typeMap.put(UserStatusEnum.FORBBIN_VALUE, UserStatusEnum.getEnumString(UserStatusEnum.FORBBIN_VALUE));
		modelAndView.addObject("typeMap", typeMap);

		//页面参数
		Map<Integer, String> platformMap = new HashMap<>();
		for (PlatformEnum e :PlatformEnum.values()) {
			platformMap.put(e.getCode(), e.getValue());
		}
		modelAndView.addObject("platformMap", platformMap);

		if(StringUtils.isEmpty(keyWord) && uid == 0 && type == 0 && StringUtils.isEmpty(regDate) && StringUtils.isEmpty(logDate) && fisbirthday==0){
			return modelAndView;
		}
		
		Pagination<FUser> pagination = this.adminUserService.selectUserPageList(pageParam, fUser);

		modelAndView.addObject("userList", pagination);
		return modelAndView;
	}

	@RequestMapping("user/goPage")
	public ModelAndView userEdit(
			@RequestParam(value = "uid", required = false) Integer uid,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "url", required = false) String url) {
		ModelAndView modelAndView = new ModelAndView();
		if (uid > 0) {
			FUser user = adminUserService.selectById(uid);
			modelAndView.addObject("fuser", user);
			modelAndView.addObject("status", status);
		}
		modelAndView.setViewName(url);
		return modelAndView;
	}

	@RequestMapping("/user/userForbbin")
	@ResponseBody
	public ReturnResult userForbbin(
			@RequestParam(value="status",required=false,defaultValue="0") Integer status,
			@RequestParam(value="uid",required=false,defaultValue="0") Integer fid,
			@RequestParam(value="remark", required=false, defaultValue = "") String remark
			) throws Exception {
		FUser user = adminUserService.selectById(fid);
		LogAdminActionEnum logAdminActionEnum = null;
		Integer BusinessType = 0;
		String msg = "";
		if (status == 1) {// 禁用帐号
			if (user.getFstatus() == UserStatusEnum.FORBBIN_VALUE) {
				return ReturnResult.FAILUER("会员已禁用，无需做此操作");
			}
			msg = "禁用成功";
			user.setFstatus(UserStatusEnum.FORBBIN_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_BAN_OK;
		} else if (status == 2) {// 启用帐号
			if (user.getFstatus() == UserStatusEnum.NORMAL_VALUE) {
				return ReturnResult.FAILUER("会员状态为正常，无需做此操作");
			}
			msg = "解除禁用成功";
			user.setFstatus(UserStatusEnum.NORMAL_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_BAN_NO;
		} else if (status == 3) {// 重设登录密码
			msg =  "重设登录密码成功，密码为:BC"+user.getFid();
			user.setFloginpassword(Utils.MD5("BC"+user.getFid()));
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_LOGINPWD;
			if(user.getFtelephone() != null){
				BusinessType = BusinessTypeEnum.SMS_MODIFY_LOGIN_REMIND.getCode();
			}
		} else if (status == 4) {// 重设交易密码
			msg = "重设交易密码成功，密码为:BC"+user.getFid();
			user.setFtradepassword(Utils.MD5("BC"+user.getFid()));
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_TRADEPWD;
			if(user.getFtelephone() != null){
				BusinessType = BusinessTypeEnum.SMS_MODIFY_TRADE_REMIND.getCode();
			}
		} else if (status == 5) {// 禁止提现
			if (user.getFiscny() == UserStatusEnum.FORBBIN_VALUE) {
				return ReturnResult.FAILUER("会员已禁止提现，无需做此操作");
			}
			msg = "禁止提现成功";
			user.setFiscny(UserStatusEnum.FORBBIN_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_RMB_WITHDRAW_NO;
		} else if (status == 6) {// 启用提现
			if (user.getFiscny() == UserStatusEnum.NORMAL_VALUE) {
				return ReturnResult.FAILUER("会员已启用提现，无需做此操作");
			}
			msg = "启用提现成功";
			user.setFiscny(UserStatusEnum.NORMAL_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_RMB_WITHDRAW_OK;
		} else if (status == 7) {// 禁止提币
			if (user.getFiscoin() == UserStatusEnum.FORBBIN_VALUE) {
				return ReturnResult.FAILUER("会员已禁止提币，无需做此操作");
			}
			msg = "禁止提币成功";
			user.setFiscoin(UserStatusEnum.FORBBIN_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_COIN_WITHDRAW_NO;
		} else if (status == 8) {// 启用提币
			if (user.getFiscoin() == UserStatusEnum.NORMAL_VALUE) {
				return ReturnResult.FAILUER("会员已启用提币，无需做此操作");
			}
			msg = "启用提币成功";
			user.setFiscoin(UserStatusEnum.NORMAL_VALUE);
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_USER_COIN_WITHDRAW_OK;
		} else if (status == 9) {// 重置谷歌
			user.setFgoogleauthenticator(null);
			user.setFgooglebind(false);
			user.setFgoogleurl(null);
			msg = "重置谷歌验证成功";
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_GOOGLE;
		} else if (status == 10) {// 视频认证
			user.setIsVideo(true);
			user.setVideoTime(new Date());
			msg = "视频认证成功";
			logAdminActionEnum = LogAdminActionEnum.SYSTEM_VIDEO;
		} else if (status == 11) {// 解除资产冻结
			user.setFtradepwdtime(null);
			msg = "解除资产冻结成功";
			logAdminActionEnum = LogAdminActionEnum.ASSET_FREEZE;
		}
		boolean result = this.adminUserService.updateUserInfo(user);
		if (!result) {
			// 错误处理
			return ReturnResult.FAILUER("操作异常");
		} else{
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			String ip = Utils.getIpAddr(request);
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), user.getFid(), logAdminActionEnum,
					ip, remark);
			if(BusinessType != 0){
				if(status == 3 || status == 4){
					validateHelper.smsSensitiveInfo(user.getFareacode(), user.getFtelephone(), LocaleEnum.ZH_CN.getCode(),
							user.getFplatform(), BusinessType);
				}
			}
		}
		return ReturnResult.SUCCESS(msg);
	}

	@RequestMapping("/user/userEdit")
	@ResponseBody
	public ReturnResult userEdit(
			@RequestParam(value = "fid", required = true) Integer fid,
			@RequestParam(value = "floginname", required = true) String loginname,
			@RequestParam(value = "fnickName", required = true) String nickname,
			@RequestParam(value = "fareacode", required = false) String areacode,
			@RequestParam(value = "femail", required = false) String email,
			@RequestParam(value = "fismailbind", required = true) Boolean fismailbind,
			@RequestParam(value = "ftelephone", required = false) String ftelephone,
			@RequestParam(value = "fistelephonebind", required = true) Boolean fistelephonebind,
			@RequestParam(value = "frealName", required = false) String realname,
			@RequestParam(value = "fhasrealvalidate", required = true) Boolean fhasrealvalidate,
			@RequestParam(value = "fidentityNo", required = false) String identityNo) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();

		FUser user = adminUserService.selectById(fid);
		// 登录名
		if(StringUtils.isEmpty(loginname)){
			user.setFloginname(null);
		} else {
			user.setFloginname(loginname);
		}
		//昵称
		if(StringUtils.isEmpty(nickname)){
			user.setFnickname(null);
		}else{
			user.setFnickname(nickname);
		}
		// 邮件
		if(StringUtils.isEmpty(email) && !fismailbind){
			user.setFemail(null);
			user.setFismailbind(false);
		}else{
			if(StringUtils.isEmpty(email)){
				return ReturnResult.FAILUER("请输入邮箱!");
			}
			if (user.getFemail() == null || !email.equals(user.getFemail())) {
				FUser paramUser = new FUser();
				paramUser.setFemail(email);
				boolean emailflag = adminUserService.selectIsExistByParam(paramUser);
				if (emailflag) {
					return ReturnResult.FAILUER("该邮件已经存在");
				}
			}
			user.setFemail(email);
			user.setFismailbind(true);
		}
		// 手机
		if(StringUtils.isEmpty(ftelephone) && !fistelephonebind){
			user.setFtelephone(null);
			user.setFistelephonebind(false);
		}else{
			if(StringUtils.isEmpty(ftelephone)){
				return ReturnResult.FAILUER("请输入手机号!");
			}
			if (user.getFtelephone()== null || !ftelephone.equals(user.getFtelephone())) {
				FUser paramUser = new FUser();
				paramUser.setFtelephone(ftelephone);
				boolean emailflag = adminUserService.selectIsExistByParam(paramUser);
				if (emailflag) {
					return ReturnResult.FAILUER("该手机号已经存在");
				}
			}
			user.setFtelephone(ftelephone);
			user.setFistelephonebind(true);
		}
		// 身份证
		if(StringUtils.isEmpty(realname) && StringUtils.isEmpty(identityNo) && !fhasrealvalidate){
			user.setFrealname(null);
			user.setFidentityno(null);
			user.setFbirth(null);
			user.setFidentitytype(null);
			user.setFhasrealvalidatetime(null);
			user.setFhasrealvalidate(false);
		}else {
			if (StringUtils.isEmpty(realname)) {
				return ReturnResult.FAILUER("请输入真实姓名!");
			}
			if (StringUtils.isEmpty(identityNo)) {
				return ReturnResult.FAILUER("请输入身份证号!");
			}
			Date birth = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String brondate = IDCard.GetIDBirthday(identityNo);
				birth = format.parse(brondate);
			} catch (Exception e){
				e.printStackTrace();
			}
			user.setFrealname(realname);
			user.setFidentityno(identityNo);
			user.setFbirth(birth);
			user.setFidentitytype(IdentityTypeEnum.IDCARD);
			user.setFhasrealvalidatetime(Utils.getTimestamp());
			user.setFhasrealvalidate(true);
		}

		boolean result = this.adminUserService.updateUserIdentity(user);
		if (!result) {
			return ReturnResult.FAILUER("修改失败");
		}else{
			// MQ_USER_ACTION 操作成功，记录日志
			String ip = Utils.getIpAddr(request);
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(),user.getFid() ,LogAdminActionEnum.SYSTEM_USERINFO, ip);
		}
		return ReturnResult.SUCCESS("修改成功");
	}
	
	@ResponseBody
	@RequestMapping("/user/saveUserBeautiful")
	public ReturnResult saveUserBeautiful(
			@RequestParam(value="fid",required=false,defaultValue="0") Integer fid,
			@RequestParam(value="userLookup.id",required=false,defaultValue="0") Integer fbid
			) throws Exception {

		FUser user = adminUserService.selectById(fid);
		FBeautiful beautiful = adminBeautifulService.selectBeautiful(fbid);
		if (null == beautiful) {
			return ReturnResult.FAILUER("未查询到该靓号！");
		}
		if(beautiful != null && beautiful.getFupdatetime() != null){
			return ReturnResult.FAILUER("该靓号已被使用");
		}
		boolean isUse = adminUserService.selectBeautiful(beautiful.getFbid());
		if (isUse) {
			return ReturnResult.FAILUER("该靓号已被使用");
		}
		if (user == null) {
			return ReturnResult.FAILUER("用户错误");
		}
		user.setFshowid(beautiful.getFbid());
		if (adminUserService.updateUserInfo(user)) {
			// 更新使用时间
			beautiful.setFupdatetime(new Date());
			adminBeautifulService.updateBeautiful(beautiful);
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			String ip = Utils.getIpAddr(request);
			FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
			mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), user.getFid(), LogAdminActionEnum.SYSTEM_BEAUTIFUL_USE, ip);
			return ReturnResult.SUCCESS("靓号分配成功");
		} else {
			return ReturnResult.FAILUER("靓号分配失败");
		}
	}
	
	@ResponseBody
	@RequestMapping("user/userExport")
	public ReturnResult userExport(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="keywords",required=false) String keyWord,
			@RequestParam(value="uid",required=false,defaultValue="0") Integer uid,
			@RequestParam(value="orderField",required=false,defaultValue="fregistertime") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection,
			@RequestParam(value="ftype",required=false,defaultValue="0") Integer type,
			@RequestParam(value="regDate",required=false) Date regDate,
			@RequestParam(value="logDate",required=false) Date logDate,
			@RequestParam(value="fisbirthday",required=false,defaultValue="0") Integer fisbirthday
			) throws Exception {
		// 定义查询条件
		Pagination<FUser> pageParam = new Pagination<FUser>(currentPage, 1000000);
		FUser fUser = new FUser();
		//参数判断
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
		}
		if (uid > 0) {
			fUser.setFid(uid);
		}
		if (type > 0) {
			fUser.setFstatus(type);
		}
		if (regDate != null){
			fUser.setFregistertime(regDate);
		}
		if (logDate != null){
			fUser.setFlastlogintime(logDate);
		}
		if(fisbirthday == 1){
			fUser.setFbirth(new Date());
		}
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);
		
//		if(StringUtils.isEmpty(keyWord) && uid == 0 && type == 0 && regDate == null && logDate == null && fisbirthday==0){
//			return ReturnResult.FAILUER("导出数据为空");
//		}
		
		Pagination<FUser> pagination = this.adminUserService.selectUserPageList(pageParam, fUser);
		if(null == pagination){
			return ReturnResult.FAILUER("导出数据为空");
		}

		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=userList.xls");
		XlsExport e = new XlsExport();
		int rowIndex = 0;

		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}

		Collection<FUser> userList = pagination.getData();
		for (FUser user : userList) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case 会员UID:
					e.setCell(filed.ordinal(), user.getFid());
					break;
				case 会员状态:
					e.setCell(filed.ordinal(), user.getFstatus() == 1 ? "正常" : "禁用");
					break;
				case 会员登录名:
					e.setCell(filed.ordinal(), user.getFloginname());
					break;
				case 证件是否已审:
					e.setCell(filed.ordinal(), user.getFhasrealvalidate() + "");
					break;
				case 昵称:
					e.setCell(filed.ordinal(), user.getFnickname());
					break;
				case 真实姓名:
					e.setCell(filed.ordinal(), user.getFrealname());
					break;
				case 会员等级:
					e.setCell(filed.ordinal(), "VIP" + user.getLevel());
					break;
				case 电话号码:
					e.setCell(filed.ordinal(), user.getFtelephone());
					break;
				case 邮箱地址:
					e.setCell(filed.ordinal(), user.getFemail());
					break;
				case 证件类型:
					e.setCell(filed.ordinal(), "身份证");
					break;
				case 证件号码:
					e.setCell(filed.ordinal(), user.getFidentityno());
					break;
				case 生日:
					e.setCell(filed.ordinal(), user.getFbirth());
					break;
				case 注册时间:
					e.setCell(filed.ordinal(), user.getFregistertime());
					break;
				case 上次登录时间:
					e.setCell(filed.ordinal(), user.getFlastlogintime());
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
	 * 导出用户
	 */
	private static enum ExportFiled {
		会员UID, 会员登录名, 会员状态, 证件是否已审, 昵称, 真实姓名, 会员等级, 是否已手机验证, 电话号码, 邮箱地址, 证件类型, 证件号码, 生日, 注册时间, 上次登录时间;
	}

	/**
	 * 用户查找带回
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/user/userLookup")
	public ModelAndView userLookup() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/lookUserList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String uid= request.getParameter("uid");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		// 定义查询条件
		Pagination<FUser> pageParam = new Pagination<FUser>(currentPage, numPerPage);
		FUser fUser = new FUser();
		// 参数判断
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		if (uid != null && uid.trim().length() > 0) {
			try {
				fUser.setFid(Integer.valueOf(uid));
				modelAndView.addObject("uid", uid);
			}catch (Exception e){}
		}

		Pagination<FUser> pagination = this.adminUserService.selectUserPageList(pageParam, fUser);
		modelAndView.addObject("userList", pagination);
		return modelAndView;
	}



	@RequestMapping("/admin/updateLevel")
	@ResponseBody
	public ReturnResult updateLevel(
			@RequestParam(value="level",required=true) Integer level,
			@RequestParam(value="uid",required=true) Integer uid) {
		FUser user = adminUserService.selectById(uid);
		if (user == null) {
			return ReturnResult.FAILUER("该用户不存在，请刷新后重试！");
		}

		Boolean isSuccess = adminUserService.updateUserLevel(user.getFid(), level);
		if(isSuccess){
			return ReturnResult.SUCCESS("修改成功！");
		}
		return ReturnResult.FAILUER("修改失败！");
	}

	@RequestMapping("/admin/levelScore")
	@ResponseBody
	public ReturnResult levelScore(
			@RequestParam(value = "score", required = true) Long score,
			@RequestParam(value = "remark", required = true) String remark,
			@RequestParam(value = "uid", required = true) Integer uid) {
		FUser user = adminUserService.selectById(uid);
		if (user == null) {
			return ReturnResult.FAILUER("该用户不存在，请刷新后重试！");
		}
		scoreHelper.SendAsync(user.getFid(), new BigDecimal(score.intValue()), ScoreTypeEnum.ACTIVITY.getCode(), remark);
		return ReturnResult.SUCCESS("增加成功！");
	}

	@RequestMapping("/admin/userFinances")
	public ModelAndView userFinances(
			@RequestParam(value = "fcoinid", required = false) Integer fcoinid,
			@RequestParam(value = "fstate", required = false) Integer fstate,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
			@RequestParam(value = "keywords", required = false) String keywords) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/userFinancesList");

		Pagination<FUserFinancesDTO> page = new Pagination<FUserFinancesDTO>(currentPage, Constant.adminPageSize);
		page.setOrderField(orderField);
		page.setOrderDirection(orderDirection);

		if(!StringUtils.isEmpty(keywords)){
			page.setKeyword(keywords);
			modelAndView.addObject("keywords", keywords);
		}
		FUserFinancesDTO filterParam = new FUserFinancesDTO();
		// 虚拟币ID
		if (fcoinid != null && fcoinid >= 0) {
			filterParam.setFcoinid(fcoinid);
			modelAndView.addObject("fcoinid", fcoinid);
		}
		// 状态
		if (fstate != null && fstate > 0) {
			filterParam.setFstate(fstate);
			modelAndView.addObject("fstate", fstate);
		}
		if (logDate != null) {
			filterParam.setFcreatetime(DateUtils.parse(logDate, DateUtils.YYYY_MM_DD_HH_MM_SS));
			modelAndView.addObject("logDate", logDate);
		}
		// 页面参数
		Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
		coinMap.put(-1, "全部");
		modelAndView.addObject("coinMap", coinMap);
		Map<Integer, String> stateMap = new HashMap<Integer, String>();
		stateMap.put(0, "全部");
		for (UserFinancesStateEnum stateEnum : UserFinancesStateEnum.values()) {
			stateMap.put(stateEnum.getCode(), stateEnum.getValue().toString());
		}
		modelAndView.addObject("stateMap", stateMap);
		page = adminUserFinances.selectUserFinancesByPage(page, filterParam);

		modelAndView.addObject("page", page);
		return modelAndView;
	}

	/**
	 * 查询
	 *
	 * @param apiKey
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/openApi")
	public ModelAndView userList(
			@RequestParam(value="apiKey",required=false) String apiKey,
			@RequestParam(value="uid",required=false,defaultValue="0") Integer uid
	) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/openApi");
		// 定义查询条件
		if (!TextUtils.isEmpty(apiKey)){
			modelAndView.addObject("apiKey",apiKey);
		}
		List<FApiAuth> fApiAuths = new ArrayList<>();
		if (uid > 0) {
			modelAndView.addObject("uid", uid);
			fApiAuths = this.adminApiAuthService.selectFApiAuthListByUID(uid);
		}else{
			fApiAuths = this.adminApiAuthService.selectAll();
			modelAndView.addObject("uid", "");
		}
		modelAndView.addObject("openApi", fApiAuths);
		return modelAndView;
	}

	/**
	 * 添加api UI
	 * @return
	 */
	@RequestMapping("/admin/goAddOpenApi")
	public ModelAndView goAddOpenApi(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/user/submitAddOpenApi");
		return modelAndView;
	}

	/**
	 * 添加api json
	 * @return
	 */
	@RequestMapping("/admin/submitAddOpenApi")
	public ReturnResult submitAddOpenApi(@RequestParam(value="fuid",required=false) Integer fuid){
		FUser fUser = adminUserService.selectById(fuid);
		if (fUser == null || fUser.getFstatus() != UserStatusEnum.NORMAL_VALUE){
		    logger.info("用户无效");
			return ReturnResult.FAILUER("当前用户无效");
		}
		try {
			FApiAuth apiAuth = adminApiAuthService.insertAuth(fuid);
			if (apiAuth != null) {
				return ReturnResult.SUCCESS("添加成功");
			}
		} catch (BCException e) {
			e.printStackTrace();
			logger.error("添加api失败了："+e.getMessage());
			return ReturnResult.FAILUER(e.getMessage());
		}
		return ReturnResult.FAILUER("添加失败");
	}

	/**
	 * 修改api UI
	 * @return
	 */
	@RequestMapping("/admin/goUpdateOpenApi")
	public ModelAndView goUpdateOpenApi(@RequestParam(value="fid",required=false) Integer fid){
		ModelAndView modelAndView = new ModelAndView();
		List<FApiAuth> fApiAuths = adminApiAuthService.selectFApiAuthListByID(fid);
		modelAndView.setViewName("/user/goUpdateOpenApi");
		if (fApiAuths != null && fApiAuths.size()>0) {
			modelAndView.addObject("api",fApiAuths.get(0));
		}
		return modelAndView;
	}

	/**
	 * 提交修改Json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/admin/submitApiUpdate")
	public ReturnResult submitApiUpdate(@RequestParam(value="fid",required=false) Integer fid,
										@RequestParam(value="fstatus",required=false) Integer fstatus,
										@RequestParam(value="fopenrate",required=false) Integer fopenrate,
										@RequestParam(value = "frate",required = false) Float frate ) {
		List<FApiAuth> fApiAuths = adminApiAuthService.selectFApiAuthListByID(fid);
		FApiAuth apiAuth = null;
		for (FApiAuth f : fApiAuths) {
			if (f.getFid() == fid) {
				apiAuth = f;
				break;
			}
		}
		if (apiAuth == null){
			return ReturnResult.FAILUER("没有找到相应的数据");
		}
		apiAuth.setFopenrate(fopenrate);
		apiAuth.setFrate(new BigDecimal(frate));
		apiAuth.setFstatus(fstatus);
		adminApiAuthService.updateByUser(apiAuth);
		return ReturnResult.SUCCESS("成功");
//		if (i > 0) {
//			return ReturnResult.SUCCESS("成功");
//		} else {
//			return ReturnResult.FAILUER("失败");
//		}
	}

	/**
	 * 佣金记录
	 * @return
	 */
	@RequestMapping("/admin/goCommissionRecord")
	public ModelAndView goCommissionRecord(
			@RequestParam(value="pageNum", required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="uid",required=false) Integer uid,
										   @RequestParam(value="introuid",required=false) Integer introuid){

		List<CommissionRecord> all = adminCommissionRecordService.getAll(uid, introuid, CommissionRecordStatusEnum.NORMAL);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user/goCommissionRecord");
		modelAndView.addObject("records",all);
		modelAndView.addObject("uid",uid);
		modelAndView.addObject("introuid",introuid);
		return modelAndView;
	}

	/**
	 * 发放
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/admin/submitCommissionUpdate")
	public ReturnResult submitCommissionUpdate(@RequestParam(value = "id",required = false) Integer id){
		try {
			adminCommissionRecordService.update(id,CommissionRecordStatusEnum.SUCCESS);
			return ReturnResult.SUCCESS("发放成功");
		} catch (BCException e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(e.getMessage());
		}

	}

}
