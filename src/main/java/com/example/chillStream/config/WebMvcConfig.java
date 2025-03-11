package com.example.chillStream.config;

import com.example.chillStream.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final VisitorService visitorService;
    
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/video1/**")
                .addResourceLocations("file:///C:/ott/contents/video1/");

        registry.addResourceHandler("/preview/**")
                .addResourceLocations("file:///C:/ott/contents/preview/");

        registry.addResourceHandler("/thumbnail/**")
                .addResourceLocations("file:///C:/ott/contents/thumbnail/");

        registry.addResourceHandler("/ad/**")
                .addResourceLocations("file:///C:/ott/contents/ad/");

        registry.addResourceHandler("/subtitle/**")
                .addResourceLocations("file:///C:/ott/contents/subtitle/");
        
        registry.addResourceHandler("/banner/**")
                .addResourceLocations("file:///C:/ott/contents/banner/");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitorInterceptor(visitorService))
              .addPathPatterns("/**");
    }
}
