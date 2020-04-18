package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;

/**
 * 后台文章接口
 * @author ZKF
 */
public interface IAdminArticleService {
	
	/**
	 * 分页查询新闻
	 * @param page 分页参数
	 * @param article 分页实体参数
	 * @return 分页记录列表
	 */
	public Pagination<FArticle> selectArticlePageList(Pagination<FArticle> page, FArticle article);

	/**
	 * 新增新闻
	 * @param article 新闻实体
	 * @return 是否新增成功
	 */
	public boolean insertArticle(FArticle article);
	
	/**
	 * 查询新闻
	 * @param id 新闻id
	 * @return 新闻实体
	 */
	public FArticle selectArticleById(int id);
	
	/**
	 * 修改新闻
	 * @param article 新闻实体
	 * @return 是否更新成功
	 */
	public boolean updateArticle(FArticle article);
	
	/**
	 * 删除新闻
	 * @param id 新闻id        
	 * @return 是否删除成功
	 */
	public boolean deleteArticle(int id);
	
	
	/**
	 * 分页查询新闻类型
	 * @param page 分页参数
	 * @param article 实体参数
	 * @return 分页记录列表
	 */
	public Pagination<FArticleType> selectArticleTypePageList(Pagination<FArticleType> page, FArticleType article);
	
	/**
	 * 新增新闻类型
	 * @param article 新闻类型实体
	 * @return 是否新增成功
	 */
	public boolean insertArticleType(FArticleType article);
	
	/**
	 * 根据id查询新闻类型
	 * @param id 新闻类型id
	 * @return 新闻类型实体
	 */
	public FArticleType selectArticleTypeById(int id);
	
	/**
	 * 更新新闻类型
	 * @param article 新闻类型实体
	 * @return 是否更新成功
	 */
	public boolean updateArticleType(FArticleType article);
	
	/**
	 * 删除新闻类型
	 * @param id 新闻类型id
	 * @return 是否删除成功
	 */
	public boolean deleteArticleType(int id);
	
}
