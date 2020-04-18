package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.news.FArticle;

/**
 * 新闻-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FArticleMapper {
	
	/**
	 * 删除
	 * @param fid 新闻id
	 * @return 删除记录数
	 */
    int deleteByPrimaryKey(Integer fid);

    /**
     * 插入
     * @param record 新闻实体
     * @return 插入记录数
     */
    int insert(FArticle record);

    /**
     * 查询
     * @param fid 新闻id
     * @return 新闻实体
     */
    FArticle selectByPrimaryKey(Integer fid);

    /**
     * 更新
     * @param record 新闻实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FArticle record);
    
    /**
     * 查询
     * @param map 参数map
     * @return 新闻列表
     */
    List<FArticle> getFArticlePaginationList(Map<String, Object> map);
    
    /**
     * 查询数目
     * @param map 参数map
     * @return 查询记录数
     */
    int getFArticleCount(Map<String, Object> map);
    
    /**
     * 查询
     * @param map 参数map
     * @return 新闻列表
     */
    List<FArticle> getArticlePageList(Map<String, Object> map);
    
    /**
     * 查询数目
     * @param map 参数map
     * @return 查询记录数
     */
    int countArticlePageList(Map<String, Object> map);
}