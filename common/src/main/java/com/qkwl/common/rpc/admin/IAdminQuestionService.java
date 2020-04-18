package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FQuestion;

/**
 * 后台问答接口
 * @author ZKF
 */
public interface IAdminQuestionService {
	
	/**
	 * 分页查询问题
	 * @param page 分页参数
	 * @param question 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<FQuestion> selectQuestionPageList(Pagination<FQuestion> page, FQuestion question);

	
	/**
	 * 更新问题
	 * @param question 问题实体
	 * @return 是否更新成功
	 */
	public boolean updateAnswerQuestion(FQuestion question);
	
	
	/**
	 * 根据id查询问题
	 * @param fid 问题id
	 * @return 问题实体
	 */
	public FQuestion selectQuestionById(int fid);
}
