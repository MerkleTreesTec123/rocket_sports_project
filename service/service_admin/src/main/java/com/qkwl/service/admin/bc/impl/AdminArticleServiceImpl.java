package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.FArticleMapper;
import com.qkwl.service.admin.bc.dao.FArticleTypeMapper;
import com.qkwl.service.admin.bc.dao.FLanguageTypeMapper;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.rpc.admin.IAdminArticleService;

/**
 * 新闻后台接口实现
 */
@Service("adminArticleService")
public class AdminArticleServiceImpl implements IAdminArticleService{
	
	@Autowired
	private FArticleMapper articleMapper;
	@Autowired
	private FArticleTypeMapper articleTypeMapper;
	@Autowired
	private FLanguageTypeMapper languageTypeMapper;
	@Autowired
	private SystemRedisInit systemRedisInit;
	
	/**
	 * 分页查询新闻
	 * @param page 分页参数
	 * @param article 分页实体参数
	 * @return 分页记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#selectArticlePageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.news.FArticle)
	 */
	@Override
	public Pagination<FArticle> selectArticlePageList(Pagination<FArticle> page, FArticle article){
		
		Map<String, Object> map = new HashMap<String, Object>();
		//基础参数
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		//实体参数
		map.put("farticletype", article.getFarticletype());
		map.put("fagentid", article.getFagentid());
		map.put("ftype", article.getFtype());

		int count = articleMapper.countArticlePageList(map);
		if(count > 0) {
			List<FArticle> articleList = articleMapper.getArticlePageList(map);
			page.setData(articleList);
		}
		page.setTotalRows(count);
		page.generate();
		
		return page;
	}

	/**
	 * 查询新闻
	 * @param id 新闻id
	 * @return 新闻实体
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#selectArticleById(int)
	 */
	@Override
	public FArticle selectArticleById(int id){
		
		FArticle farticle = articleMapper.selectByPrimaryKey(id);
		
		FArticleType articleType = articleTypeMapper.selectByPrimaryKey(farticle.getFarticletype());
		farticle.setFarticletype_s(articleType.getFname());
		return farticle;
	}
	
	/**
	 * 新增新闻
	 * @param article 新闻实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#insertArticle(com.qkwl.common.dto.news.FArticle)
	 */
	@Override
	public boolean insertArticle(FArticle article){
		int i = articleMapper.insert(article);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initWebArticle(1);
		systemRedisInit.initWebArticle(2);
		return true;
	}

	/**
	 * 修改新闻
	 * @param article 新闻实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#updateArticle(com.qkwl.common.dto.news.FArticle)
	 */
	@Override
	public boolean updateArticle(FArticle article){
		int i = articleMapper.updateByPrimaryKey(article);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initWebArticle(1);
		systemRedisInit.initWebArticle(2);
		return true;
	}
	
	/**
	 * 删除新闻
	 * @param id 新闻id        
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#deleteArticle(int)
	 */
	@Override
	public boolean deleteArticle(int id){
		int i = articleMapper.deleteByPrimaryKey(id);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initWebArticle(1);
		systemRedisInit.initWebArticle(2);
		return true;
	}
	
	
	/**
	 * 分页查询新闻类型
	 * @param page 分页参数
	 * @param article 实体参数
	 * @return 分页记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#selectArticleTypePageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.news.FArticleType)
	 */
	@Override
	public Pagination<FArticleType> selectArticleTypePageList(Pagination<FArticleType> page, FArticleType article){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		int count = articleTypeMapper.countArticleTypePageList(map);
		if(count > 0) {
			List<FArticleType> articleTypeList = articleTypeMapper.getArticleTypePageList(map);
			for (FArticleType fArticleType : articleTypeList) {
				FSystemLan lang = languageTypeMapper.selectByPrimaryKey(fArticleType.getFlanguageid());
				fArticleType.setFlanguagename(lang.getFname());
			}
			page.setData(articleTypeList);
		}
		page.setTotalRows(count);
		return page;
	}
	
	/**
	 * 根据id查询新闻类型
	 * @param id 新闻类型id
	 * @return 新闻类型实体
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#selectArticleTypeById(int)
	 */
	@Override
	public FArticleType selectArticleTypeById(int id){
		FArticleType fArticleType = articleTypeMapper.selectByPrimaryKey(id);

		FSystemLan lang = languageTypeMapper.selectByPrimaryKey(fArticleType.getFlanguageid());
		fArticleType.setFlanguagename(lang.getFname());
		
		return fArticleType;
	}
	
	/**
	 * 新增新闻类型
	 * @param article 新闻类型实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#insertArticleType(com.qkwl.common.dto.news.FArticleType)
	 */
	@Override
	public boolean insertArticleType(FArticleType article){
		int i = articleTypeMapper.insert(article);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initArticleType();
		return true;
	}
	
	/**
	 * 更新新闻类型
	 * @param article 新闻类型实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#updateArticleType(com.qkwl.common.dto.news.FArticleType)
	 */
	@Override
	public boolean updateArticleType(FArticleType article){
		int i = articleTypeMapper.updateByPrimaryKey(article);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initArticleType();
		return true;
	}
	
	/**
	 * 删除新闻类型
	 * @param id 新闻类型id
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminArticleService#deleteArticleType(int)
	 */
	@Override
	public boolean deleteArticleType(int id){
		int i = articleTypeMapper.deleteByPrimaryKey(id);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initArticleType();
		return true;
	}
	
}
