package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FVirtualFinances;

import java.util.List;
import java.util.Map;

/**
 * 后台虚拟币接口
 * @author ZKF
 */
public interface IAdminVirtualCoinService {
	
	/**
	 * 获取虚拟币列表
	 * @param page 分页实体对象
	 * @param type 虚拟币实体对象
	 * @return 分页实体对象
	 */
	public Pagination<SystemCoinType> selectVirtualCoinList(Pagination<SystemCoinType> page, SystemCoinType type);
	
	/**
	 * 查询虚拟币基本信息
	 * @param id 虚拟币ID
	 * @return 虚拟币实体对象
	 */
	public SystemCoinType selectVirtualCoinById(int id);
	
	/**
	 * 新增虚拟币
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */
	public boolean insert(SystemCoinType coin);
		
	/**
	 * 修改虚拟币基本信息
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */
	public boolean updateVirtualCoin(SystemCoinType coin);
	
	/**
	 * 启用虚拟币钱包
	 * @param coin 虚拟币实体对象
	 * @param password 钱包密码
	 * @return true：成功，false：失败
	 */
	public boolean updateVirtualCoinByEnabled(SystemCoinType coin,String password);

	/**
	 * 禁用虚拟币钱包
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */
	public boolean updateVirtualCoinBydDisable(SystemCoinType coin);
	
	/**
	 * 虚拟币开盘停盘
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */
	public boolean updateVirtualCoinStartStop(SystemCoinType coin);
	
	/**
	 * 修改钱包链接
	 * @param coin 虚拟币实体对象
	 * @return true：成功，false：失败
	 */	
	public boolean updateVirtualCoinWalletLink(SystemCoinType coin);
	
	/***************虚拟币地址操作****************/
	/**
	 * 查询虚拟币地址数量列表
	 * @param page 分页实体对象
	 * @return 分页实体对象
	 */
	public Pagination<Map<String, Object>> selectVirtualCoinAddressNumList(Pagination<Map<String, Object>> page);
	
	/**
	 * 生成虚拟币地址(内部执行事物，该方法不走事物)
	 * @param virtualCoinType 虚拟币实体对象
	 * @param count 生成数量
	 * @param password 钱包密码
	 * @return 200添加成功,302钱包连接失败，请检查配置信息，303取地址受限，304钱包连接失败，请检查配置信息，未知错误
	 */
	public int createVirtualCoinAddress(SystemCoinType virtualCoinType,int count,String password);
	
	/**
	 * 插入存币理财设置
	 * @param record 实体对象
	 * @return
	 */
	public boolean insertVirtualFinances(FVirtualFinances record);
	
	/**
	 * 查询存币理财设置
	 * @param fid 主键ID
	 * @return
	 */
	public FVirtualFinances selectVirtualFinances(Integer fid);
	
	/**
	 * 查询存币理财设置列表
	 * @param fcoinid 币种ID
	 * @return
	 */
	public List<FVirtualFinances> selectVirtualFinancesList(Integer fcoinid, Integer fstate);
	
	/**
	 * 修改存币理财设置
	 * @param record 实体对象
	 * @return
	 */
	public boolean updateVirtualFinances(FVirtualFinances record);
	
	/**
	 * 删除存币理财设置
	 * @param fid 主键ID
	 * @return
	 */
	public boolean deleteVirtualFinances(Integer fid);
	
}
