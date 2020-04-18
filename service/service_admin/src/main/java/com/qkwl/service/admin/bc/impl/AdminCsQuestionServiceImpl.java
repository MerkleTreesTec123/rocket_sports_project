package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.dao.FCsQuestionMapper;
import com.qkwl.common.dto.admin.FCsQuestion;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminCsQuestionService;

@Service("adminCsQuestionService")
public class AdminCsQuestionServiceImpl implements IAdminCsQuestionService{
	
	@Autowired
	private FCsQuestionMapper csQuestionMapper;
	
	
	public boolean insertQuestion(FCsQuestion cs){
		int i = csQuestionMapper.insert(cs);
		return i > 0 ? true : false;
	}
	
	public boolean updateQuestion(FCsQuestion cs){
		int i = csQuestionMapper.updateByPrimaryKey(cs);
		return i > 0 ? true : false;
	}
	
	public FCsQuestion selectQuestionById(Integer csid){
		return csQuestionMapper.selectByPrimaryKey(csid);
	}
	
	public boolean deleteQuestionById(Integer csid){
		int i = csQuestionMapper.deleteByPrimaryKey(csid);
		return i > 0 ? true : false;
	}
	
	public Pagination<FCsQuestion> selectQuestionByPage(Pagination<FCsQuestion> page, FCsQuestion cs){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("fuid", cs.getFuid());
		map.put("fquestion", cs.getFquestion());
		map.put("foperation", cs.getFoperation());
		map.put("fstatus", cs.getFstatus());
		map.put("fdetail", cs.getFdetail());

		int count = csQuestionMapper.countByPage(map);
		page.setTotalRows(count);
		if(count <= 0) {
			return page;
		}
		List<FCsQuestion> list = csQuestionMapper.selectByPage(map);
		page.setData(list);
		return page;
	}
}
