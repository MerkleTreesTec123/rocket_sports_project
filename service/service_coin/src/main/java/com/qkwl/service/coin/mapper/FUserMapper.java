package com.qkwl.service.coin.mapper;

import com.qkwl.common.dto.user.FUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
	
}