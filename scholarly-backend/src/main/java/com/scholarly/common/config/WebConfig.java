package com.scholarly.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map virtual /uploads/** paths directly to localized absolute filesystem blocks
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///c:/Users/User/Desktop/scholarly/uploads/");
    }
}
