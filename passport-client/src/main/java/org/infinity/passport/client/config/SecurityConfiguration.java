package org.infinity.passport.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(requests -> requests.anyRequest().authenticated())
                .oauth2Login(oauth2clientLogin -> oauth2clientLogin.loginPage("/oauth2/authorization/felord"))
                .oauth2Client()
                .and()
                .exceptionHandling();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer ignore() {
        return web ->
                web.ignoring()
                        .antMatchers("/content/**", "/favicon.ico", "/index.html", "/home.html", "/", "/login", "/logout");
    }
}


