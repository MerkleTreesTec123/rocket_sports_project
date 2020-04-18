package com.qkwl.common.Enum.redis;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;

/**
 * Created by ZKF on 2017/9/4.
 */
public enum RedisTypeEnum {

    Article(1, "新闻"),
    AboutUs(2, "关于我们"),
    FriendshipLink(3, "友链"),
    Coin(4, "币种"),
    Trade(5, "交易"),
    Finance(6, "存币理财"),
    SystemArgs(7, "系统参数"),
    Language(8, "语言"),
    SystemBank(9, "系统银行"),
    Agent(10, "券商"),
    SmsEmail(13, "短信邮件配置"),
    Activity(15, "活动配置"),
    AuthApi(17,"授权api");

    private Integer code;
    private String value;

    RedisTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (BusinessTypeEnum e : BusinessTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
