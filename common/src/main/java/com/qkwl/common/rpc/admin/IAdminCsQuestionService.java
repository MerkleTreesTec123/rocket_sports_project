package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.admin.FCsQuestion;
import com.qkwl.common.dto.common.Pagination;

/**
 * 客服问题记录
 * @author ZKF
 */
public interface IAdminCsQuestionService {

	/**
	 * 新增客服问题记录
	 * @param cs
	 * @return
	 */
	public boolean insertQuestion(FCsQuestion cs);
	
	/**
	 * 更新客服问题记录
	 * @param cs
	 * @return
	 */
	public boolean updateQuestion(FCsQuestion cs);
	
	/**
	 * 查询客服问题记录
	 * @param csid
	 * @return
	 */
	public FCsQuestion selectQuestionById(Integer csid);
	
	/**
	 * 删除客服问题记录
	 * @param csid
	 * @return
	 */
	public boolean deleteQuestionById(Integer csid);
	
	/**
	 * 分页查询客服问题记录
	 * @param page
	 * @param cs
	 * @return
	 */
	public Pagination<FCsQuestion> selectQuestionByPage(Pagination<FCsQuestion> page, FCsQuestion cs);
}
