package com.qkwl.web.config;

import com.qkwl.common.auth.SessionContextUtils;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.oss.OssHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;


@Configuration
public class BeanConfig {

    @Bean
    public SessionContextUtils sessionContextUtils() {
        return new SessionContextUtils();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource i18nSource = new ResourceBundleMessageSource();
        i18nSource.setBasenames("i18n/international_msg", "i18n/page_msg", "i18n/result");
        return i18nSource;
    }

    @Bean(name = "localeResolver")
    public CookieLocaleResolver cookieLocaleResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.TAIWAN);
        resolver.setCookieName("oex_lan");
        resolver.setCookieMaxAge(31536000);
        return resolver;
    }

    @Bean
    public OssHelper ossHelper(RedisHelper redisHelper) {
        OssHelper ossHelper = new OssHelper();
        ossHelper.setRedisHelper(redisHelper);
        return ossHelper;
    }

}
