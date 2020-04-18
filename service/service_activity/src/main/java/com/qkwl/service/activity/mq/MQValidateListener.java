package com.qkwl.service.activity.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.validate.ValidateSendDTO;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.service.activity.impl.ValidateServiceImpl;
import com.qkwl.service.activity.model.ValidateSmsDO;
import com.qkwl.service.activity.run.AutoBlackList;
import com.qkwl.service.activity.run.AutoStatistics;
import com.qkwl.service.activity.utils.JobUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 验证相关
 * @author TT
 */
public class MQValidateListener implements MessageListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQValidateListener.class);

	@Autowired
	private JobUtils jobUtils;
	
	@Autowired
	private ValidateServiceImpl validateService;

	@Autowired
	private AutoStatistics autoStatistics;
	@Autowired
	private AutoBlackList autoBlackList;

	@Override
	public Action consume(Message message, ConsumeContext context) {
		// body
		String body = new String(message.getBody());
		try {
			//System.out.println("Validate : " + body);
			if (message.getTag().equals(MQConstant.TAG_SMS_VALIDATE)
					|| message.getTag().equals(MQConstant.TAG_EMAIL_VALIDATE)) {
				ValidateSendDTO vs = JSON.parseObject(body, ValidateSendDTO.class);

				// 类型判断
				if(StringUtils.isEmpty(SendTypeEnum.getValueByCode(vs.getSendType()))){
					return Action.CommitMessage;
				}
				// 黑名单判断
				if(vs != null && !vs.getSendType().equals(SendTypeEnum.EMAIL.getCode())){
					List<ValidateSmsDO> list = autoBlackList.getBlackList();
					if(list != null && list.size() > 0){
						for(ValidateSmsDO sms : list){
							if(!StringUtils.isEmpty(sms.getPhone()) && vs.getPhone().equals(sms.getPhone())){
								System.out.println("black : " + vs.getPhone());
								return Action.CommitMessage;
							}
						}
					}
				}
				// 幂等判断
				String result = jobUtils.getRedisData(RedisDBConstant.REDIS_DB_MQ, vs.getUuid());
				if (result != null && !"".equals(result)) {
					return Action.CommitMessage;
				}
				// 操作
				if(validateService.updateSend(vs)) {
					// 保存Redis
					String uuid = vs.getUuid();
					jobUtils.setRedisData(RedisDBConstant.REDIS_DB_MQ, uuid, uuid, MQConstant.MQ_EXPRIE_TIME);
				} else {
					logger.error("MQValidateListener update failed : {}, {}", message.getMsgID(), body);
					return Action.CommitMessage;
				}
				// 统计埋点
				String key = RedisConstant.STATISTICS_VALIDATE_KEY + vs.getPlatformType() + "_" + vs.getSendType();
				autoStatistics.addCounterMap(key);

				return Action.CommitMessage;
			}
			return Action.ReconsumeLater;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("MQValidateListener send failed : {}, {}", message.getMsgID(), body);
			return Action.CommitMessage;
		}
	}
}
