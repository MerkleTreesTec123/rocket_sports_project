package com.qkwl.service.activity.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.service.activity.impl.AdminLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 收集管理员行为落日志
 * @author TT
 */
public class MQAdminActionListener implements MessageListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQAdminActionListener.class);
	
	@Autowired
	private AdminLogService adminLogService;	
	@Autowired 
	private MemCache memCache;
	
	@Override
	public Action consume(Message message, ConsumeContext context) {
		// body
		String body = new String(message.getBody());
		try {
			//System.out.println("AdminAction : " + body);
			// 序列号对象
			FLogAdminAction fLogAdminAction = JSON.parseObject(body, FLogAdminAction.class);
			// 幂等判断
			String result = memCache.get(RedisDBConstant.REDIS_DB_MQ, fLogAdminAction.getUuid());
			if (result != null && !"".equals(result)) {
				return Action.CommitMessage;
			}
			// 解析日志
			adminLogService.insertMQ(fLogAdminAction);
			adminLogService.upAdmin(fLogAdminAction);
			// 保存Redis
			String uuid = fLogAdminAction.getUuid();
			memCache.set(RedisDBConstant.REDIS_DB_MQ, uuid, uuid, MQConstant.MQ_EXPRIE_TIME);
			return Action.CommitMessage;
		} catch (Exception e) {	
			e.printStackTrace();
			logger.error("---> mq adminaction failed_" + message.getMsgID() + "_body_" + body);
			return Action.ReconsumeLater;
		}
	}
}
