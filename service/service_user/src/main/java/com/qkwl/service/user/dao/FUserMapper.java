package com.qkwl.service.user.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUser;

/**
 * 用户数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserMapper {
	
	/**
	 * 新增用户
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(FUser record);

    /**
     * 根据id查询用户
     * @param fid 主键ID
     * @return 实体对象
     */
    FUser selectByPrimaryKey(Integer fid);
    
    /**
     * 根据显示ID查询用户
     * @param fshowid 显示ID
     * @return 实体对象
     */
    FUser selectByShowId(Integer fshowid);
    
    /**
     * 更新用户
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FUser record);

    /**
     * 更新用户安全信息
     * @param record 实体对象
     * @return 成功条数
     */
    int updateSecurityByPrimaryKey(FUser record);
    
    /**
     * 根据实体查询用户列表
     * @param user 实体对象
     * @return 实体对象列表
     */
    List<FUser> getUserListByParam(FUser user);

    /**
     * 根据实体查询用户数量
     * @param user 实体对象
     * @return 实体对象列表
     */
    Integer getUserCountByParam(Map<String,Object> param);

    /**
     * 更新用户登录时间
     * @param user
     * @return 成功条数
     */
    int updateLoginTime(FUser user); 
    
    /**
     * 更新用户杠杆状态
     * @param user 实体对象
     * @return 杠杆状态
     */
    int updateLeverLock(FUser user);
    
    /**
     * 查询推广人数
     * @param fuid 推荐人用户ID
     * @return 推广人数
     */
    int selectIntroUserCount(int fuid);
    
    /**
     * 查询IP人数
     * @param flastip
     * @return 本IP人数
     */
    int selectIpUserCount(String flastip);

    /**
     * 更新主键ID
     * @param fshowid
     * @param fid 旧主键Id
     * @return
     */
    int updateFshowidByFid(@Param("fshowid") int fshowid,@Param("fid") int fid);

    /**
     * 查询迁移数据是否存在
     * @param user
     * @return
     */
    int selectMigrationUser(FUser user);

    /**
     * 查询迁移用户
     * @param folduid
     * @return
     */
    FUser selectUserByOldUid(@Param("folduid") int folduid);
}