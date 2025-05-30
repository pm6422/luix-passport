package cn.luixtech.passport.server.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web application configuration
 */
@Configuration
@AllArgsConstructor
@Slf4j
public class WebConfigurer implements WebMvcConfigurer {
    private final       ApplicationProperties applicationProperties;
    public static final String[]              EXCLUDED_PATHS = {
            "/login",
            "/sign-up",
            "/activate-account",
            "/forgot-password",
            "/reset-password",
    };

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Map specific paths to their respective views
        for (String path : EXCLUDED_PATHS) {
            registry.addViewController(path).setViewName(path.substring(1));
        }

        // Map all other paths to the root
        // only supports one lovel path
//        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        applicationProperties.getAllowedCors().getMappings().forEach((key, value) -> registry
                .addMapping(key)
                .allowedOrigins(value)
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
    }
}