package com.qkwl.admin.layui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.qkwl.common.auth.SessionContextUtils;

@ImportResource(locations = "classpath:edas-hsf.xml")
@SpringBootApplication
@EnableScheduling
public class Application extends WebMvcConfigurerAdapter {

	@Bean
	public SessionContextUtils sessionContextUtils() {
		return new SessionContextUtils();
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
