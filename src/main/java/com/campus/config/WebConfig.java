package com.campus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowedOriginPatterns("*") // 支持所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 支持所有方法
                .allowCredentials(true) // 允许带cookie
                .maxAge(3600) // 缓存时间
                .allowedHeaders("*");
    }
}