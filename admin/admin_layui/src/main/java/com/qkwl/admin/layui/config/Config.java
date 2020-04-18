package com.qkwl.admin.layui.config;

import com.qkwl.admin.layui.listener.PrivilegeInteceptor;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.oss.OssHelper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/**
 * @author tr
 * @date 17-4-27.
 */
@Configuration
public class Config extends WebMvcConfigurerAdapter {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
        factory.setMaxFileSize("2MB"); //KB,MB
        // 设置总上传数据总大小
        factory.setMaxRequestSize("2MB");
        //Sets the directory location where files will be stored.
        //factory.setLocation("路径地址");
        return factory.createMultipartConfig();
    }

    @Bean
    public OssHelper ossHelper(RedisHelper redisHelper) {
        OssHelper ossHelper = new OssHelper();
        ossHelper.setRedisHelper(redisHelper);
        return ossHelper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMyInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public HandlerInterceptor getMyInterceptor(){
        return new PrivilegeInteceptor();
    }

}
