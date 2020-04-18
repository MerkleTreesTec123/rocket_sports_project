package com.qkwl.service.activity.config;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.properties.MQProperties;
import com.qkwl.service.activity.mq.MQAdminActionListener;
import com.qkwl.service.activity.mq.MQUserActionListener;

import com.qkwl.service.activity.mq.MQUserScoreListener;
import com.qkwl.service.activity.mq.MQValidateListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
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
    public ProducerBean validateProducer(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getPid().getValidate());
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    public ValidateHelper validateHelper(ProducerBean validateProducer,RedisHelper redisHelper){
        ValidateHelper validateHelper = new ValidateHelper();
        validateHelper.setValidateProducer(validateProducer);
        validateHelper.setRedisHelper(redisHelper);
        return validateHelper;
    }

    @Bean
    public MQAdminActionListener adminActionListener() {
        return new MQAdminActionListener();
    }

    @Bean
    public Map<Subscription, MessageListener> adminActionSubscription(MQAdminActionListener adminActionListener) {
        Subscription subscription = new Subscription();
        subscription.setTopic(MQTopic.ADMIN_ACTION);
        subscription.setExpression("*");
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        subscriptionTable.put(subscription, adminActionListener);
        return subscriptionTable;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean adminActionConsumer(MQProperties mqProperties, Map<Subscription, MessageListener> adminActionSubscription) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getAdminAction());
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);
        consumerBean.setSubscriptionTable(adminActionSubscription);
        return consumerBean;
    }

    @Bean
    public MQUserActionListener userActionListener() {
        return new MQUserActionListener();
    }

    @Bean
    public Map<Subscription, MessageListener> userActionSubscription(MQUserActionListener userActionListener) {
        Subscription subscription = new Subscription();
        subscription.setTopic(MQTopic.USER_ACTION);
        subscription.setExpression("*");
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        subscriptionTable.put(subscription, userActionListener);
        return subscriptionTable;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean userActionConsumer(MQProperties mqProperties, Map<Subscription, MessageListener> userActionSubscription) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getUserAction());
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);
        consumerBean.setSubscriptionTable(userActionSubscription);
        return consumerBean;
    }

    @Bean
    public MQUserScoreListener scoreListener() {
        return new MQUserScoreListener();
    }

    @Bean
    public Map<Subscription, MessageListener>  scoreSubscription(MQUserScoreListener scoreListener) {
        Subscription subscription = new Subscription();
        subscription.setTopic(MQTopic.SCORE);
        subscription.setExpression("*");
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        subscriptionTable.put(subscription, scoreListener);
        return subscriptionTable;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean scoreConsumer(MQProperties mqProperties, Map<Subscription, MessageListener> scoreSubscription) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getScore());
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);
        consumerBean.setSubscriptionTable(scoreSubscription);
        return consumerBean;
    }

    @Bean
    public MQValidateListener validateListener() {
        return new MQValidateListener();
    }

    @Bean
    public Map<Subscription, MessageListener> validateSubscription(MQValidateListener validateListener) {
        Subscription subscription = new Subscription();
        subscription.setTopic(MQTopic.VALIDATE);
        subscription.setExpression("*");
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        subscriptionTable.put(subscription, validateListener);
        return subscriptionTable;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean validateConsumer(MQProperties mqProperties, Map<Subscription, MessageListener> validateSubscription) {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqProperties.getCid().getValidate());

        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);
        consumerBean.setSubscriptionTable(validateSubscription);

        return consumerBean;
    }

}
