package com.qkwl.service.activity.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.service.activity.impl.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 收集用户行为落日志
 * @author TT
 */
public class MQUserActionListener implements MessageListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQUserActionListener.class);
	
	@Autowired
	private UserLogService userLogService;	
	@Autowired 
	private MemCache memCache;
	
	@Override
	public Action consume(Message message, ConsumeContext context) {
		// body
		String body = new String(message.getBody());
		try {
			//System.out.println("UserAction : " + body);
			// 序列号对象
			FLogUserAction fLogUserAction = JSON.parseObject(body, FLogUserAction.class);
			// 幂等判断
			String result = memCache.get(RedisDBConstant.REDIS_DB_MQ, fLogUserAction.getUuid());
			if (result != null && !"".equals(result)) {
				return Action.CommitMessage;
			}

			// 保存日志
			userLogService.insertMQ(fLogUserAction);
			// 生成每日数据
			userLogService.upDayMQ(fLogUserAction);
			// 保存Redis
			String uuid = fLogUserAction.getUuid();
			memCache.set(RedisDBConstant.REDIS_DB_MQ, uuid, uuid, MQConstant.MQ_EXPRIE_TIME);
			return Action.CommitMessage;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("---> mq useraction failed_" + message.getMsgID() + "_body_" + body);
			return Action.ReconsumeLater;
		}
	}
}
