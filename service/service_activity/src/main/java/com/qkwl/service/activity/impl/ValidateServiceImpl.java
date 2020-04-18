package com.qkwl.service.activity.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.ParameterTypeEnum;
import com.qkwl.common.Enum.validate.SendStatusEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.validate.ValidateSendDTO;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.DateUtils;
import com.qkwl.service.activity.dao.ValidateEmailMapper;
import com.qkwl.service.activity.dao.ValidateSmsMapper;
import com.qkwl.service.activity.model.*;
import com.qkwl.service.activity.utils.JobUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service("validateService")
@Scope("prototype")
public class ValidateServiceImpl {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ValidateServiceImpl.class);

    @Autowired
    private ValidateSmsMapper smsMapper;
    @Autowired
    private ValidateEmailMapper emailMapper;
    @Autowired
    private JobUtils jobUtils;


//    public static void main(String args[]){
//    }

    /**
     * 发送消息
     *
     * @param vs 消息传输对象
     */
    public Boolean updateSend(ValidateSendDTO vs) {
        // 查询平台信息
        ValidatePlatformDO platform = jobUtils.getValidatePlatform(vs.getPlatformType());
        if (platform == null) {
            logger.error("平台信息未找到！" + vs.toString());
            return false;
        }
        // 查询账号信息
        ValidateAccountDO account = null;
        if (vs.getSendType().equals(SendTypeEnum.SMS_TEXT.getCode())) {
            account = jobUtils.getValidateAccount(platform.getSmsId());
        } else if (vs.getSendType().equals(SendTypeEnum.SMS_VOICE.getCode())) {
            account = jobUtils.getValidateAccount(platform.getVoiceSmsId());
        } else if (vs.getSendType().equals(SendTypeEnum.SMS_INTERNATIONAL.getCode())) {
            account = jobUtils.getValidateAccount(platform.getInternationalSmsId());
        } else if (vs.getSendType().equals(SendTypeEnum.EMAIL.getCode())) {
            account = jobUtils.getValidateAccount(platform.getEmailId());
        }
        if (account == null) {
            logger.error("账号信息未找到！");
            return false;
        }

        //由于现在没有手机注册短信模板，所以如果是手机注册不需要获取模板

        // 查询模板信息
        ValidateTemplateDO template = jobUtils.getValidateTemplate(vs.getPlatformType(), vs.getSendType(),
                vs.getBusinessType(), vs.getLanguageType());
        if (template == null) {
            logger.error("模板信息未找到！ info : {}" + vs.toString());
            return false;
        }   
        // 拼接内容
        String content = template.getTemplate();
        if (StringUtils.isEmpty(content)) {
            logger.error("模板内容未找到！");
            return false;
        }

        if (!StringUtils.isEmpty(template.getParams())) {
            try {
                logger.info("模板 = "+content);
                logger.info("params = "+template.getParams());
                content = getSendContent(content, template.getParams(), platform, vs);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("模板错误 "+e.getMessage());
                return false;
            }
        }

        // 组装发送请求
        if (vs.getSendType().equals(SendTypeEnum.EMAIL.getCode())) {
            if(StringUtils.isEmpty(vs.getEmail())){
                logger.error("邮箱地址为空, info:" + vs.toString());
                return true;
            }
            ValidateEmailDO email = new ValidateEmailDO();
            email.setUid(vs.getUid());
            email.setEmail(vs.getEmail());
            email.setPlatform(vs.getPlatformType());
            email.setTemplateId(template.getId());
            email.setCode(vs.getCode());
            email.setUuid(vs.getUuid());
            email.setStatus(SendStatusEnum.SEND_SUCCESS.getCode());
            email.setGmtCreate(new Date());
            email.setGmtSend(new Date());
            email.setVersion(0);
            email.setContent(content);
            email.setTitle(String.format("【%s】%s",platform.getName(),vs.getLanguageType().equals(LocaleEnum.EN_US.getCode()) ? "Email Notification" : "邮件通知"));
            email.setAlias(platform.getName());

            if (!sendAliEmail(account, email)) {
                email.setStatus(SendStatusEnum.SEND_FAILURE.getCode());
            }
            try {
                if (emailMapper.insert(email) <= 0) {
                    logger.error("邮件信息插入数据库失败");
                    return false;
                }
            } catch (Exception e) {
                logger.error("邮件新增异常, info:" + vs.toString(), e);
                return false;
            }
            return true;
        } else {
            if(StringUtils.isEmpty(vs.getPhone())){
                logger.error("短信手机为空, info:" + vs.toString());
                return true;
            }

            //如果是web注册短信
//            if (vs.getBusinessType() == BusinessTypeEnum.SMS_WEB_REGISTER.getCode()){
//                if (vs.getSendType().equals(SendTypeEnum.SMS_INTERNATIONAL.getCode())){
//                    content = "your verification code is "+vs.getCode();
//                }else{
//                    content = "尊敬的用户，您本次的验证码为"+vs.getCode()+"有效期10分钟。";
//                }
//            }

            ValidateSmsDO sms = new ValidateSmsDO();
            sms.setPhone(vs.getPhone());
            sms.setUid(vs.getUid());
            sms.setContent(content);
            sms.setSendType(vs.getSendType());
            sms.setPlatform(vs.getPlatformType());
            sms.setTemplateId(template.getId());
            sms.setGmtCreate(new Date());
            sms.setGmtSend(new Date());
            sms.setVersion(0);
            sms.setStatus(SendStatusEnum.SEND_SUCCESS.getCode());
            boolean send;
            if (vs.getSendType().equals(SendTypeEnum.SMS_INTERNATIONAL.getCode())) {
                send = send253InternationalSms(account, sms);
            } else if (vs.getSendType().equals(SendTypeEnum.SMS_VOICE.getCode())) {
                send = send253VoiceSms(account, sms);
            } else {
                send = send253NormalSMS(account,sms);
            }
            if (!send) {
                sms.setStatus(SendStatusEnum.SEND_FAILURE.getCode());
            }
            try {
                if (smsMapper.insert(sms) <= 0) {
                    return false;
                }
            } catch (Exception e) {
                logger.error("短信新增异常, info:" + vs.toString(), e);
                return false;
            }
            return true;
        }
    }

    /**
     * 短信内容组装
     *
     * @param content  内容
     * @param params   组装参数
     * @param platform 平台
     * @param vs       参数DTO
     * @return 短信内容
     */
    private String getSendContent(String content, String params, ValidatePlatformDO platform, ValidateSendDTO vs) {
        String[] paramArray = params.split("#");
        for (String aParamArray : paramArray) {
            if (StringUtils.isEmpty(aParamArray)) {
                continue;
            }
            Integer paramType = Integer.valueOf(aParamArray);
            if (paramType.equals(ParameterTypeEnum.CODE.getCode())) {
                content = content.replace("#code#", vs.getCode());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.DATE.getCode())) {
                content = content.replace("#date#", DateUtils.format(new Date(),
                        DateUtils.YYYY_MM_DD_HH_MM_SS));
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.ABBRDATE.getCode())) {
                content = content.replace("#abbrDate#", vs.getAbbrDate());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.DOMAIN.getCode())) {
                content = content.replace("#domain#", platform.getDomain());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.CUSTOMURL.getCode())) {
                content = content.replace("#customUrl#", vs.getCustomUrl());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.SIGN.getCode())) {
                content = content.replace("#sign#", platform.getSign());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.UUID.getCode())) {
                content = content.replace("#uuid#", vs.getUuid());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.USER.getCode())) {
                content = content.replace("#user#", vs.getUsername());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.TYPE.getCode())) {
                content = content.replace("#type#", vs.getType());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.PRICE.getCode())) {
                content = content.replace("#price#", MathUtils.decimalFormat(vs.getPrice()));
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.USERPRICE.getCode())) {
                content = content.replace("#userPrice#", MathUtils.decimalFormat(vs.getUserPrice()));
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.AMOUNT.getCode())) {
                content = content.replace("#amount#", MathUtils.decimalFormat(vs.getAmount()));
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.COIN.getCode())) {
                content = content.replaceAll("#coin#", vs.getCoin());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.UID.getCode())) {
                content = content.replaceAll("#uid#", vs.getUid().toString());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.PLATFORM.getCode())) {
                content = content.replaceAll("#platform#", platform.getName());
                continue;
            }
            if (paramType.equals(ParameterTypeEnum.IPADDRESS.getCode())) {
                content = content.replaceAll("#ipAddress#", vs.getIp());
            }
        }
        return content;
    }

    /**
     * 普通短信
     * @return
     */
    private boolean send253NormalSMS(ValidateAccountDO va,ValidateSmsDO vs) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", va.getAccessKey());
        jsonObject.put("password", va.getSecretKey());
        jsonObject.put("msg",vs.getContent());
        jsonObject.put("phone",vs.getPhone());

        String submitString = jsonObject.toString();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder().url(va.getUrl())
                .post(RequestBody.create(MediaType.parse("application/json"), submitString))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            JSONObject jsonObj = (JSONObject) JSON.parse(execute.body().string());
            logger.info("phone = " + vs.getPhone()+" code = " + jsonObj.getString("code") + " errorMsg = "+jsonObj.getString("errorMsg"));
            return "0".equals(jsonObject.getString("code"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

        // JSONObject jsonObject = new JSONObject();
        // jsonObject.put("account", va.getAccessKey());
        // jsonObject.put("password", va.getSecretKey());
        // jsonObject.put("msg",vs.getContent());
        // jsonObject.put("mobile",vs.getPhone());

        // String submitString = jsonObject.toString();
        // OkHttpClient okHttpClient = new OkHttpClient.Builder()
        //         .build();
        // Request request = new Request.Builder().url(va.getUrl())
        //         .post(RequestBody.create(MediaType.parse("application/json"), submitString))
        //         .build();
        // try {
        //     Response execute = okHttpClient.newCall(request).execute();
        //     JSONObject jsonObj = (JSONObject) JSON.parse(execute.body().string());
        //     if ( "0".equals(jsonObj.getString("code"))) {
        //         return true;
        //     }
        //     logger.error(jsonObj.toJSONString());
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     logger.error("send Internation SMS error");
        // }

        // return false;
    }

    /**
     * 创蓝短信接口
     *
     * @param va 账号信息
     * @param vs 短信信息
     */
//    private static boolean send253NormalSms(ValidateAccountDO va, ValidateSmsDO vs) {
//        try {
//            // 创建StringBuffer对象用来操作字符串
//            StringBuilder sb = new StringBuilder();
//            sb.append(va.getUrl());
//            sb.append("?");
//            // APIKEY
//            sb.append("account=");
//            sb.append(va.getAccessKey());
//            // 用户名
//            sb.append("&pswd=");
//            sb.append(va.getSecretKey());
//            // 向StringBuffer追加手机号码
//            sb.append("&mobile=");
//            sb.append(vs.getPhone());
//            // 向StringBuffer追加消息内容转URL标准码
//            sb.append("&msg=");
//            sb.append(URLEncoder.encode(vs.getContent(), "UTF-8"));
//            // 是否状态报告
//            sb.append("&needstatus=" + 1);
//            // 创建url对象
//            URL url = new URL(sb.toString());
//            // 打开url连接
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            // 设置url请求方式 ‘get’ 或者 ‘post’
//            connection.setRequestMethod("POST");
//            // 发送
//            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//            // 返回发送结果
//            String inputline = in.readLine();
//            connection.disconnect();
//            // 输出结果
//            String result[] = inputline.split(",");
//            if (result[1] != null && !result[1].equals("0")) {
//                logger.info("send253NormalSms fail, phone:{}, {}", vs.getPhone(), inputline);
//                return false;
//            }
//        } catch (Exception e) {
//            logger.error("send253NormalSms failed, info:" + JSON.toJSONString(vs), e);
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     * 创蓝语音接口
     *
     * @param va 账号信息
     * @param vs 短信信息
     * @throws Exception 执行异常
     */
    private boolean send253VoiceSms(ValidateAccountDO va, ValidateSmsDO vs) {
        try {
            // 创建StringBuffer对象用来操作字符串
            StringBuffer sb = new StringBuffer(va.getUrl());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date()); //当前时间戳
            String signStr = "c52e35808b43930d011bce6d57489514" + vs.getPhone() + va.getSecretKey() + timestamp;
            String content = MD5(signStr).toLowerCase();

            JSONObject obj = new JSONObject();
            obj.put("organization", va.getAccessKey());
            obj.put("phonenum", vs.getPhone());
            obj.put("timestamp", timestamp);
            obj.put("vfcode", vs.getContent().substring(vs.getContent().length() - 6, vs.getContent().length()));
            obj.put("shownum", "95213141");
            obj.put("content", content);

            JSONObject param = new JSONObject();
            param.put("voiceinfo", obj);

            sb.append("&voiceinfo=" + URLEncoder.encode(param.toString(), "UTF-8"));
            // 创建url对象
            URL url = new URL(sb.toString());
            // 打开url连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置url请求方式 ‘get’ 或者 ‘post’
            connection.setRequestMethod("POST");
            // 发送
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            // 返回发送结果
            String inputline = in.readLine();
            connection.disconnect();
            JSONObject result = JSON.parseObject(inputline);
            if(!"0".equals(result.getString("code"))){
                logger.info("send253VoiceSms fail, phone:{}, {}", vs.getPhone(), inputline);
                return false;
            }
        } catch (Exception e) {
            logger.error("send253VoiceSms err, info:" + JSON.toJSONString(vs), e);
            return false;
        }
        return true;
    }

    /**
     * MD5加密
     */
    private static String MD5(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    /**
     * 创蓝国际短信接口
     *
     * @param va 账号信息
     * @param vs 短信信息
     */
    private boolean send253InternationalSms(ValidateAccountDO va, ValidateSmsDO vs) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", va.getAccessKey());
        jsonObject.put("password", va.getSecretKey());
        jsonObject.put("msg",vs.getContent());
        jsonObject.put("mobile",vs.getPhone());

        String submitString = jsonObject.toString();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder().url(va.getUrl())
                .post(RequestBody.create(MediaType.parse("application/json"), submitString))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            JSONObject jsonObj = (JSONObject) JSON.parse(execute.body().string());
            if ( "0".equals(jsonObj.getString("code"))) {
                return true;
            }
            logger.error(jsonObj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("send Internation SMS error");
        }

        return false;
    }

    /**
     * 发送阿里邮件
     *
     * @param va 账号信息
     * @param ve 短信信息
     */
    private static boolean sendAliEmail(ValidateAccountDO va, ValidateEmailDO ve) {
        boolean flag = true;
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", va.getAccessKey(), va.getSecretKey());
        try {
            DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setVersion("2017-06-22");
            request.setAccountName(va.getUrl());
            request.setAddressType(1);
            request.setReplyToAddress(true);
            request.setToAddress(ve.getEmail());
            request.setSubject(ve.getTitle());
            request.setHtmlBody(ve.getContent());
            request.setFromAlias(ve.getAlias());
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            logger.info("content = "+ve.getContent());
            logger.info("url = "+va.getUrl());
            logger.info("requestID = "+httpResponse.getRequestId());
            if("".equals(httpResponse.getRequestId())){
                flag = false;
            }
        } catch (ServerException e) {
            flag = false;
            e.printStackTrace();
        } catch (ClientException e) {
            logger.error("sendAliEmail failed, info:" + JSON.toJSONString(ve), e);
            flag = false;
        }
        return flag;
    }

    public static void main(String args[]) {
        ValidateAccountDO va = new ValidateAccountDO();
        va.setAccessKey("OL234wnqcP3a21");
        va.setSecretKey("rdGiT321ssbDiSjb2Wz12BE343ptot");
        va.setUrl("push@irocket.irocket.io");

        ValidateEmailDO ve = new ValidateEmailDO();
        ve.setEmail("776422800@qq.com");
        ve.setTitle("【IRocket】邮件通知");
        ve.setContent("邮件测试");
        ve.setAlias("IRocket");
        sendAliEmail(va, ve);
    }
}
