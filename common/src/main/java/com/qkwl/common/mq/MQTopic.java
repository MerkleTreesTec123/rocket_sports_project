package com.qkwl.common.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tr
 * @date 17-5-27
 */
@ConfigurationProperties(prefix = "aliyun.mq.topic")
public class MQTopic {

    /**
     * TOPIC-委单状态
     */
    public static String ENTRUST_STATE;

    /**
     * TOPIC-用户行为
     */
    public static String USER_ACTION;

    /**
     * TOPIC-管理员行为
     */
    public static String ADMIN_ACTION;

    /**
     * TOPIC-验证相关
     */
    public static String VALIDATE;

    /**
     * TOPIC-积分相关
     */
    public static String SCORE;


    public void setEntrustState(String entrustState) {
        ENTRUST_STATE = entrustState;
    }

    public void setUserAction(String userAction) {
        USER_ACTION = userAction;
    }

    public void setAdminAction(String adminAction) {
        ADMIN_ACTION = adminAction;
    }

    public void setVALIDATE(String VALIDATE) {
        MQTopic.VALIDATE = VALIDATE;
    }

    public void setSCORE(String SCORE) {
        MQTopic.SCORE = SCORE;
    }
}
