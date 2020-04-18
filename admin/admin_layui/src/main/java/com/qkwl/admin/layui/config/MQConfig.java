package com.qkwl.admin.layui.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.qkwl.admin.layui.utils.MQSend;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.properties.MQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class MQConfig {

    @Bean
    public MQTopic mqTopic() {
        return new MQTopic();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean logActionProducer(MQProperties mqProperties) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getAdminAction());

        ProducerBean producer = new ProducerBean();
        producer.setProperties(properties);
        return producer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean validateProducer(MQProperties mqProperties) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getValidate());
        ProducerBean producer = new ProducerBean();
        producer.setProperties(properties);
        return producer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean scoreProducer(MQProperties mqProperties) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getScore());
        ProducerBean producer = new ProducerBean();
        producer.setProperties(properties);
        return producer;
    }

    @Bean
    public ValidateHelper validateHelper(RedisHelper redisHelper, ProducerBean validateProducer) {
        ValidateHelper validateHelper = new ValidateHelper();
        validateHelper.setRedisHelper(redisHelper);
        validateHelper.setValidateProducer(validateProducer);
        return validateHelper;
    }

    @Bean
    public ScoreHelper scoreHelper(ProducerBean scoreProducer) {
        ScoreHelper scoreHelper = new ScoreHelper();
        scoreHelper.setScoreProducer(scoreProducer);
        return scoreHelper;
    }

    @Bean
    public MQSend mqSend(ProducerBean logActionProducer){
        MQSend mqSend = new MQSend();
        mqSend.setLogActionProducer(logActionProducer);
        return mqSend;
    }
}
