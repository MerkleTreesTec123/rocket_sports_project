package com.qkwl.common.framework.validate;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.validate.ValidateSendDTO;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.GUIDUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.common.util.ValidataeConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 短信邮件公共接口
 *
 *         PS: 新调用在spring中增加以下配置
 *         <bean id="validateHelper" class="com.qkwl.common.framework.validate.ValidateHelper">
 *         <property name="redisHelper" ref="redisHelper" />
 *         <property name="validateProducer" ref="validateProducer" />
 *         </bean>
 *
 *         如果redisHelper和validateProducer不存在需要再增加他们的配置
 */
public class ValidateHelper {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ValidateHelper.class);

    private Producer validateProducer;

    private RedisHelper redisHelper;


    public void setValidateProducer(Producer validateProducer) {
        this.validateProducer = validateProducer;
    }

    public void setRedisHelper(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 新版发送通知短信调用
     */
    private boolean sendBaseSms(ValidateSendDTO send) throws BCException {
        // MQ
        Message message = new Message(MQTopic.VALIDATE, MQConstant.TAG_SMS_VALIDATE,
                JSON.toJSONString(send).getBytes());
        message.setKey("VALIDATE_SMS_" + send.getPhone() + "_" + send.getPlatformType()
                + "_" + send.getBusinessType() + "_" + send.getSendType());
        validateProducer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(OnExceptionContext context) {
                logger.error("SendSMS mq send failed");
            }
        });
        return true;
    }

    public boolean smsValidateCode(ValidateSendDTO sendSmsMsg) throws BCException {
        return smsValidateCode(sendSmsMsg.getUid(), sendSmsMsg.getAreaCode(), sendSmsMsg.getPhone(), sendSmsMsg.getSendType(),
                sendSmsMsg.getPlatformType(), sendSmsMsg.getBusinessType(), sendSmsMsg.getLanguageType());
    }

    /**
     * 发送验证码短信
     */
    public boolean smsValidateCode(Integer uid, String areaCode, String phone, Integer sendType,
                                   Integer platform, Integer businessType, Integer languageType) throws BCException {
        if (StringUtils.isEmpty(areaCode) || StringUtils.isEmpty(phone) || sendType == null || platform == null
                || businessType == null || languageType == null) {
            logger.warn("smsValidateCode params is null, areaCode:{}, phone:{}, sendType:{}, platform:{}, businessType:{}, languageType:{}",
                    areaCode, phone, sendType, platform, businessType, languageType);
            return false;
        }
        String token = RedisConstant.VALIDATE_KEY + phone + "_" + platform + "_" + businessType;
        // 检查缓存
        RedisObject obj = redisHelper.getRedisObject(token);
        if (obj != null) {
            long lastActive = obj.getLastActiveDateTime();
            long now = System.currentTimeMillis() / 1000;
            if ((now - lastActive) < ValidataeConstant.MSG_SEND_TIME) {
                return false;
            }
        }
        //判断短信类型
        if (!areaCode.equals("86")) {
            if (phone.charAt(0) == '0') {
                phone = phone.substring(1, phone.length());
            }
            phone = areaCode + phone;
            sendType = SendTypeEnum.SMS_INTERNATIONAL.getCode();
            languageType = LocaleEnum.EN_US.getCode();
        } else {
            switch (sendType) {
                case 2:
                    sendType = SendTypeEnum.SMS_VOICE.getCode();
                    break;
                case 3:
                    sendType = SendTypeEnum.SMS_INTERNATIONAL.getCode();
                    break;
                default:
                    sendType = SendTypeEnum.SMS_TEXT.getCode();
            }
            //由于现在是走国际短信，所以如果是国内号码也要强制加上areacode
            //phone = areaCode + phone;
        }

        ValidateSendDTO send = new ValidateSendDTO();
        send.setUid(uid);
        send.setPhone(phone);
        send.setPlatformType(platform);
        send.setBusinessType(businessType);
        send.setSendType(sendType);
        send.setLanguageType(languageType);

        String code = Utils.randomInteger(6);
        send.setCode(code);

        // 投递Redis
        redisHelper.setRedisData(token, code, ValidataeConstant.MSG_OUT_TIME);

        return sendBaseSms(send);
    }

    /**
     * 发送价格提醒
     */
    public boolean smsPriceClock(FUser user, Integer sendType, Integer platform, Integer businessType, Integer languageType,
                                 String coinId, String type, BigDecimal currPrice, BigDecimal settingPrice) throws BCException {
        if (StringUtils.isEmpty(coinId) || StringUtils.isEmpty(type) || user == null || sendType == null || platform == null
                || businessType == null || languageType == null || currPrice == null || settingPrice == null) {
            logger.warn("smsValidateCode params is null, user:{}, sendType:{}, platform:{}, businessType:{}, languageType:{}" +
                            ", coinId:{}, type:{}, currPrice:{}, settingPrice:{}",
                    user, sendType, platform, businessType, languageType, coinId, type, currPrice, settingPrice);
            return false;
        }
        String phone = user.getFtelephone();
        // 国际短信处理
        if (!user.getFareacode().equals("86")) {
            if (phone.charAt(0) == '0') {
                phone = phone.substring(1, phone.length());
            }
            phone = user.getFareacode() + phone;
            sendType = SendTypeEnum.SMS_INTERNATIONAL.getCode();
            languageType = LocaleEnum.EN_US.getCode();
        }

        ValidateSendDTO send = new ValidateSendDTO();
        send.setUid(user.getFid());
        send.setPhone(phone);
        send.setPlatformType(platform);
        send.setBusinessType(businessType);
        send.setSendType(sendType);
        send.setLanguageType(languageType);
        send.setCoin(coinId);
        send.setType(type);
        send.setPrice(currPrice);
        send.setUserPrice(settingPrice);

        return sendBaseSms(send);
    }

    /**
     * 发送风控短信
     */
    public boolean smsRiskManage(String username, String phone, Integer platform, Integer businessType,
                                 String type, BigDecimal amount, String coin) throws BCException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(type) || StringUtils.isEmpty(coin)
                || platform == null || businessType == null || amount == null) {
            logger.warn("smsValidateCode params is null, username:{}, phone:{}, platform:{}, businessType:{}, type:{}, " +
                    "amount:{}, coin:{}", username, phone, platform, businessType, type, amount, coin);
            return false;
        }
        ValidateSendDTO send = new ValidateSendDTO();
        send.setUsername(username);
        send.setPhone(phone);
        send.setPlatformType(platform);
        send.setBusinessType(businessType);
        send.setSendType(SendTypeEnum.SMS_TEXT.getCode());
        send.setLanguageType(LocaleEnum.ZH_CN.getCode());
        send.setType(type);
        send.setAmount(amount);
        send.setCoin(coin);

        return sendBaseSms(send);
    }

    /**
     * 敏感信息短信
     */
    public boolean smsSensitiveInfo(String areaCode, String phone, Integer languageType,
                                    Integer platform, Integer businessType) throws BCException {
        if (StringUtils.isEmpty(areaCode) || StringUtils.isEmpty(phone) || languageType == null || platform == null || businessType == null) {
            logger.warn("smsSensitiveInfo params is null, areaCode:{}, phone:{}, languageType:{}, platform:{}, businessType:{}"
                    , areaCode, phone, languageType, platform, businessType);
            return false;
        }
        Integer sendType = SendTypeEnum.SMS_TEXT.getCode();
        // 国际短信处理
        if (StringUtils.isEmpty(areaCode)) {
            logger.error("----> mq send failed : areaCode is null");
            throw new BCException("区号为空!");
        }
        if (!areaCode.equals("86")) {
            if (phone.charAt(0) == '0') {
                phone = phone.substring(1, phone.length());
            }
            phone = areaCode + phone;
            sendType = SendTypeEnum.SMS_INTERNATIONAL.getCode();
            languageType = LocaleEnum.EN_US.getCode();
        }
        ValidateSendDTO send = new ValidateSendDTO();
        send.setPhone(phone);
        send.setPlatformType(platform);
        send.setBusinessType(businessType);
        send.setSendType(sendType);
        send.setLanguageType(languageType);

        return sendBaseSms(send);
    }


    /**
     * 发送用户充值短信
     */
    public boolean smsUserRecharge(String areaCode, String phone, Integer platform, Integer businessType,
                                   BigDecimal amount) throws BCException {
        if (StringUtils.isEmpty(areaCode) || StringUtils.isEmpty(phone) || amount == null || platform == null || businessType == null) {
            logger.warn("smsUserRecharge params is null, areaCode:{}, phone:{}, amount:{}, platform:{}, businessType:{}"
                    , areaCode, phone, amount, platform, businessType);
            return false;
        }
        Integer sendType = SendTypeEnum.SMS_TEXT.getCode();
        Integer languageType = LocaleEnum.ZH_CN.getCode();

        // 国际短信处理
        if (StringUtils.isEmpty(areaCode)) {
            logger.error("----> mq send failed : areaCode is null");
            throw new BCException("区号为空!");
        }
        if (!areaCode.equals("86")) {
            if (phone.charAt(0) == '0') {
                phone = phone.substring(1, phone.length());
            }
            phone = areaCode + phone;
            sendType = SendTypeEnum.SMS_INTERNATIONAL.getCode();
            languageType = LocaleEnum.EN_US.getCode();
        }

        ValidateSendDTO send = new ValidateSendDTO();
        send.setPhone(phone);
        send.setPlatformType(platform);
        send.setBusinessType(businessType);
        send.setSendType(sendType);
        send.setLanguageType(languageType);
        send.setAmount(amount);

        return sendBaseSms(send);
    }

    /**
     * 新版校验验证码
     */
    public Boolean validateSmsCode(String areaCode, String phone,
                                   Integer platform, Integer businessType, String code) {
        if (StringUtils.isEmpty(phone) || code == null || platform == null || businessType == null) {
            logger.warn("smsUserRecharge params is null, phone:{}, code:{}, platform:{}, businessType:{}"
                    , phone, code, platform, businessType);
            return false;
        }
        String token = RedisConstant.VALIDATE_KEY + phone + "_" + platform + "_" + businessType;
        String vadaliteCode = redisHelper.getRedisData(token);
        if (StringUtils.isEmpty(vadaliteCode)) {
            return false;
        }

        if (vadaliteCode.equals(code)) {
            redisHelper.deletRedisData(token);
            return true;
        }
        return false;
    }

    /**
     * 发送邮件验证码验证
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param businessType 业务枚举 {@link BusinessTypeEnum}
     * @param code         验证码
     * @return true 未通过，false 已通过
     */
    public Boolean mailCodeValidate(String email, Integer platform, Integer businessType, String code) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code) || platform == null || businessType == null) {
            logger.warn("mailCodeValidate params is null, email:{}, platform:{}, businessType:{}, code:{}",
                    email, platform, businessType, code);
            return true;
        }
        String token = RedisConstant.VALIDATE_KEY + businessType + "_" + platform + "_" + email;
        String vadaliteCode = redisHelper.getRedisData(token);
        if (StringUtils.isEmpty(vadaliteCode)) {
            return true;
        }
        if (vadaliteCode.equals(code)) {
            redisHelper.deletRedisData(token);
            return false;
        }
        return true;
    }

    /**
     * 发送邮件过期验证
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param businessType 业务枚举 {@link BusinessTypeEnum}
     * @return true 未过期，false 已过期
     */
    public Boolean mailOverdueValidate(String email, PlatformEnum platform, BusinessTypeEnum businessType) {
        if (StringUtils.isEmpty(email) || platform == null || businessType == null) {
            logger.warn("mailOverdueValidate params is null, email:{}, platform:{}, businessType:{}",
                    email, platform, businessType);
            return false;
        }
        String token = RedisConstant.VALIDATE_KEY + businessType.getCode() + "_" + platform.getCode() + "_" + email;
        String data = redisHelper.getRedisData(token);
        return !StringUtils.isEmpty(data);
    }

    /**
     * 发送内容邮件
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param locale       语言枚举 {@link LocaleEnum}
     * @param businessType 业务枚举 {@link BusinessTypeEnum}
     * @param ip           当前IP
     * @return true 成功，false 失败
     */
    public Boolean mailSendContent(String email, PlatformEnum platform, LocaleEnum locale, BusinessTypeEnum businessType, String ip, FUser user) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(ip) || platform == null
                || businessType == null || locale == null || user == null) {
            logger.warn("mailSendContent params is null, email:{}, platform:{}, businessType:{}, locale:{}, ip:{}, user:{}",
                    email, platform, businessType, locale, ip, user);
            return false;
        }
        // 判断是否已发送
        String token = RedisConstant.VALIDATE_KEY + businessType.getCode() + "_" + platform.getCode() + "_" + email;
        RedisObject json = redisHelper.getRedisObject(token);
        if (json != null) {
            return false;
        }
        String uuid = GUIDUtils.getGUIDString();
        // 数据组装
        ValidateSendDTO sendDTO = new ValidateSendDTO();
        sendDTO.setBusinessType(businessType.getCode());
        sendDTO.setEmail(email);
        sendDTO.setPlatformType(platform.getCode());
        sendDTO.setSendType(SendTypeEnum.EMAIL.getCode());
        sendDTO.setLanguageType(locale.getCode());
        sendDTO.setUuid(uuid);
        sendDTO.setUid(user.getFid());
        sendDTO.setUsername(user.getFloginname());
        sendDTO.setIp(ip);
        // 投递redis
        redisHelper.setRedisData(token, uuid, ValidataeConstant.EMAIL_OUT_TIME);
        // 发送
        return mailSendBase(sendDTO);
    }

    /**
     * 发送验证码邮件
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param languageType 语言枚举 {@link LocaleEnum}
     * @param ip           当前IP
     * @return true 成功，false 失败
     */
    public Boolean mailSendCode(String email, PlatformEnum platform, LocaleEnum languageType, String ip) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(ip) || platform == null
                || languageType == null) {
            logger.warn("mailSendCode params is null, email:{}, platform:{}, languageType:{}, ip:{}",
                    email, platform, languageType, ip);
            return false;
        }
        // 判断是否已发送
        String token = RedisConstant.VALIDATE_KEY + BusinessTypeEnum.EMAIL_REGISTER_CODE.getCode() + "_"
                + platform.getCode() + "_" + email;
        RedisObject json = redisHelper.getRedisObject(token);
        if (json != null) {
            return false;
        }
        // 数据组装
        String code = Utils.randomInteger(6);
        String uuid = GUIDUtils.getGUIDString();

        ValidateSendDTO sendDTO = new ValidateSendDTO();
        sendDTO.setBusinessType(BusinessTypeEnum.EMAIL_REGISTER_CODE.getCode());
        sendDTO.setCode(code);
        sendDTO.setEmail(email);
        sendDTO.setPlatformType(platform.getCode());
        sendDTO.setSendType(SendTypeEnum.EMAIL.getCode());
        sendDTO.setLanguageType(languageType.getCode());
        sendDTO.setUuid(uuid);
        sendDTO.setIp(ip);
        // 投递redis
        redisHelper.setRedisData(token, code, ValidataeConstant.EMAIL_CODE_OUT_TIME);
        // 发送
        return mailSendBase(sendDTO);
    }

    /**
     * 发送验证码邮件
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param languageType 语言枚举 {@link LocaleEnum}
     * @param ip           当前IP
     * @return true 成功，false 失败
     */
    public Boolean mailSendCode(String email, PlatformEnum platform,int businessType, LocaleEnum languageType, String ip) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(ip) || platform == null
                || languageType == null) {
            logger.warn("mailSendCode params is null, email:{}, platform:{}, languageType:{}, ip:{}",
                    email, platform, languageType, ip);
            return false;
        }
        // 判断是否已发送
        String token = RedisConstant.VALIDATE_KEY + businessType + "_"
                + platform.getCode() + "_" + email;
        RedisObject json = redisHelper.getRedisObject(token);
        if (json != null) {
            return false;
        }
        // 数据组装
        String code = Utils.randomInteger(6);
        String uuid = GUIDUtils.getGUIDString();

        ValidateSendDTO sendDTO = new ValidateSendDTO();
        sendDTO.setBusinessType(businessType);
        sendDTO.setCode(code);
        sendDTO.setEmail(email);
        sendDTO.setPlatformType(platform.getCode());
        sendDTO.setSendType(SendTypeEnum.EMAIL.getCode());
        sendDTO.setLanguageType(languageType.getCode());
        sendDTO.setUuid(uuid);
        sendDTO.setIp(ip);
        // 投递redis
        redisHelper.setRedisData(token, code, ValidataeConstant.EMAIL_CODE_OUT_TIME);
        // 发送
        return mailSendBase(sendDTO);
    }


    /**
     * /**
     * 发送价格提醒邮件
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param locale       语言枚举 {@link LocaleEnum}
     * @param businessType 业务枚举 {@link BusinessTypeEnum}
     * @param coinName     币种名称
     * @param type         类型
     * @param currPrice    当前价格
     * @param settingPrice 设置价格
     * @return true 成功，false 失败
     */
    public Boolean mailSendPriceClock(String email, PlatformEnum platform, LocaleEnum locale, BusinessTypeEnum businessType,
                                      String coinName, String type, BigDecimal currPrice, BigDecimal settingPrice, Integer uid) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(coinName) || StringUtils.isEmpty(type)
                || platform == null || locale == null || businessType == null || currPrice == null
                || settingPrice == null || uid == null) {
            logger.warn("mailSendPriceClock params is null, email:{}, platform:{}, locale:{}, businessType:{}, " +
                            "coinName:{}, type:{}, currPrice:{}, settingPrice:{}, uid:{}",
                    email, platform, locale, businessType, coinName, type, currPrice, settingPrice, uid);
            return false;
        }
        // 判断是否已发送
        String token = RedisConstant.PRICECLOCK_EMAIL_KEY + BusinessTypeEnum.EMAIL_PRICE_CLOCK + "_" + email;
        RedisObject json = redisHelper.getRedisObject(token);
        if (json != null) {
            return false;
        }
        // 数据组装
        String uuid = GUIDUtils.getGUIDString();
        ValidateSendDTO sendDTO = new ValidateSendDTO();
        sendDTO.setBusinessType(businessType.getCode());
        sendDTO.setEmail(email);
        sendDTO.setPlatformType(platform.getCode());
        sendDTO.setSendType(SendTypeEnum.EMAIL.getCode());
        sendDTO.setLanguageType(locale.getCode());
        sendDTO.setCoin(coinName);
        sendDTO.setType(type);
        sendDTO.setPrice(currPrice);
        sendDTO.setUserPrice(settingPrice);
        sendDTO.setUuid(uuid);
        sendDTO.setUid(uid);
        // 投递redis
        redisHelper.setRedisData(token, GUIDUtils.getGUIDString(), ValidataeConstant.EMAIL_OUT_TIME);
        // 发送
        return mailSendBase(sendDTO);
    }


    /**
     * /**
     * 发送价格提醒邮件
     *
     * @param email        邮件地址
     * @param platform     平台枚举 {@link PlatformEnum}
     * @param locale       语言枚举 {@link LocaleEnum}
     * @param businessType 业务枚举 {@link BusinessTypeEnum}
     * @param yesterdayStr 时间
     * @param customUrl    自定义url
     * @param account      账号
     * @return true 成功，false 失败
     */
    public Boolean mailSendFinanceBalance(String email, PlatformEnum platform, LocaleEnum locale, BusinessTypeEnum businessType,
                                      String yesterdayStr, String customUrl, String account) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(yesterdayStr) || StringUtils.isEmpty(customUrl)
                || platform == null || locale == null || businessType == null || account == null) {
            logger.warn("mailSendFinanceBalance params is null, email:{}, platform:{}, locale:{}, businessType:{}, " +
                            "yesterdayStr:{}, customUrl:{}, account:{}",
                    email, platform, locale, businessType, yesterdayStr, customUrl, account);
            return false;
        }

        // 数据组装
        String uuid = GUIDUtils.getGUIDString();
        ValidateSendDTO sendDTO = new ValidateSendDTO();
        sendDTO.setBusinessType(businessType.getCode());
        sendDTO.setEmail(email);
        sendDTO.setPlatformType(platform.getCode());
        sendDTO.setSendType(SendTypeEnum.EMAIL.getCode());
        sendDTO.setLanguageType(locale.getCode());
        sendDTO.setUuid(uuid);
        sendDTO.setUsername(account);
        sendDTO.setAbbrDate(yesterdayStr);
        sendDTO.setCustomUrl(customUrl);



        // 发送
        return mailSendBase(sendDTO);
    }


    /**
     * 发送邮件
     *
     * @param sendDTO 验证实体 {@link ValidateSendDTO}
     */
    private Boolean mailSendBase(ValidateSendDTO sendDTO) {
        String messageey = "VALIDATE_EMAIL_" + sendDTO.getEmail() + "_" + sendDTO.getPlatformType()
                + "_" + sendDTO.getBusinessType() + "_" + sendDTO.getSendType();
        // MQ
        Message message = new Message(MQTopic.VALIDATE, MQConstant.TAG_EMAIL_VALIDATE, JSON.toJSONString(sendDTO).getBytes());
        message.setKey(messageey);
        validateProducer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(OnExceptionContext context) {
                logger.error("SendEmail mq send failed");
            }
        });
        return true;
    }
}
