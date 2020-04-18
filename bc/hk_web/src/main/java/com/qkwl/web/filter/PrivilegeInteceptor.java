package com.qkwl.web.filter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.redis.RedisConstant;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PrivilegeInteceptor implements HandlerInterceptor {

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 版本号
     */
    private final static int VERSION = 2;


    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        return preHandle20(request, response, arg2);
    }   

    private boolean preHandle20(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        String uri = request.getRequestURI();

        //对外api
        if (UrlController.isApiUrls(uri)) {
            if (!checkSign(request,false)) {
                ReturnResult returnResult = ReturnResult.FAILUER("签名错误");
                response.getWriter().write(JSON.toJSONString(returnResult));
                //System.out.println("签名错误");
                return false;
            }
            //System.out.println("签名正确");
            return true;
        }

        // 不接受任何jsp请求
        if (uri.endsWith(".jsp")) {
            return false;
        }
        // 只拦截.html结尾的请求
        if (!uri.endsWith(".html")) {
            return false;
        }
        // 跳过不需要验证的页面
        if (UrlController.isValidationUrls(uri)) {
            return true;
        }

        // cookie取值
        String token = getToken(request);

        // 签名验证
        //解决redisHelper为null无法注入问题
        if (redisHelper == null) {
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            redisHelper = (RedisHelper) factory.getBean("redisHelper");
        }
        // token验证
        if (StringUtils.isEmpty(token)) {
            ReturnResult rst = ReturnResult.FAILUER(401, "登录已失效，请重新登录!");
            response.getWriter().write(JSON.toJSONString(rst));
            return false;
        } else {
            String json = redisHelper.get(token);
            if (StringUtils.isEmpty(json)) {
                ReturnResult rst = ReturnResult.FAILUER(401, "登录已失效，请重新登录!");
                response.getWriter().write(JSON.toJSONString(rst));
                return false;
            } else {
                RedisObject obj = JSON.parseObject(json, RedisObject.class);
                if (Utils.getTimestamp().getTime() / 1000 - obj.getLastActiveDateTime() > Constant.EXPIRETIME) {
                    ReturnResult rst = ReturnResult.FAILUER(401, "登录已失效，请重新登录!");
                    response.getWriter().write(JSON.toJSONString(rst));
                    redisHelper.delete(token);
                    return false;
                }
                Object resultStr = obj.getExtObject();
                if (resultStr == null) {
                    ReturnResult rst = ReturnResult.FAILUER(401, "登录已失效，请重新登录!");
                    response.getWriter().write(JSON.toJSONString(rst));
                    return false;
                }

                //app的接口需要验证签名是否正确
                if (UrlController.isAppSignUrls(uri)){
                    if (!checkSign(request,true)) {
                        ReturnResult returnResult = ReturnResult.FAILUER(ReturnResult.FAILURE_SIGN_ERROR,"签名错误");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Signature",request.getParameter("Signature"));
                        returnResult.setData(jsonObject);
                        response.getWriter().write(JSON.toJSONString(returnResult));
                        System.out.println("errorSign = "+request.getParameter("Signature"));
                        return false;
                    }
                }

                FUser fuser = JSON.parseObject(resultStr.toString(), FUser.class);
                RedisObject newobj = new RedisObject();
                newobj.setExtObject(fuser);
                redisHelper.set(token, newobj, Constant.EXPIRETIME);
            }
        }
        return true;
    }

    public String getToken(HttpServletRequest request){
        String uri = request.getRequestURI();
        if (UrlController.isAppUrls(uri)){
            //TODO 如果是app请求还需要对特定的接口做签名的校验
            return request.getParameter("token");
        }
        Cookie[] cookies = request.getCookies();
        boolean cookieFlag = false;
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("open")) {
                    cookieFlag = true;
                }
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
                if (cookieFlag && token != null) {
                    break;
                }
            }
        }

        return token;
    }

    /**
     * api的签名验证
     *
     * @param httpServletRequest
     * @return
     */
    private boolean checkSign(HttpServletRequest httpServletRequest,boolean isApp) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        Map<String, String> realParams = new HashMap<>();
        //这四个参数是必须的

        if (!parameterMap.containsKey("AccessKeyId")
                || !parameterMap.containsKey("SignatureVersion")
                || !parameterMap.containsKey("SignatureMethod")
                || !parameterMap.containsKey("Timestamp")
                || !parameterMap.containsKey("Signature")) {
            System.out.println("params size = "+parameterMap.size());
            return false;
        }

        //签名方法不正确
        if (!httpServletRequest.getParameter("SignatureMethod").equals("HmacSHA256")) {
            System.out.println("SignatureMethod not equals HmacSHA256");
            return false;
        }

        String Signature = httpServletRequest.getParameter("Signature");
        String AccessKeyId = httpServletRequest.getParameter("AccessKeyId");
        //根据 AccessKeyId 获取对应的appSecret
        String appSecret = null;
        if(isApp){
            appSecret = findAppApiSecret(httpServletRequest.getParameter("token"));
        }else{
            appSecret = findWebApiSecret(AccessKeyId);
        }
        if (null == appSecret || "".equals(appSecret)) {
            System.out.println("appSecret == null");
            return false;
        }   
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            if (!"Signature".equals(key) && !"token".equals(key)) {
                realParams.put(key, httpServletRequest.getParameter(key));
            }
        }   
        String method = httpServletRequest.getMethod();
        String requestURI = httpServletRequest.getRequestURI();
        String host = "www.irocket.io";
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(requestURI).append('\n'); // /path
        SortedMap<String, String> map = new TreeMap<>(realParams);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }   
        sb.deleteCharAt(sb.length() - 1);
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey =
                    new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("NoSuchAlgorithmException ");
            return false;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("InvalidKeyException ");
            return false;
        }   
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.getEncoder().encodeToString(hash);
        Signature = Signature.replace("\n","");
        actualSign = actualSign.replace("\n","");
        if (!Signature.equals(actualSign)) {
            System.out.println("signature = " + Signature);
            System.out.println("actualSign = " + actualSign);
            StringBuilder logBuilder = new StringBuilder();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                logBuilder.append(key);
                logBuilder.append("=");
                logBuilder.append(httpServletRequest.getParameter(key));
                logBuilder.append("&");
            }
            System.out.println("error sign params = " + logBuilder.toString());
            return false;
        }
        return true;
    }

    private String findWebApiSecret(String appKey) {
        FApiAuth apiAuth = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + appKey);
        if (apiAuth == null || !apiAuth.isValid()) {
            return null;
        }
        return apiAuth.getFsecretkey();
    }

    private String findAppApiSecret(String token){
        FUser user = redisHelper.getCurrentUserInfoByToken(token);
        if (user == null) {
            return "";
        }
        String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
        String md5Account = MD5Util.md5(MD5Util.md5(token) + md5AccountId);
        String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_";
        String keyname = accountKeyInfo + md5Account;
        RedisObject redisObject = redisHelper.getRedisObject(keyname);
        if (redisObject != null && redisObject.getExtObject() != null) {
            String secret = redisObject.getExtObject().toString();
            redisHelper.setRedisData(token, secret,Constant.EXPIRETIME);
            return secret;
        }
        return "";
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

}
