package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FBeautiful;

public interface IAdminBeautifulService {
	
	/**
	 * 分页查询靓号列表
	 * @param page 分页参数
	 * @param beautiful
	 * @return
	 */
	public Pagination<FBeautiful> selectBeautifulPageList(Pagination<FBeautiful> page, boolean isUse);
	
	/**
	 * 根据主键ID查询实体
	 * @param fid 主键ID
	 * @return
	 */
	public FBeautiful selectBeautiful(Integer fid);
	
	/**
	 * 插入靓号
	 * @param beautiful
	 * @return
	 */
	public boolean insertBeautiful(FBeautiful beautiful);
	
	/**
	 * 更新靓号
	 * @param beautiful
	 * @return
	 */
	public boolean updateBeautiful(FBeautiful beautiful);
	
	/**
	 * 删除靓号
	 * @param fid
	 * @return
	 */
	public boolean deleteBeautiful(Integer fid);
}
