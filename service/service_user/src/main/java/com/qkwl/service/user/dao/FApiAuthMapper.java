package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.api.FApiAuth;

import java.util.List;

/**
 * 用户API数据接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FApiAuthMapper {

	/**
	 * 增加api
	 * 
	 * @param record
	 *            API实体对象
	 * @return 成功条数
	 */
	int insert(FApiAuth record);

	/**
	 * 查询api
	 * 
	 * @param fid
	 *            主键ID
	 * @return API实体对象
	 */
	FApiAuth selectByPrimaryKey(Integer fid);

	/**
	 * 根据用户查询api
	 * 
	 * @param fuid
	 *            用户ID
	 * @return API列表
	 */
	List<FApiAuth> selectByUser(int fuid);

	/**
	 * 根据apikey查询api
	 * 
	 * @param apikey
	 *            用户ID
	 * @return API实体
	 */
	FApiAuth selectByApi(String apikey);

	/**
	 * 删除api
	 * 
	 * @param id
	 *            主键ID
	 * @return 成功条数
	 */
	int deleteByPrimaryKey(int id);
}