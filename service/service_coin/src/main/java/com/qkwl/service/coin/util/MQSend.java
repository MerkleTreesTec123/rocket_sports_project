package com.qkwl.service.coin.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component("mqSend")
public class MQSend {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQSend.class);
	
	@Resource(name="userActionProducer")
	private Producer userActionProducer;

	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action) throws BCException {
		SendUserAction(fagentid, fuid, action, null,null, "", BigDecimal.ZERO,"" ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, String fip) throws BCException {
		SendUserAction(fagentid, fuid, action, null,null, fip, BigDecimal.ZERO,"" ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, String fip, int fdatatype, String fcontent) throws BCException {
		SendUserAction(fagentid, fuid, action, fdatatype,null, fip, BigDecimal.ZERO,fcontent ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, BigDecimal data, String ip) throws BCException {
		SendUserAction(fagentid, fuid, action, null,data,ip, BigDecimal.ZERO,"" ,null);
	}

	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, String ip) throws BCException {
		SendUserAction(fagentid, fuid, action, fdatatype,data,ip, BigDecimal.ZERO,"" ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, BigDecimal ffees, String ip) throws BCException {
		SendUserAction(fagentid, fuid, action, fdatatype,data,ip, ffees,"" ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, String fcontent, String ip) throws BCException {
		SendUserAction(fagentid, fuid, action, fdatatype,data,ip, BigDecimal.ZERO,fcontent ,null);
	}
	
	/**
	 * 发送 MQ_USER_ACTION
	 * @param fuid
	 * @param action
	 * @param data
	 * @throws BCException
	 */
	public void SendUserAction(int fagentid, int fuid, LogUserActionEnum action, int fdatatype, int fcapitaltype, BigDecimal data) throws BCException {
		SendUserAction(fagentid, fuid, action, fdatatype,data,"", BigDecimal.ZERO,"" ,fcapitaltype);
	}

	public void SendUserAction(int agentId, int uid, LogUserActionEnum action, Integer dataType, BigDecimal data, String ip, BigDecimal fees,String content , Integer capitalType ) throws BCException {
		FLogUserAction fLogUserAction = new FLogUserAction();
		fLogUserAction.setFuid(uid);
		fLogUserAction.setFtype(action.getCode());
		fLogUserAction.setFdatatype(dataType);
		fLogUserAction.setFcapitaltype(capitalType);
		fLogUserAction.setFdata(data);
		fLogUserAction.setFfees(fees);
		fLogUserAction.setFcontent(content);
		fLogUserAction.setFagentid(agentId);
		fLogUserAction.setFip(ip);
		Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
		message.setKey("USER_ACTION_" + uid + "_" + action + "_" + ip + "_" + dataType + "_" + data);
		userActionProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("MQ : SendUserAction failed");
			}
		});
	}
}
