package org.infinity.passport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

import static org.infinity.passport.config.OAuth2AuthorizationServerSecurityConfiguration.LOGIN_PAGE_URI;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@ServletComponentScan
@Slf4j
public class PassportServerLauncher implements WebMvcConfigurer {

    @Resource
    private Environment env;

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PassportServerLauncher.class, args);
    }

    @PostConstruct
    private void validateProfiles() {
        Assert.notEmpty(env.getActiveProfiles(), "No Spring profile configured!");
        Assert.isTrue(env.getActiveProfiles().length == 1, "Multiple profiles are not allowed!");
        Arrays.stream(env.getActiveProfiles())
                .filter(activeProfile -> !ArrayUtils.contains(ApplicationConstants.AVAILABLE_PROFILES, activeProfile))
                .findFirst().ifPresent((activeProfile) -> {
                    log.error("Mis-configured application with an illegal profile '{}'!", activeProfile);
                    System.exit(0);
                });
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(LOGIN_PAGE_URI).setViewName(LOGIN_PAGE_URI);
//        registry.addViewController("/oauth/confirm_access").setViewName("oauth2/authorize");
    }
}
