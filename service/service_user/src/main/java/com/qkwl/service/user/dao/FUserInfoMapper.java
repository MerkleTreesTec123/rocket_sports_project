package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserInfo;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 用户信息表数据操作接口
 * @author LY
 *
 */
@Mapper
public interface FUserInfoMapper {

	/**
	 * 插入
	 * @param record
	 * @return
	 */
    int insert(FUserInfo record);

	/**
	 * 修改
	 * @param record
	 * @return
	 */
	int update(FUserInfo record);

    /**
     * 根据用户id查询用户信息表列表
     * @param fuid
     * @return
     */
    List<FUserInfo> selectByFuid(@Param("fuid") Integer fuid);
}