package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FBeautiful;

/**
 * 用户靓号数据接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FBeautifulMapper {

	/**
	 * 查询靓号
	 * @param fbid 靓号
	 * @return 实体对象
	 */
	FBeautiful selectByBid(Integer fbid);

}