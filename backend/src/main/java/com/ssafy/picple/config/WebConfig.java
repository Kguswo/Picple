package com.ssafy.picple.config;

import com.ssafy.picple.interceptor.JWTInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JWTInterceptor jwtInterceptor;

    // @Value("${cors.allowed.origins}")
    // private String[] allowOrigins;

    // jwt interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/users/login", "/users/sign-up", "/users/mail", "/users/mail/find", "/users/mailcheck",
                        "/users/test-email", "/users/reset-password", "/users/refresh-token");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://i11a503.p.ssafy.io")
                .allowCredentials(true)
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
                .maxAge(1800);
    }
}
