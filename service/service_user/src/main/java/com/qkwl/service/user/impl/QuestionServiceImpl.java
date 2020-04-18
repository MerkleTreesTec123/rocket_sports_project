package com.qkwl.service.user.impl;

import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.MessageStatusEnum;
import com.qkwl.common.dto.Enum.QuestionIsAnswerEnum;
import com.qkwl.common.dto.Enum.QuestionStatusEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FMessage;
import com.qkwl.common.dto.user.FQuestion;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.rpc.user.IQuestionService;
import com.qkwl.service.user.dao.FMessageMapper;
import com.qkwl.service.user.dao.FQuestionMapper;
import com.qkwl.service.user.dao.FUserMapper;
import com.qkwl.service.user.utils.MQSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提问接口实现
 * @author LY
 */
@Service("questionService")
public class QuestionServiceImpl implements IQuestionService{
	
	@Autowired
	private FQuestionMapper questionMapper;
	@Autowired
	private FMessageMapper messageMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private MQSend mqSend;
	
	/**
	 * 提问
	 * @param question 实体对象
	 * @param pid 父ID
	 * @param ip IP地址
	 * @return true：成功，false：失败
	 * @throws BCException
	 * @see com.qkwl.common.rpc.user.IQuestionService#insertQuestion(com.qkwl.common.dto.user.FQuestion, int, java.lang.String)
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean insertQuestion(FQuestion question, int pid, String ip)  throws BCException {
		// 插入
		int result = questionMapper.insert(question);
		if (result <= 0) {
			return false;
		}
		// 追问
		if (pid > 0) {
			FQuestion f = questionMapper.selectByPrimaryKey(pid);
			f.setFcid(f.getFid());
			f.setFisanswer(QuestionIsAnswerEnum.YES);
			result = questionMapper.updateByPrimaryKey(f);
			if (result <= 0) {
				throw new BCException();
			}
		}
		FUser user = userMapper.selectByPrimaryKey(question.getFuid());
		// MQ_USER_ACTION
		mqSend.SendUserAction(user.getFagentid(), question.getFuid(), LogUserActionEnum.QUESTION_SUBMIT, ip);
		return true;
	}

	/**
	 * 根据id查询问答记录
	 * @param fid 问答主键ID
	 * @return 实体对象
	 * @see com.qkwl.common.rpc.user.IQuestionService#selectQuestionById(int)
	 */
	@Override
	public FQuestion selectQuestionById(int fid){
		return questionMapper.selectByPrimaryKey(fid);
	}
	
	/**
	 * 根据id查询问答记录列表
	 * @param id 问答主键ID
	 * @return 实体对象列表
	 * @see com.qkwl.common.rpc.user.IQuestionService#selectDetailById(int)
	 */
	@Override
	public List<FQuestion> selectDetailById(int id) {
		List<FQuestion> list = new ArrayList<FQuestion>();
		FQuestion fquestion = questionMapper.selectByPrimaryKey(id);
		if(fquestion != null){
			list.add(fquestion);
			while(fquestion.getFisanswer()!=0){
				fquestion = questionMapper.selectByPrimaryKey(fquestion.getFcid());
				list.add(fquestion);
			}
		}
		return list;
	}
	
	/**
	 * 分页查询问答记录
	 * @param param 分页实体对象
	 * @param operation 问答实体对象
	 * @return  分页实体对象
	 * @see com.qkwl.common.rpc.user.IQuestionService#selectPageQuestionList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.user.FQuestion)
	 */
	@Override
	public Pagination<FQuestion> selectPageQuestionList(Pagination<FQuestion> param,FQuestion operation){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", param.getOffset());
		map.put("limit", param.getPageSize());
		map.put("fuid", operation.getFuid());
		//读条数
		int count  = questionMapper.getPageFQuestionCount(map);
		if(count > 0) {
			//读记录
			List<FQuestion> list = questionMapper.getPageFQuestionList(map);
			param.setData(list);
		}
	    param.setTotalRows(count);
	    param.generate();
	    return param;
	}
	
	/**
	 * 根据id删除问答记录
	 * @param fid 			用户id
	 * @param isValidUser	是否验证用户(false-不验证，ture-验证)
	 * @param fuid			验证用户才传，不验证时直接传0
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.user.IQuestionService#deleteQuestionById(int, boolean, int)
	 */
	@Override
	public boolean deleteQuestionById(int fid,boolean isValidUser,int fuid){
		FQuestion fquestion= questionMapper.selectByPrimaryKey(fid);
		if(isValidUser){
			//如果早不到订单或者不是指定用户，直接跳出
			if (fquestion == null || fquestion.getFuid() != fuid) {
				return false;
			}
		}
		fquestion.setFstatus(QuestionStatusEnum.DEL.getCode());
		return questionMapper.updateByPrimaryKey(fquestion) > 0 ? true : false;
	}
	
	/*---------------------用户系统消息------------------------------*/
	
	/**
	 * 根据id查询用户消息记录
	 * @param fid		消息ID
	 * @param isRead	是否更新消息状态
	 * @return 消息实体对象
	 * @see com.qkwl.common.rpc.user.IQuestionService#updateFMessageById(int, boolean)
	 */
	@Override
	public FMessage updateFMessageById(int fid, boolean isRead){
		FMessage fmessage = messageMapper.selectByPrimaryKey(fid);
		if(isRead){
			fmessage.setFstatus(MessageStatusEnum.NOREAD.getCode());
			messageMapper.updateByPrimaryKey(fmessage);
		}
		return fmessage;
	}
	
	/**
	 * 查询用户系统记录
	 * @param param 分页实体对象
	 * @param operation 消息实体对象
	 * @return 分页实体对象
	 * @see com.qkwl.common.rpc.user.IQuestionService#selectPageFMessageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.user.FMessage)
	 */
	@Override
	public Pagination<FMessage> selectPageFMessageList(Pagination<FMessage> param,FMessage operation){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", param.getOffset());
		map.put("limit", param.getPageSize());
		map.put("freceiverid", operation.getFreceiverid());
		map.put("fstatus", operation.getFstatus());

		int count  =  messageMapper.getPageFMessageCount(map);
		if(count > 0) {
			List<FMessage> list = messageMapper.getPageFMessageList(map);
			param.setData(list);
		}
	    param.setTotalRows(count);
	    param.generate();
	    return param;
	}
	
	/**
	 * 根据id删除用户消息记录
	 * @param fid 			用户id
	 * @param isValidUser	是否验证用户(false-不验证，ture-验证)
	 * @param fuid			验证用户才传，不验证时直接传0
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.user.IQuestionService#deleteMessageById(int, boolean, int)
	 */
	@Override
	public boolean deleteMessageById(int fid,boolean isValidUser,int fuid){
		if(isValidUser){
			FMessage fquestion= messageMapper.selectByPrimaryKey(fid);
			//如果早不到订单或者不是指定用户，直接跳出
			if (fquestion == null || fquestion.getFreceiverid() != fuid) {
				return false;
			}
		}
		return messageMapper.deleteByPrimaryKey(fid) > 0 ? true : false;
	}
}
