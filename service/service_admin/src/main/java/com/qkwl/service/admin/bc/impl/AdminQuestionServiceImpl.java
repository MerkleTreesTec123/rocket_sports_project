package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FQuestion;
import com.qkwl.common.rpc.admin.IAdminQuestionService;
import com.qkwl.service.admin.bc.dao.FAdminMapper;
import com.qkwl.service.admin.bc.dao.FQuestionMapper;

/**
 * 后台问答接口实现	
 */
@Service("adminQuestionService")
public class AdminQuestionServiceImpl implements IAdminQuestionService{
	
	@Autowired
	private FQuestionMapper questionMapper;
	@Autowired
	private FAdminMapper adminMapper;

	/**
	 * 分页查询问题
	 * @param page 分页参数
	 * @param question 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminQuestionService#getQuestionPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.user.FQuestion)
	 */
	@Override
	public Pagination<FQuestion> selectQuestionPageList(Pagination<FQuestion> page, FQuestion question) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("fstatus", question.getFstatus());

		int count = questionMapper.countQuestionPageList(map);
		if(count > 0) {
			List<FQuestion> questionList = questionMapper.getQuestionPageList(map);
			page.setData(questionList);
		}
		page.setTotalRows(count);
		
		return page;
	}

	/**
	 * 更新问题
	 * @param question 问题实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminQuestionService#updateAnswerQuestion(com.qkwl.common.dto.user.FQuestion)
	 */
	@Override
	public boolean updateAnswerQuestion(FQuestion question) {
		int i = questionMapper.answerQuestion(question);
		return i > 0 ? true : false;
	}

	/**
	 * 根据id查询问题
	 * @param fid 问题id
	 * @return 问题实体
	 * @see com.qkwl.common.rpc.admin.IAdminQuestionService#getQuestionById(int)
	 */
	@Override
	public FQuestion selectQuestionById(int fid) {
		FQuestion fquestion = questionMapper.selectByPrimaryKey(fid);
		
		if(fquestion.getFaid()!=null){
			FAdmin admin = adminMapper.selectByPrimaryKey(fquestion.getFaid());
			fquestion.setFadmin(admin.getFname());
		}
		return fquestion;
	}
}
