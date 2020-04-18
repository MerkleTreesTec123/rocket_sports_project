package com.qkwl.service.admin.bc.utils;

import javax.annotation.Resource;

import com.qkwl.common.mq.MQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.dto.log.FLogUserAction;

import java.math.BigDecimal;

@Component("mqSend")
public class MQSend {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQSend.class);
	
	@Resource(name="adminActionProducer")
	private Producer adminActionProducer;
	
	@Resource(name="userActionProducer")
	private Producer userActionProducer;

	/**
	 * 发送管理员日志
	 * @param fagentid 券商id
	 * @param fadminid 管理员id
	 * @param fuid 用户id
	 * @param fdatatype 数据类型
	 * @param data 数据
	 * @param action 操作
	 * @throws BCException 队列发送异常
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, int fdatatype, BigDecimal data) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFagentid(fagentid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFdatatype(fdatatype);
		fLogAdminAction.setFdata(data);
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		adminActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("adminacttion mq send failed");
			}
		});
	}
	/**
	 * 发送管理员日志
	 * @param fagentid 券商id
	 * @param fadminid 管理员id
	 * @param fuid 用户id
	 * @param fdatatype 数据类型
	 * @param data 数据
	 * @param action 操作
	 * @param fcontent 备注
	 * @throws BCException 队列发送异常
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, int fdatatype, BigDecimal data,String fcontent) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFagentid(fagentid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFdatatype(fdatatype);
		fLogAdminAction.setFdata(data);
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFcontent(fcontent);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		adminActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("adminacttion mq send failed");
			}
		});
	}
	
	/**
	 * 发送用户日志
	 * @param fagentid 券商id
	 * @param fuid 用户id
	 * @param action 操作
	 * @param data 数据
	 * @param fdatatype 数据类型
	 * @param fcontent 内容
	 * @throws BCException 队列发送异常
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, String fcontent) throws BCException {
		FLogUserAction fLogUserAction = new FLogUserAction();
		fLogUserAction.setFuid(fuid);
		fLogUserAction.setFagentid(fagentid);
		fLogUserAction.setFtype(action.getCode());
		fLogUserAction.setFdatatype(fdatatype);
		fLogUserAction.setFdata(data);
		fLogUserAction.setFcontent(fcontent);
		Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
		message.setKey("USER_ACTION_" + fuid + "_" + action + "_" + data);
		userActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("useracttion mq send failed");
			}
		});
	}
	
	/**
	 * 发送用户日志
	 * @param fagentid 券商id
	 * @param fuid 用户id
	 * @param action 操作
	 * @param fdatatype 数据类型
	 * @param data 数据
	 * @throws BCException 队列发送异常
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data) throws BCException {
		FLogUserAction fLogUserAction = new FLogUserAction();
		fLogUserAction.setFuid(fuid);
		fLogUserAction.setFagentid(fagentid);
		fLogUserAction.setFtype(action.getCode());
		fLogUserAction.setFdatatype(fdatatype);
		fLogUserAction.setFdata(data);
		Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
		message.setKey("USER_ACTION_" + fuid + "_" + action + "_" + fdatatype + "_" + data);
		userActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("useracttion mq send failed");
			}
		});
	}
	
	/**
	 * 发送用户日志
	 * @param fagentid 券商id
	 * @param fuid 用户id
	 * @param action 操作
	 * @param fdatatype 数据类型
	 * @param data 数据
	 * @param ffees 费率
	 * @throws BCException 队列发送异常
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, BigDecimal ffees) throws BCException {
		FLogUserAction fLogUserAction = new FLogUserAction();
		fLogUserAction.setFuid(fuid);
		fLogUserAction.setFagentid(fagentid);
		fLogUserAction.setFtype(action.getCode());
		fLogUserAction.setFdatatype(fdatatype);
		fLogUserAction.setFdata(data);
		fLogUserAction.setFfees(ffees);
		Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
		message.setKey("USER_ACTION_" + fuid + "_" + action + "_" + fdatatype + "_" + data);
		userActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("useracttion mq send failed");
			}
		});
	}
	
	
	/**
	 * 发送用户日志
	 * @param fagentid 券商id
	 * @param fuid 用户id
	 * @param action 操作
	 * @param fdatatype 数据类型
	 * @param data 数据
	 * @param ffees 费率
	 * @param fbtcfees 网络手续费
	 * @throws BCException 队列发送异常
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, BigDecimal ffees, BigDecimal fbtcfees) throws BCException {
		FLogUserAction fLogUserAction = new FLogUserAction();
		fLogUserAction.setFuid(fuid);
		fLogUserAction.setFagentid(fagentid);
		fLogUserAction.setFtype(action.getCode());
		fLogUserAction.setFdatatype(fdatatype);
		fLogUserAction.setFdata(data);
		fLogUserAction.setFfees(ffees);
		fLogUserAction.setFbtcfees(fbtcfees);
		Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
		message.setKey("USER_ACTION_" + fuid + "_" + action + "_" + fdatatype + "_" + data);
		userActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("useracttion mq send failed");
			}
		});
	}
}
