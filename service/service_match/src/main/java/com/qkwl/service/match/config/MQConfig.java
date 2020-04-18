package com.qkwl.service.match.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.qkwl.common.framework.mq.MQSendHelper;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.properties.MQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author jany
 * @Date 17-4-20
 */
@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class MQConfig {

    @Bean
    public MQTopic mqTopic() {
        return new MQTopic();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean entrustStateProducer(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getEntrustState());
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean scoreProducer(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getScore());
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public MQSendHelper entrustMQSend(ProducerBean entrustStateProducer) {
        MQSendHelper mqSendHelper = new MQSendHelper();
        mqSendHelper.setProducer(entrustStateProducer);
        return mqSendHelper;
    }

    @Bean
    public ScoreHelper scoreHelper(ProducerBean scoreProducer) {
        ScoreHelper scoreHelper = new ScoreHelper();
        scoreHelper.setScoreProducer(scoreProducer);
        return scoreHelper;
    }
}
