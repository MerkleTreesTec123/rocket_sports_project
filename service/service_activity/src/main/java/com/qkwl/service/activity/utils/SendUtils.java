package com.qkwl.service.activity.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.qkwl.service.activity.model.ValidateAccountDO;
import com.qkwl.service.activity.model.ValidateEmailDO;
import com.qkwl.service.activity.model.ValidateSmsDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 发送工具
 * Created by ZKF on 2017/5/4.
 */
public class SendUtils {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(SendUtils.class);

    /**
     * 创蓝短信接口
     * @param va 账号信息
     * @param vs 短信信息
     * @throws Exception 执行异常
     */
    private static boolean send253NormalSms(ValidateAccountDO va, ValidateSmsDO vs) {
        try {
            // 创建StringBuffer对象用来操作字符串
            StringBuffer sb = new StringBuffer(va.getUrl() + "?");
            // APIKEY
            sb.append("account=" + va.getAccessKey());
            // 用户名
            sb.append("&pswd=" + va.getSecretKey());
            // 向StringBuffer追加手机号码
            sb.append("&mobile=" + vs.getPhone());
            // 向StringBuffer追加消息内容转URL标准码
            sb.append("&msg=" + URLEncoder.encode(vs.getContent(), "UTF-8"));
            // 是否状态报告
            sb.append("&needstatus=" + 1);
            // 创建url对象
            URL url = new URL(sb.toString());
            // 打开url连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置url请求方式 ‘get’ 或者 ‘post’
            connection.setRequestMethod("POST");
            // 发送
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            // 返回发送结果
            String inputline = in.readLine();
            // 输出结果
            System.out.println(inputline);
            connection.disconnect();
        } catch (Exception e) {
            logger.error("sendmsg err");
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 创蓝国际短信接口
     * @param va 账号信息
     * @param vs 短信信息
     * @throws Exception 执行异常
     */
    private boolean send253InternationalSms(ValidateAccountDO va, ValidateSmsDO vs) {
        try {
            // 创建StringBuffer对象用来操作字符串
            StringBuffer sb = new StringBuffer(va.getUrl() + "?");
            // APIKEY
            sb.append("un=" + va.getAccessKey());
            // 用户名
            sb.append("&pw=" + va.getSecretKey());
            // 向StringBuffer追加手机号码
            sb.append("&da=" + vs.getPhone());
            // 向StringBuffer追加消息内容转URL标准码
            sb.append("&sm=" + URLEncoder.encode(vs.getContent(), "UTF-8"));
            // 是否状态报告
            sb.append("&dc=15&rd=1&rf=2&tf=3");
            // 创建url对象
            URL url = new URL(sb.toString());
            // 打开url连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置url请求方式 ‘get’ 或者 ‘post’
            connection.setRequestMethod("POST");
            // 发送
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            // 返回发送结果
            String inputline = in.readLine();
            // 输出结果
            System.out.println(inputline);
            connection.disconnect();
        } catch (Exception e) {
            logger.error("sendmsg err");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送阿里邮件
     * @param va 账号信息
     * @param ve 短信信息
     * @throws Exception 执行异常
     */
    private boolean sendAliEmail(ValidateAccountDO va, ValidateEmailDO ve) {
        boolean flag = true;
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", va.getAccessKey(), va.getSecretKey());
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName(va.getUrl());
            request.setAddressType(1);
            request.setReplyToAddress(true);
            request.setToAddress(ve.getEmail());
            request.setSubject(ve.getTitle());
            request.setHtmlBody(ve.getContent());
			/*SingleSendMailResponse httpResponse = */client.getAcsResponse(request);
        } catch (ServerException e) {
            flag = false;
            e.printStackTrace();
        } catch (ClientException e) {
            logger.error("sendmail err");
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }


}
