package com.qkwl.service.market.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.mq.MQEntrustState;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.service.market.run.AutoEntrustState;
import com.qkwl.service.market.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 委单状态处理
 * @author TT
 */
public class MQEntrustStateListener implements MessageListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQEntrustStateListener.class);

	@Autowired
	private JobUtils jobUtils;
	@Autowired
	private AutoEntrustState entrustStateQueue;

	@Override
	public Action consume(Message message, ConsumeContext context) {
		// 判断幂等
		String result = jobUtils.getRedisData(RedisDBConstant.REDIS_DB_MQ, message.getKey());
		// 判断
		if (result != null && !"".equals(result)) {
			logger.error("result is not null");
			return Action.CommitMessage;
		}

		// body
		String body = new String(message.getBody());
		try {
			//System.out.println("EntrustState : " + body);
			// 序列号对象
			MQEntrustState entrustState = JSON.parseObject(body, MQEntrustState.class);
			// 参数判断
			if (entrustState == null) {
				return Action.ReconsumeLater;
			}
			// 币种判断
			SystemTradeType tradeType = jobUtils.getTradeType(entrustState.getTradeId());
			if (tradeType == null) {
				return Action.ReconsumeLater;
			}
			// 添加队列
			if (!entrustStateQueue.addEntrustState(entrustState)) {
				return Action.ReconsumeLater;
			}
			jobUtils.setRedisData(RedisDBConstant.REDIS_DB_MQ, message.getKey(), message.getKey(), MQConstant.MQ_EXPRIE_TIME);
			return Action.CommitMessage;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("MQInsertEntrustlistener failed : {} body : {}", message.getMsgID(), body);
			return Action.ReconsumeLater;
		}
	}
}
