package com.mrbai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Value("${blogUrl}")
    private String blogUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String url = "file:" + blogUrl;
        registry.addResourceHandler("/doc/**").addResourceLocations(url);
    }
}
