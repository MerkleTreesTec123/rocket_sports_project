package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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
	 * 获取靓号列表
	 * @param map
	 * @return
	 */
	List<FBeautiful> getBeautifulPageList(Map<String, Object> map);
	
	/**
	 * 获取靓号列表条数
	 * @param map
	 * @return
	 */
	int getBeautifulPageCount(Map<String, Object> map);
	
	/**
	 * 查询靓号
	 * @param fid
	 * @return
	 */
	FBeautiful selectByFid(@Param("fid") Integer fid);
	
	/**
	 * 更新靓号
	 * @param fid
	 * @return
	 */
	int update(FBeautiful beautiful);
	
	/**
	 * 删除靓号
	 * @param fid
	 * @return
	 */
	int delete(@Param("fid") Integer fid);
	
	/**
	 * 插入靓号
	 * @param fid
	 * @return
	 */
	int insert(FBeautiful beautiful);

}