package com.qkwl.service.activity.impl;

import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.daylog.FDayOperat;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.FLogAdminActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("adminLogService")
public class AdminLogService {

	@Autowired
	private FLogAdminActionMapper fLogAdminActionMapper;
	@Autowired
	private LogActionTx logActionTx;
	
	/**
	 * MQ更新
	 * @param fLogAdminAction 管理员日志
	 * @throws Exception 执行异常
	 */
	public synchronized void insertMQ(FLogAdminAction fLogAdminAction) throws Exception {
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminActionMapper.insert(fLogAdminAction);
	}
	
	/**
	 * 定时器更新
	 * @param fLogAdminAction 管理员日志
	 * @throws Exception 执行异常
	 */
	public synchronized void upAdmin(FLogAdminAction fLogAdminAction) throws Exception {
		// 当天时间
		String nowTime = Utils.dateFormatYYYYMMDD(Utils.getTimestamp());
		// 运营 回复提问
		if (fLogAdminAction.getFtype().equals(LogAdminActionEnum.ANSWER_QUESTION.getCode())) {
			FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogAdminAction.getFagentid());
			fdDayOperat.setFreplyquestion(fdDayOperat.getFreplyquestion() + 1);
			fdDayOperat.setFupdatetime(Utils.getTimestamp());
			logActionTx.updatefDayOperat(fdDayOperat);
		}
	}
}
