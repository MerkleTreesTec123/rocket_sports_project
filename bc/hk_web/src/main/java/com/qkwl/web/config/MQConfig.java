package com.qkwl.web.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
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
    public ProducerBean scoreProducerBean(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getScore());
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public ScoreHelper scoreHelper(ProducerBean scoreProducerBean) {
        ScoreHelper scoreHelper = new ScoreHelper();
        scoreHelper.setScoreProducer(scoreProducerBean);
        return scoreHelper;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean validateProducerBean(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getValidate());
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public ValidateHelper validateHelper(ProducerBean validateProducerBean, RedisHelper redisHelper) {
        ValidateHelper validateHelper = new ValidateHelper();
        validateHelper.setValidateProducer(validateProducerBean);
        validateHelper.setRedisHelper(redisHelper);
        return validateHelper;
    }
}
