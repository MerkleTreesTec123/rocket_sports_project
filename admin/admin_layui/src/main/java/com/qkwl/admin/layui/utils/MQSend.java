package com.qkwl.admin.layui.utils;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQSend {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQSend.class);

	private Producer logActionProducer;

	public void setLogActionProducer(Producer logActionProducer) {
		this.logActionProducer = logActionProducer;
	}

	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param action
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, LogAdminActionEnum action,String ip) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFip(ip);
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, String fip) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}

	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, String fip, String content) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFcontent(content);
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, double data) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendAdminAction(int fagentid, int fadminid, int fuid, LogAdminActionEnum action, int fdatatype, int fcapitaltype, double data) throws BCException {
		FLogAdminAction fLogAdminAction = new FLogAdminAction();
		fLogAdminAction.setFadminid(fadminid);
		fLogAdminAction.setFuid(fuid);
		fLogAdminAction.setFtype(action.getCode());
		fLogAdminAction.setFcreatetime(Utils.getTimestamp());
		fLogAdminAction.setFupdatetime(Utils.getTimestamp());
		fLogAdminAction.setFagentid(fagentid);
		Message message = new Message(MQTopic.ADMIN_ACTION, MQConstant.TAG_ADMIN_ACTION, JSON.toJSONString(fLogAdminAction).getBytes());
		message.setKey("ADMIN_ACTION_" + fadminid + "_" +fuid + "_" + action);
		logActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendAdminAction failed");
			}
		});
	}
}
