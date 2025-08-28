// src/main/java/com/example/finalassignment/config/WebMvcConfig.java

package com.example.finalassignment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // プロジェクトルートにある 'review-images' フォルダのパスを取得
        String imageDir = Paths.get("review-images").toAbsolutePath().toString();

        // /images/reviews/** というURLへのアクセスを、
        // PC上の 'review-images' フォルダにマッピングする
        registry.addResourceHandler("/images/reviews/**")
                .addResourceLocations("file:" + imageDir + "/");
    }
}
