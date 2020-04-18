package com.qkwl.service.activity.dao;

import java.util.List;

import com.qkwl.common.dto.user.FUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据接口
 * @author LY
 */
@Mapper
public interface FUserMapper {
	
	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	List<FUser> selectAll();
	
	/**
	 * ID查询用户
	 */
	FUser selectByPrimaryKey(int fid);

	/**
	 * 查询刷号用户
	 */
	List<FUser> listBrush();

	/**
	 * 禁用用户
	 */
	int updateStatus(FUser user);
}