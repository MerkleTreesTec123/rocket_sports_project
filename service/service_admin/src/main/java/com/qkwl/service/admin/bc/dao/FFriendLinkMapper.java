package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.web.FFriendLink;

import java.util.List;
import java.util.Map;

/**
 * 友情链接-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FFriendLinkMapper {
	
	/**
	 * 删除
	 * @param fid 友链id
	 * @return 删除记录数
	 */
    int deleteByPrimaryKey(Integer fid);

    /**
     * 增加
     * @param record 友链实体
     * @return 插入记录数
     */
    int insert(FFriendLink record);

    /**
     * 查找
     * @param fid 友链id
     * @return 友链实体
     */
    FFriendLink selectByPrimaryKey(Integer fid);

    /**
     * 更新友情链接
     * @param record 友链实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FFriendLink record);
    
    /**
     * 分页查询友情链接
     * @param map 参数map
     * @return 友链列表
     */
    List<FFriendLink> getLinkPageList(Map<String, Object> map);
    
    /**
     * 分页查询友情链接的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countLinkByParam(Map<String, Object> map);
    
    /**
	 * 查询所有列 
	 * @return 友链列表
	 */
	List<FFriendLink> selectAll();
}