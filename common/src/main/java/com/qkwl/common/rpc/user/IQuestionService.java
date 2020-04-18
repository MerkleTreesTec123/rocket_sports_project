package com.qkwl.common.rpc.user;

import java.util.List;

import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FMessage;
import com.qkwl.common.dto.user.FQuestion;

/**
 * 提问接口
 * @author TT
 */
public interface IQuestionService {
	
	/**
	 * 提问
	 * @param question 实体对象
	 * @param pid 父ID
	 * @param ip IP地址
	 * @return true：成功，false：失败
	 * @throws BCException 执行失败
	 */
	public boolean insertQuestion(FQuestion question, int pid, String ip) throws BCException;
	
	
	/**
	 * 根据id查询问答记录
	 * @param fid 问答主键ID
	 * @return 实体对象
	 */
	public FQuestion selectQuestionById(int fid);
	
	/**
	 * 根据id查询问答记录列表
	 * @param fid 问答主键ID
	 * @return 实体对象列表
	 */
	public List<FQuestion> selectDetailById(int fid);

	/**
	 * 分页查询问答记录
	 * @param param 分页实体对象
	 * @param operation 问答实体对象
	 * @return  分页实体对象
	 */
	public Pagination<FQuestion> selectPageQuestionList(Pagination<FQuestion> param,FQuestion operation);
	
	/**
	 * 根据id删除问答记录
	 * @param fid 			用户id
	 * @param isValidUser	是否验证用户(false-不验证，ture-验证)
	 * @param fuid			验证用户才传，不验证时直接传0
	 * @return true：成功，false：失败
	 */
	public boolean deleteQuestionById(int fid,boolean isValidUser,int fuid);
	
	/**
	 * 根据id查询用户消息记录
	 * @param fid		消息ID
	 * @param isRead	是否更新消息状态
	 * @return 消息实体对象
	 */
	public FMessage updateFMessageById(int fid,boolean isRead);
	
	/**
	 * 查询用户系统记录
	 * @param param 分页实体对象
	 * @param operation 消息实体对象
	 * @return 分页实体对象
	 */
	public Pagination<FMessage> selectPageFMessageList(Pagination<FMessage> param,FMessage operation);
	
	/**
	 * 根据id删除用户消息记录
	 * @param fid 			用户id
	 * @param isValidUser	是否验证用户(false-不验证，ture-验证)
	 * @param fuid			验证用户才传，不验证时直接传0
	 * @return true：成功，false：失败
	 */
	public boolean deleteMessageById(int fid,boolean isValidUser,int fuid);
}
