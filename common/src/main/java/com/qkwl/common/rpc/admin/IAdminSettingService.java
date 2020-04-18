package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.FSystemArgs;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.dto.web.FSystemLan;

/**
 * 后台系统设置接口
 * @author ZKF
 */
public interface IAdminSettingService {
	
	/**
	 * 分页查询系统参数列表
	 * @param page 分页参数
	 * @param args 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<FSystemArgs> selectSystemArgsPageList(Pagination<FSystemArgs> page, FSystemArgs args);
	
	/**
	 * 新增系统参数
	 * @param args 实体参数
	 * @return 是否新增成功
	 */
	public boolean insertSystemArgs(FSystemArgs args);
	
	/**
	 * 更新系统参数
	 * @param args 实体参数
	 * @return 是否更新成功
	 */
	public boolean updateSystemArgs(FSystemArgs args);
	
	/**
	 * 根据id查询系统参数
	 * @param id 系统参数id
	 * @return 系统参数实体
	 */
	public FSystemArgs selectSystemArgsById(int id);
	
	/**
	 * 分页查询关于我们列表
	 * @param page 分页参数
	 * @param about 实体参数
	 * @param fagentid 券商id
	 * @return 分页查询列表
	 */
	public Pagination<FAbout> selectAboutPageList(Pagination<FAbout> page, FAbout about, Integer fagentid);

	/**
	 * 更新关于我们
	 * @param about 实体参数
	 * @return 是否更新成功
	 */
	public boolean updateAbout(FAbout about);
	
	/**
	 * 根据id查询
	 * @param id 关于我们id
	 * @return 关于我们实体
	 */
	public FAbout selectAboutById(int id);
	
	/**
	 * 根据id查询充值银行信息
	 * @param fid 银行卡id
	 * @return 充值银行卡实体
	 */
	public FSystemBankinfoRecharge selectBankInfoById(int fid);
	
	/**
	 * 分页查询充值银行信息
	 * @param page 分页参数
	 * @param bank 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<FSystemBankinfoRecharge> selectBankPageList(Pagination<FSystemBankinfoRecharge> page, FSystemBankinfoRecharge bank);
	
	/**
	 * 新增充值银行卡
	 * @param recharge 充值银行卡实体
	 * @return 是否充值成功
	 */
	public boolean insertBank(FSystemBankinfoRecharge recharge);
	
	/**
	 * 更新充值银行卡
	 * @param recharge 充值银行卡实体
	 * @return 是否更新成功
	 */
	public boolean updateBankInfo(FSystemBankinfoRecharge recharge);


	/************友情链接*******************/
	
	/**
	 * 分页查询友链
	 * @param page 分页参数
	 * @param link 实体参数
	 * @return 分页记录列表
	 */
	public Pagination<FFriendLink> selectLinkPageList(Pagination<FFriendLink> page, FFriendLink link);
	
	/**
	 * 新增友链
	 * @param link 友链实体
	 * @return 是否新增成功
	 */
	public boolean insertLink(FFriendLink link);
	
	/**
	 * 更新友链
	 * @param link 友链实体
	 * @return 是否更新成功
	 */
	public boolean updateLink(FFriendLink link);
	
	/**
	 * 删除链接
	 * @param id 友链id
	 * @return 是否删除成功
	 */
	public boolean deleteLink(int id);
	
	/**
	 * 根据id查询友链
	 * @param id 友链id
	 * @return 友链实体
	 */
	public FFriendLink selectLinkById(int id);

	/**
	 * 查询语言
	 * @param page 分页参数
	 * @return 分页查询列表
	 */
	public Pagination<FSystemLan> selectLanguagePageList(Pagination<FSystemLan> page);

	/**
	 * 查询语言
	 * @return 语言列表
	 */
	public List<FSystemLan> selectLanguageList();
}
