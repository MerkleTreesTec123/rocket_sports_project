package com.qkwl.common.framework.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.mq.MQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 积分队列发送公共接口
 * @author ZKF
 * 
 * PS: 新调用在spring中增加以下配置
 *  <bean id="scoreHelper" class="com.qkwl.common.framework.mq.ScoreHelper">
		<property name="scoreProducer" ref="scoreProducer" />
	</bean>
	
	调用：
	
	private ScoreHelper scoreHelper;
	
	登录：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.LOGIN.getCode(), 		"登录");
	充值：	scoreHelper.SendUserScore(fuid, rechargeAmount, ScoreTypeEnum.RECHARGE.getCode(), 	"充值"+ coinname + ":" + rechargeNum);
	交易：	scoreHelper.SendUserScore(fuid, tradeAmount, 	ScoreTypeEnum.TRADING.getCode(), 	"交易-" + tradeType + coinname + ":" + tradeNum);
	净资产：	scoreHelper.SendUserScore(fuid, assetsAmount, 	ScoreTypeEnum.ASSETLIMIT.getCode(), "净资产折合人民币：" + assetsAmount + "元");
	实名认证：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.REALNAME.getCode(), 	"实名认证");
	手机认证：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.PHONE.getCode(), 		"手机认证");
	邮箱认证：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.EMAIL.getCode(), 		"邮箱认证");
	谷歌认证：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.GOOGLE.getCode(), 	"谷歌认证");
	首次充值：	scoreHelper.SendUserScore(fuid, 0D, 			ScoreTypeEnum.FIRSTCHARGE.getCode(),"首次充值"+coinname);
 */
public class ScoreHelper {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ScoreHelper.class);
	
	private Producer scoreProducer;

	public void setScoreProducer(Producer scoreProducer) {
		this.scoreProducer = scoreProducer;
	}

	public void SendUserScore(Integer fuid, BigDecimal amount, Integer ftype, String fremark){
		FLogUserScore logUserScore = new FLogUserScore();
		logUserScore.setFuid(fuid);
		logUserScore.setFtype(ftype);
		logUserScore.setAmount(amount);
		logUserScore.setFremark(fremark);
		Message message = new Message(MQTopic.SCORE, MQConstant.TAG_USER_SCORE,
				JSON.toJSONBytes(logUserScore));
		message.setKey("USER_SCORE_" + UUID.randomUUID().toString());
		try {
			scoreProducer.send(message);
		} catch (ONSClientException e) {
			logger.error("ScoreMQ send failed");
		}
	}

	public void SendAsync(Integer fuid, BigDecimal amount, Integer ftype, String fremark){
		FLogUserScore logUserScore = new FLogUserScore();
		logUserScore.setFuid(fuid);
		logUserScore.setFtype(ftype);
		logUserScore.setAmount(amount);
		logUserScore.setFremark(fremark);
		Message message = new Message(MQTopic.SCORE, MQConstant.TAG_USER_SCORE,
				JSON.toJSONBytes(logUserScore));
		message.setKey("USER_SCORE_" + UUID.randomUUID().toString());
		scoreProducer.sendAsync(message, new SendCallback(){
			@Override
			public void onSuccess(SendResult sendResult) {}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("ScoreMQ sendAsync failed");
			}
		});
	}
}
