package cn.luixtech.passport.server.config;

import cn.luixtech.passport.server.interceptor.SpaForwardInterceptor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static cn.luixtech.passport.server.config.WebServerSecurityConfiguration.MANAGEMENT_REQUESTS;
import static cn.luixtech.passport.server.config.WebServerSecurityConfiguration.STATIC_RESOURCES;

/**
 * Web application configuration
 */
@Configuration
@AllArgsConstructor
@Slf4j
public class WebConfigurer implements WebMvcConfigurer {
    private static final String[]              NON_SPA_PATHS = {
            "/login",
//            "/sign-up",
            "/oauth2/consent"
//            "/activate-account",
//            "/forgot-password",
//            "/reset-password",
    };
    private final        SpaForwardInterceptor spaForwardInterceptor;
    private final        ApplicationProperties applicationProperties;

    private static final String[] API_REQUESTS = {
            "/api/**",
            "/open-api/**",
    };

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Map specific paths to their respective views
        for (String path : NON_SPA_PATHS) {
            registry.addViewController(path).setViewName(path.substring(1));
        }
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        applicationProperties.getAllowedCors().getMappings().forEach((key, value) -> registry
                .addMapping(key)
                .allowedOrigins(value)
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(spaForwardInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(STATIC_RESOURCES)
                .excludePathPatterns(NON_SPA_PATHS)
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**")
                .excludePathPatterns(API_REQUESTS)
                .excludePathPatterns(MANAGEMENT_REQUESTS);
    }
}