package com.qkwl.service.user.utils;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component("mqSendUtils")
public class MQSendUtils {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MQSendUtils.class);

    @Resource(name = "userActionProducer")
    private Producer userActionProducer;

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action) {
        SendUserAction(fuid, action, null, null, "", BigDecimal.ZERO, "", null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, String fip) {
        SendUserAction(fuid, action, null, null, fip, BigDecimal.ZERO, "", null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, String fip, int fdatatype, String fcontent) {
        SendUserAction(fuid, action, fdatatype, null, fip, BigDecimal.ZERO, fcontent, null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, BigDecimal data, String ip) {
        SendUserAction(fuid, action, null, data, ip, BigDecimal.ZERO, "", null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, String ip) {
        SendUserAction(fuid, action, fdatatype, data, ip, BigDecimal.ZERO, "", null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, BigDecimal ffees, String ip) {
        SendUserAction(fuid, action, fdatatype, data, ip, ffees, "", null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data, String fcontent, String ip) {
        SendUserAction(fuid, action, fdatatype, data, ip, BigDecimal.ZERO, fcontent, null);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, int fdatatype, int fcapitaltype, BigDecimal data) {
        SendUserAction(fuid, action, fdatatype, data, "", BigDecimal.ZERO, "", fcapitaltype);
    }

    /**
     * 发送 MQ_USER_ACTION
     */
    public void SendUserAction(int fuid, LogUserActionEnum action, int fdatatype, BigDecimal data,BigDecimal fees) {
        SendUserAction(fuid, action, fdatatype, data, "", fees, "", null);
    }

    private void SendUserAction(int uid, LogUserActionEnum action, Integer dataType, BigDecimal data, String ip, BigDecimal fees, String content, Integer capitalType) {
        FLogUserAction fLogUserAction = new FLogUserAction();
        fLogUserAction.setFuid(uid);
        fLogUserAction.setFtype(action.getCode());
        fLogUserAction.setFdatatype(dataType);
        fLogUserAction.setFcapitaltype(capitalType);
        fLogUserAction.setFdata(data);
        fLogUserAction.setFfees(fees);
        fLogUserAction.setFcontent(content);
        fLogUserAction.setFagentid(0);
        fLogUserAction.setFip(ip);
        Message message = new Message(MQTopic.USER_ACTION, MQConstant.TAG_USER_ACTION, JSON.toJSONString(fLogUserAction).getBytes());
        message.setKey("USER_ACTION_" + uid + "_" + action + "_" + ip + "_" + dataType + "_" + data);
        userActionProducer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(OnExceptionContext context) {
                logger.error("MQ : SendUserAction failed");
            }
        });
    }
}
