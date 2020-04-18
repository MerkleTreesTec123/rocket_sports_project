package com.qkwl.service.activity.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.service.activity.service.UserScoreService;
import com.qkwl.service.activity.utils.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 收集用户积分队列
 * @author ZKF
 */
public class MQUserScoreListener implements MessageListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQUserScoreListener.class);

	@Autowired
	private JobUtils jobUtils;
	@Autowired
	private UserScoreService userScoreService;
	
	@Override
	public Action consume(Message message, ConsumeContext context) {
		// body
		String body = new String(message.getBody());
		try {
			// 幂等判断
			String result = jobUtils.getRedisData(RedisDBConstant.REDIS_DB_MQ, message.getKey());
			if (result != null && !"".equals(result)) {
				return Action.CommitMessage;
			}
			// 序列号对象
//			FLogUserScore logUserScore = JSON.parseObject(body, FLogUserScore.class);
//			// 首冲
//			if (logUserScore.getFtype().equals(ScoreTypeEnum.RECHARGE.getCode())) {
//                if (userScoreService.isFirstCharge(logUserScore.getFuid())) {
//                    FLogUserScore firstLog = new FLogUserScore();
//                    firstLog.setFuid(logUserScore.getFuid());
//                    firstLog.setAmount(logUserScore.getAmount());
//                    firstLog.setFtype(ScoreTypeEnum.FIRSTCHARGE.getCode());
//                    firstLog.setFremark("首次" + logUserScore.getFremark());
//                    userScoreService.updateUserScore(firstLog);
//                }
//			}
//			// 增加积分、积分日志，更新运营数据
//			userScoreService.updateUserScore(logUserScore);
			// 保存Redis
			String uuid = message.getKey();
			jobUtils.setRedisData(RedisDBConstant.REDIS_DB_MQ, uuid, uuid, MQConstant.MQ_EXPRIE_TIME);
			return Action.CommitMessage;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mq useraction failed : {} body : {}", message.getMsgID(), body);
			return Action.ReconsumeLater;
		}
	}
}
